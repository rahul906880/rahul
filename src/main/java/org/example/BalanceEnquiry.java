package org.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BalanceEnquiry extends JFrame implements ActionListener {
    public JLabel label2;
    JButton b1;
    String pin;

    public BalanceEnquiry(String pin) {
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1350, 700, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel bg = new JLabel(i3);
        bg.setBounds(0, 0, 1050, 690);
        add(bg);

        JLabel label1 = new JLabel("Your Current Balance is Rs ");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(230, 125, 700, 35);
        bg.add(label1);

        label2 = new JLabel();
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(230, 160, 400, 35);
        bg.add(label2);

        b1 = new JButton("Back");
        b1.setBounds(460, 342, 130, 30);
        b1.setBackground(new Color(65, 125, 128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        bg.add(b1);

        setLayout(null);
        setSize(950, 750);
        setLocation(275, 5);
        setVisible(true);

        JDialog processingDialog = new JDialog(this,"Processing", true);
        processingDialog.setLayout(new BorderLayout());
        processingDialog.add(new JLabel("Processing...",SwingConstants.CENTER), BorderLayout.CENTER);
        processingDialog.setBounds(602,270,200,100);
        processingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                double balance = 0.0;
                Conn c = new Conn();
                try {
                    Integer accountId = resolveAccountIdByPin(c, pin);
                    if (accountId != null) {
                        PreparedStatement ps = c.prepareStatement(
                                "SELECT balance FROM accounts WHERE account_id = ?"
                        );
                        ps.setInt(1, accountId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            balance = rs.getDouble("balance");
                        }
                    } else {
                        throw new Exception("Invalid PIN");
                    }
                } finally {
                    c.close();
                }
                return balance;
            }

            @Override
            protected void done() {
                processingDialog.dispose();
                try {
                    Double balance = get();
                    label2.setText("" + balance);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(BalanceEnquiry.this, ex.getMessage().equals("Invalid PIN") ?
                            "Invalid PIN" : "An error occurred. Please try again.");
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
        processingDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        new main_Class(pin);
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
        new BalanceEnquiry("");
    }
}
