/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Admin;

import Login.landingPage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;



/**
 *
 * @author yeojvaldez
 */
public class adminMainHome extends javax.swing.JFrame {

    private int userID;
    
    public adminMainHome(String name, int userID) throws SQLException {
        initComponents();
        showHome();
        this.userID = userID;
        txtname.setText(name);
        setupNavigation();
        connectToDatabase();
    }
    
    Connection con; 
    PreparedStatement pst;
    ResultSet rs; 
    
        private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3307/beachResortManagement";
            String user = "root";  // MySQL username
            String password = "";   // MySQL password

            
            // Load MySQL JDBC driver (optional in newer versions of JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create the connection
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully!");
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
       
    }
    
    public void showHome() {
         ResortDashboard ad = new ResortDashboard();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(ad, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        ad.setVisible(true);   
    }
    
   private void addPanelHoverEffect(JPanel panel, JLabel label, JPanel[] allPanels, JLabel[] allLabels) {
        // Colors for hover and default state
        Color selectedBG = new Color(242,242,242); // Background color when hovered or clicked
        Color selectedFG = Color.BLACK;  // Text color when hovered or clicked
        Color defaultBG = new Color(27, 59, 95);  // Default background color for the panel
        Color defaultFG = Color.WHITE;  // Default text color for the label
        Color borderColor = new Color(39, 114, 160);  // Border color for hover effect

        // Default label border and padding setup
        Border defaultLabelBorder = BorderFactory.createEmptyBorder(8, 15, 8, 15);  // Padding
        Border selectedLabelBorder = BorderFactory.createMatteBorder(0, 10, 0, 0, borderColor); // Left inset border for hover effect

        // Set default properties for the label
        label.setOpaque(true);
        label.setBackground(defaultBG);
        label.setForeground(defaultFG);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Set the cursor to hand (pointer)
        label.setBorder(defaultLabelBorder);
        label.setFont(new Font("Tahoma", Font.BOLD, 13));  // Set default font to Tahoma Bold 13

        // Mouse listener to handle hover and click on the label
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Apply hover effect (background color change) when mouse enters the label
                resetOtherPanels(allPanels, allLabels); // Reset other panels and labels to default
                panel.setBackground(selectedBG);  // Change panel background to white
                label.setBackground(selectedBG);  // Change label background to white
                label.setForeground(selectedFG);  // Change label text color to black
                label.setBorder(selectedLabelBorder);  // Add left inset border on hover
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Apply the same effect when clicked on the label
                resetOtherPanels(allPanels, allLabels); // Reset other panels and labels to default
                panel.setBackground(selectedBG);  // Change panel background to white
                label.setBackground(selectedBG);  // Change label background to white
                label.setForeground(selectedFG);  // Change label text color to black
                label.setBorder(selectedLabelBorder);  // Add left inset border on click
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Optional action on click (e.g., navigating to another page)
                System.out.println(label.getText() + " clicked!");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Do not revert the background after mouse exit
                // This keeps the clicked label hover effect active
            }
        });
    }

    // Method to reset all panels and labels to their default color
    private void resetOtherPanels(JPanel[] allPanels, JLabel[] allLabels) {
        Color defaultBG = new Color(27, 59, 95);  // Default background color for the panel
        Color defaultFG = Color.WHITE;  // Default text color for the label
        Border defaultLabelBorder = BorderFactory.createEmptyBorder(8, 15, 8, 15);  // Padding

        // Loop through all panels and labels and reset their colors to default
        for (int i = 0; i < allPanels.length; i++) {
            allPanels[i].setBackground(defaultBG);
            allLabels[i].setBackground(defaultBG);
            allLabels[i].setForeground(defaultFG);
            allLabels[i].setBorder(defaultLabelBorder);
        }
    }







