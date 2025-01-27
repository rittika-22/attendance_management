package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class UpdateInfo extends JFrame {
    private Connection con;
    private JTextField idField, columnField, valueField;
    private JButton updateButton, resetButton, backButton;

    public UpdateInfo() {
        try {
            connect();
            initComponents();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connect() throws SQLException {
        String url = "jdbc:mysql://localhost/attenadnce_management";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful!");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found.", e);
        }
    }

    private void initComponents() {
        // Set up JFrame
        setTitle("Update Info");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(224, 247, 250)); // Light pastel blue background

        // Header
        JLabel headerLabel = new JLabel("Update Student Information", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(33, 150, 243));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        inputPanel.setBackground(new Color(224, 247, 250));

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idField = new JTextField();
        styleInputField(idField);

        JLabel columnLabel = new JLabel("Column to Update:");
        columnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        columnField = new JTextField();
        styleInputField(columnField);

        JLabel valueLabel = new JLabel("New Value:");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        valueField = new JTextField();
        styleInputField(valueField);

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(columnLabel);
        inputPanel.add(columnField);
        inputPanel.add(valueLabel);
        inputPanel.add(valueField);

        add(inputPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(224, 247, 250));

        updateButton = createStyledButton("Update");
        updateButton.addActionListener(e -> updateInfo());

        resetButton = createStyledButton("Reset");
        resetButton.addActionListener(e -> resetFields());

        backButton = createStyledButton("Back");
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Finalize Frame
        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2));
        field.setPreferredSize(new Dimension(200, 30));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243)); // Soft blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(25, 118, 210), 2));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 136, 229));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243));
            }
        });

        return button;
    }

    private void updateInfo() {
        String studentId = idField.getText().trim();
        String column = columnField.getText().trim();
        String value = valueField.getText().trim();

        if (studentId.isEmpty() || column.isEmpty() || value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE student_attn SET `" + column + "` = ? WHERE ID_Number = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, value);
            pst.setString(2, studentId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Update successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No record found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        idField.setText("");
        columnField.setText("");
        valueField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateInfo().setVisible(true));
    }
}
