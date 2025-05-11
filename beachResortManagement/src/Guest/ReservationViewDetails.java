package Guest;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ReservationViewDetails {
    // Modern color scheme with improved contrast and vibrancy
    private static final Color BG_COLOR = new Color(248, 249, 252);
    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);
    private static final Color PRIMARY_DARK = new Color(10, 88, 202);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(25, 135, 84);
    private static final Color SUCCESS_DARK = new Color(20, 108, 67);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color WARNING_DARK = new Color(204, 154, 6);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color DANGER_DARK = new Color(176, 42, 55);
    private static final Color INFO_COLOR = new Color(13, 202, 240);
    private static final Color INFO_DARK = new Color(10, 162, 192);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(222, 226, 230);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color ACCENT_COLOR = new Color(111, 66, 193);
    
    // Enhanced typography with font family fallbacks
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    private JFrame frame;
    private JPanel mainPanel;
    private int roomReservationId;
    
    // Shadow effects for cards
    private static final Border SHADOW_BORDER = new CompoundBorder(
        new EmptyBorder(0, 0, 10, 0),
        BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1),
            new EmptyBorder(25, 30, 25, 30)
        )
    );

    public ReservationViewDetails(int roomReservationId) {
        this.roomReservationId = roomReservationId;
        initialize();
        loadReservationDetails();
    }

    private void initialize() {
        // Set look and feel for better native integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.WARNING, "Failed to set system look and feel", e);
        }
        
        frame = new JFrame("Reservation Details");
        frame.setSize(1000, 800);
        frame.setMinimumSize(new Dimension(800, 650));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(BG_COLOR);
        
        // Add a subtle pattern or gradient to the background for depth
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Create subtle gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(248, 249, 252),
                    0, h, new Color(237, 242, 249)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);
        
        // Main content panel with improved layout
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(35, 45, 45, 45));
        
        // Header with resort logo/icon
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);
        
        // Resort icon/logo placeholder
        JLabel logoLabel = new JLabel(createResortLogo());
        headerPanel.add(logoLabel, BorderLayout.WEST);
        
        // Page title with accent
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        
        JLabel pageTitle = new JLabel("Reservation Details");
        pageTitle.setFont(TITLE_FONT);
        pageTitle.setForeground(TEXT_PRIMARY);
        titleWrapper.add(pageTitle, BorderLayout.CENTER);
        
        JLabel subTitle = new JLabel("Paradise Resort & Spa");
        subTitle.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subTitle.setForeground(ACCENT_COLOR);
        titleWrapper.add(subTitle, BorderLayout.SOUTH);
        
        headerPanel.add(titleWrapper, BorderLayout.CENTER);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content area using a responsive layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        
        // Scrollable area for main content with improved scrolling
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Custom scrollbar UI
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(210, 210, 210);
                this.trackColor = BG_COLOR;
            }
            
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
        });
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add a nice footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel footerLabel = new JLabel("© 2025 Paradise Resort & Spa • All Rights Reserved");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_SECONDARY);
        
        footerPanel.add(footerLabel);
        backgroundPanel.add(footerPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }
    
    private ImageIcon createResortLogo() {
        // Create a simple placeholder logo
        int logoSize = 48;
        BufferedImage logo = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ACCENT_COLOR);
        g2d.fillOval(0, 0, logoSize, logoSize);
        
        // Draw a palm tree icon
        g2d.setColor(Color.WHITE);
        
        // Tree trunk
        g2d.fillRect(22, 20, 4, 22);
        
        // Palm leaves
        g2d.fillArc(12, 8, 24, 16, 0, 100);
        g2d.fillArc(10, 12, 28, 16, 180, 100);
        g2d.fillArc(18, 5, 24, 16, 270, 100);
        g2d.fillArc(6, 15, 24, 16, 90, 100);
        
        g2d.dispose();
        return new ImageIcon(logo);
    }

    private void loadReservationDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT r.*, rs.status, br.boat_id, br.boat_tour_date, br.boat_tour_start_time, " +
                "br.boat_tour_end_time, br.tour_price, rm.room_type, b.boat_name, rs.status " +
                "FROM room_reservation r " +
                "LEFT JOIN boat_reservation br ON r.room_reservation_id = br.room_reservation_id " +
                "LEFT JOIN room rm ON r.room_number = rm.room_number " +
                "LEFT JOIN boat b ON br.boat_id = b.boat_id " +
                "LEFT JOIN reservation rs ON r.reservation_number = rs.reservation_number " +
                "WHERE r.room_reservation_id = ?")) {

            stmt.setInt(1, roomReservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Add the reservation header with status badge
                String status = rs.getString("rs.status");
                addReservationHeader(rs.getString("reservation_number"), status);
                
                // Improved layout with two columns in a row
                JPanel contentGrid = new JPanel(new GridLayout(1, 2, 25, 0));
                contentGrid.setOpaque(false);
                contentGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
                
                // Left column - ROOM RESERVATION DETAILS
                JPanel leftColumn = new JPanel();
                leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
                leftColumn.setOpaque(false);
                
                addCardToPanel("Room Information", leftColumn, () -> {
                    try {
                        JPanel detailsPanel = new JPanel();
                        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                        detailsPanel.setBackground(CARD_BG);
                        
                        // Room type with icon
                        JPanel roomTypePanel = createIconLabelPanel("Room Type", rs.getString("room_type"), 
                                UIManager.getIcon("FileView.directoryIcon"));
                        
                        // Room number with icon
                        JPanel roomNumberPanel = createIconLabelPanel("Room Number", rs.getString("room_number"), 
                                UIManager.getIcon("FileView.fileIcon"));
                        
                        // Guests with icon
                        String guests = rs.getInt("adult") + " adults, " + rs.getInt("child") + " children";
                        JPanel guestsPanel = createIconLabelPanel("Guests", guests, 
                                UIManager.getIcon("FileChooser.upFolderIcon"));
                        
                        // Dates with calendar icons
                        LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                        LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                        
                        JPanel checkInPanel = createIconLabelPanel("Check-in", checkIn.format(DATE_FORMATTER), 
                                UIManager.getIcon("Tree.openIcon"));
                        
                        JPanel checkOutPanel = createIconLabelPanel("Check-out", checkOut.format(DATE_FORMATTER), 
                                UIManager.getIcon("Tree.closedIcon"));
                        
                        // Duration with clock icon
                        int nights = checkOut.compareTo(checkIn);
                        JPanel durationPanel = createIconLabelPanel("Duration", nights + " night" + (nights > 1 ? "s" : ""), 
                                UIManager.getIcon("Table.descendingSortIcon"));
                        
                        // Add all panels
                        detailsPanel.add(roomTypePanel);
                        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                        detailsPanel.add(roomNumberPanel);
                        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                        detailsPanel.add(guestsPanel);
                        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                        detailsPanel.add(checkInPanel);
                        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                        detailsPanel.add(checkOutPanel);
                        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                        detailsPanel.add(durationPanel);
                        
                        return detailsPanel;
                    } catch (SQLException ex) {
                        Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                        return new JPanel();
                    }
                });
                
                // Right column - BOAT RESERVATION DETAILS (if applicable)
                JPanel rightColumn = new JPanel();
                rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
                rightColumn.setOpaque(false);
                
                if ("Availed".equalsIgnoreCase(rs.getString("boat_tour_status"))) {
                    addCardToPanel("Boat Tour Details", rightColumn, () -> {
                        try {
                            JPanel detailsPanel = new JPanel();
                            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                            detailsPanel.setBackground(CARD_BG);
                            
                            if (rs.getObject("boat_id") != null) {
                                // Boat name with icon
                                JPanel boatNamePanel = createIconLabelPanel("Boat Name", rs.getString("boat_name"),
                                        UIManager.getIcon("FileChooser.detailsViewIcon"));
                                
                                // Tour date with calendar icon
                                JPanel tourDatePanel = createIconLabelPanel("Tour Date", 
                                        rs.getDate("boat_tour_date").toLocalDate().format(DATE_FORMATTER),
                                        UIManager.getIcon("FileView.fileIcon"));
                                
                                // Time panels with clock icons
                                LocalTime startTime = rs.getTime("boat_tour_start_time").toLocalTime();
                                LocalTime endTime = rs.getTime("boat_tour_end_time").toLocalTime();
                                
                                JPanel startTimePanel = createIconLabelPanel("Start Time", startTime.format(TIME_FORMATTER),
                                        UIManager.getIcon("Tree.openIcon"));
                                
                                JPanel endTimePanel = createIconLabelPanel("End Time", endTime.format(TIME_FORMATTER),
                                        UIManager.getIcon("Tree.closedIcon"));
                                
                                // Tour price with money icon
                                JPanel pricePanel = createIconLabelPanel("Tour Price", formatCurrency(rs.getBigDecimal("tour_price")),
                                        UIManager.getIcon("FileView.hardDriveIcon"));
                                
                                // Add all panels
                                detailsPanel.add(boatNamePanel);
                                detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                                detailsPanel.add(tourDatePanel);
                                detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                                detailsPanel.add(startTimePanel);
                                detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                                detailsPanel.add(endTimePanel);
                                detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                                detailsPanel.add(pricePanel);
                                
                            } else {
                                JPanel infoPanel = createIconLabelPanel("Information", "No boat tour reserved",
                                        UIManager.getIcon("OptionPane.informationIcon"));
                                detailsPanel.add(infoPanel);
                            }
                            
                            return detailsPanel;
                        } catch (SQLException ex) {
                            Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                            return new JPanel();
                        }
                    });
                } else {
                    addCardToPanel("Boat Tour", rightColumn, () -> {
                        JPanel detailsPanel = new JPanel();
                        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                        detailsPanel.setBackground(CARD_BG);
                        
                        // Status with icon
                        JPanel statusPanel = createIconLabelPanel("Status", "Not Availed",
                                UIManager.getIcon("OptionPane.warningIcon"));
                        
                        // Add "Book Now" suggestion
                        JPanel bookNowPanel = new JPanel(new BorderLayout(10, 0));
                        bookNowPanel.setOpaque(false);
                        bookNowPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
                        
                        JLabel suggestLabel = new JLabel("Would you like to book a boat tour?");
                        suggestLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                        suggestLabel.setForeground(SECONDARY_COLOR);
                        
                        JPanel bookButton = createPillButton("Book Now", INFO_COLOR);
                        
                        bookNowPanel.add(suggestLabel, BorderLayout.CENTER);
                        bookNowPanel.add(bookButton, BorderLayout.EAST);
                        
                        detailsPanel.add(statusPanel);
                        detailsPanel.add(bookNowPanel);
                        
                        return detailsPanel;
                    });
                }
                
                contentGrid.add(leftColumn);
                contentGrid.add(rightColumn);
                mainPanel.add(contentGrid);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

                // PAYMENT DETAILS - Full width with improved design
                addCard("Payment Summary", () -> {
                    try {
                        BigDecimal roomPrice = rs.getBigDecimal("total_room_price");
                        BigDecimal entranceFee = rs.getBigDecimal("total_entrance_fee");
                        BigDecimal ecologicalFee = rs.getBigDecimal("total_ecological_fee");
                        BigDecimal boatPrice = rs.getObject("tour_price") != null ?
                                rs.getBigDecimal("tour_price") : BigDecimal.ZERO;
                        
                        // Create a stylish payment table with card-like appearance
                        JPanel paymentTable = new JPanel();
                        paymentTable.setLayout(new BoxLayout(paymentTable, BoxLayout.Y_AXIS));
                        paymentTable.setBackground(CARD_BG);
                        
                        // Payment items with icons
                        addPaymentRow(paymentTable, "Room Charges", formatCurrency(roomPrice), 
                                UIManager.getIcon("FileView.fileIcon"), false);
                        
                        addPaymentRow(paymentTable, "Entrance Fees", formatCurrency(entranceFee), 
                                UIManager.getIcon("FileView.directoryIcon"), false);
                        
                        addPaymentRow(paymentTable, "Ecological Fees", formatCurrency(ecologicalFee), 
                                UIManager.getIcon("Tree.leafIcon"), false);
                        
                        if (boatPrice.compareTo(BigDecimal.ZERO) > 0) {
                            addPaymentRow(paymentTable, "Boat Tour", formatCurrency(boatPrice), 
                                    UIManager.getIcon("FileChooser.detailsViewIcon"), false);
                        }
                        
                        // Add styled separator before total
                        JPanel separatorPanel = new JPanel(new BorderLayout());
                        separatorPanel.setBackground(CARD_BG);
                        separatorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
                        
                        JSeparator separator = new JSeparator();
                        separator.setForeground(new Color(222, 226, 230));
                        separatorPanel.add(separator, BorderLayout.CENTER);
                        
                        paymentTable.add(Box.createRigidArea(new Dimension(0, 10)));
                        paymentTable.add(separatorPanel);
                        paymentTable.add(Box.createRigidArea(new Dimension(0, 10)));
                        
                        // Add total amount with accent color
                        BigDecimal totalAmount = roomPrice.add(entranceFee).add(ecologicalFee).add(boatPrice);
                        addPaymentRow(paymentTable, "Total Amount", formatCurrency(totalAmount), 
                                UIManager.getIcon("FileView.hardDriveIcon"), true);
                        
                        // Add payment status badge
                        JPanel paymentStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        paymentStatusPanel.setOpaque(false);
                        paymentStatusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        paymentStatusPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
                        
                        // Assume payment is completed for demonstration
                        JPanel paymentBadge = createStatusBadge("PAID", SUCCESS_COLOR);
                        paymentStatusPanel.add(paymentBadge);
                        
                        paymentTable.add(Box.createRigidArea(new Dimension(0, 15)));
                        paymentTable.add(paymentStatusPanel);
                        
                        return paymentTable;
                    } catch (SQLException ex) {
                        Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                        return new JPanel();
                    }
                });
                
                // Add an amenities card
                addCard("Resort Amenities", () -> {
                    JPanel amenitiesPanel = new JPanel(new GridLayout(2, 3, 20, 20));
                    amenitiesPanel.setBackground(CARD_BG);
                    
                    // Add some amenity items with icons
                    amenitiesPanel.add(createAmenityPanel("WiFi", "Free high-speed WiFi", 
                            UIManager.getIcon("OptionPane.informationIcon")));
                    
                    amenitiesPanel.add(createAmenityPanel("Breakfast", "Complimentary breakfast", 
                            UIManager.getIcon("FileChooser.upFolderIcon")));
                    
                    amenitiesPanel.add(createAmenityPanel("Swimming Pool", "Open 6AM-10PM", 
                            UIManager.getIcon("Tree.leafIcon")));
                    
                    amenitiesPanel.add(createAmenityPanel("Spa Services", "Available on request", 
                            UIManager.getIcon("FileView.fileIcon")));
                    
                    amenitiesPanel.add(createAmenityPanel("Restaurant", "Open 24/7", 
                            UIManager.getIcon("FileView.directoryIcon")));
                    
                    amenitiesPanel.add(createAmenityPanel("Beach Access", "Private beach area", 
                            UIManager.getIcon("FileChooser.detailsViewIcon")));
                    
                    return amenitiesPanel;
                });

                // ADD ACTION BUTTONS
                addActionButtons(status);

            } else {
                showErrorDialog("Reservation not found.");
                frame.dispose();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Database error: " + ex.getMessage());
            frame.dispose();
        }
    }
    
    private void addReservationHeader(String reservationNumber, String status) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_BG);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setBorder(SHADOW_BORDER);
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 140));
        
        // Reservation number and status in one row with better spacing
        JPanel infoRow = new JPanel(new BorderLayout(20, 0));
        infoRow.setOpaque(false);
        
        // Reservation number with icon
        JPanel resNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        resNumberPanel.setOpaque(false);
        
        JLabel resIcon = new JLabel(UIManager.getIcon("FileView.hardDriveIcon"));
        
        JLabel resNumberLabel = new JLabel("Reservation #" + reservationNumber);
        resNumberLabel.setFont(HEADING_FONT);
        resNumberLabel.setForeground(TEXT_PRIMARY);
        
        resNumberPanel.add(resIcon);
        resNumberPanel.add(resNumberLabel);
        
        infoRow.add(resNumberPanel, BorderLayout.WEST);
        
        // Status badge with improved design
        infoRow.add(createStatusBadge(status.toUpperCase(), getStatusColor(status)), BorderLayout.EAST);
        
        // Add the row to header panel
        headerPanel.add(infoRow);
        
        // Add a decorative separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(233, 236, 239));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(separator);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Add a welcome message with icon
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        messagePanel.setOpaque(false);
        
        JLabel thankIcon = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        
        JLabel messageLabel = new JLabel("Thank you for choosing Paradise Resort & Spa! We look forward to your stay.");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        messageLabel.setForeground(ACCENT_COLOR);
        
        messagePanel.add(thankIcon);
        messagePanel.add(messageLabel);
        
        headerPanel.add(messagePanel);
        
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    }
    
    private JPanel createAmenityPanel(String title, String description, Icon icon) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 5));
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(233, 236, 239), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Icon at the top
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(iconLabel, BorderLayout.NORTH);
        
        // Title in the middle
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        // Description at the bottom
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(SECONDARY_COLOR);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createIconLabelPanel(String label, String value, Icon icon) {
        JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
        rowPanel.setBackground(CARD_BG);
        rowPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Icon on the left
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(24, 24));
        
        // Label and value in the center
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        labelComponent.setForeground(SECONDARY_COLOR);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(VALUE_FONT);
        valueComponent.setForeground(TEXT_PRIMARY);
        
        textPanel.add(labelComponent);
        textPanel.add(valueComponent);
        
        rowPanel.add(iconLabel, BorderLayout.WEST);
        rowPanel.add(textPanel, BorderLayout.CENTER);
        
        return rowPanel;
    }
    
    private Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
                return SUCCESS_COLOR;
            case "pending":
                return WARNING_COLOR;
            case "cancelled":
                return DANGER_COLOR;
            case "completed":
                return INFO_COLOR;
            default:
                return SECONDARY_COLOR;
        }
    }
    
    private JPanel createStatusBadge(String status, Color bgColor) {
        JPanel badgePanel = new JPanel();
        badgePanel.setLayout(new BorderLayout());
        
        JLabel badge = new JLabel(status);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(Color.WHITE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Use darker text for warning (yellow) badge
        if (bgColor.equals(WARNING_COLOR)) {
            badge.setForeground(new Color(33, 37, 41));
        }
        
        badgePanel.setBackground(bgColor);
        badgePanel.setBorder(new EmptyBorder(6, 18, 6, 18));
        
        // Make the badge corners rounded
        badgePanel.setBorder(new CompoundBorder(
            new EmptyBorder(0, 0, 0, 0) {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgColor);
                    g2.fillRoundRect(x, y, width - 1, height - 1, 16, 16);
                    g2.dispose();
                }
            },
            new EmptyBorder(6, 18, 6, 18)
        ));
        
        badgePanel.add(badge, BorderLayout.CENTER);
        
        return badgePanel;
    }
    
    private JPanel createPillButton(String text, Color bgColor) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(new EmptyBorder(8, 18, 8, 18));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel label = new JLabel(text);
        label.setFont(BUTTON_FONT);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Make the button corners rounded
        buttonPanel.setBorder(new CompoundBorder(
            new EmptyBorder(0, 0, 0, 0) {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgColor);
                    g2.fillRoundRect(x, y, width - 1, height - 1, 20, 20);
                    g2.dispose();
                }
            },
            new EmptyBorder(8, 18, 8, 18)
        ));
        
        buttonPanel.add(label, BorderLayout.CENTER);
        
        // Hover effects
        buttonPanel.addMouseListener(new MouseAdapter() {
            private Color originalColor = bgColor;
            private Color hoverColor = brighten(bgColor);
            
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(hoverColor);
                buttonPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(originalColor);
                buttonPanel.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                buttonPanel.setBorder(new CompoundBorder(
                    new EmptyBorder(0, 0, 0, 0) {
                        @Override
                        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(originalColor.darker());
                            g2.fillRoundRect(x, y, width - 1, height - 1, 20, 20);
                            g2.dispose();
                        }
                    },
                    new EmptyBorder(9, 18, 7, 18)
                ));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                buttonPanel.setBorder(new CompoundBorder(
                    new EmptyBorder(0, 0, 0, 0) {
                        @Override
                        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(hoverColor);
                            g2.fillRoundRect(x, y, width - 1, height - 1, 20, 20);
                            g2.dispose();
                        }
                    },
                    new EmptyBorder(8, 18, 8, 18)
                ));
            }
        });
        
        return buttonPanel;
    }

    private void addCard(String title, CardContentProvider contentProvider) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(SHADOW_BORDER);
        cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));

        // Card title with accent line
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Add a small colored accent bar before the title
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(4, 24));
        accentBar.setBackground(ACCENT_COLOR);
        
        titlePanel.add(accentBar, BorderLayout.WEST);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.CENTER);
        titlePanel.add(titleLabel, BorderLayout.EAST);
        
        cardPanel.add(titlePanel);

        // Add content
        JComponent content = contentProvider.provide();
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(content);

        // Add card to main panel with spacing
        mainPanel.add(cardPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    }
    
    private void addCardToPanel(String title, JPanel targetPanel, CardContentProvider contentProvider) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(SHADOW_BORDER);
        cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));

        // Card title with accent line
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Add a small colored accent bar before the title
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(4, 24));
        accentBar.setBackground(ACCENT_COLOR);
        
        titlePanel.add(accentBar, BorderLayout.WEST);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.CENTER);
        titlePanel.add(titleLabel, BorderLayout.EAST);
        
        cardPanel.add(titlePanel);

        // Add content
        JComponent content = contentProvider.provide();
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(content);

        // Add card to target panel with spacing
        targetPanel.add(cardPanel);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    private void addPaymentRow(JPanel panel, String description, String amount, Icon icon, boolean isTotal) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(CARD_BG);
        rowPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        // Left side with icon and description
        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        
        Font font = isTotal ? 
            new Font("Segoe UI", Font.BOLD, 16) : 
            new Font("Segoe UI", Font.PLAIN, 14);
        Color textColor = isTotal ? 
            TEXT_PRIMARY : 
            SECONDARY_COLOR;
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(font);
        descLabel.setForeground(textColor);
        
        leftPanel.add(iconLabel, BorderLayout.WEST);
        leftPanel.add(descLabel, BorderLayout.CENTER);
        
        // Amount label with special formatting for total
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(font);
        
        if (isTotal) {
            amountLabel.setForeground(ACCENT_COLOR);
            amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        } else {
            amountLabel.setForeground(textColor);
        }
        
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rowPanel.add(leftPanel, BorderLayout.WEST);
        rowPanel.add(amountLabel, BorderLayout.EAST);
        
        panel.add(rowPanel);
    }
    
    private void addActionButtons(String status) {
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);
        buttonContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonContainer.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        
        if ("confirmed".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
            JPanel cancelButton = createStyledButton("Cancel Reservation", DANGER_COLOR, DANGER_DARK);
            cancelButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int response = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to cancel this reservation?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (response == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(
                            frame,
                            "Your reservation has been cancelled.",
                            "Cancellation Confirmed",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        frame.dispose();
                    }
                }
            });
            buttonPanel.add(cancelButton);
        }
        
        if ("confirmed".equalsIgnoreCase(status)) {
            JPanel modifyButton = createStyledButton("Modify Reservation", WARNING_COLOR, WARNING_DARK);
            buttonPanel.add(modifyButton);
        }
        
        JPanel printButton = createStyledButton("Print Details", INFO_COLOR, INFO_DARK);
        buttonPanel.add(printButton);
        
        JPanel closeButton = createStyledButton("Close", PRIMARY_COLOR, PRIMARY_DARK);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });
        buttonPanel.add(closeButton);
        
        buttonContainer.add(buttonPanel);
        mainPanel.add(buttonContainer);
    }
    
    private JPanel createStyledButton(String text, Color bgColor, Color hoverColor) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(new EmptyBorder(10, 22, 10, 22));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add a button icon based on the text
        Icon buttonIcon = null;
        if (text.contains("Cancel")) {
            buttonIcon = UIManager.getIcon("OptionPane.errorIcon");
        } else if (text.contains("Modify")) {
            buttonIcon = UIManager.getIcon("Tree.openIcon");
        } else if (text.contains("Print")) {
            buttonIcon = UIManager.getIcon("FileView.fileIcon");
        } else if (text.contains("Close")) {
            buttonIcon = UIManager.getIcon("InternalFrame.closeIcon");
        }
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        contentPanel.setOpaque(false);
        
        if (buttonIcon != null) {
            JLabel iconLabel = new JLabel(buttonIcon);
            iconLabel.setPreferredSize(new Dimension(16, 16));
            contentPanel.add(iconLabel);
        }
        
        JLabel label = new JLabel(text);
        label.setFont(BUTTON_FONT);
        label.setForeground(Color.WHITE);
        contentPanel.add(label);
        
        buttonPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Apply rounded corners
        buttonPanel.setBorder(new CompoundBorder(
            new EmptyBorder(0, 0, 0, 0) {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(buttonPanel.getBackground());
                    g2.fillRoundRect(x, y, width - 1, height - 1, 8, 8);
                    g2.dispose();
                }
            },
            new EmptyBorder(10, 22, 10, 22)
        ));
        
        // Add hover and click effects
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(hoverColor);
                buttonPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(bgColor);
                buttonPanel.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                buttonPanel.setBackground(hoverColor.darker());
                buttonPanel.setBorder(new CompoundBorder(
                    new EmptyBorder(0, 0, 0, 0) {
                        @Override
                        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(buttonPanel.getBackground());
                            g2.fillRoundRect(x, y, width - 1, height - 1, 8, 8);
                            g2.dispose();
                        }
                    },
                    new EmptyBorder(11, 22, 9, 22) // Slight press effect
                ));
                buttonPanel.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                buttonPanel.setBackground(hoverColor);
                buttonPanel.setBorder(new CompoundBorder(
                    new EmptyBorder(0, 0, 0, 0) {
                        @Override
                        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(buttonPanel.getBackground());
                            g2.fillRoundRect(x, y, width - 1, height - 1, 8, 8);
                            g2.dispose();
                        }
                    },
                    new EmptyBorder(10, 22, 10, 22)
                ));
                buttonPanel.repaint();
            }
        });
        
        return buttonPanel;
    }

    private Color brighten(Color color) {
        int r = Math.min(255, (int)(color.getRed() * 1.2));
        int g = Math.min(255, (int)(color.getGreen() * 1.2));
        int b = Math.min(255, (int)(color.getBlue() * 1.2));
        return new Color(r, g, b);
    }

    private String formatCurrency(Number amount) {
        return amount != null ? CURRENCY_FORMAT.format(amount) : "₱0.00";
    }

    private void showErrorDialog(String message) {
        // Modern error dialog
        JPanel customPanel = new JPanel(new BorderLayout(20, 15));
        customPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel errorIcon = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        
        JLabel errorMessage = new JLabel("<html><b>Error</b><br>" + message + "</html>");
        errorMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        customPanel.add(errorIcon, BorderLayout.WEST);
        customPanel.add(errorMessage, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(frame, customPanel, "Error", JOptionPane.PLAIN_MESSAGE);
    }
    
    // Functional interface for card content creation
    private interface CardContentProvider {
        JComponent provide();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ReservationViewDetails(62); // Example reservation ID
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/beachResortManagement";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}