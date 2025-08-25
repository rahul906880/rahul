package org.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class Signup3 extends JFrame implements ActionListener {
    JRadioButton r1, r2, r3, r4;
    JCheckBox c1, c2, c3, c4, c5, c6;
    JButton s, c;
    String formno;

    Signup3(String formno) {
        this.formno = formno;
        setLayout(null);
        setSize(850, 800);
        setLocation(315, 1);
        getContentPane().setBackground(new Color(215, 252, 252));

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel image = new JLabel(new ImageIcon(i2));
        image.setBounds(10, 5, 100, 100);
        add(image);

        JLabel l1 = new JLabel("Page 3:");
        l1.setFont(new Font("Raleway", Font.BOLD, 22));
        l1.setBounds(370, 6, 400, 40);
        add(l1);

        JLabel l2 = new JLabel("Accounts Details:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));
        l2.setBounds(335, 35, 400, 40);
        add(l2);

        JLabel l3 = new JLabel("Account Type:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));
        l3.setBounds(100, 140, 200, 30);
        add(l3);

        r1 = new JRadioButton("Saving Account");
        r2 = new JRadioButton("Fixed Deposit Account");
        r3 = new JRadioButton("Current Account");
        r4 = new JRadioButton("Recurring Deposit Account");
        r1.setBounds(100, 180, 200, 30);
        r2.setBounds(350, 180, 300, 30);
        r3.setBounds(100, 220, 250, 30);
        r4.setBounds(350, 220, 300, 30);

        Font f = new Font("Raleway", Font.BOLD, 16);
        for (JRadioButton rb : new JRadioButton[]{r1, r2, r3, r4}) {
            rb.setFont(f);
            rb.setBackground(new Color(215, 252, 252));
            add(rb);
        }

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(r1);
        buttonGroup.add(r2);
        buttonGroup.add(r3);
        buttonGroup.add(r4);

        JLabel l4 = new JLabel("Card Number:");
        l4.setFont(new Font("Raleway", Font.BOLD, 18));
        l4.setBounds(100, 280, 200, 30);
        add(l4);

        JLabel l5 = new JLabel("(Your 16-digit Card Number)");
        l5.setFont(new Font("Raleway", Font.BOLD, 12));
        l5.setBounds(100, 310, 200, 20);
        add(l5);

        JLabel l6 = new JLabel("XXXX-XXXX-XXXX-4841");
        l6.setFont(new Font("Raleway", Font.BOLD, 18));
        l6.setBounds(330, 280, 250, 30);
        add(l6);

        JLabel l7 = new JLabel("(It would appear on atm card/cheque bank and statements)");
        l7.setFont(new Font("Raleway", Font.BOLD, 12));
        l7.setBounds(330, 300, 500, 30);
        add(l7);

        JLabel l8 = new JLabel("PIN:");
        l8.setFont(new Font("Raleway", Font.BOLD, 18));
        l8.setBounds(100, 350, 200, 30);
        add(l8);

        JLabel l9 = new JLabel("XXXX");
        l9.setFont(new Font("Raleway", Font.BOLD, 18));
        l9.setBounds(330, 350, 200, 30);
        add(l9);

        JLabel l10 = new JLabel("4-digit Password:");
        l10.setFont(new Font("Raleway", Font.BOLD, 12));
        l10.setBounds(100, 380, 200, 30);
        add(l10);

        JLabel l11 = new JLabel("Services Required:");
        l11.setFont(new Font("Raleway", Font.BOLD, 18));
        l11.setBounds(100, 430, 200, 30);
        add(l11);

        c1 = new JCheckBox("ATM CARD");
        c2 = new JCheckBox("Internet Banking");
        c3 = new JCheckBox("Mobile Banking");
        c4 = new JCheckBox("Email Alerts");
        c5 = new JCheckBox("Cheque Book");
        c6 = new JCheckBox("E-Statement");

        JCheckBox[] all = {c1, c2, c3, c4, c5, c6};
        int[][] pos = {{100, 480}, {350, 480}, {100, 530}, {350, 530}, {100, 580}, {350, 580}};
        for (int i = 0; i < all.length; i++) {
            all[i].setBackground(new Color(215, 252, 252));
            all[i].setFont(new Font("Raleway", Font.BOLD, 16));
            all[i].setBounds(pos[i][0], pos[i][1], 200, 30);
            add(all[i]);
        }

        JCheckBox c7 = new JCheckBox("I hereby declare that the above entered details are correct.", true);
        c7.setBackground(new Color(215, 252, 252));
        c7.setFont(new Font("Raleway", Font.BOLD, 12));
        c7.setBounds(100, 630, 600, 20);
        add(c7);

        JLabel l12 = new JLabel("Form No:");
        l12.setFont(new Font("Raleway", Font.BOLD, 18));
        l12.setBounds(600, 10, 100, 30);
        add(l12);

        JLabel l13 = new JLabel(formno);
        l13.setFont(new Font("Raleway", Font.BOLD, 16));
        l13.setBounds(690, 10, 50, 30);
        add(l13);

        s = new JButton("Submit");
        s.setFont(new Font("Raleway", Font.BOLD, 14));
        s.setBackground(Color.BLACK);
        s.setForeground(Color.WHITE);
        s.setBounds(250, 680, 100, 30);
        s.addActionListener(this);
        add(s);

        c = new JButton("Cancel");
        c.setFont(new Font("Raleway", Font.BOLD, 14));
        c.setBackground(Color.BLACK);
        c.setForeground(Color.WHITE);
        c.setBounds(420, 680, 100, 30);
        c.addActionListener(this);
        add(c);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String atype = r1.isSelected() ? "Saving Account" :
                r2.isSelected() ? "Fixed Deposit Account" :
                        r3.isSelected() ? "Current Account" :
                                r4.isSelected() ? "Recurring Deposit Account" : null;

        String fac = "";
        if (c1.isSelected()) fac += " ATM CARD";
        if (c2.isSelected()) fac += " Internet Banking";
        if (c3.isSelected()) fac += " Mobile Banking";
        if (c4.isSelected()) fac += " Email Alerts";
        if (c5.isSelected()) fac += " Cheque Book";
        if (c6.isSelected()) fac += " E-Statement";

        try {
            if (e.getSource() == s) {
                if (atype == null) {
                    JOptionPane.showMessageDialog(null, "Account Type is required");
                    return;
                }
                if (fac.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "At least one Service is required");
                    return;
                }

                Conn c1 = new Conn();
                Random ran = new Random();
                String cardno;
                String pin;

                while (true) {
                    long first16 = 1000000000000000L + (Math.abs(ran.nextLong()) % 9000000000000000L);
                    cardno = "" + first16;
                    PreparedStatement checkCard = c1.prepareStatement("SELECT 1 FROM login WHERE card_number = ?");
                    checkCard.setString(1, cardno);
                    ResultSet rs = checkCard.executeQuery();
                    if (!rs.next()) break;
                }

                pin = String.valueOf(1000 + ran.nextInt(9000));
                String hashedPin = BCrypt.hashpw(pin, BCrypt.gensalt());

                PreparedStatement userPstmt = c1.prepareStatement("SELECT user_id FROM users WHERE form_no = ?");
                userPstmt.setString(1, formno);
                ResultSet userRs = userPstmt.executeQuery();
                int userId = -1;
                if (userRs.next()) userId = userRs.getInt("user_id");

                PreparedStatement acctPstmt = c1.prepareStatement(
                        "INSERT INTO accounts (user_id, account_number, account_type, balance, facilities) VALUES (?, ?, ?, 0.00, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS
                );
                acctPstmt.setInt(1, userId);
                acctPstmt.setString(2, cardno);
                acctPstmt.setString(3, atype);
                acctPstmt.setString(4, fac);
                acctPstmt.executeUpdate();

                ResultSet acctRs = acctPstmt.getGeneratedKeys();
                int accountId = -1;
                if (acctRs.next()) accountId = acctRs.getInt(1);

                PreparedStatement loginPstmt = c1.prepareStatement(
                        "INSERT INTO login (account_id, card_number, pin_hash) VALUES (?, ?, ?)"
                );
                loginPstmt.setInt(1, accountId);
                loginPstmt.setString(2, cardno);
                loginPstmt.setString(3, hashedPin);
                loginPstmt.executeUpdate();

                JOptionPane.showMessageDialog(null,
                        "Account Created Successfully!\nCard Number: " + cardno + "\nPIN: " + pin);

                setVisible(false);
                new main_Class(pin);
                c1.close();
            } else if (e.getSource() == c) {
                System.exit(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Signup3("101");
    }
}