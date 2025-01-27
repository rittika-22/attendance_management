package form;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AttendanceShee extends JFrame {
    private Connection con;
    private AttendanceTableModel model;
    private JTable table;

    public AttendanceShee() {
        try {
            connect();
            initComponents();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Attendance Sheet");
        getContentPane().setBackground(new Color(224, 247, 250));

        model = new AttendanceTableModel(con);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(27).setPreferredWidth(100);
        columnModel.getColumn(28).setPreferredWidth(100);
        
        for (int i = 2; i <= 26; i++) {
            columnModel.getColumn(i).setPreferredWidth(50);
            columnModel.getColumn(i).setCellRenderer(new AttendanceCellRenderer());
        }

        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshButton = createStyledButton("Refresh Data");
        refreshButton.addActionListener(e -> model.refreshData());

        JButton saveButton = createStyledButton("Save Changes");
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Changes saved to the database."));
        
        JButton addRowButton = createStyledButton("Add Row");
        addRowButton.addActionListener(e -> {
            model.addNewRow();
            JOptionPane.showMessageDialog(null, "New row added.");
        });
         
        JButton backButton = createStyledButton("Back to Dashboard");
        backButton.addActionListener(e -> {
        dispose(); // Close the AttendanceShee frame
        new Dashboard1().setVisible(true); // Open the Dashboard1 frame
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(addRowButton);
        buttonPanel.add(backButton);

        getContentPane().setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setSize(1200, 800); // Set the desired size of the frame
        //setResizable(false); // Optional: Make the window non-resizable
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(25, 118, 210), 2));

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

    public static void main(String[]args){
        SwingUtilities.invokeLater(() -> new AttendanceShee().setVisible(true));
    }

    static class AttendanceTableModel extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;
        private Connection con;

        public AttendanceTableModel(Connection connection) {
            this.con = connection;
            columnNames = initializeColumnNames();
            fetchDataFromDatabase();
        }

        private String[] initializeColumnNames() {
            String[] names = new String[29];
            names[0] = "Student Name";
            names[1] = "ID Number";
            for (int i = 2; i <= 26; i++) {
                names[i] = "C" + (i - 1);
            }
            names[27] = "Total Attendance";
            names[28] = "Attendance %";
            return names;
        }
        
        public void addNewRow() {
            Object[][] newData = new Object[data.length + 1][columnNames.length];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
            fireTableDataChanged();
        }
        
        // Fetch data from the database
        private void fetchDataFromDatabase() {
            try {
                String query = "SELECT * FROM student_attn"; // Use the correct table name
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(query);

                rs.last();
                int rowCount = rs.getRow(); // Get the number of rows
                rs.beforeFirst();

                data = new Object[rowCount][29]; // Initialize the data array
                int rowIndex = 0;

                while (rs.next()) {
                    data[rowIndex][0] = rs.getString("student_name");
                    data[rowIndex][1] = rs.getString("ID_Number");

                    for (int col = 2; col <= 26; col++) {
                        data[rowIndex][col] = rs.getString("C" + (col - 1));
                    }

                    data[rowIndex][27] = rs.getDouble("Total_attendance");
                    data[rowIndex][28] = String.format("%.2f%%", rs.getDouble("Percentage"));
                    rowIndex++;
                }

                fireTableDataChanged(); // Notify the table of data changes
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public int getRowCount() {
            return data == null ? 0 : data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex >= 2 && columnIndex <= 26; // Allow editing only attendance columns
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex >= 2 && columnIndex <= 26) {
                String value = aValue.toString().trim();
                if (value.equalsIgnoreCase("P") || value.equalsIgnoreCase("A") || value.equalsIgnoreCase("L")) {
                    data[rowIndex][columnIndex] = value;
                    updateDatabase(rowIndex, columnIndex, value); // Update the database
                    updateAttendanceCount(rowIndex);
                }
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        // Update the database when a cell is edited
        private void updateDatabase(int rowIndex, int columnIndex, String value) {
            try {
                String studentId = data[rowIndex][1].toString();
                String columnName = "C" + (columnIndex - 1);

                String query = "UPDATE student_attn SET " + columnName + " = ? WHERE ID_Number = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, value);
                pst.setString(2, studentId);
                pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Update total attendance and percentage
        private void updateAttendanceCount(int rowIndex) {
            double totalAttendance = 0;
            int totalClasses = 25;

            for (int col = 2; col <= 26; col++) {
                String status = (String) data[rowIndex][col];
                if ("P".equalsIgnoreCase(status)) {
                    totalAttendance += 1;
                } else if ("L".equalsIgnoreCase(status)) {
                    totalAttendance += 0.5;
                }
            }

            data[rowIndex][27] = totalAttendance;
            double percentage = (totalAttendance / totalClasses) * 100;
            data[rowIndex][28] = String.format("%.2f%%", percentage);

            try {
                String studentId = data[rowIndex][1].toString();
                String query = "UPDATE student_attn SET Total_attendance = ?,Percentage = ? WHERE ID_Number = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setDouble(1, totalAttendance);
                pst.setDouble(2, percentage);
                pst.setString(3, studentId);
                pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating totals in database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            fireTableCellUpdated(rowIndex, 27);
            fireTableCellUpdated(rowIndex, 28);
        }

        // Refresh data from the database
        public void refreshData() {
            fetchDataFromDatabase();
        }
    }

  static class AttendanceCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null) {
            String attendance = value.toString();
            switch (attendance) {
                case "P":
                    cell.setBackground(new Color(144, 238, 144)); // Light green for Present
                    break;
                case "A":
                    cell.setBackground(new Color(255, 99, 71)); // Light red for Absent
                    break;
                case "L":
                    cell.setBackground(new Color(255, 223, 0)); // Yellow for Late
                    break;
                default:
                    cell.setBackground(Color.WHITE); // Default white background
            }
        } else {
            cell.setBackground(Color.WHITE);
        }

        return cell;
    }
}

}
