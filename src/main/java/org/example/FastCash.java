package org.example;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class FastCash extends JFrame implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(FastCash.class);

    JButton b1, b2, b3, b4, b5, b6, b7;
    String pin;

    public FastCash(String pin) {
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1350, 700, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel bg = new JLabel(i3);
        bg.setBounds(0, 0, 1050, 690);
        add(bg);

        JLabel label1 = new JLabel("SELECT WITHDRAWAL AMOUNT");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 18));
        label1.setBounds(260, 130, 400, 35);
        bg.add(label1);

        b1 = mk(btn("Rs. 100"), bg, 205, 223);
        b2 = mk(btn("Rs. 500"), bg, 447, 223);
        b3 = mk(btn("Rs. 1000"), bg, 205, 263);
        b4 = mk(btn("Rs. 2000"), bg, 447, 263);
        b5 = mk(btn("Rs. 5000"), bg, 205, 303);
        b6 = mk(btn("Rs. 10000"), bg, 447, 303);

        b7 = new JButton("Back");
        b7.setForeground(Color.WHITE);
        b7.setBackground(new Color(65, 125, 128));
        b7.setBounds(447, 343, 140, 30);
        b7.addActionListener(this);
        bg.add(b7);

        setLayout(null);
        setSize(950, 750);
        setLocation(275, 5);
        setVisible(true);
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(65, 125, 128));
        b.setSize(140, 30);
        b.addActionListener(this);
        return b;
    }

    private JButton mk(JButton b, JLabel bg, int x, int y) {
        b.setBounds(x, y, 140, 30);
        bg.add(b);
        return b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b7) {
            setVisible(false);
            new main_Class(pin);
            return;
        }

        String amountStr = ((JButton) e.getSource()).getText().substring(4).trim();
        try {
            double amount = Double.parseDouble(amountStr);

            JDialog processingDialog = new JDialog(this,"Processing", true);
            processingDialog.setLayout(new BorderLayout());
            processingDialog.add(new JLabel("Processing...",SwingConstants.CENTER), BorderLayout.CENTER);
          
            processingDialog.setBounds(602,270,200,100);
          
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
                            logger.info("FastCash withdrawal successful: accountId={}, Amount={}", accountId, amountStr);
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
                        JOptionPane.showMessageDialog(FastCash.this, "Rs. " + amountStr + " Debited Successfully");
                        setVisible(false);
                        new main_Class(pin);
                    } catch (Exception ex) {
                        logger.error("FastCash withdrawal failed", ex);
                        JOptionPane.showMessageDialog(FastCash.this, ex.getMessage().equals("Invalid PIN") ?
                                "Invalid PIN" : ex.getMessage().equals("Insufficient Balance") ?
                                "Insufficient Balance" : "An error occurred. Please try again.");
                        ex.printStackTrace();
                    }
                }
            };

            worker.execute();
            processingDialog.setVisible(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid amount format");
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
        new FastCash("");
    }
}
