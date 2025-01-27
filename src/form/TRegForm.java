package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TRegForm extends JFrame {

    private JTextField teacherNameField, courseCodeField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;
    private JCheckBox showPasswordCheckBox;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/attenadnce_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TRegForm() {
        setTitle("Teacher Registration");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main Panel with Background Color
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(44, 62, 80)); // Dark Blue Background

        // Title Label
        JLabel title = new JLabel("Teacher Registration", SwingConstants.CENTER);
        title.setBounds(80, 10, 340, 40);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(236, 240, 241)); // Light Gray
        panel.add(title);

        // Labels and Fields
        JLabel teacherLabel = createLabel("Teacher Name:");
        teacherLabel.setBounds(50, 70, 150, 30);
        panel.add(teacherLabel);

        teacherNameField = createTextField();
        teacherNameField.setBounds(210, 70, 220, 30);
        panel.add(teacherNameField);

        JLabel courseLabel = createLabel("Course Code:");
        courseLabel.setBounds(50, 120, 150, 30);
        panel.add(courseLabel);

        courseCodeField = createTextField();
        courseCodeField.setBounds(210, 120, 220, 30);
        panel.add(courseCodeField);

        JLabel passwordLabel = createLabel("Password:");
        passwordLabel.setBounds(50, 170, 150, 30);
        panel.add(passwordLabel);

        passwordField = createPasswordField();
        passwordField.setBounds(210, 170, 220, 30);
        panel.add(passwordField);

        JLabel confirmPasswordLabel = createLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 220, 150, 30);
        panel.add(confirmPasswordLabel);

        confirmPasswordField = createPasswordField();
        confirmPasswordField.setBounds(210, 220, 220, 30);
        panel.add(confirmPasswordField);

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(210, 260, 180, 25);
        showPasswordCheckBox.setBackground(new Color(44, 62, 80)); // Match background
        showPasswordCheckBox.setForeground(new Color(236, 240, 241)); // Light Gray
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        panel.add(showPasswordCheckBox);

        // Buttons
        registerButton = createButton("Register", new Color(34, 167, 240)); // Blue
        registerButton.setBounds(100, 320, 130, 40);
        panel.add(registerButton);

        cancelButton = createButton("Cancel", new Color(192, 57, 43)); // Red
        cancelButton.setBounds(250, 320, 130, 40);
        panel.add(cancelButton);

        add(panel);
        setVisible(true);

        // Button Actions
        registerButton.addActionListener(e -> registerTeacher());
        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * Creates a formatted JLabel
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(236, 240, 241)); // Light Gray
        return label;
    }

    /**
     * Creates a formatted JTextField
     */
    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    /**
     * Creates a formatted JPasswordField
     */
    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return passwordField;
    }

    /**
     * Creates a formatted JButton
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color.darker()));
        return button;
    }

    /**
     * Toggles password visibility
     */
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
            confirmPasswordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('*');
            confirmPasswordField.setEchoChar('*');
        }
    }

    /**
     * Registers a teacher in the database
     */
    private void registerTeacher() {
        String tname = teacherNameField.getText();
        String ccode = courseCodeField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (tname.isEmpty() || ccode.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO teachers (teacher_name, course_code, password) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tname);
            pstmt.setString(2, ccode);
            pstmt.setString(3, hashPassword(password));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Teacher Registered Successfully!");
            this.dispose();
            new TeacherCode().setVisible(true); // Redirect to Teacher Login
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hashes the password using SHA-256
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
        new TRegForm();
    }
}
