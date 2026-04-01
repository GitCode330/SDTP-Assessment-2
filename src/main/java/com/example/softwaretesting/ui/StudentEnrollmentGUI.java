package com.example.softwaretesting.ui;

import com.example.softwaretesting.model.Enrollment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentEnrollmentGUI extends JFrame {

    private final ApiClient apiClient;

    // UI Components
    private JTextField studentIdField;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel studentNameLabel;

    public StudentEnrollmentGUI() {
        this.apiClient = new ApiClient();
        initializeUI();
        checkBackendConnection();
    }

    private void initializeUI() {
        setTitle("Student Enrollment Viewer - MAL2021");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Panel - Input section
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Center Panel - Results table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Bottom Panel - Status bar
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Search Student Enrollments"));

        // Student ID label and field
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));

        studentIdField = new JTextField(10);
        studentIdField.setFont(new Font("Arial", Font.PLAIN, 12));

        // Search button
        searchButton = new JButton("Search Enrollments");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(new Color(59, 89, 182));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchEnrollments());

        // Student name display
        studentNameLabel = new JLabel("Student: ");
        studentNameLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        studentNameLabel.setForeground(new Color(100, 100, 100));

        // Add Enter key listener
        studentIdField.addActionListener(e -> searchEnrollments());

        panel.add(idLabel);
        panel.add(studentIdField);
        panel.add(searchButton);
        panel.add(studentNameLabel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Enrollment Results"));

        // Create table columns
        String[] columns = {"Course ID", "Course Name", "Course Code", "Student ID", "Student Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Set column widths
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Ready. Enter a Student ID and click Search.");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(80, 80, 80));
        panel.add(statusLabel);
        return panel;
    }

    private void checkBackendConnection() {
        if (!apiClient.isBackendRunning()) {
            statusLabel.setText("WARNING: Backend server not running at http://localhost:8080");
            statusLabel.setForeground(Color.RED);
            searchButton.setEnabled(false);

            JOptionPane.showMessageDialog(this,
                    "Cannot connect to backend server!\n\n" +
                            "Please ensure your Spring Boot application is running at:\n" +
                            "http://localhost:8080\n\n" +
                            "The Swing app will not work without the backend.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            statusLabel.setText("Connected to backend. Ready to search.");
            statusLabel.setForeground(new Color(0, 150, 0));
            searchButton.setEnabled(true);
        }
    }

    private void searchEnrollments() {
        String idText = studentIdField.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a Student ID.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long studentId = Long.parseLong(idText);
            searchButton.setEnabled(false);
            statusLabel.setText("Searching for student ID: " + studentId + "...");

            // Run in background thread to avoid freezing UI
            SwingWorker<List<Enrollment>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<Enrollment> doInBackground() {
                    return apiClient.getStudentEnrollments(studentId);
                }

                @Override
                protected void done() {
                    try {
                        List<Enrollment> enrollments = get();
                        displayResults(enrollments, studentId);
                    } catch (Exception e) {
                        statusLabel.setText("Error: " + e.getMessage());
                        JOptionPane.showMessageDialog(StudentEnrollmentGUI.this,
                                "Error fetching data: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        searchButton.setEnabled(true);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric Student ID.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            studentIdField.setText("");
            studentIdField.requestFocus();
        }
    }

    private void displayResults(List<Enrollment> enrollments, Long studentId) {
        // Clear existing data
        tableModel.setRowCount(0);

        if (enrollments.isEmpty()) {
            studentNameLabel.setText("Student: ID " + studentId);
            statusLabel.setText("No enrollments found for Student ID: " + studentId);

            // Create a more helpful message based on student ID
            String specificMessage = "";
            if (studentId == 4) {
                specificMessage = "\n\nNote: Student ID 4 (David Wilson) exists but has no enrollments.\n" +
                        "Try Student IDs 1, 2, or 3 to see enrollments.";
            } else if (studentId > 4) {
                specificMessage = "\n\nNote: Student ID " + studentId + " does not exist in the system.\n" +
                        "Valid student IDs are 1, 2, 3, and 4.";
            } else if (studentId >= 1 && studentId <= 3) {
                specificMessage = "\n\nNote: This student should have enrollments. There might be a data issue.";
            }

            JOptionPane.showMessageDialog(this,
                    "No enrollments found for Student ID: " + studentId + specificMessage,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Get student name from first enrollment
            String studentName = enrollments.get(0).getStudentName();
            studentNameLabel.setText("Student: " + studentName + " (ID: " + studentId + ")");

            // Add each enrollment to table
            for (Enrollment enrollment : enrollments) {
                Object[] row = {
                        enrollment.getCourseId(),
                        enrollment.getCourseName(),
                        enrollment.getCourseCode(),
                        enrollment.getStudentId(),
                        enrollment.getStudentName()
                };
                tableModel.addRow(row);
            }

            statusLabel.setText(String.format("Found %d enrollment(s) for Student ID: %d",
                    enrollments.size(), studentId));
        }
    }

    public static void main(String[] args) {
        // Run Swing UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new StudentEnrollmentGUI().setVisible(true);
        });
    }
}