private void setupNavigation() {
    // Apply hover effect to each panel with its corresponding label
    JPanel[] panels = {dashboardPanel, manageBoatPanel, manageRoomPanel, manageUserPanel, reservationPanel, activityLogPanel};
    JLabel[] labels = {dashboardLabel, manageBoatLabel, manageRoomLabel, manageUserLabel, reservationLabel, activityLogLabel};

    addPanelHoverEffect(dashboardPanel, dashboardLabel, panels, labels);
    addPanelHoverEffect(manageBoatPanel, manageBoatLabel, panels, labels);
    addPanelHoverEffect(manageRoomPanel, manageRoomLabel, panels, labels);
    addPanelHoverEffect(manageUserPanel, manageUserLabel, panels, labels);
    addPanelHoverEffect(reservationPanel, reservationLabel, panels, labels);
    addPanelHoverEffect(activityLogPanel, activityLogLabel, panels, labels);
}


    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtname = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        dashboardPanel = new javax.swing.JPanel();
        dashboardLabel = new javax.swing.JLabel();
        manageRoomPanel = new javax.swing.JPanel();
        manageRoomLabel = new javax.swing.JLabel();
        reservationPanel = new javax.swing.JPanel();
        reservationLabel = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        manageBoatPanel = new javax.swing.JPanel();
        manageBoatLabel = new javax.swing.JLabel();
        manageUserPanel = new javax.swing.JPanel();
        manageUserLabel = new javax.swing.JLabel();
        activityLogPanel = new javax.swing.JPanel();
        activityLogLabel = new javax.swing.JLabel();
        pnlmain = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(39, 114, 160));
        jPanel2.setPreferredSize(new java.awt.Dimension(80, 80));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Logout");
        jPanel13.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel2.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, -1, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("WELCOME,");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 220, 50));

        txtname.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        txtname.setForeground(new java.awt.Color(255, 255, 255));
        txtname.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtname.setText("Default Name");
        jPanel2.add(txtname, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 220, 40));

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
        jLabel8.setText("Logout");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        panelRound2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        jPanel2.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 17, 90, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 1200, 70));

        jPanel3.setBackground(new java.awt.Color(27, 59, 95));
        jPanel3.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(170, 200));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardPanel.setBackground(new java.awt.Color(27, 59, 95));
        dashboardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        dashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
        dashboardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardLabel.setText("DASHBOARD");
        dashboardLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardLabelMouseClicked(evt);
            }
        });
        dashboardPanel.add(dashboardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 200, 50));

        jPanel3.add(dashboardPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 220, 50));

        manageRoomPanel.setBackground(new java.awt.Color(27, 59, 95));
        manageRoomPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        manageRoomLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        manageRoomLabel.setForeground(new java.awt.Color(255, 255, 255));
        manageRoomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        manageRoomLabel.setText("MANAGE ROOMS");
        manageRoomLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                manageRoomLabelMouseClicked(evt);
            }
        });
        manageRoomPanel.add(manageRoomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 200, 50));

        jPanel3.add(manageRoomPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 220, 50));

        reservationPanel.setBackground(new java.awt.Color(27, 59, 95));
        reservationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        reservationLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        reservationLabel.setForeground(new java.awt.Color(255, 255, 255));
        reservationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reservationLabel.setText("RESERVATION");
        reservationLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reservationLabelMouseClicked(evt);
            }
        });
        reservationPanel.add(reservationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 200, 50));

        jPanel3.add(reservationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 490, 220, 50));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("PAPAYA");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 220, 40));

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Beach Resort");
        jPanel3.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 220, 20));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal.png"))); // NOI18N
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 160, -1));

        manageBoatPanel.setBackground(new java.awt.Color(27, 59, 95));
        manageBoatPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        manageBoatLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        manageBoatLabel.setForeground(new java.awt.Color(255, 255, 255));
        manageBoatLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        manageBoatLabel.setText("MANAGE BOATS");
        manageBoatLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                manageBoatLabelMouseClicked(evt);
            }
        });
        manageBoatPanel.add(manageBoatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 200, 50));

        jPanel3.add(manageBoatPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 220, 50));

        manageUserPanel.setBackground(new java.awt.Color(27, 59, 95));
        manageUserPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        manageUserLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        manageUserLabel.setForeground(new java.awt.Color(255, 255, 255));
        manageUserLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        manageUserLabel.setText("MANAGE USERS");
        manageUserLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                manageUserLabelMouseClicked(evt);
            }
        });
        manageUserPanel.add(manageUserLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 200, 50));

        jPanel3.add(manageUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 220, 50));

        activityLogPanel.setBackground(new java.awt.Color(27, 59, 95));
        activityLogPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        activityLogLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        activityLogLabel.setForeground(new java.awt.Color(255, 255, 255));
        activityLogLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        activityLogLabel.setText("ACTIVITY LOG");
        activityLogLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                activityLogLabelMouseClicked(evt);
            }
        });
        activityLogPanel.add(activityLogLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 200, 50));

        jPanel3.add(activityLogPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 540, 220, 50));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 830));

        pnlmain.setBackground(new java.awt.Color(242, 242, 242));
        pnlmain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(pnlmain, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 70, 1200, 760));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void manageRoomLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_manageRoomLabelMouseClicked
     
        adminRoom room = new adminRoom(userID);
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(room, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        room.setVisible(true);   
    }//GEN-LAST:event_manageRoomLabelMouseClicked

    private void reservationLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reservationLabelMouseClicked
        adminReservation ar = new adminReservation();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(ar, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        ar.setVisible(true); 
    }//GEN-LAST:event_reservationLabelMouseClicked

    private void dashboardLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabelMouseClicked
    // Clear the panel
    
