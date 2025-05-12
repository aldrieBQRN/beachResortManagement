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
import net.miginfocom.swing.MigLayout;

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
            reservationCard.setPreferredSize(new Dimension(840, 220));
            reservationCard.setMaximumSize(new Dimension(840, 220));
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
    // Clear and setup main panel
    completeReservationPanel.removeAll();
    completeReservationPanel.setLayout(new BorderLayout());
    completeReservationPanel.setBackground(new Color(240, 242, 245));
    
    // Create container panel with padding
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
    containerPanel.setBackground(new Color(240, 242, 245));
    
    // Add more horizontal padding to account for scrollbar
    containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

    // Configure scroll pane with improved settings
    JScrollPane scrollPane = new JScrollPane(containerPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setBackground(new Color(240, 242, 245));
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    // Set a better scroll bar UI
    scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    
    // Ensure some padding between scrollbar and content
    scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement( "SELECT r.reservation_number, r.check_in_date, rr.room_reservation_id, " +
              "r.check_out_date, rr.room_number, rm.room_image, rm.room_type, " +
              "rat.rating_value, rat.comments " +
              "FROM reservation r " +
              "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
              "JOIN room rm ON rr.room_number = rm.room_number " +
              "LEFT JOIN reservation_ratings rat ON rr.room_reservation_id = rat.room_reservation_id " +
              "WHERE r.user_id = ? AND r.status = 'Check-out' " +
              "ORDER BY r.reservation_number DESC")) {

        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        boolean hasReservations = false;

        while (rs.next()) {
            hasReservations = true;
            final int roomReservationId = rs.getInt("room_reservation_id");
            
            // Create a more flexible card with proper constraints
            JPanel reservationCard = createReservationCard(rs, roomReservationId);
            
            // Make card maintain width when container is resized but add some margin
            reservationCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add card to container with spacing
            containerPanel.add(reservationCard);
            containerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        if (!hasReservations) {
            JPanel noReservationPanel = new JPanel(new GridBagLayout());
            noReservationPanel.setBackground(new Color(240, 242, 245));
            JLabel noReservationLabel = new JLabel("No completed reservations found");
            noReservationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noReservationLabel.setForeground(new Color(120, 120, 120));
            noReservationPanel.add(noReservationLabel);
            containerPanel.add(noReservationPanel);
        }

        completeReservationPanel.add(scrollPane, BorderLayout.CENTER);
        
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

// New helper method to create a more flexible reservation card
private JPanel createReservationCard(ResultSet rs, int roomReservationId) throws SQLException {
    // Main card using MigLayout for better component placement
    JPanel card = new JPanel();
    // Change layout constraint to center the actions panel vertically
    card.setLayout(new MigLayout("fillx, insets 15", "[200:200:200][grow][130:130:130]", "[]"));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
    
    // Set preferred width but allow height to be flexible - reduced width to avoid scrollbar overlap
    card.setMaximumSize(new Dimension(820, 240));
    card.setPreferredSize(new Dimension(820, 240));
    card.setMinimumSize(new Dimension(740, 200));
    
    // IMAGE PANEL - with constrained size
    JPanel imagePanel = createImagePanel(rs);
    
    // DETAILS PANEL - with flexible width
    JPanel detailsPanel = createDetailsPanel(rs);
    
    // ACTIONS PANEL - with fixed width for buttons
    JPanel actionsPanel = createActionsPanel(rs, roomReservationId);
    
    // Add components to card with MigLayout constraints
    // Use "cell" constraint to ensure actions panel is centered vertically
    card.add(imagePanel, "w 200!, h 190!, spany");
    card.add(detailsPanel, "grow, push");
    // Change to "center" alignment to center vertically
    card.add(actionsPanel, "w 130!, center, growy");
    
    return card;
}

private JPanel createImagePanel(ResultSet rs) throws SQLException {
    JPanel imagePanel = new JPanel(new BorderLayout());
    imagePanel.setBackground(new Color(245, 245, 245));
    
    byte[] imgBytes = rs.getBytes("room_image");
    JLabel lblImage = new JLabel();
    lblImage.setHorizontalAlignment(JLabel.CENTER);
    lblImage.setVerticalAlignment(JLabel.CENTER);

    if (imgBytes != null) {
        ImageIcon imageIcon = new ImageIcon(imgBytes);
        Image img = imageIcon.getImage().getScaledInstance(190, 170, Image.SCALE_SMOOTH);
        lblImage.setIcon(new ImageIcon(img));
    } else {
        lblImage.setText("No Image Available");
        lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblImage.setForeground(Color.GRAY);
    }
    imagePanel.add(lblImage, BorderLayout.CENTER);
    
    return imagePanel;
}

private JPanel createDetailsPanel(ResultSet rs) throws SQLException {
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new MigLayout("fillx, insets 10 15 10 0", "[grow]", "[]5[]5[]5[]10[]"));
    detailsPanel.setOpaque(false);

    // Reservation info
    JLabel lblReservationNumber = new JLabel("Reservation #" + rs.getString("reservation_number"));
    lblReservationNumber.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
    lblReservationNumber.setForeground(new Color(40, 40, 40));

    // Room details with flexible width
    JLabel lblRoomType = createDetailLabel("Room Type: " + rs.getString("room_type"));
    JLabel lblCheckIn = createDetailLabel("Check-in: " + formatDate(rs.getDate("check_in_date")));
    JLabel lblCheckOut = createDetailLabel("Check-out: " + formatDate(rs.getDate("check_out_date")));
    JLabel lblRoomNumber = createDetailLabel("Room: " + rs.getString("room_number"));

    // Add components with MigLayout constraints
    detailsPanel.add(lblReservationNumber, "wrap, growx");
    detailsPanel.add(lblRoomType, "wrap, growx");
    detailsPanel.add(lblCheckIn, "wrap, growx");
    detailsPanel.add(lblCheckOut, "wrap, growx");
    detailsPanel.add(lblRoomNumber, "wrap, growx");

    // Rating panel
    JPanel ratingPanel = createRatingPanel(rs);
    detailsPanel.add(ratingPanel, "growx");

    return detailsPanel;
}

private JPanel createActionsPanel(ResultSet rs, int roomReservationId) throws SQLException {
    // Use vertical layout with proper vertical alignment
    JPanel actionsPanel = new JPanel();
    // Change layout to use BoxLayout for better vertical centering
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
    actionsPanel.setOpaque(false);
    
    // Add vertical glue at top for centering
    actionsPanel.add(Box.createVerticalGlue());
    
    // VIEW DETAILS BUTTON - Custom panel with label for better customization
    JPanel viewDetailsPanel = new JPanel(new BorderLayout());
    viewDetailsPanel.setPreferredSize(new Dimension(120, 40));
    viewDetailsPanel.setMaximumSize(new Dimension(120, 40));
    viewDetailsPanel.setBackground(new Color(0, 123, 255));
    viewDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 100, 200)),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)
    ));
    viewDetailsPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    viewDetailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
    
    JLabel viewDetailsLabel = new JLabel("View Details");
    viewDetailsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    viewDetailsLabel.setForeground(Color.WHITE);
    viewDetailsLabel.setHorizontalAlignment(JLabel.CENTER);
    
    viewDetailsPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            viewDetailsPanel.setBackground(new Color(0, 100, 220));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            viewDetailsPanel.setBackground(new Color(0, 123, 255));
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            new ReservationViewDetails(roomReservationId);
        }
    });
    
    viewDetailsPanel.add(viewDetailsLabel, BorderLayout.CENTER);
    
    // Add view details button to panel with center alignment
    actionsPanel.add(viewDetailsPanel);
    
    // Add spacing between buttons
    actionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    
    // Check if rating exists and add Rate Now button if needed
    int rating = rs.getInt("rating_value");
    if (rs.wasNull()) {
        // Rate Now button - Custom panel with label for better customization
        JPanel rateNowPanel = new JPanel(new BorderLayout());
        rateNowPanel.setPreferredSize(new Dimension(120, 40));
        rateNowPanel.setMaximumSize(new Dimension(120, 40));
        rateNowPanel.setBackground(new Color(40, 167, 69));
        rateNowPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 150, 60)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        rateNowPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rateNowPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
        
        JLabel rateNowLabel = new JLabel("Rate Now");
        rateNowLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rateNowLabel.setForeground(Color.WHITE);
        rateNowLabel.setHorizontalAlignment(JLabel.CENTER);
        
        rateNowPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                rateNowPanel.setBackground(new Color(30, 150, 60));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                rateNowPanel.setBackground(new Color(40, 167, 69));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                showRatingDialog(roomReservationId);
            }
        });
        
        rateNowPanel.add(rateNowLabel, BorderLayout.CENTER);
        
        actionsPanel.add(rateNowPanel);
    }
    
    // Add vertical glue at bottom for centering
    actionsPanel.add(Box.createVerticalGlue());
    
    return actionsPanel;
}

