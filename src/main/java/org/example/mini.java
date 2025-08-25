package org.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class mini extends JFrame implements java.awt.event.ActionListener {
    String pin;
    JButton exitButton;
    JTextArea textArea;
    JLabel balanceLabel;

    public mini(String pin) {
        this.pin = pin;
        setTitle("Mini Statement");
        getContentPane().setBackground(new Color(245, 245, 245));
        setSize(520, 600);
        setLocation(100, 60);
        setLayout(null);

        JLabel title = new JLabel("Mini Statement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setBounds(160, 20, 250, 30);
        add(title);

        JLabel cardLabel = new JLabel();
        cardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardLabel.setBounds(20, 70, 400, 20);
        add(cardLabel);

        JPanel statementPanel = new JPanel();
        statementPanel.setLayout(new BorderLayout());
        statementPanel.setBackground(Color.WHITE);
        statementPanel.setBounds(20, 110, 470, 300);
        statementPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        add(statementPanel);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        statementPanel.add(scrollPane, BorderLayout.CENTER);

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setBounds(20, 430, 400, 25);
        balanceLabel.setForeground(new Color(0, 102, 51));
        add(balanceLabel);

        exitButton = new JButton("Close");
        exitButton.setBounds(200, 480, 100, 30);
        exitButton.setBackground(new Color(0, 102, 204));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitButton.addActionListener(this);
        add(exitButton);

        try {
            Conn c = new Conn();
            try {
                Integer accountId = resolveAccountIdByPin(c, pin);
                if (accountId == null) {
                    JOptionPane.showMessageDialog(null, "Invalid PIN");
                } else {
                    // Card number
                    PreparedStatement cps = c.prepareStatement(
                            "SELECT card_number FROM login WHERE account_id = ?"
                    );
                    cps.setInt(1, accountId);
                    ResultSet crs = cps.executeQuery();
                    if (crs.next()) {
                        String cardNumber = crs.getString("card_number");
                        if (cardNumber != null && cardNumber.length() == 16) {
                            cardLabel.setText("Card Number: " + cardNumber.substring(0, 4) + "XXXXXXXX" + cardNumber.substring(12));
                        } else {
                            cardLabel.setText("Card Number: " + cardNumber);
                        }
                    }

                    // Transactions (latest first)
                    PreparedStatement tps = c.prepareStatement(
                            "SELECT transaction_date, type, amount FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC LIMIT 10"
                    );
                    tps.setInt(1, accountId);
                    ResultSet trs = tps.executeQuery();

                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%-25s %-12s %12s%n", "Date", "Type", "Amount"));
                    sb.append("------------------------------------------------------------\n");
                    while (trs.next()) {
                        String date = trs.getString("transaction_date");
                        String type = trs.getString("type");
                        String amt = trs.getString("amount");
                        sb.append(String.format("%-25s %-12s %12s%n", date, type, "Rs. " + amt));
                    }
                    textArea.setText(sb.toString());

                    // Current balance
                    PreparedStatement bps = c.prepareStatement(
                            "SELECT balance FROM accounts WHERE account_id = ?"
                    );
                    bps.setInt(1, accountId);
                    ResultSet brs = bps.executeQuery();
                    double balance = 0.0;
                    if (brs.next()) {
                        balance = brs.getDouble("balance");
                    }
                    balanceLabel.setText("Available Balance: Rs " + balance);
                }
            } finally {
                c.close();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred. Please try again.");
            ex.printStackTrace();
        }

        setVisible(true);
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
        new mini("");
    }
}
