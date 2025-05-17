/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Login;

import Admin.adminMainHome;
import Guest.guestHome;
import Staff.staffHome;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author yeojvaldez
 */
public class Home extends javax.swing.JInternalFrame {

    /**
     * Creates new form Home
     */
    public Home() {
        removeBackground();
        initComponents();
        DatabaseConnection();
        
        
    }
    
    public final void removeBackground(){
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI UI = (BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null); 
    }
         
  
    
    java.sql.Connection con; 
    PreparedStatement pst;
    ResultSet rs; 
    
    public final void DatabaseConnection() {
          String url = "jdbc:mysql://localhost:3307/beachResortManagement";
        String user = "root"; // MySQL username
        String password = ""; // MySQL password
        
        // Establishing the connection
        try {
            // Load MySQL JDBC driver (optional in newer versions of JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create the connection
            con = DriverManager.getConnection(url, user, password);
            
            System.out.println("Connected to the database successfully!");

            // Perform database operations here...

      
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
    
    private void setFrameDarkness(float darkness) {
    // darkness should be between 0 (black) and 1 (original brightness)
    if (getContentPane() instanceof JPanel) {
        JPanel panel = (JPanel) getContentPane();
        Color original = panel.getBackground();
        
        // Calculate darker color
        int r = (int)(original.getRed() * darkness);
        int g = (int)(original.getGreen() * darkness);
        int b = (int)(original.getBlue() * darkness);
        
        panel.setBackground(new Color(r, g, b));
        panel.repaint();
    }
}
    
   public void showSignUp() {
    signUp su = new signUp();  // Create a new instance of the signUp JPanel

    // Add the signUp JPanel to pnllogin using CardLayout
    pnllogin.add(su, "SignUp");  // 'SignUp' is the identifier for the CardLayout
    
    // Switch to the SignUp card
    java.awt.CardLayout layout = (java.awt.CardLayout) pnllogin.getLayout();
    layout.show(pnllogin, "SignUp");
}

   private void loginUser() {
    // Get input values
    String email = txtemail.getText().trim();
    char[] passwordChars = txtpass.getPassword();
    String password = new String(passwordChars).trim();
    
    // Clear password from memory as soon as possible
    Arrays.fill(passwordChars, '\0');

    // Validate input fields
    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Email and password are required!", 
            "Login Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // First, get the stored hash and user details
        String query = "SELECT user_id, full_name, role, password FROM user_details WHERE email = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, email);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String userName = rs.getString("full_name");
                    String role = rs.getString("role");
                    int userID = rs.getInt("user_id");
                    
                    // Verify the password against the stored hash
                    if (BCrypt.checkpw(password, storedHash)) {
                        String logSql = "INSERT INTO activity_log (user_id, action_type, action_description) VALUES (?, ?, ?)";
                        try (PreparedStatement logPst = con.prepareStatement(logSql)) {
                            logPst.setInt(1, userID);
                            logPst.setString(2, "LOGIN");
                            logPst.setString(3, "User logged in successfully");
                            logPst.executeUpdate();
                        } catch (SQLException e) {
                            Logger.getLogger(landingPage.class.getName()).log(Level.WARNING, "Login activity not logged", e);
                        }

                        // Login successful
                        JOptionPane.showMessageDialog(this, 
                            "Login Successful", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Open appropriate home page
                        openHomePage(role, userName, userID);
                        this.dispose();
                    } else {
                        // Incorrect password
                        JOptionPane.showMessageDialog(this, 
                            "Invalid email or password!", 
                            "Login Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Email not found
                    JOptionPane.showMessageDialog(this, 
                        "Invalid email or password!", 
                        "Login Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Database error: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(landingPage.class.getName()).log(Level.SEVERE, null, ex);
    }
}

private void openHomePage(String role, String userName, int userID) throws SQLException {
    switch (role) {
        case "Admin":
            new adminMainHome(userName, userID).setVisible(true);
            break;
        case "Staff":
            new staffHome(userName, userID).setVisible(true);
            break;
        case "Guest":
            new guestHome(userID).setVisible(true);
            break;
        default:
            JOptionPane.showMessageDialog(this, 
                "Unknown role: " + role, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        pnlgraphics = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        pnllogin = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        txtemail = new textfield.TextField();
        txtpass = new textfield.PasswordField();
        jPanel1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlgraphics.setBackground(new java.awt.Color(255, 255, 255));
        pnlgraphics.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/background copy.png"))); // NOI18N
        jLabel7.setText("jLabel7");
        pnlgraphics.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(-60, 30, -1, -1));

        jPanel2.add(pnlgraphics, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 0, 860, 730));

        pnllogin.setBackground(new java.awt.Color(255, 255, 255));
        pnllogin.setForeground(new java.awt.Color(51, 51, 51));
        pnllogin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal.png"))); // NOI18N
        pnllogin.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 510, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Papaya Beach Resort");
        jLabel6.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 5, 0, new java.awt.Color(27, 59, 95)));
        pnllogin.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, 390, 60));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Login your Account");
        pnllogin.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, 390, -1));

