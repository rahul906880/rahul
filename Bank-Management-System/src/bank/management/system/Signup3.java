package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.sql.ResultSet;

public class Signup3 extends JFrame implements ActionListener {

    JRadioButton r1, r2, r3, r4;
    JCheckBox c1, c2, c3, c4, c5, c6;
    JButton s, c;
    String formno;

    Signup3(String formno) {
        this.formno = formno;
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
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

        for (JRadioButton rb : new JRadioButton[]{r1, r2, r3, r4}) {
            rb.setFont(new Font("Raleway", Font.BOLD, 16));
            rb.setBackground(new Color(215, 252, 252));
            add(rb);
        }

        r1.setBounds(100, 180, 150, 30);
        r2.setBounds(350, 180, 300, 30);
        r3.setBounds(100, 220, 250, 30);
        r4.setBounds(350, 220, 250, 30);

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

        JCheckBox[] services = {c1, c2, c3, c4, c5, c6};

        int y = 480;
        for (int i = 0; i < services.length; i++) {
            services[i].setBackground(new Color(215, 252, 252));
            services[i].setFont(new Font("Raleway", Font.BOLD, 16));
            services[i].setBounds(i % 2 == 0 ? 100 : 350, y, 200, 30);
            add(services[i]);
            if (i % 2 == 1) y += 50;
        }

        JCheckBox c7 = new JCheckBox("I hereby declare that the above entered details are correct to the best of my knowledge.", true);
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

        getContentPane().setBackground(new Color(215, 252, 252));
        setSize(850, 800);
        setLayout(null);
        setLocation(315, 1);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String atype = null;
        if (r1.isSelected()) atype = "Saving Account";
        else if (r2.isSelected()) atype = "Fixed Deposit Account";
        else if (r3.isSelected()) atype = "Current Account";
        else if (r4.isSelected()) atype = "Recurring Deposit Account";

        String fac = "";
        if (c1.isSelected()) fac += " ATM CARD";
        if (c2.isSelected()) fac += " Internet Banking";
        if (c3.isSelected()) fac += " Mobile Banking";
        if (c4.isSelected()) fac += " Email Alerts";
        if (c5.isSelected()) fac += " Cheque Book";
        if (c6.isSelected()) fac += " E-Statement";

        String cardno = "";
        String pin = "";

        try {
            if (e.getSource() == s) {
                if (atype == null) {
                    JOptionPane.showMessageDialog(null, "Please select an account type");
                } else {
                    Conn c1 = new Conn();
                    boolean isUnique = false;
                    Random ran = new Random();

                    // ✅ Unique generation loop
                    while (!isUnique) {
                        long first7 = (ran.nextLong() % 90000000L) + 1409963000000000L;
                        cardno = "" + Math.abs(first7);

                        long first3 = (ran.nextLong() % 9000L) + 1000L;
                        pin = "" + Math.abs(first3);

                        String checkQuery = "SELECT * FROM login WHERE card_number = '" + cardno + "' OR pin = '" + pin + "'";
                        ResultSet rs = c1.statement.executeQuery(checkQuery);
                        if (!rs.next()) {
                            isUnique = true;
                        }
                    }

                    // Insert into DB
                    String q1 = "insert into signupthree values('" + formno + "','" + atype + "','" + cardno + "','" + pin + "','" + fac + "')";
                    String q2 = "insert into login values('" + formno + "','" + cardno + "','" + pin + "')";
                    c1.statement.executeUpdate(q1);
                    c1.statement.executeUpdate(q2);

                    JOptionPane.showMessageDialog(null, "Card Number : " + cardno + "\n Pin : " + pin);
                    new Deposit(pin);
                    setVisible(false);
                }
            } else if (e.getSource() == c) {
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Signup3("");
    }
}
