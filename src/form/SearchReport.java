package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchReport extends JDialog {
    private JTextField studentIdField;
    private JTextArea reportTextArea;

    public SearchReport(JFrame parent) {
        super(parent, "Search Student Report", true);
        setSize(600, 600);
        setLocationRelativeTo(parent);
        
        // Setting background color for the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // Light soothing blue

        // Top panel for input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(173, 216, 230)); // Light blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Search Student Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112)); // Midnight blue
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(new Color(173, 216, 230));

        JLabel studentIdLabel = new JLabel("Enter Student ID: ");
        studentIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        studentIdField = new JTextField(15);

        JButton searchButton = new JButton("Search");
        // Style the button
        searchButton.setBackground(new Color(70, 130, 180)); // Steel blue
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        inputPanel.add(studentIdLabel);
        inputPanel.add(studentIdField);
        inputPanel.add(searchButton);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10)); // Spacing
        topPanel.add(inputPanel);

        // Center panel for displaying the report
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportTextArea.setBackground(new Color(255, 250, 250)); // Light warm white
        reportTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Report"));

        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Set layout
        setLayout(new BorderLayout());
        add(mainPanel);

        // Add search action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText().trim();
                if (!studentId.isEmpty()) {
                    fetchStudentReport(studentId);
                } else {
                    JOptionPane.showMessageDialog(SearchReport.this, 
                        "Please enter a Student ID", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void fetchStudentReport(String studentId) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost/attenadnce_management", 
                "root", 
                ""
            );

            String query = "SELECT * FROM student_attn WHERE ID_Number = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, studentId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                StringBuilder report = new StringBuilder();
                report.append("Student Report\n");
                report.append("-------------------\n");
                report.append("Name: ").append(rs.getString("student_name")).append("\n");
                report.append("ID Number: ").append(rs.getString("ID_Number")).append("\n");
                report.append("Total Attendance: ").append(rs.getDouble("Total_attendance")).append("\n");
                report.append("Attendance Percentage: ").append(rs.getDouble("Percentage")).append("%\n\n");

                report.append("Detailed Attendance:\n");
                for (int i = 1; i <= 25; i++) {
                    String status = rs.getString("C" + i);
                    report.append("Class ").append(i).append(": ").append(status != null ? status : "N/A").append("\n");
                }

                reportTextArea.setText(report.toString());
            } else {
                reportTextArea.setText("No student found with ID: " + studentId);
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error fetching student report: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SearchReport dialog = new SearchReport(frame);
            dialog.setVisible(true);
        });
    }
}
