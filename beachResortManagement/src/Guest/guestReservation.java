/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Admin.*;
import Staff.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author yeojvaldez
 */
public class guestReservation extends javax.swing.JFrame {

    private int userId;
    
    public guestReservation(int userId) {
        this.userId = userId;
        initComponents();
        DatabaseConnection();
        showPendingRoomReservations();
        showCompleteRoomReservations();
    }
    
    Connection con; 
    PreparedStatement pst;
    ResultSet rs; 
    
    public final void DatabaseConnection() {
          String url = "jdbc:mysql://localhost:3306/beachResortManagement";
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
    

public final void showPendingRoomReservations() {
    pending.removeAll();
    pending.setLayout(new BoxLayout(pending, BoxLayout.Y_AXIS));
    pending.setBackground(new Color(240, 242, 245));
    
    

    
    // If this panel is contained in another container, you'll need to add the scrollPane instead
    // For example, if you previously added pendingReservationPanel directly to a parent container,
    // you should now add scrollPane instead
    
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT r.reservation_number, r.check_in_date, rr.room_reservation_id, r.check_out_date, rr.room_number, rm.room_image " +
                                                      "FROM reservation r " +
                                                      "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
                                                      "JOIN room rm ON rr.room_number = rm.room_number " +
                                                      "WHERE r.user_id = ? AND r.status = 'Pending'")) {
        
        pst.setInt(1, userId); // Use dynamic user ID
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            // Create a reservation card
            JPanel reservationCard = new JPanel(new BorderLayout());
            reservationCard.setBackground(Color.WHITE);
            reservationCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            reservationCard.setPreferredSize(new Dimension(900, 220));
            reservationCard.setMaximumSize(new Dimension(1000, 220));
            reservationCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Left panel (Image + Details)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
            leftPanel.setOpaque(false);

            // Image
            byte[] imgBytes = rs.getBytes("room_image");
            JLabel lblImage = new JLabel();
            lblImage.setPreferredSize(new Dimension(250, 170));
            lblImage.setHorizontalAlignment(JLabel.CENTER);
            lblImage.setVerticalAlignment(JLabel.CENTER);

            if (imgBytes != null) {
                ImageIcon imageIcon = new ImageIcon(imgBytes);
                Image img = imageIcon.getImage().getScaledInstance(250, 170, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } else {
                lblImage.setText("No Image");
                lblImage.setForeground(Color.GRAY);
                lblImage.setFont(new Font("Segoe UI", Font.BOLD, 16));
            }

            // Details Panel
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setOpaque(false);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

            JLabel lblReservationNumber = new JLabel(rs.getString("reservation_number"));
            lblReservationNumber.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblReservationNumber.setForeground(new Color(30, 30, 30));

            JLabel lblCheckIn = new JLabel("Check-in: " + rs.getDate("check_in_date"));
            lblCheckIn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblCheckIn.setForeground(new Color(100, 100, 100));

            JLabel lblCheckOut = new JLabel("Check-out: " + rs.getDate("check_out_date"));
            lblCheckOut.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblCheckOut.setForeground(new Color(100, 100, 100));

            JLabel lblRoomNumber = new JLabel("Room: " + rs.getString("room_number"));
            lblRoomNumber.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblRoomNumber.setForeground(new Color(100, 100, 100));

            detailsPanel.add(lblReservationNumber);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            detailsPanel.add(lblCheckIn);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblCheckOut);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblRoomNumber);

            leftPanel.add(lblImage);
            leftPanel.add(detailsPanel);

            // Right panel (Action panels)
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setOpaque(false);

            // View Panel
            JPanel viewPanel = createActionPanel("View", new Color(0, 123, 255));
            JLabel lblView = (JLabel) viewPanel.getComponent(0);
            int roomReservationId = rs.getInt("room_reservation_id");
            lblView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new ReservationViewDetails(roomReservationId);
                }
            });

            // Cancel Panel
            JPanel cancelPanel = createActionPanel("Cancel", new Color(220, 53, 69));
            JLabel lblCancel = (JLabel) cancelPanel.getComponent(0);
            lblCancel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        JOptionPane.showMessageDialog(null, "Canceling reservation: " + rs.getString("reservation_number"));
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(guestReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                }
            });

            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(viewPanel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            rightPanel.add(cancelPanel);
            rightPanel.add(Box.createVerticalGlue());

            reservationCard.add(leftPanel, BorderLayout.CENTER);
            reservationCard.add(rightPanel, BorderLayout.EAST);

            pending.add(reservationCard);
            pending.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        pending.revalidate();
        pending.repaint();
     
      
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}




