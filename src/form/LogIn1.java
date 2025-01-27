package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LogIn1 extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPass;
    private JButton btnLogin, btnCancel;
    private JCheckBox showPasswordCheckBox;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/attenadnce_management";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = ""; 

    public LogIn1() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255)); // Light Blue Background

        JLabel lblTitle = new JLabel("Welcome Back!");
        lblTitle.setBounds(120, 20, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 144, 255));
        panel.add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 70, 100, 25);
        panel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 70, 180, 30);
        txtUsername.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 110, 100, 25);
        panel.add(lblPassword);

        txtPass = new JPasswordField();
        txtPass.setBounds(150, 110, 180, 30);
        txtPass.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(txtPass);

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(150, 145, 180, 25);
        panel.add(showPasswordCheckBox);

        btnLogin = new JButton("Log In");
        btnLogin.setBounds(80, 190, 100, 35);
        btnLogin.setBackground(new Color(30, 144, 255));
        btnLogin.setForeground(Color.WHITE);
        panel.add(btnLogin);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(200, 190, 100, 35);
        btnCancel.setBackground(new Color(220, 20, 60));
        btnCancel.setForeground(Color.WHITE);
        panel.add(btnCancel);

        add(panel);

        showPasswordCheckBox.addActionListener(e -> 
            txtPass.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '*'));

        btnLogin.addActionListener(e -> authenticateUser());

        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void authenticateUser() {
    String username = txtUsername.getText();
    String password = new String(txtPass.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String sql = "SELECT password FROM users WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String storedHashedPassword = rs.getString("password");
            if (storedHashedPassword.equals(hashPassword(password))) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                this.dispose();
                new Dashboard1().setVisible(true); // Open Dashboard1 after successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "User not found! Please register.", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            new RegistrationForm().setVisible(true); // Open Registration Form
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this, "Error hashing password!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        new LogIn1();
    }
}
