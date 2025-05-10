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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import static java.awt.SystemColor.scrollbar;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicScrollBarUI;
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

public final void showPendingRoomReservations() {
    // First, clear the original panel
    pending.removeAll();
    pending.setLayout(new BorderLayout());
    pending.setBackground(new Color(240, 242, 245));
    
    // Create a container panel for the scroll pane
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
    containerPanel.setBackground(new Color(240, 242, 245));
    
    // Create scroll pane with custom scrollbar
    JScrollPane scrollPane = new JScrollPane(containerPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    // Create modern-looking scrollbar
    scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT r.reservation_number, r.check_in_date, rr.room_reservation_id, r.check_out_date, rr.room_number, rm.room_image " +
                                                      "FROM reservation r " +
                                                      "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
                                                      "JOIN room rm ON rr.room_number = rm.room_number " +
                                                      "WHERE r.user_id = ? AND r.status = 'Pending'")) {
        
        pst.setInt(1, userId); // Use dynamic user ID
        ResultSet rs = pst.executeQuery();

        boolean hasReservations = false;

        while (rs.next()) {
            hasReservations = true;
            // Create a reservation card
            JPanel reservationCard = new JPanel(new BorderLayout());
            reservationCard.setBackground(Color.WHITE);
            reservationCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            reservationCard.setPreferredSize(new Dimension(640, 220));
            reservationCard.setMaximumSize(new Dimension(680, 220));
            reservationCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Left panel (Image + Details)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
            leftPanel.setOpaque(false);

            // Image
            byte[] imgBytes = rs.getBytes("room_image");
            JLabel lblImage = new JLabel();
            lblImage.setPreferredSize(new Dimension(200, 170));
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
            rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Add some padding

            // View Panel
            JPanel viewPanel = createActionPanel("View", new Color(0, 123, 255));
            viewPanel.setPreferredSize(new Dimension(120, 40));
            viewPanel.setMaximumSize(new Dimension(120, 40));
            viewPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            JLabel lblView = (JLabel) viewPanel.getComponent(0);
            int roomReservationId = rs.getInt("room_reservation_id");
            final String resNumber = rs.getString("reservation_number"); // Capture reservation number
            
            lblView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new ReservationViewDetails(roomReservationId);
                }
            });

            // Cancel Panel
            JPanel cancelPanel = createActionPanel("Cancel", new Color(220, 53, 69));
            cancelPanel.setPreferredSize(new Dimension(120, 40));
            cancelPanel.setMaximumSize(new Dimension(120, 40));
            cancelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            JLabel lblCancel = (JLabel) cancelPanel.getComponent(0);
            lblCancel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                         cancelReservation(roomReservationId);
                    } catch (Exception ex) {
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

            containerPanel.add(reservationCard);
            containerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        if (!hasReservations) {
            JPanel noReservationPanel = new JPanel(new GridBagLayout());
            noReservationPanel.setBackground(new Color(240, 242, 245));
            JLabel noReservationLabel = new JLabel("No pending reservations found");
            noReservationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noReservationLabel.setForeground(new Color(100, 100, 100));
            noReservationPanel.add(noReservationLabel);
            containerPanel.add(noReservationPanel);
        }

        // Add the scroll pane to the pending panel
        pending.add(scrollPane, BorderLayout.CENTER);
        
        // Update UI
        containerPanel.revalidate();
        containerPanel.repaint();
        pending.revalidate();
        pending.repaint();
      
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading pending reservations: " + ex.getMessage(), 
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
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
    // First, clear the original panel
    completeReservationPanel.removeAll();
    completeReservationPanel.setLayout(new BorderLayout());
    completeReservationPanel.setBackground(new Color(240, 242, 245));
    
    // Create a container panel for the scroll pane
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
    containerPanel.setBackground(new Color(240, 242, 245));

    // Create scroll pane with custom scrollbar
    JScrollPane scrollPane = new JScrollPane(containerPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    // Create modern-looking scrollbar
    scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT r.reservation_number, r.check_in_date, rr.room_reservation_id, " +
                                                    "r.check_out_date, rr.room_number, rm.room_image, rm.room_type " +
                                                    "FROM reservation r " +
                                                    "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
                                                    "JOIN room rm ON rr.room_number = rm.room_number " +
                                                    "WHERE r.user_id = ? AND r.status != 'Pending'")) {

        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        boolean hasReservations = false;

        while (rs.next()) {
            hasReservations = true;
            // Create a reservation card
            JPanel reservationCard = new JPanel(new BorderLayout());
            reservationCard.setBackground(Color.WHITE);
            reservationCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            reservationCard.setPreferredSize(new Dimension(640, 220));
            reservationCard.setMaximumSize(new Dimension(680, 220));
            reservationCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Left panel (Image + Details)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
            leftPanel.setOpaque(false);

            // Image
            byte[] imgBytes = rs.getBytes("room_image");
            JLabel lblImage = new JLabel();
            lblImage.setPreferredSize(new Dimension(200, 170));
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
            rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Add some padding

            // View Panel
            JPanel viewPanel = createActionPanel("View Details", new Color(0, 123, 255));
            viewPanel.setPreferredSize(new Dimension(120, 40));
            viewPanel.setMaximumSize(new Dimension(120, 40));
            viewPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            JLabel lblView = (JLabel) viewPanel.getComponent(0);
            int roomReservationId = rs.getInt("room_reservation_id");
            lblView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new ReservationViewDetails(roomReservationId);
                }
            });

            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(viewPanel);
            rightPanel.add(Box.createVerticalGlue());

            reservationCard.add(leftPanel, BorderLayout.CENTER);
            reservationCard.add(rightPanel, BorderLayout.EAST);

            containerPanel.add(reservationCard);
            containerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        if (!hasReservations) {
            JPanel noReservationPanel = new JPanel(new GridBagLayout());
            noReservationPanel.setBackground(new Color(240, 242, 245));
            JLabel noReservationLabel = new JLabel("No completed reservations found");
            noReservationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noReservationLabel.setForeground(new Color(100, 100, 100));
            noReservationPanel.add(noReservationLabel);
            containerPanel.add(noReservationPanel);
        }

        // Add the scroll pane to the completed reservations panel
        completeReservationPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Update UI
        containerPanel.revalidate();
        containerPanel.repaint();
        completeReservationPanel.revalidate();
        completeReservationPanel.repaint();

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading completed reservations: " + ex.getMessage(), 
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Custom Modern ScrollBar UI class
class ModernScrollBarUI extends BasicScrollBarUI {
    private final int THUMB_SIZE = 8;

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(new Color(240, 240, 240));
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Modern thumb color
        Color thumbColor = scrollbar.getValueIsAdjusting() ? new Color(130, 130, 130) : new Color(180, 180, 180);
        g2.setColor(thumbColor);