// Helper method to create Action Panel
private JPanel createActionPanel(String text, Color backgroundColor) {
    JPanel panel = new JPanel();
    panel.setBackground(backgroundColor);
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    panel.setMaximumSize(new Dimension(150, 40));
    panel.setLayout(new GridBagLayout());

    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.BOLD, 15));
    label.setForeground(Color.WHITE);

    panel.add(label);

    return panel;
}


    
   public final void showCompleteRoomReservations() {
    // Clear the existing content
    completeReservationPanel.removeAll();
    completeReservationPanel.setLayout(new BoxLayout(completeReservationPanel, BoxLayout.Y_AXIS));
    completeReservationPanel.setBackground(new Color(240, 242, 245));

   

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT r.reservation_number, r.check_in_date, rr.room_reservation_id, " +
                                                    "r.check_out_date, rr.room_number, rm.room_image, rm.room_type " +
                                                    "FROM reservation r " +
                                                    "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
                                                    "JOIN room rm ON rr.room_number = rm.room_number " +
                                                    "WHERE r.user_id = ? AND r.status = 'Check-out'")) {

        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            // Create a reservation card
            JPanel reservationCard = new JPanel(new BorderLayout());
            reservationCard.setBackground(Color.WHITE);
            reservationCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            reservationCard.setPreferredSize(new Dimension(900, 220));
            reservationCard.setMaximumSize(new Dimension(1000, 220));
            reservationCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Left panel (Image + Details)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
            leftPanel.setOpaque(false);

            // Image
            byte[] imgBytes = rs.getBytes("room_image");
            JLabel lblImage = new JLabel();
            lblImage.setPreferredSize(new Dimension(250, 170));
            lblImage.setHorizontalAlignment(JLabel.CENTER);
            lblImage.setVerticalAlignment(JLabel.CENTER);

            if (imgBytes != null) {
                ImageIcon imageIcon = new ImageIcon(imgBytes);
                Image img = imageIcon.getImage().getScaledInstance(250, 170, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } else {
                lblImage.setText("No Image");
                lblImage.setForeground(Color.GRAY);
                lblImage.setFont(new Font("Segoe UI", Font.BOLD, 16));
            }

            // Details Panel
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setOpaque(false);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

            JLabel lblReservationNumber = new JLabel(rs.getString("reservation_number"));
            lblReservationNumber.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblReservationNumber.setForeground(new Color(30, 30, 30));

            JLabel lblRoomType = new JLabel("Room Type: " + rs.getString("room_type"));
            lblRoomType.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblRoomType.setForeground(new Color(100, 100, 100));

            JLabel lblCheckIn = new JLabel("Check-in: " + rs.getDate("check_in_date"));
            lblCheckIn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblCheckIn.setForeground(new Color(100, 100, 100));

            JLabel lblCheckOut = new JLabel("Check-out: " + rs.getDate("check_out_date"));
            lblCheckOut.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblCheckOut.setForeground(new Color(100, 100, 100));

            JLabel lblRoomNumber = new JLabel("Room: " + rs.getString("room_number"));
            lblRoomNumber.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblRoomNumber.setForeground(new Color(100, 100, 100));

            detailsPanel.add(lblReservationNumber);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            detailsPanel.add(lblRoomType);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblCheckIn);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblCheckOut);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblRoomNumber);

            leftPanel.add(lblImage);
            leftPanel.add(detailsPanel);

            // Right panel (Action panels)
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setOpaque(false);

            // View Panel
            JPanel viewPanel = createActionPanel("View Details", new Color(0, 123, 255));
            JLabel lblView = (JLabel) viewPanel.getComponent(0);
            int roomReservationId = rs.getInt("room_reservation_id");
            lblView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new ReservationViewDetails(roomReservationId);
                }
            });

            // Rate Panel
            JPanel ratePanel = createActionPanel("Rate Stay", new Color(40, 167, 69));
            JLabel lblRate = (JLabel) ratePanel.getComponent(0);
            lblRate.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        String roomNumber = rs.getString("room_number");
                        // Call your rating dialog here
                        showRatingDialog(roomReservationId, roomNumber);
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(guestReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                }
            });

            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(viewPanel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            rightPanel.add(ratePanel);
            rightPanel.add(Box.createVerticalGlue());

            reservationCard.add(leftPanel, BorderLayout.CENTER);
            reservationCard.add(rightPanel, BorderLayout.EAST);

            completeReservationPanel.add(reservationCard);
            completeReservationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        completeReservationPanel.revalidate();
        completeReservationPanel.repaint();
       

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading completed reservations: " + ex.getMessage(), 
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Helper method for rating dialog
private void showRatingDialog(int reservationId, String roomNumber) {
    JDialog ratingDialog = new JDialog();
    ratingDialog.setTitle("Rate Your Stay - Room " + roomNumber);
    ratingDialog.setSize(400, 300);
    ratingDialog.setLayout(new BorderLayout());
    ratingDialog.setLocationRelativeTo(null);
    ratingDialog.setModal(true);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("How was your stay?");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Star rating component
    JPanel starsPanel = new JPanel();
    starsPanel.setLayout(new BoxLayout(starsPanel, BoxLayout.X_AXIS));
    starsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Add your star rating implementation here
    // This could be a custom component or using a library like JRating

    // Comment area
    JTextArea commentArea = new JTextArea(5, 30);
    commentArea.setLineWrap(true);
    commentArea.setWrapStyleWord(true);
    commentArea.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    // Submit button
    JButton submitButton = new JButton("Submit Rating");
    submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    submitButton.addActionListener(e -> {
        // Save the rating to database
        saveRating(reservationId, roomNumber, 5, commentArea.getText()); // Replace 5 with actual rating
        ratingDialog.dispose();
    });

    mainPanel.add(titleLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(starsPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(new JScrollPane(commentArea));
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(submitButton);

    ratingDialog.add(mainPanel, BorderLayout.CENTER);
    ratingDialog.setVisible(true);
}

private void saveRating(int reservationId, String roomNumber, int rating, String comment) {
    // Implement your database save logic here
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement(
             "INSERT INTO ratings (reservation_id, room_number, rating, comment, created_at) " +
             "VALUES (?, ?, ?, ?, NOW())")) {
        
        pst.setInt(1, reservationId);
        pst.setString(2, roomNumber);
        pst.setInt(3, rating);
        pst.setString(4, comment);
        
        pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Thank you for your feedback!", "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving rating: " + ex.getMessage(), 
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        materialTabbed1 = new GUI.MaterialTabbed();
        pending = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        completeReservationPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(39, 114, 160), 7));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(27, 59, 95)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        materialTabbed1.setForeground(new java.awt.Color(0, 0, 0));
        materialTabbed1.setFont(new java.awt.Font("Helvetica Neue", 0, 15)); // NOI18N

        pending.setBackground(new java.awt.Color(255, 255, 255));
        pending.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pending.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 0, 30, 410));

        materialTabbed1.addTab("Pending", pending);

        completeReservationPanel.setBackground(new java.awt.Color(255, 255, 255));
        completeReservationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        materialTabbed1.addTab("History", completeReservationPanel);

        jPanel2.add(materialTabbed1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 680, 450));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 680, 460));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("My Reservation");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 680, 60));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("X");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 10, -1, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 740, 570));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1MouseClicked

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
            java.util.logging.Logger.getLogger(guestReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int userId = 1;
                new guestReservation(userId).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel completeReservationPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private GUI.MaterialTabbed materialTabbed1;
    private javax.swing.JPanel pending;
    // End of variables declaration//GEN-END:variables
}