// Helper method for consistent detail labels
private JLabel createDetailLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    label.setForeground(new Color(80, 80, 80));
    return label;
}

// Helper method for date formatting
private String formatDate(Date date) {
    if (date == null) return "N/A";
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
    return sdf.format(date);
}

// Helper method for rating panel
private JPanel createRatingPanel(ResultSet rs) throws SQLException {
    JPanel ratingPanel = new JPanel();
    ratingPanel.setLayout(new MigLayout("fillx, insets 5 0 0 0", "[grow]", "[]"));
    ratingPanel.setOpaque(false);
    
    int rating = rs.getInt("rating_value");
    if (!rs.wasNull()) {
        // Existing rating display with flexible layout
        JPanel ratingContainer = new JPanel();
        ratingContainer.setLayout(new MigLayout("insets 0", "[][]", ""));
        ratingContainer.setOpaque(false);
        
        JLabel lblRatingText = new JLabel("Your Rating: ");
        lblRatingText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRatingText.setForeground(new Color(100, 100, 100));
        ratingContainer.add(lblRatingText);
        
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsPanel.setOpaque(false);
        
        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel(i <= rating ? "★" : "☆");
            star.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            star.setForeground(i <= rating ? new Color(255, 193, 7) : Color.LIGHT_GRAY);
            starsPanel.add(star);
        }
        
        ratingContainer.add(starsPanel);
        ratingPanel.add(ratingContainer, "wrap");
        
        String feedback = rs.getString("comments");
        if (feedback != null && !feedback.isEmpty()) {
            JLabel lblFeedback = new JLabel("\"" + feedback + "\"");
            lblFeedback.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblFeedback.setForeground(new Color(120, 120, 120));
            ratingPanel.add(lblFeedback, "wrap");
        }
    }
    
    return ratingPanel;
}

