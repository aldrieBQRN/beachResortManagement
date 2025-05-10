package Guest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserDetailsFetcher extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPanel updatePanel; // Panel that will act as a button
    private JLabel updateLabel; // Label inside the panel

    private int userId;

    public UserDetailsFetcher(int userId) {
        this.userId = userId;
        setTitle("User Profile");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("User Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(39, 114, 160));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        nameField = createTextField("Full Name:", formPanel);
        phoneField = createTextField("Phone:", formPanel);
        emailField = createTextField("Email:", formPanel);
        passwordField = createPasswordField("Password:", formPanel);

        // Create update panel that acts as a button
        updatePanel = new JPanel();
        updatePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        updatePanel.setBackground(new Color(46, 204, 113));
        updatePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        updatePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updatePanel.setOpaque(true);

        updateLabel = new JLabel("Update Profile");
        updateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateLabel.setForeground(Color.WHITE);
        updatePanel.add(updateLabel);

        // Add mouse listener to make the panel act like a button
        updatePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(UserDetailsFetcher.this, 
                    "Are you sure you want to update your profile?", 
                    "Confirm Update", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    updatePanel.setBackground(new Color(39, 114, 160));
                    updatePanel.setEnabled(false);
                    updateUserDetails();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                updatePanel.setBackground(new Color(39, 174, 96));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updatePanel.setBackground(new Color(46, 204, 113));
            }
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(updatePanel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadUserDetails();
    }

    private JTextField createTextField(String label, JPanel parent) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(new Color(245, 245, 245));
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setForeground(new Color(80, 80, 80));

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        fieldPanel.add(jLabel, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        parent.add(fieldPanel);
        parent.add(Box.createVerticalStrut(5));

        return field;
    }

    private JPasswordField createPasswordField(String label, JPanel parent) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(new Color(245, 245, 245));
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setForeground(new Color(80, 80, 80));

        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        fieldPanel.add(jLabel, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        parent.add(fieldPanel);
        parent.add(Box.createVerticalStrut(5));

        return field;
    }

    private void loadUserDetails() {
        String url = "jdbc:mysql://localhost:3307/beachResortManagement";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT full_name, phone, email, password FROM user_details WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nameField.setText(rs.getString("full_name"));
                        phoneField.setText(rs.getString("phone"));
                        emailField.setText(rs.getString("email"));
                        passwordField.setText(rs.getString("password"));
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

    private void updateUserDetails() {
        String url = "jdbc:mysql://localhost:3307/beachResortManagement";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE user_details SET full_name = ?, phone = ?, email = ?, password = ? WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nameField.getText());
                stmt.setString(2, phoneField.getText());
                stmt.setString(3, emailField.getText());
                stmt.setString(4, new String(passwordField.getPassword()));
                stmt.setInt(5, userId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            updatePanel.setBackground(new Color(46, 204, 113));
            // Note: There's no setEnabled for JPanel, so we need to handle this differently if needed
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