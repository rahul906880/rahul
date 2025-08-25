package org.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Pin extends JFrame implements ActionListener {
    JButton b1, b2;
    JPasswordField p1, p2;
    String pin;

    public Pin(String pin) {
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1350,700,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel bg = new JLabel(i3);
        bg.setBounds(0,0,1050,690);
        add(bg);

        JLabel label1 = new JLabel("CHANGE YOUR PIN");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(240,120,400,35);
        bg.add(label1);

        JLabel label2 = new JLabel("NEW PIN:");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(240,156,150,35);
        bg.add(label2);

        p1 = new JPasswordField();
        p1.setBackground(new Color(65,125,128));
        p1.setForeground(Color.WHITE);
        p1.setBounds(410,160,180,25);
        p1.setFont(new Font("Raleway", Font.BOLD,22));
        bg.add(p1);

        JLabel label3 = new JLabel("Re-Enter NEW PIN:");
        label3.setForeground(Color.WHITE);
        label3.setFont(new Font("System", Font.BOLD, 16));
        label3.setBounds(240,190,400,35);
        bg.add(label3);

        p2 = new JPasswordField();
        p2.setBackground(new Color(65,125,128));
        p2.setForeground(Color.WHITE);
        p2.setBounds(410,195,180,25);
        p2.setFont(new Font("Raleway", Font.BOLD,22));
        bg.add(p2);

        b1 = new JButton("CHANGE");
        b1.setBounds(460,300,130,30);
        b1.setBackground(new Color(65,125,128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        bg.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(460,340,130,30);
        b2.setBackground(new Color(65,125,128));
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        bg.add(b2);

        setSize(950,750);
        setLayout(null);
        setLocation(275,5);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == b2) {
                setVisible(false);
                new main_Class(pin);
                return;
            }

            String pin1 = new String(p1.getPassword()).trim();
            String pin2 = new String(p2.getPassword()).trim();

            if (pin1.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter New PIN");
                return;
            }
            if (!pin1.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(null, "New PIN must be exactly 4 digits");
                return;
            }
            if (!pin1.equals(pin2)) {
                JOptionPane.showMessageDialog(null, "Entered PIN does not match");
                return;
            }

            Conn c = new Conn();
            try {
                Integer accountId = resolveAccountIdByPin(c, pin);
                if (accountId == null) {
                    JOptionPane.showMessageDialog(null, "Invalid current PIN");
                    return;
                }

                String newHash = BCrypt.hashpw(pin1, BCrypt.gensalt());

                PreparedStatement ps = c.prepareStatement(
                        "UPDATE login SET pin_hash = ? WHERE account_id = ?"
                );
                ps.setString(1, newHash);
                ps.setInt(2, accountId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "PIN changed successfully");
                setVisible(false);
                new main_Class(pin1);
            } finally {
                c.close();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred. Please try again.");
            ex.printStackTrace();
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
        new Pin("");
    }
}