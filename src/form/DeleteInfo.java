package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DeleteInfo extends JFrame {
    private JTextField idField;
    private JButton deleteButton;
    private Connection con;

    public DeleteInfo() {
        setTitle("Delete Student Attendance");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = new JLabel("Delete Student Attendance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 42, 86)); // Soft navy blue color
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel idLabel = new JLabel("Enter Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(new Color(0, 51, 102)); // Darker blue text
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        add(idLabel, gbc);

        idField = new JTextField(20);
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idField.setPreferredSize(new Dimension(100, 35)); // Set width to 250px and height to 35px
        idField.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2)); // Light blue border
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.CENTER;
        add(idField, gbc);

        deleteButton = new JButton("Delete Record");
        styleButton(deleteButton);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(deleteButton, gbc);

        // Connect to the database
        connectToDatabase();

        // Delete student attendance on button click
        deleteButton.addActionListener(e -> deleteStudentAttendance());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(255, 69, 0)); // Red-orange color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(178, 34, 34), 2));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 20, 60)); // Crimson hover color
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 69, 0));
            }
        });
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost/attenadnce_management";
            String username = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
        }
    }

    private void deleteStudentAttendance() {
        try {
            String studentID = idField.getText().trim();

            if (studentID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Student ID!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student's attendance record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM student_attn WHERE ID_Number = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, studentID);

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Student attendance record deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "No record found for this Student ID!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new DeleteInfo();
    }
}
