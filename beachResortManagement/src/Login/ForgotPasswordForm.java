package Login;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class ForgotPasswordForm extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String email;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Color primaryColor = new Color(70, 130, 180); // Steel blue
    private Color accentColor = new Color(0, 102, 204);   // Darker blue for buttons

    // Constructor accepting email
    public ForgotPasswordForm(String email) {
        this.email = email;
        setTitle("Password Recovery");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set custom icon (optional)
        try {
            // Replace with your app icon path if available
            // setIconImage(new ImageIcon("path/to/icon.png").getImage());
        } catch (Exception e) {
            // Fallback if icon can't be loaded
        }

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Account: " + email);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Card Layout for steps
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Status panel at bottom
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        statusLabel = new JLabel("Step 1 of 2: Verify your identity");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        progressBar.setStringPainted(true);
        progressBar.setForeground(primaryColor);
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);

        // First Panel: Enter Recovery Code
        JPanel panel1 = createRecoveryCodePanel();
        
        // Second Panel: Enter New Password
        JPanel panel2 = createNewPasswordPanel();

        // Add panels to card layout
        cardPanel.add(panel1, "CodePanel");
        cardPanel.add(panel2, "PasswordPanel");

        // Set initial card
        cardLayout.show(cardPanel, "CodePanel");

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // You could add a confirmation dialog here if needed
                dispose();
            }
        });
    }
    
    private JPanel createRecoveryCodePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel infoLabel = new JLabel("<html>A recovery code has been sent to your email address. Please check your inbox and enter the code below.</html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel codeLabel = new JLabel("Recovery Code:");
        codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField codeField = new JTextField();
        codeField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        codeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        codeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        stylizeTextField(codeField);
        
        JButton resendButton = new JButton("Resend Code");
        resendButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resendButton.setFont(new Font("Arial", Font.PLAIN, 12));
        stylizeButton(resendButton, false);
        resendButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "A new code has been sent to your email.", "Code Sent", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JButton cancelButton = new JButton("Cancel");
        stylizeButton(cancelButton, false);
        cancelButton.addActionListener(e -> dispose());
        
        JButton submitCodeButton = new JButton("Next");
        stylizeButton(submitCodeButton, true);
        submitCodeButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                showError("Please enter the recovery code.");
            } else if (code.length() < 6) {
                showError("The recovery code should be at least 6 characters.");
            } else {
                // Here you could validate the code against what was sent
                // For now, we'll just proceed to the next step
                cardLayout.show(cardPanel, "PasswordPanel");
                progressBar.setValue(100);
                statusLabel.setText("Step 2 of 2: Create a new password");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitCodeButton);
        
        // Add components with spacing
        panel.add(infoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(codeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(codeField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(resendButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createNewPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel infoLabel = new JLabel("<html>Create a strong password that you don't use for other websites.</html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel passwordLabel = new JLabel("New Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        stylizeTextField(newPasswordField);
        
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        stylizeTextField(confirmPasswordField);
        
        JPanel strengthPanel = new JPanel(new BorderLayout(5, 0));
        strengthPanel.setBackground(Color.WHITE);
        strengthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel strengthLabel = new JLabel("Password Strength:");
        
        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(true);
        strengthBar.setString("Too weak");
        strengthBar.setForeground(Color.RED);
        
        strengthPanel.add(strengthLabel, BorderLayout.WEST);
        strengthPanel.add(strengthBar, BorderLayout.CENTER);
        
        // Password strength calculator
        newPasswordField.addCaretListener(e -> {
            char[] password = newPasswordField.getPassword();
            int strength = calculatePasswordStrength(new String(password));
            strengthBar.setValue(strength);
            
            if (strength < 30) {
                strengthBar.setString("Too weak");
                strengthBar.setForeground(Color.RED);
            } else if (strength < 60) {
                strengthBar.setString("Moderate");
                strengthBar.setForeground(Color.ORANGE);
            } else if (strength < 80) {
                strengthBar.setString("Strong");
                strengthBar.setForeground(Color.GREEN);
            } else {
                strengthBar.setString("Very Strong");
                strengthBar.setForeground(new Color(0, 128, 0)); // Dark Green
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JButton backButton = new JButton("Back");
        stylizeButton(backButton, false);
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "CodePanel");
            progressBar.setValue(50);
            statusLabel.setText("Step 1 of 2: Verify your identity");
        });
        
        JButton savePasswordButton = new JButton("Save");
        stylizeButton(savePasswordButton, true);
        savePasswordButton.addActionListener(e -> {
            char[] password = newPasswordField.getPassword();
            char[] confirmPassword = confirmPasswordField.getPassword();
            
            if (password.length == 0) {
                showError("Please enter a new password.");
                return;
            }
            
            if (!new String(password).equals(new String(confirmPassword))) {
                showError("Passwords do not match.");
                return;
            }
            
            if (calculatePasswordStrength(new String(password)) < 30) {
                int option = JOptionPane.showConfirmDialog(
                    this,
                    "Your password is weak. Are you sure you want to use it?",
                    "Weak Password",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Show updating status
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            savePasswordButton.setEnabled(false);
            backButton.setEnabled(false);
            statusLabel.setText("Updating password...");
            
            // Use SwingWorker to run DB operation in background
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return updatePasswordInDatabase(new String(password));
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(
                                ForgotPasswordForm.this,
                                "Password has been changed successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            dispose(); // Close the form
                        } else {
                            showError("Failed to update password. Please try again.");
                            savePasswordButton.setEnabled(true);
                            backButton.setEnabled(true);
                            statusLabel.setText("Step 2 of 2: Create a new password");
                        }
                    } catch (Exception ex) {
                        showError("An error occurred: " + ex.getMessage());
                        savePasswordButton.setEnabled(true);
                        backButton.setEnabled(true);
                    }
                    setCursor(Cursor.getDefaultCursor());
                }
            }.execute();
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(savePasswordButton);
        
        // Add components with spacing
        panel.add(infoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(passwordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(newPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(confirmLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(confirmPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(strengthPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private void stylizeTextField(JTextField field) {
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 7, 5, 7)
        ));
    }
    
    private void stylizeButton(JButton button, boolean isPrimary) {
        if (isPrimary) {
            button.setBackground(accentColor);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 14));
        } else {
            button.setBackground(new Color(240, 240, 240));
            button.setForeground(Color.DARK_GRAY);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
        }
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 35));
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private int calculatePasswordStrength(String password) {
        int score = 0;
        
        if (password.length() > 8) {
            score += 20;
        } else if (password.length() > 5) {
            score += 10;
        }
        
        // Check for digits
        if (password.matches(".*\\d.*")) {
            score += 20;
        }
        
        // Check for lowercase letters
        if (password.matches(".*[a-z].*")) {
            score += 20;
        }
        
        // Check for uppercase letters
        if (password.matches(".*[A-Z].*")) {
            score += 20;
        }
        
        // Check for special characters
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            score += 20;
        }
        
        return Math.min(score, 100);
    }

    
    private boolean updatePasswordInDatabase(String newPassword) {
        String dbURL = "jdbc:mysql://localhost:3307/beachResortManagement";
        String dbUser = "root";
        String dbPass = "";

        // Hash the new password before storing
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        
        String sql = "UPDATE user_details SET password = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword); // Store hashed password
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Test the form
    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ForgotPasswordForm("user@example.com").setVisible(true);
        });
    }
}