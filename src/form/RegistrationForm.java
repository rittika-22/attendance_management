package form;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm extends JFrame {

    private JTextField firstNameField, lastNameField, usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextArea addressArea;
    private JButton registerButton, cancelButton;
    private JCheckBox showPasswordCheckBox;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/attenadnce_management";
    private static final String DB_USER = "root"; // Change if needed
    private static final String DB_PASSWORD = ""; // Change if needed

    public RegistrationForm() {
        setTitle("User Registration");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("User Registration");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.WHITE);

        // Form Fields
        formPanel.add(createLabel("First Name:"));
        firstNameField = createTextField();
        formPanel.add(firstNameField);

        formPanel.add(createLabel("Last Name:"));
        lastNameField = createTextField();
        formPanel.add(lastNameField);

        formPanel.add(createLabel("Username:"));
        usernameField = createTextField();
        formPanel.add(usernameField);

        formPanel.add(createLabel("Password:"));
        passwordField = createPasswordField();
        formPanel.add(passwordField);

        formPanel.add(createLabel("Confirm Password:"));
        confirmPasswordField = createPasswordField();
        formPanel.add(confirmPasswordField);

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBackground(Color.WHITE);
        formPanel.add(showPasswordCheckBox);
        formPanel.add(new JLabel()); // Placeholder

        formPanel.add(createLabel("Address:"));
        addressArea = new JTextArea(3, 20);
        addressArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(new JScrollPane(addressArea));

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = createButton("Register", new Color(51, 153, 255));
        cancelButton = createButton("Cancel", Color.RED);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        mainPanel.add(buttonPanel);

        add(mainPanel);

        // Action Listeners
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        registerButton.addActionListener(e -> registerUser());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return passwordField;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color.darker()));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
            confirmPasswordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('*');
            confirmPasswordField.setEchoChar('*');
        }
    }

     private void registerUser() {
    String firstName = firstNameField.getText();
    String lastName = lastNameField.getText();
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());
    String confirmPassword = new String(confirmPasswordField.getPassword());
    String address = addressArea.getText();

    if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || address.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hash the password before storing it
    String hashedPassword = hashPassword(password);

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, address) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setString(3, username);
        pstmt.setString(4, hashedPassword);  // Store the hashed password
        pstmt.setString(5, address);
        pstmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Registration Successful!");
        this.dispose();
        new LogIn1().setVisible(true); // Go back to LogIn1
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Hashes the password using SHA-256 encryption.
 */
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
        new RegistrationForm();
    }
}
