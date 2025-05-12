/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Staff;

import Login.landingPage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.sql.*;



/**
 *
 * @author yeojvaldez
 */
public class staffHome extends javax.swing.JFrame {

    private int userID;
    
    public staffHome(String name, int userID) {
        this.userID = userID;
        initComponents();
        setMainHome();
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
        java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (SQLException ex) {
        java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}
    
    public void setMainHome(){
        staffMainHome boat = new staffMainHome();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(boat, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        boat.setVisible(true); 
    }
    
 // Method to set up panel and label hover effect
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
    // Apply hover effect to each existing panel with its corresponding label
    JPanel[] panels = {dashboardPanel, listOfRoomPanel, listOfBoatPanel, pendingPanel, checkInOutPanel, reservationPanel};
        JLabel[] labels = {dashboardLabel, listOfRoomLabel, listOfBoatLabel, pendingLabel, checkInOutLabel, reservationLabel};

        // Apply hover effect to each panel with its corresponding label
        addPanelHoverEffect(dashboardPanel, dashboardLabel, panels, labels);
        addPanelHoverEffect(listOfRoomPanel, listOfRoomLabel, panels, labels);
        addPanelHoverEffect(listOfBoatPanel, listOfBoatLabel, panels, labels);
        addPanelHoverEffect(pendingPanel, pendingLabel, panels, labels);
        addPanelHoverEffect(checkInOutPanel, checkInOutLabel, panels, labels);
        addPanelHoverEffect(reservationPanel, reservationLabel, panels, labels);
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
        listOfRoomPanel = new javax.swing.JPanel();
        listOfRoomLabel = new javax.swing.JLabel();
        listOfBoatPanel = new javax.swing.JPanel();
        listOfBoatLabel = new javax.swing.JLabel();
        pendingPanel = new javax.swing.JPanel();
        pendingLabel = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        checkInOutPanel = new javax.swing.JPanel();
        checkInOutLabel = new javax.swing.JLabel();
        reservationPanel = new javax.swing.JPanel();
        reservationLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
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
        jPanel3.setPreferredSize(new java.awt.Dimension(170, 200));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardPanel.setBackground(new java.awt.Color(27, 59, 95));
        dashboardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardLabel.setBackground(new java.awt.Color(0, 0, 0));
        dashboardLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        dashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
        dashboardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardLabel.setText("DASHBOARD");
        dashboardLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardLabelMouseClicked(evt);
            }
        });
        dashboardPanel.add(dashboardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 50));

        jPanel3.add(dashboardPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 220, 50));

        listOfRoomPanel.setBackground(new java.awt.Color(27, 59, 95));
        listOfRoomPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listOfRoomLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        listOfRoomLabel.setForeground(new java.awt.Color(255, 255, 255));
        listOfRoomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listOfRoomLabel.setText("ROOMS");
        listOfRoomLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listOfRoomLabelMouseClicked(evt);
            }
        });
        listOfRoomPanel.add(listOfRoomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 50));

        jPanel3.add(listOfRoomPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 220, 50));

        listOfBoatPanel.setBackground(new java.awt.Color(27, 59, 95));
        listOfBoatPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listOfBoatLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        listOfBoatLabel.setForeground(new java.awt.Color(255, 255, 255));
        listOfBoatLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listOfBoatLabel.setText("BOATS");
        listOfBoatLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listOfBoatLabelMouseClicked(evt);
            }
        });
        listOfBoatPanel.add(listOfBoatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 50));

        jPanel3.add(listOfBoatPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 220, 50));

        pendingPanel.setBackground(new java.awt.Color(27, 59, 95));
        pendingPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pendingLabel.setBackground(new java.awt.Color(242, 242, 242));
        pendingLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        pendingLabel.setForeground(new java.awt.Color(255, 255, 255));
        pendingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pendingLabel.setText("PENDING");
        pendingLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pendingLabelMouseClicked(evt);
            }
        });
        pendingPanel.add(pendingLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 50));

        jPanel3.add(pendingPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 220, 50));

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

        checkInOutPanel.setBackground(new java.awt.Color(27, 59, 95));
        checkInOutPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkInOutLabel.setBackground(new java.awt.Color(204, 0, 153));
        checkInOutLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        checkInOutLabel.setForeground(new java.awt.Color(255, 255, 255));
        checkInOutLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        checkInOutLabel.setText("CHECK IN/OUT");
        checkInOutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkInOutLabelMouseClicked(evt);
            }
        });
        checkInOutPanel.add(checkInOutLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 50));

        jPanel3.add(checkInOutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 490, 220, 50));

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
        reservationPanel.add(reservationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 50));

        jPanel3.add(reservationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 540, 220, 50));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal.png"))); // NOI18N
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 160, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 830));

        pnlmain.setBackground(new java.awt.Color(242, 242, 242));
        pnlmain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(pnlmain, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 70, 1200, 760));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void listOfRoomLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listOfRoomLabelMouseClicked
        staffRooms room = new staffRooms();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(room, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        room.setVisible(true);   
    }//GEN-LAST:event_listOfRoomLabelMouseClicked

    private void listOfBoatLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listOfBoatLabelMouseClicked
        staffBoats boat = new staffBoats();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(boat, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        boat.setVisible(true); 
    }//GEN-LAST:event_listOfBoatLabelMouseClicked

    private void dashboardLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabelMouseClicked
    
        staffMainHome boat = new staffMainHome();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(boat, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        boat.setVisible(true); 
        
    }//GEN-LAST:event_dashboardLabelMouseClicked

    private void checkInOutLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkInOutLabelMouseClicked
       
        staffCheckin checkin = new staffCheckin(userID);
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(checkin, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        checkin.setVisible(true); 
        
    }//GEN-LAST:event_checkInOutLabelMouseClicked

    private void pendingLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pendingLabelMouseClicked
        staffReservation reservation = new staffReservation(userID);
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(reservation, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        reservation.setVisible(true); 
        
    }//GEN-LAST:event_pendingLabelMouseClicked

    private void reservationLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reservationLabelMouseClicked
        staffHistory history = new staffHistory();
        pnlmain.removeAll(); // Remove existing components
        pnlmain.setLayout(new BorderLayout()); // Set the layout
        pnlmain.add(history, BorderLayout.CENTER); // Add new component
        pnlmain.revalidate(); // Revalidate to reflect changes
        pnlmain.repaint(); // Repaint the panel to show updates
        history.setVisible(true); 
    }//GEN-LAST:event_reservationLabelMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
                                      
    // Assuming userID is available and assigned after login
    try {
        // Prepare the SQL query for logging the logout action
        String logSql = "INSERT INTO activity_log (user_id, action_type, action_description) VALUES (?, ?, ?)";
        
        // Log the logout activity using the userID of the logged-in user
        try (PreparedStatement pst = con.prepareStatement(logSql)) {
            pst.setInt(1, userID);  // Assuming userID is available after login
            pst.setString(2, "LOGOUT");
            pst.setString(3, "User logged out successfully");
            
            // Execute the update to log the action
            pst.executeUpdate();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, "Error logging logout activity", ex);
        }
        
        // Close the current window and open the landing page (logout action)
        this.dispose();
        new landingPage().setVisible(true);

    }catch (Exception ex) {
        // Handle any other unforeseen exceptions
        java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, "Unexpected error during logout", ex);
    }
        // Handle any SQL errors during the process
        



    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked

    }//GEN-LAST:event_panelRound2MouseClicked

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
            java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(staffHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            String name = "";
new staffHome(name, 1).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel checkInOutLabel;
    private javax.swing.JPanel checkInOutPanel;
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
    private javax.swing.JLabel listOfBoatLabel;
    private javax.swing.JPanel listOfBoatPanel;
    private javax.swing.JLabel listOfRoomLabel;
    private javax.swing.JPanel listOfRoomPanel;
    private GUI.PanelRound panelRound2;
    private javax.swing.JLabel pendingLabel;
    private javax.swing.JPanel pendingPanel;
    private javax.swing.JPanel pnlmain;
    private javax.swing.JLabel reservationLabel;
    private javax.swing.JPanel reservationPanel;
    private javax.swing.JLabel txtname;
    // End of variables declaration//GEN-END:variables
}