private boolean hasExistingRating(int roomReservationId) {
    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3307/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement(
             "SELECT rating_id FROM reservation_ratings WHERE room_reservation_id = ?")) {

        pst.setInt(1, roomReservationId);
        ResultSet rs = pst.executeQuery();
        return rs.next();

    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}

private void showRatingDialog(int roomReservationId) {
    if (hasExistingRating(roomReservationId)) {
        JOptionPane.showMessageDialog(null, "You've already rated this reservation.", 
                                    "Rating Exists", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JDialog ratingDialog = new JDialog();
    ratingDialog.setTitle("Rate Your Stay");
    ratingDialog.setSize(400, 300);
    ratingDialog.setLayout(new BorderLayout());
    ratingDialog.setLocationRelativeTo(null);
    ratingDialog.setModal(true);
    ratingDialog.getContentPane().setBackground(Color.WHITE);
    
    // Main content panel using MigLayout for better control
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new MigLayout("fillx, insets 20", "[grow]", "[]20[]15[]5[]20[]"));
    contentPanel.setBackground(Color.WHITE);
    
    // Title
    JLabel titleLabel = new JLabel("How was your stay?");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    
    // Star rating panel
    JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
    starsPanel.setOpaque(false);
    
    JLabel[] stars = new JLabel[5];
    final int[] selectedRating = {0};
    
    for (int i = 0; i < 5; i++) {
        stars[i] = new JLabel("☆");
        stars[i].setFont(new Font("Segoe UI", Font.PLAIN, 30));
        stars[i].setForeground(Color.LIGHT_GRAY);
        final int index = i;
        
        stars[i].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRating[0] = index + 1;
                for (int j = 0; j < 5; j++) {
                    stars[j].setText(j <= index ? "★" : "☆");
                    stars[j].setForeground(j <= index ? new Color(255, 193, 7) : Color.LIGHT_GRAY);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                for (int j = 0; j <= index; j++) {
                    stars[j].setForeground(new Color(255, 193, 7));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                for (int j = 0; j < 5; j++) {
                    stars[j].setForeground(j < selectedRating[0] ? new Color(255, 193, 7) : Color.LIGHT_GRAY);
                }
            }
        });
        
        starsPanel.add(stars[i]);
    }
    
    // Feedback text area
    JLabel feedbackLabel = new JLabel("Share your experience (optional):");
    feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
    JTextArea feedbackArea = new JTextArea();
    feedbackArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    feedbackArea.setLineWrap(true);
    feedbackArea.setWrapStyleWord(true);
    feedbackArea.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
    
    JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
    feedbackScroll.setBorder(BorderFactory.createEmptyBorder());
    feedbackScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    buttonPanel.setOpaque(false);
    
    // Cancel button as panel
    JPanel cancelPanel = new JPanel(new BorderLayout());
    cancelPanel.setBackground(new Color(220, 220, 220));
    cancelPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    cancelPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    JLabel cancelLabel = new JLabel("Cancel");
    cancelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    cancelLabel.setHorizontalAlignment(JLabel.CENTER);
    cancelPanel.add(cancelLabel, BorderLayout.CENTER);
    
    cancelPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            cancelPanel.setBackground(new Color(200, 200, 200));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            cancelPanel.setBackground(new Color(220, 220, 220));
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            ratingDialog.dispose();
        }
    });
    
    // Submit button as panel
    JPanel submitPanel = new JPanel(new BorderLayout());
    submitPanel.setBackground(new Color(0, 123, 255));
    submitPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    submitPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    JLabel submitLabel = new JLabel("Submit Rating");
    submitLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    submitLabel.setForeground(Color.WHITE);
    submitLabel.setHorizontalAlignment(JLabel.CENTER);
    submitPanel.add(submitLabel, BorderLayout.CENTER);
    
    submitPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            submitPanel.setBackground(new Color(0, 100, 220));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            submitPanel.setBackground(new Color(0, 123, 255));
        }
        
         @Override
    public void mouseClicked(MouseEvent e) {
        if (selectedRating[0] == 0) {
            JOptionPane.showMessageDialog(ratingDialog, "Please select a rating", 
                                        "Rating Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String feedback = feedbackArea.getText().trim();
        saveRating(roomReservationId, selectedRating[0], feedback);
        ratingDialog.dispose();
        showCompleteRoomReservations(); // Refresh the view
    }
});

    
    buttonPanel.add(cancelPanel);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(submitPanel);
    
    // Add components to content panel with MigLayout constraints
    contentPanel.add(titleLabel, "wrap, align center");
    contentPanel.add(starsPanel, "wrap, align center");
    contentPanel.add(feedbackLabel, "wrap");
    contentPanel.add(feedbackScroll, "grow, h 80!, wrap");
    contentPanel.add(buttonPanel, "align center");
    
    ratingDialog.add(contentPanel, BorderLayout.CENTER);
    ratingDialog.setVisible(true);
}