        jLabel14.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 102));
        jLabel14.setText("Forgot password?");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });
        pnllogin.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(102, 480, 140, -1));

        jLabel13.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 153, 255));
        jLabel13.setText("Register");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        pnllogin.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 581, 60, -1));

        panelRound2.setBackground(new java.awt.Color(0, 153, 255));
        panelRound2.setRoundBottomLeft(20);
        panelRound2.setRoundBottomRight(20);
        panelRound2.setRoundTopLeft(20);
        panelRound2.setRoundTopRight(20);
        panelRound2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRound2MouseClicked(evt);
            }
        });
        panelRound2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Login");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        panelRound2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        pnllogin.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 500, 90, 35));

        txtemail.setBackground(new java.awt.Color(255, 255, 255));
        txtemail.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtemail.setLabelText("Email");
        txtemail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtemailKeyReleased(evt);
            }
        });
        pnllogin.add(txtemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 370, 330, 45));

        txtpass.setBackground(new java.awt.Color(255, 255, 255));
        txtpass.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtpass.setLabelText("Password");
        txtpass.setShowAndHide(true);
        txtpass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpassActionPerformed(evt);
            }
        });
        txtpass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtpassKeyReleased(evt);
            }
        });
        pnllogin.add(txtpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 430, 330, 45));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        pnllogin.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 560, 330, 10));

        jLabel16.setFont(new java.awt.Font("Arial Unicode MS", 0, 13)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Don't have an account?");
        pnllogin.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 580, 140, -1));

        jPanel2.add(pnllogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, 730));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 730));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        String email = txtemail.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if email exists in the database
        PreparedStatement checkStmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT email FROM user_details WHERE email = ?";
            checkStmt = con.prepareStatement(query);
            checkStmt.setString(1, email);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Email exists - open password reset form
                ForgotPasswordForm passwordForm = new ForgotPasswordForm(email);
                passwordForm.setVisible(true);
                passwordForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                passwordForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        DatabaseConnection(); // Refresh or reconnect if needed
                    }
                });
            } else {
                // Email doesn't exist
                JOptionPane.showMessageDialog(this,
                    "Email not found.",
                    "Error", JOptionPane.WARNING_MESSAGE);

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        signUp signUpFrame = new signUp();
        signUpFrame.setVisible(true);  // Show the signUp window

        // Assuming addBoatWindow is already initialized elsewhere in your code
        // Add a window listener to the signUpFrame to detect when it's closed
        signUpFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // When the signUp window is closed, call DatabaseConnection
                // It's important to ensure the database connection is established here
                DatabaseConnection();
            }
        });
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked

        loginUser();

    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked

    }//GEN-LAST:event_panelRound2MouseClicked

    private void txtemailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtemailKeyReleased

    }//GEN-LAST:event_txtemailKeyReleased

    private void txtpassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpassActionPerformed

    private void txtpassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpassKeyReleased

    }//GEN-LAST:event_txtpassKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private GUI.PanelRound panelRound2;
    private javax.swing.JPanel pnlgraphics;
    private javax.swing.JPanel pnllogin;
    private textfield.TextField txtemail;
    private textfield.PasswordField txtpass;
    // End of variables declaration//GEN-END:variables
}
