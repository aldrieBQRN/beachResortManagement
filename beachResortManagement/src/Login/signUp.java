/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author yeojvaldez
 */
public class signUp extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public signUp() {
        initComponents();
        DatabaseConnection();
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
    
   private void insertToDatabase() {
    // Get the form data
    String firstName = textField3.getText().trim();
    String lastName = textField1.getText().trim();
    String phone = textField4.getText().trim();
    String email = textField2.getText().trim();
    String password1 = new String(passwordField1.getPassword());
    String password2 = new String(passwordField1.getPassword());
    
    // Validate all required fields
    if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || 
        email.isEmpty() || password1.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required!", 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Validate password match
    if (!password1.equals(password2)) {
        JOptionPane.showMessageDialog(this, "Passwords do not match!", 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Validate password strength
    if (password1.length() < 8) {
        JOptionPane.showMessageDialog(this, "Password must be at least 8 characters!", 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Validate email format
    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        JOptionPane.showMessageDialog(this, "Invalid email format!", 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Validate phone format (basic check)
    if (!phone.matches("^\\d{10,15}$")) {
        JOptionPane.showMessageDialog(this, "Phone must be 10-15 digits!", 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

       
    // Create full name
    String fullName = firstName + " " + lastName;
    
    PreparedStatement pst = null;
    try {
        // Check if email already exists
        String checkQuery = "SELECT email FROM user_details WHERE email = ?";
        pst = con.prepareStatement(checkQuery);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            JOptionPane.showMessageDialog(this, "Email already registered!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Insert new user
        String insertQuery = "INSERT INTO user_details (full_name, phone, email, password, role) " +
                           "VALUES (?, ?, ?, ?, 'Guest')"; // Default role as Guest
        pst = con.prepareStatement(insertQuery);
        pst.setString(1, fullName);
        pst.setString(2, phone);
        pst.setString(3, email);
        pst.setString(4, password1);
        
        int rowsAffected = pst.executeUpdate();
        
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Account created successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(signUp.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        try {
            if (pst != null) pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(signUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

   

private void clearFields() {
    textField3.setText("");
    textField1.setText("");
    textField4.setText("");
    textField2.setText("");
    passwordField1.setText("");
    passwordField3.setText("");
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnllogin = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textField3 = new textfield.TextField();
        textField1 = new textfield.TextField();
        textField4 = new textfield.TextField();
        textField2 = new textfield.TextField();
        passwordField1 = new textfield.PasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        passwordField3 = new textfield.PasswordField();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnllogin.setBackground(new java.awt.Color(255, 255, 255));
        pnllogin.setForeground(new java.awt.Color(51, 51, 51));
        pnllogin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal.png"))); // NOI18N
        jLabel15.setText("x");
        pnllogin.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 510, 130));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Create your Account");
        pnllogin.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 510, -1));

        textField3.setBackground(new java.awt.Color(255, 255, 255));
        textField3.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField3.setLabelText("First Name");
        pnllogin.add(textField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 160, 45));

        textField1.setBackground(new java.awt.Color(255, 255, 255));
        textField1.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField1.setLabelText("Last Name");
        textField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textField1ActionPerformed(evt);
            }
        });
        pnllogin.add(textField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 230, 160, 45));

        textField4.setBackground(new java.awt.Color(255, 255, 255));
        textField4.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField4.setLabelText("Phone");
        pnllogin.add(textField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 290, 330, 45));

        textField2.setBackground(new java.awt.Color(255, 255, 255));
        textField2.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField2.setLabelText("Email");
        pnllogin.add(textField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 350, 330, 45));

        passwordField1.setBackground(new java.awt.Color(255, 255, 255));
        passwordField1.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        passwordField1.setLabelText("Password");
        passwordField1.setShowAndHide(true);
        passwordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordField1ActionPerformed(evt);
            }
        });
        pnllogin.add(passwordField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 410, 330, 45));

        jLabel5.setFont(new java.awt.Font("Arial Unicode MS", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Have already an account?");
        pnllogin.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 600, 160, -1));

        jLabel7.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Sign In");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        pnllogin.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 600, 50, 20));

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

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Submit");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        panelRound2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        pnllogin.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 540, 90, 35));

        passwordField3.setBackground(new java.awt.Color(255, 255, 255));
        passwordField3.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        passwordField3.setLabelText("Confirm Password");
        passwordField3.setShowAndHide(true);
        passwordField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordField3ActionPerformed(evt);
            }
        });
        pnllogin.add(passwordField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 470, 330, 45));

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

        pnllogin.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 590, 330, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pnllogin, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pnllogin, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void passwordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordField1ActionPerformed

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        this.dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
 insertToDatabase();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked
      
    }//GEN-LAST:event_panelRound2MouseClicked

    private void textField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textField1ActionPerformed

    private void passwordField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordField3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new signUp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private GUI.PanelRound panelRound2;
    private textfield.PasswordField passwordField1;
    private textfield.PasswordField passwordField3;
    private javax.swing.JPanel pnllogin;
    private textfield.TextField textField1;
    private textfield.TextField textField2;
    private textfield.TextField textField3;
    private textfield.TextField textField4;
    // End of variables declaration//GEN-END:variables
}