private void saveRating(int roomReservationId, int rating, String feedback) {
     try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3307/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement(
             "INSERT INTO reservation_ratings (user_id, room_reservation_id, rating_value, comments) " +
             "VALUES (?, ?, ?, ?)");
         PreparedStatement logPst = con.prepareStatement(
             "INSERT INTO activity_log (user_id, action_type, action_description) VALUES (?, ?, ?)")) {

        // Insert rating
        pst.setInt(1, userId);
        pst.setInt(2, roomReservationId);
        pst.setInt(3, rating);
        pst.setString(4, feedback.isEmpty() ? null : feedback);

        int affected = pst.executeUpdate();

        if (affected > 0) {
            logPst.setInt(1, userId);
            logPst.setString(2, "SUBMIT_RATING");
            logPst.setString(3, "Submitted a rating for reservation ID: " + roomReservationId);
            logPst.executeUpdate();
            // Create custom success message dialog with styling
            JDialog successDialog = new JDialog();
            successDialog.setUndecorated(true); // Remove window decorations for cleaner look
            successDialog.setSize(350, 180);
            successDialog.setLocationRelativeTo(null);
            successDialog.setModal(true);
            
            // Main panel with border
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createLineBorder(new Color(40, 167, 69), 2));
            
            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(40, 167, 69));
            headerPanel.setPreferredSize(new Dimension(350, 40));
            
            JLabel headerLabel = new JLabel("Rating Submitted");
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            headerPanel.add(headerLabel, BorderLayout.CENTER);
            
            // Content panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Create checkmark icon (optional)
            JLabel iconLabel = new JLabel("✓");
            iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            iconLabel.setForeground(new Color(40, 167, 69));
            iconLabel.setHorizontalAlignment(JLabel.CENTER);
            iconLabel.setPreferredSize(new Dimension(50, 50));
            
            // Message label
            JLabel messageLabel = new JLabel("Thank you for your rating!");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messageLabel.setHorizontalAlignment(JLabel.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setOpaque(false);
            
            JPanel okButtonPanel = new JPanel();
            okButtonPanel.setLayout(new BorderLayout());
            okButtonPanel.setBackground(new Color(40, 167, 69));
            okButtonPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            okButtonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            JLabel okLabel = new JLabel("OK");
            okLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            okLabel.setForeground(Color.WHITE);
            okButtonPanel.add(okLabel);
            
            okButtonPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    successDialog.dispose();
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    okButtonPanel.setBackground(new Color(30, 150, 60));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    okButtonPanel.setBackground(new Color(40, 167, 69));
                }
            });
            
            buttonPanel.add(okButtonPanel);
            
            // Add all components to content panel
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setOpaque(false);
            messagePanel.add(iconLabel, BorderLayout.WEST);
            messagePanel.add(messageLabel, BorderLayout.CENTER);
            
            contentPanel.add(messagePanel, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add panels to main panel
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            // Add to dialog and show
            successDialog.add(mainPanel);
            successDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to save rating", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), 
                                    "Error", JOptionPane.ERROR_MESSAGE);
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
                 "WHERE rr.room_reservation_id = ?");
             PreparedStatement logPst = con.prepareStatement(
                 "INSERT INTO activity_log (user_id, action_type, action_description) VALUES (?, ?, ?)")) {

            pst.setInt(1, roomReservationId);
            int affected = pst.executeUpdate();

            if (affected > 0) {
                // Log activity
                logPst.setInt(1, userId); // make sure userID is accessible in this class
                logPst.setString(2, "CANCEL_RESERVATION");
                logPst.setString(3, "Cancelled reservation with room_reservation_id: " + roomReservationId);
                logPst.executeUpdate();

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

        jPanel2.add(materialTabbed1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 840, 450));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 840, 460));

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
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, -1, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 570));

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
