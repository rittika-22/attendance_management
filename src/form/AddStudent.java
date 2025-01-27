package form;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddStudent extends JFrame {
    private JTextField nameField, idField;
    private JButton addButton;
    private Connection con;

    public AddStudent() {
        setTitle("Add Student");
        setSize(400, 300); // Slightly larger for better spacing
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout()); // Improved alignment
        getContentPane().setBackground(new Color(224, 247, 250)); // Light pastel blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing

        // Labels with custom font
        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(30, 30, 30)); // Darker text for contrast

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setForeground(new Color(30, 30, 30));

        // Text Fields with Rounded Borders
        nameField = createRoundedTextField();
        idField = createRoundedTextField();

        // Button with gradient-like effect
        addButton = new JButton("Add Student");
        styleButton(addButton);

        // Add Components to Frame
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        add(nameLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        add(idLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(idField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addButton, gbc);

        // Center the JFrame
        setLocationRelativeTo(null);

        // Connect to database
        connectToDatabase();

        // Add student to database
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudentToDatabase();
            }
        });

        setVisible(true);
    }

    private JTextField createRoundedTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(new LineBorder(new Color(100, 100, 100), 1, true)); // Rounded border
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        return field;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243)); // Soft blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(25, 118, 210), 2, true));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 136, 229)); // Slightly darker blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243));
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

    private void addStudentToDatabase() {
        try {
           String query = "INSERT INTO student_attn (student_name, ID_Number, total_attendance, Percentage) VALUES (?, ?, ?, ?)";
           PreparedStatement pst = con.prepareStatement(query);
           pst.setString(1, nameField.getText());
           pst.setString(2, idField.getText());
           pst.setInt(3, 0); // Set total_attendance to 0
           pst.setDouble(4, 0.0); // Set Percentage to 0.0%


            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student Added Successfully!");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Adding Student: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new AddStudent();
    }
}
