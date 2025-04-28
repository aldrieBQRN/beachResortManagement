package Guest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserDetailsFetcher extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean editMode = false;
    private int userId;

    public UserDetailsFetcher(int userId) {
        this.userId = userId;
        setTitle("User Profile");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main panel with shadow effect
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("User Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(new Color(108, 117, 125));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);

        // Profile content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        contentPanel.setMaximumSize(new Dimension(500, 400));

        // Initialize fields
        nameField = createStyledTextField();
        phoneField = createStyledTextField();
        emailField = createStyledTextField();
        roleComboBox = new JComboBox<>(new String[]{"Guest", "Staff", "Admin"});
        styleComboBox(roleComboBox);

        // Add form fields
        contentPanel.add(createFormField("Full Name:", nameField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createFormField("Phone:", phoneField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createFormField("Email:", emailField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createFormField("Role:", roleComboBox));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        editButton = createActionButton("Edit", new Color(52, 152, 219));
        editButton.addActionListener(e -> toggleEditMode(true));
        buttonPanel.add(editButton);

        saveButton = createActionButton("Save", new Color(46, 204, 113));
        saveButton.addActionListener(e -> saveUserDetails());
        saveButton.setVisible(false);
        buttonPanel.add(saveButton);

        cancelButton = createActionButton("Cancel", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> toggleEditMode(false));
        cancelButton.setVisible(false);
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Load user data
        loadUserDetails();
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldLabel.setForeground(new Color(108, 117, 125));
        panel.add(fieldLabel, BorderLayout.NORTH);
        
        field.setEnabled(false);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboBox.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        comboBox.setBackground(Color.WHITE);
        comboBox.setEnabled(false);
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void toggleEditMode(boolean enable) {
        editMode = enable;
        nameField.setEnabled(enable);
        phoneField.setEnabled(enable);
        emailField.setEnabled(enable);
        roleComboBox.setEnabled(enable);
        
        editButton.setVisible(!enable);
        saveButton.setVisible(enable);
        cancelButton.setVisible(enable);
    }

    private void loadUserDetails() {
        String url = "jdbc:mysql://localhost:3306/beachResortManagement";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT full_name, phone, email, role FROM user_details WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nameField.setText(rs.getString("full_name"));
                        phoneField.setText(rs.getString("phone"));
                        emailField.setText(rs.getString("email"));
                        roleComboBox.setSelectedItem(rs.getString("role"));
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveUserDetails() {
        String url = "jdbc:mysql://localhost:3306/beachResortManagement";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE user_details SET full_name = ?, phone = ?, email = ?, role = ? WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nameField.getText());
                stmt.setString(2, phoneField.getText());
                stmt.setString(3, emailField.getText());
                stmt.setString(4, (String) roleComboBox.getSelectedItem());
                stmt.setInt(5, userId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully");
                    toggleEditMode(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new UserDetailsFetcher(1).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}