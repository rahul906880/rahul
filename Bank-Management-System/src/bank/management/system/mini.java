package bank.management.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class mini extends JFrame implements ActionListener {
    String pin;
    JButton exitButton;
    JTextArea textArea;
    JLabel balanceLabel;

    mini(String pin){
        this.pin = pin;
        setTitle("Mini Statement");
        getContentPane().setBackground(new Color(245, 245, 245));
        setSize(520, 600); // Wider frame to avoid horizontal scroll
        setLocation(100, 60);
        setLayout(null);

        // Heading
        JLabel title = new JLabel("Mini Statement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setBounds(160, 20, 250, 30);
        add(title);

        // Card number label
        JLabel cardLabel = new JLabel();
        cardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardLabel.setBounds(20, 70, 400, 20);
        add(cardLabel);

        // Statement Box Panel with curved border
        JPanel statementPanel = new JPanel();
        statementPanel.setLayout(new BorderLayout());
        statementPanel.setBackground(Color.WHITE);
        statementPanel.setBounds(20, 110, 470, 300);
        statementPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true)); // curved border

        // Transaction area with scroll
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13)); // Monospaced font for alignment
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setLineWrap(false); // Prevent text wrapping

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        statementPanel.add(scrollPane, BorderLayout.CENTER);
        add(statementPanel);

        // Balance label
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setBounds(20, 430, 400, 25);
        balanceLabel.setForeground(new Color(0, 102, 51));
        add(balanceLabel);

        // Exit button
        exitButton = new JButton("Close");
        exitButton.setBounds(200, 480, 100, 30);
        exitButton.setBackground(new Color(0, 102, 204));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitButton.addActionListener(this);
        add(exitButton);

        // Fetch Card Number
        try {
            Conn c = new Conn();
            ResultSet resultSet = c.statement.executeQuery("SELECT * FROM login WHERE pin = '"+pin+"'");
            while (resultSet.next()){
                String cardNumber = resultSet.getString("card_number");
                cardLabel.setText("Card Number: " + cardNumber.substring(0,4) + "XXXXXXXX" + cardNumber.substring(12));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        // Fetch Transactions with alignment
        try {
            int balance = 0;
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery("SELECT * FROM bank WHERE pin = '"+pin+"'");
            StringBuilder sb = new StringBuilder();

            // Header
            // Header with fixed widths
            sb.append(String.format("%-25s %-12s %12s\n", "Date", "Type", "Amount"));
            sb.append("------------------------------------------------------------\n");

            while (rs.next()) {
                String date = rs.getString("date");
                String type = rs.getString("type");
                String amount = rs.getString("amount");

                // Align with fixed width for each column
                sb.append(String.format("%-25s %-12s %12s\n", date, type, "Rs. " + amount));

                if (type.equals("Deposit")) {
                    balance += Integer.parseInt(amount);
                } else {
                    balance -= Integer.parseInt(amount);
                }
            }


            textArea.setText(sb.toString());
            balanceLabel.setText("Available Balance: Rs " + balance);
        } catch (Exception e){
            e.printStackTrace();
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }

    public static void main(String[] args) {
        new mini("");
    }
}