showHome();

    
   
    }//GEN-LAST:event_dashboardLabelMouseClicked

    private void manageBoatLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_manageBoatLabelMouseClicked
        adminBoat boat = new adminBoat(userID);
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(boat, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        boat.setVisible(true); 
    }//GEN-LAST:event_manageBoatLabelMouseClicked

    private void manageUserLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_manageUserLabelMouseClicked
         adminUsers user = new adminUsers(userID);
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(user, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        user.setVisible(true); 
        
    }//GEN-LAST:event_manageUserLabelMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        try {
            String logSql = "INSERT INTO activity_log (user_id, action_type, action_description) VALUES (?, ?, ?)";
            pst = con.prepareStatement(logSql);
            // Log the logout activity using the userID of the logged-in user
            pst.setInt(1, userID);  // Assuming userID is available after login
            pst.setString(2, "LOGOUT");
            pst.setString(3, "User logged out successfully");
            pst.executeUpdate();
            
            // Close the current window and open the landing page (logout action)
            this.dispose();
            new landingPage().setVisible(true);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    

    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked
        
    }//GEN-LAST:event_panelRound2MouseClicked

    private void activityLogLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_activityLogLabelMouseClicked
              adminActivityLog log = new adminActivityLog();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(log, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        log.setVisible(true); 
        
    }//GEN-LAST:event_activityLogLabelMouseClicked

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
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                String name = "";
                int userID = 2;
                new adminMainHome(name, userID).setVisible(true);
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(adminMainHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activityLogLabel;
    private javax.swing.JPanel activityLogPanel;
    private javax.swing.JLabel dashboardLabel;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel manageBoatLabel;
    private javax.swing.JPanel manageBoatPanel;
    private javax.swing.JLabel manageRoomLabel;
    private javax.swing.JPanel manageRoomPanel;
    private javax.swing.JLabel manageUserLabel;
    private javax.swing.JPanel manageUserPanel;
    private GUI.PanelRound panelRound2;
    private javax.swing.JPanel pnlmain;
    private javax.swing.JLabel reservationLabel;
    private javax.swing.JPanel reservationPanel;
    private javax.swing.JLabel txtname;
    // End of variables declaration//GEN-END:variables
}
