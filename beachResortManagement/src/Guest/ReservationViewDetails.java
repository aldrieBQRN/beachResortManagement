package Guest;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileWriter;

public class ReservationViewDetails {
    // Professional color scheme
    private static final Color BG_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(127, 140, 141);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color SUBTEXT_COLOR = new Color(102, 102, 102);
    
    // Modern typography
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBHEADING_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    private JFrame frame;
    private JPanel mainPanel;
    private int roomReservationId;
    private String reservationNumber;
    private String roomType;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private String totalAmount;
    private String boatDetails = "";

    public ReservationViewDetails(int roomReservationId) {
        this.roomReservationId = roomReservationId;
        initialize();
        loadReservationDetails();
    }

    private void initialize() {
        frame = new JFrame("Reservation Details");
        frame.setSize(1000, 800);
        frame.setMinimumSize(new Dimension(850, 650));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(BG_COLOR);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        JLabel titleLabel = new JLabel("Reservation Details");
        titleLabel.setFont(HEADING_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(separator, BorderLayout.SOUTH);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content area with modern scrollbar
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        
        // Modern scrollbar styling
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(contentPanel);
    }

    // Modern scrollbar UI class
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int SCROLLBAR_WIDTH = 8;
        private static final Color THUMB_COLOR = new Color(180, 180, 180);
        private static final Color TRACK_COLOR = new Color(240, 240, 240);

        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = THUMB_COLOR;
            this.trackColor = TRACK_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(THUMB_COLOR);
            
            // Rounded thumb design
            int arc = SCROLLBAR_WIDTH;
            g2.fillRoundRect(
                thumbBounds.x + (thumbBounds.width - SCROLLBAR_WIDTH)/2,
                thumbBounds.y,
                SCROLLBAR_WIDTH,
                thumbBounds.height,
                arc, arc
            );
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(TRACK_COLOR);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(SCROLLBAR_WIDTH, SCROLLBAR_WIDTH * 3);
        }
    }


    private void loadReservationDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                       "SELECT r.*,rs.status, br.boat_id, br.boat_tour_date, br.boat_tour_start_time, " +
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
                String status = rs.getString("rs.status");
                reservationNumber = rs.getString("reservation_number");
                roomType = rs.getString("room_type");
                roomNumber = rs.getString("room_number");
                checkInDate = rs.getDate("check_in_date").toLocalDate().format(DATE_FORMATTER);
                checkOutDate = rs.getDate("check_out_date").toLocalDate().format(DATE_FORMATTER);
                
                // Store boat details if available
                if ("Availed".equalsIgnoreCase(rs.getString("boat_tour_status"))) {
                    if (rs.getObject("boat_id") != null) {
                        boatDetails = "Boat Name: " + rs.getString("boat_name") + "\n" +
                                     "Tour Date: " + rs.getDate("boat_tour_date").toLocalDate().format(DATE_FORMATTER) + "\n" +
                                     "Start Time: " + rs.getTime("boat_tour_start_time").toLocalTime().format(TIME_FORMATTER) + "\n" +
                                     "End Time: " + rs.getTime("boat_tour_end_time").toLocalTime().format(TIME_FORMATTER) + "\n" +
                                     "Tour Price: " + formatCurrency(rs.getBigDecimal("tour_price"));
                    }
                }
                
                addReservationHeader(reservationNumber, status);
                
                // Two-column layout
                JPanel contentGrid = new JPanel(new GridLayout(1, 2, 30, 0));
                contentGrid.setBackground(BG_COLOR);
                contentGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
                
                // Left column - Room details
                JPanel leftColumn = new JPanel();
                leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
                leftColumn.setBackground(BG_COLOR);
                
                addCardToPanel("Room Information", leftColumn, () -> {
                    try {
                        JPanel detailsGrid = new JPanel(new GridLayout(0, 1, 0, 15));
                        detailsGrid.setBackground(CARD_COLOR);
                        
                        addDetailRow(detailsGrid, "Room Type:", roomType);
                        addDetailRow(detailsGrid, "Room Number:", roomNumber);
                        
                        String guests = rs.getInt("adult") + " adults, " + rs.getInt("child") + " children";
                        addDetailRow(detailsGrid, "Guests:", guests);
                                
                        addDetailRow(detailsGrid, "Check-in:", checkInDate);
                        addDetailRow(detailsGrid, "Check-out:", checkOutDate); 
                        addDetailRow(detailsGrid, "Duration:", 
                            rs.getDate("check_out_date").toLocalDate()
                                .compareTo(rs.getDate("check_in_date").toLocalDate()) + " nights");
                        
                        return detailsGrid;
                    } catch (SQLException ex) {
                        Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                        return new JPanel();
                    }
                });
                
                // Right column - Boat details
                JPanel rightColumn = new JPanel();
                rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
                rightColumn.setBackground(BG_COLOR);
                
                if (!boatDetails.isEmpty()) {
                    addCardToPanel("Boat Tour", rightColumn, () -> {
                        JPanel detailsGrid = new JPanel(new GridLayout(0, 1, 0, 15));
                        detailsGrid.setBackground(CARD_COLOR);
                        
                        for (String line : boatDetails.split("\n")) {
                            String[] parts = line.split(": ");
                            if (parts.length == 2) {
                                addDetailRow(detailsGrid, parts[0] + ":", parts[1]);
                            }
                        }
                        
                        return detailsGrid;
                    });
                }
                
                contentGrid.add(leftColumn);
                contentGrid.add(rightColumn);
                mainPanel.add(contentGrid);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

                // Payment details
                addCard("Payment Summary", () -> {
                    try {
                        BigDecimal roomPrice = rs.getBigDecimal("total_room_price");
                        BigDecimal entranceFee = rs.getBigDecimal("total_entrance_fee");
                        BigDecimal ecologicalFee = rs.getBigDecimal("total_ecological_fee");
                        BigDecimal boatPrice = rs.getObject("tour_price") != null ?
                                rs.getBigDecimal("tour_price") : BigDecimal.ZERO;
                        
                        JPanel paymentTable = new JPanel();
                        paymentTable.setLayout(new BoxLayout(paymentTable, BoxLayout.Y_AXIS));
                        paymentTable.setBackground(CARD_COLOR);
                        
                        addPaymentRow(paymentTable, "Room Charges", formatCurrency(roomPrice), false);
                        addPaymentRow(paymentTable, "Entrance Fees", formatCurrency(entranceFee), false);
                        addPaymentRow(paymentTable, "Ecological Fees", formatCurrency(ecologicalFee), false);
                        
                        if (boatPrice.compareTo(BigDecimal.ZERO) > 0) {
                            addPaymentRow(paymentTable, "Boat Tour", formatCurrency(boatPrice), false);
                        }
                        
                        JSeparator sep = new JSeparator();
                        sep.setForeground(BORDER_COLOR);
                        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
                        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
                        paymentTable.add(sep);
                        paymentTable.add(Box.createRigidArea(new Dimension(0, 15)));
                        
                        BigDecimal total = roomPrice.add(entranceFee).add(ecologicalFee).add(boatPrice);
                        totalAmount = formatCurrency(total);
                        addPaymentRow(paymentTable, "Total Amount", totalAmount, true);
                        
                        return paymentTable;
                    } catch (SQLException ex) {
                        Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                        return new JPanel();
                    }
                });

                addActionButtons(status);
                frame.setVisible(true);

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
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(25, 30, 25, 30)));
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        
        // Top row with reservation number and status
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel resNumberLabel = new JLabel("Reservation #" + reservationNumber);
        resNumberLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        resNumberLabel.setForeground(TEXT_COLOR);
        topRow.add(resNumberLabel, BorderLayout.WEST);
        
        topRow.add(createStatusBadge(status), BorderLayout.EAST);
        headerPanel.add(topRow, BorderLayout.NORTH);
        
        // Thank you message
        JLabel messageLabel = new JLabel("Thank you for choosing Papaya Beach Resort");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(SUBTEXT_COLOR);
        messageLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        headerPanel.add(messageLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    }
    
    private JPanel createStatusBadge(String status) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badge.setBorder(new CompoundBorder(
            new LineBorder(getStatusBorderColor(status), 1),
            new EmptyBorder(6, 20, 6, 20)));
        badge.setBackground(getStatusColor(status));
        
        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(getStatusTextColor(status));
        badge.add(statusLabel);
        
        return badge;
    }
    
    private Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed": return new Color(223, 240, 216);
            case "pending": return new Color(254, 245, 231);
            case "cancelled": return new Color(253, 235, 238);
            case "completed": return new Color(232, 244, 253);
            default: return new Color(240, 240, 240);
        }
    }
    
    private Color getStatusBorderColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed": return new Color(192, 229, 184);
            case "pending": return new Color(254, 234, 201);
            case "cancelled": return new Color(253, 213, 219);
            case "completed": return new Color(204, 234, 253);
            default: return new Color(220, 220, 220);
        }
    }
    
    private Color getStatusTextColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed": return new Color(60, 118, 61);
            case "pending": return new Color(138, 109, 59);
            case "cancelled": return new Color(169, 68, 66);
            case "completed": return new Color(49, 112, 143);
            default: return new Color(100, 100, 100);
        }
    }

    private void addCard(String title, CardContentProvider contentProvider) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(25, 30, 30, 30)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));

        // Card header with divider
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JSeparator divider = new JSeparator();
        divider.setForeground(BORDER_COLOR);
        divider.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        card.add(titleLabel);
        card.add(divider);
        card.add(contentProvider.provide());

        mainPanel.add(card);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    }
    
    private void addCardToPanel(String title, JPanel targetPanel, CardContentProvider contentProvider) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(25, 30, 30, 30)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));

        // Card header with divider
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JSeparator divider = new JSeparator();
        divider.setForeground(BORDER_COLOR);
        divider.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        card.add(titleLabel);
        card.add(divider);
        card.add(contentProvider.provide());

        targetPanel.add(card);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(CARD_COLOR);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(LABEL_FONT);
        labelComp.setForeground(SUBTEXT_COLOR);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(VALUE_FONT);
        valueComp.setForeground(TEXT_COLOR);
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);
        panel.add(row);
    }
    
    private void addPaymentRow(JPanel panel, String description, String amount, boolean isTotal) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_COLOR);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        Font font = isTotal ? BUTTON_FONT : VALUE_FONT;
        Color color = isTotal ? TEXT_COLOR : SUBTEXT_COLOR;
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(font);
        descLabel.setForeground(color);
        
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(font);
        amountLabel.setForeground(color);
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        row.add(descLabel, BorderLayout.WEST);
        row.add(amountLabel, BorderLayout.EAST);
        panel.add(row);
    }
    
    private void addActionButtons(String status) {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
    buttonPanel.setBackground(BG_COLOR);
    buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
    
    // Only show Cancel button if status is Pending
    if ("pending".equalsIgnoreCase(status)) {
        buttonPanel.add(createPanelButton("Cancel Reservation", DANGER_COLOR, e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to cancel this reservation?", 
                "Confirm Cancellation", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Update reservation status to Cancelled
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(
                             "UPDATE reservation SET status = 'Cancelled' WHERE reservation_number = ?")) {
                        
                        stmt.setString(1, reservationNumber);
                        int rowsAffected = stmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, 
                                "Reservation has been cancelled successfully.", 
                                "Cancellation Successful", 
                                JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(frame, 
                                "Failed to cancel reservation.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, 
                        "Database error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }));
    }
    
    // Add Export button with dropdown options
    buttonPanel.add(createExportButton());
    
    buttonPanel.add(createPanelButton("Close", PRIMARY_COLOR, e -> frame.dispose()));
    
    mainPanel.add(buttonPanel);
}
    
    private JPanel createExportButton() {
        JPanel exportButton = createPanelButton("Export", INFO_COLOR, null);
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem textItem = new JMenuItem("Export as Text");
        JMenuItem htmlItem = new JMenuItem("Export as HTML");
        
        textItem.addActionListener(e -> exportToText());
        htmlItem.addActionListener(e -> exportToHTML());
        
        popupMenu.add(textItem);
        popupMenu.add(htmlItem);
        
        exportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.show(exportButton, 0, exportButton.getHeight());
            }
        });
        
        return exportButton;
    }
    
    private JPanel createPanelButton(String text, Color bgColor, ActionListener action) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(new EmptyBorder(12, 25, 12, 25));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel label = new JLabel(text);
        label.setFont(BUTTON_FONT);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(label, BorderLayout.CENTER);
        
        // Add hover effects
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(brighter(bgColor, 1.2f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(bgColor);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.actionPerformed(new ActionEvent(buttonPanel, ActionEvent.ACTION_PERFORMED, text));
                }
            }
        });
        
        return buttonPanel;
    }
    
    private void exportToText() {
        try {
            // Create the content
            StringBuilder content = new StringBuilder();
            content.append("Reservation Details\n");
            content.append("===================\n\n");
            content.append("Reservation Number: ").append(reservationNumber).append("\n");
            content.append("Room Type: ").append(roomType).append("\n");
            content.append("Room Number: ").append(roomNumber).append("\n");
            content.append("Check-in Date: ").append(checkInDate).append("\n");
            content.append("Check-out Date: ").append(checkOutDate).append("\n\n");
            
            if (!boatDetails.isEmpty()) {
                content.append("Boat Tour Details\n");
                content.append("----------------\n");
                content.append(boatDetails).append("\n\n");
            }
            
            content.append("Payment Summary\n");
            content.append("---------------\n");
            content.append("Total Amount: ").append(totalAmount).append("\n");

            // Show file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Reservation Details");
            fileChooser.setSelectedFile(new File("Reservation_" + reservationNumber + ".txt"));
            
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                // Write to file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(content.toString());
                }
                
                JOptionPane.showMessageDialog(frame, 
                    "Reservation details exported as text file!", 
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error exporting reservation details: " + ex.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToHTML() {
        try {
            // Create HTML content
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n<head>\n");
            html.append("<title>Reservation Details</title>\n");
            html.append("<style>\n");
            html.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
            html.append("h1 { color: #2c3e50; border-bottom: 1px solid #eee; padding-bottom: 10px; }\n");
            html.append("h2 { color: #3498db; margin-top: 30px; }\n");
            html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n");
            html.append("th { background-color: #f2f2f2; text-align: left; padding: 8px; }\n");
            html.append("td { padding: 8px; border-bottom: 1px solid #ddd; }\n");
            html.append(".total { font-weight: bold; }\n");
            html.append("</style>\n</head>\n<body>\n");
            
            html.append("<h1>Reservation Details</h1>\n");
            html.append("<table>\n");
            html.append("<tr><th>Reservation Number:</th><td>").append(reservationNumber).append("</td></tr>\n");
            html.append("<tr><th>Room Type:</th><td>").append(roomType).append("</td></tr>\n");
            html.append("<tr><th>Room Number:</th><td>").append(roomNumber).append("</td></tr>\n");
            html.append("<tr><th>Check-in Date:</th><td>").append(checkInDate).append("</td></tr>\n");
            html.append("<tr><th>Check-out Date:</th><td>").append(checkOutDate).append("</td></tr>\n");
            html.append("</table>\n");
            
            if (!boatDetails.isEmpty()) {
                html.append("<h2>Boat Tour Details</h2>\n");
                html.append("<table>\n");
                for (String line : boatDetails.split("\n")) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        html.append("<tr><th>").append(parts[0]).append("</th><td>").append(parts[1]).append("</td></tr>\n");
                    }
                }
                html.append("</table>\n");
            }
            
            html.append("<h2>Payment Summary</h2>\n");
            html.append("<table>\n");
            html.append("<tr class=\"total\"><th>Total Amount:</th><td>").append(totalAmount).append("</td></tr>\n");
            html.append("</table>\n");
            
            html.append("</body>\n</html>");

            // Show file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Reservation Details");
            fileChooser.setSelectedFile(new File("Reservation_" + reservationNumber + ".html"));
            
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                // Write to file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(html.toString());
                }
                
                JOptionPane.showMessageDialog(frame, 
                    "Reservation details exported as HTML file!", 
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error exporting reservation details: " + ex.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Color brighter(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }

    private String formatCurrency(Number amount) {
    if (amount == null) {
        return "0.00";
    }
    
    // Format the number with commas and 2 decimal places
    NumberFormat format = NumberFormat.getNumberInstance();
    format.setMinimumFractionDigits(2);
    format.setMaximumFractionDigits(2);
    format.setGroupingUsed(true); // This enables comma separators
    
    return format.format(amount);
}

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private interface CardContentProvider {
        JComponent provide();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
                new ReservationViewDetails(62);
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