        // Create rounded thumb
        int width = thumbBounds.width;
        int height = thumbBounds.height;
        
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            width = THUMB_SIZE;
            thumbBounds.x = thumbBounds.x + (thumbBounds.width - width) / 2;
        } else {
            height = THUMB_SIZE;
            thumbBounds.y = thumbBounds.y + (thumbBounds.height - height) / 2;
        }
        
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, width, height, 5, 5);
        g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }
}

private void cancelReservation(int roomReservationId) {
    int confirm = JOptionPane.showConfirmDialog(null, 
        "Are you sure you want to cancel this reservation?", 
        "Cancel Reservation", 
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/beachResortManagement", "root", "");
             PreparedStatement pst = con.prepareStatement(
                 "UPDATE reservation r " +
                 "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
                 "SET r.status = 'Cancelled' " +
                 "WHERE rr.room_reservation_id = ?")) {

            pst.setInt(1, roomReservationId);
            int affected = pst.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Reservation canceled successfully.");
                showPendingRoomReservations(); // Refresh the panel
            } else {
                JOptionPane.showMessageDialog(null, "Failed to cancel reservation.", 
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        materialTabbed1.addTab("Pending", pending);

        completeReservationPanel.setBackground(new java.awt.Color(255, 255, 255));
        completeReservationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        materialTabbed1.addTab("Reservation", completeReservationPanel);

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
    private GUI.MaterialTabbed materialTabbed1;
    private javax.swing.JPanel pending;
    // End of variables declaration//GEN-END:variables
}
