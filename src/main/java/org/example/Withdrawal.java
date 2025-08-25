package org.example;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Withdrawal extends JFrame implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(Withdrawal.class);
    JTextField t1;
    JButton b1, b2;
    String pin;

    public Withdrawal(String pin) {
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1350, 700, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel bg = new JLabel(i3);
        bg.setBounds(0, 0, 1050, 690);
        add(bg);

        JLabel label1 = new JLabel("MAXIMUM WITHDRAWAL IS RS. 10,000");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(230, 125, 400, 35);
        bg.add(label1);

        JLabel label2 = new JLabel("PLEASE ENTER YOUR AMOUNT");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(230, 160, 400, 35);
        bg.add(label2);

        t1 = new JTextField();
        t1.setFont(new Font("Raleway", Font.BOLD, 22));
        t1.setBounds(230, 200, 320, 25);
        bg.add(t1);

        b1 = new JButton("WITHDRAW");
        b1.setBounds(460, 305, 130, 30);
        b1.setBackground(new Color(65, 125, 128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        bg.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(460, 342, 130, 30);
        b2.setBackground(new Color(65, 125, 128));
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        bg.add(b2);

        setLayout(null);
        setSize(950, 750);
        setLocation(275, 5);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b2) {
            setVisible(false);
            new main_Class(pin);
            return;
        }

        String amountStr = t1.getText().trim();
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a valid amount greater than 0");
                return;
            }
            if (amount > 10000) {
                JOptionPane.showMessageDialog(null, "Maximum withdrawal is Rs. 10,000");
                return;
            }

            // Show processing dialog
            JDialog processingDialog = new JDialog(this,"Processing", true);
            processingDialog.setLayout(new BorderLayout());
            processingDialog.add(new JLabel("Processing...",SwingConstants.CENTER), BorderLayout.CENTER);
            // processingDialog.setSize(200, 100);
            processingDialog.setBounds(602,270,200,100);
            //processingDialog.setLocationRelativeTo(this);
            processingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Conn c = new Conn();
                    try {
                        c.connection.setAutoCommit(false);
                        try {
                            Integer accountId = resolveAccountIdByPin(c, pin);
                            if (accountId == null) {
                                throw new Exception("Invalid PIN");
                            }

                            PreparedStatement balPs = c.prepareStatement(
                                    "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE"
                            );
                            balPs.setInt(1, accountId);
                            ResultSet rs = balPs.executeQuery();
                            double balance = 0.0;
                            if (rs.next()) {
                                balance = rs.getDouble("balance");
                            }

                            if (balance < amount) {
                                throw new Exception("Insufficient Balance");
                            }

                            PreparedStatement ups = c.prepareStatement(
                                    "UPDATE accounts SET balance = balance - ? WHERE account_id = ?"
                            );
                            ups.setDouble(1, amount);
                            ups.setInt(2, accountId);
                            ups.executeUpdate();

                            PreparedStatement tps = c.prepareStatement(
                                    "INSERT INTO transactions (account_id, transaction_date, type, amount) VALUES (?, now(), ?, ?)"
                            );
                            tps.setInt(1, accountId);
                            tps.setString(2, "Withdrawal");
                            tps.setDouble(3, amount);
                            tps.executeUpdate();

                            c.connection.commit();
                            logger.info("Withdrawal successful: accountId={}, Amount={}", accountId, amountStr);
                        } catch (Exception ex) {
                            c.connection.rollback();
                            throw ex;
                        } finally {
                            c.connection.setAutoCommit(true);
                        }
                    } finally {
                        c.close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    processingDialog.dispose();
                    try {
                        get();
                        JOptionPane.showMessageDialog(Withdrawal.this, "Rs. " + amountStr + " Debited Successfully");
                        setVisible(false);
                        new main_Class(pin);
                    } catch (Exception ex) {
                        logger.error("Withdrawal failed", ex);
                        JOptionPane.showMessageDialog(Withdrawal.this, ex.getMessage().equals("Invalid PIN") ?
                                "Invalid PIN" : ex.getMessage().equals("Insufficient Balance") ?
                                "Insufficient Balance" : "An error occurred. Please try again.");
                        ex.printStackTrace();
                    }
                }
            };

            worker.execute();
            processingDialog.setVisible(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid amount");
        }
    }

    private Integer resolveAccountIdByPin(Conn c, String plainPin) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT account_id, pin_hash FROM login");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int aid = rs.getInt("account_id");
            String hash = rs.getString("pin_hash");
            if (BCrypt.checkpw(plainPin, hash)) {
                return aid;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new Withdrawal("");
    }
}