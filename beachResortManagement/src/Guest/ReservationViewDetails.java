package Guest;

import javax.swing.*;
import javax.swing.border.*;
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

public class ReservationViewDetails {
    // Modern color scheme
    private static final Color BG_COLOR = new Color(246, 248, 250);
    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(25, 135, 84);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color INFO_COLOR = new Color(13, 202, 240);
    private static final Color CARD_BORDER = new Color(222, 226, 230);
    
    // Modern typography
      private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    private JFrame frame;
    private JPanel mainPanel;
    private int roomReservationId;

    public ReservationViewDetails(int roomReservationId) {
        this.roomReservationId = roomReservationId;
        initialize();
        loadReservationDetails();
    }

    private void initialize() {
        frame = new JFrame("Reservation Details");
        frame.setSize(900, 750);
        frame.setMinimumSize(new Dimension(750, 600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(BG_COLOR);
        
        // Main content panel with improved layout
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // Page title
        JLabel pageTitle = new JLabel("Reservation Details");
        pageTitle.setFont(HEADING_FONT);
        pageTitle.setForeground(new Color(33, 37, 41));
        contentPanel.add(pageTitle, BorderLayout.NORTH);
        
        // Main content area using a responsive grid layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        
        // Scrollable area for main content
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(contentPanel);
        frame.setVisible(true);
    }

    private void loadReservationDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                       "SELECT r.*,rs.status, br.boat_id, br.boat_tour_date, br.boat_tour_start_time, " +
            "br.boat_tour_end_time, br.tour_price, rm.room_type, b.boat_name, rs.status " + // Added rs.status for the reservation status
            "FROM room_reservation r " +
            "LEFT JOIN boat_reservation br ON r.room_reservation_id = br.room_reservation_id " +
            "LEFT JOIN room rm ON r.room_number = rm.room_number " +
            "LEFT JOIN boat b ON br.boat_id = b.boat_id " +
            "LEFT JOIN reservation rs ON r.reservation_number = rs.reservation_number " + // Added join with reservation table
            "WHERE r.room_reservation_id = ?")) {

            stmt.setInt(1, roomReservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Add the reservation header with status badge
                String status = rs.getString("rs.status");
                addReservationHeader(rs.getString("reservation_number"), status);
                
                // Improved layout with two columns in a row
                JPanel contentGrid = new JPanel(new GridLayout(1, 2, 20, 0));
                contentGrid.setBackground(BG_COLOR);
                contentGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 350));
                
                // Left column - ROOM RESERVATION DETAILS
                JPanel leftColumn = new JPanel();
                leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
                leftColumn.setBackground(BG_COLOR);
                
                addCardToPanel("Room Information", leftColumn, () -> {
                    try {
                        JPanel detailsGrid = new JPanel(new GridLayout(0, 1, 0, 12));
                        detailsGrid.setBackground(Color.WHITE);
                        
                        addDetailRowToPanel("Room Type:", rs.getString("room_type"), detailsGrid);
                        addDetailRowToPanel("Room Number:", rs.getString("room_number"), detailsGrid);
                        
                        String guests = rs.getInt("adult") + " adults, " + rs.getInt("child") + " children";
                        addDetailRowToPanel("Guests:", guests, detailsGrid);
                                
                        LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                        LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                        addDetailRowToPanel("Check-in:", checkIn.format(DATE_FORMATTER), detailsGrid);
                        addDetailRowToPanel("Check-out:", checkOut.format(DATE_FORMATTER), detailsGrid); 
                        addDetailRowToPanel("Duration:", checkOut.compareTo(checkIn) + " nights", detailsGrid);
                        
                        return detailsGrid;
                    } catch (SQLException ex) {
                        Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                        return new JPanel();
                    }
                });
                
                // Right column - BOAT RESERVATION DETAILS (if applicable)
                JPanel rightColumn = new JPanel();
                rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
                rightColumn.setBackground(BG_COLOR);
                
                if ("Availed".equalsIgnoreCase(rs.getString("boat_tour_status"))) {
                    addCardToPanel("Boat Tour Details", rightColumn, () -> {
                        try {
                            JPanel detailsGrid = new JPanel(new GridLayout(0, 1, 0, 12));
                            detailsGrid.setBackground(Color.WHITE);
                            
                            if (rs.getObject("boat_id") != null) {
                                addDetailRowToPanel("Boat Name:", rs.getString("boat_name"), detailsGrid);
                                addDetailRowToPanel("Tour Date:",
                                        rs.getDate("boat_tour_date").toLocalDate().format(DATE_FORMATTER), detailsGrid);
                                
                                LocalTime startTime = rs.getTime("boat_tour_start_time").toLocalTime();
                                LocalTime endTime = rs.getTime("boat_tour_end_time").toLocalTime();
                                addDetailRowToPanel("Start Time:", startTime.format(TIME_FORMATTER), detailsGrid);
                                addDetailRowToPanel("End Time:", endTime.format(TIME_FORMATTER), detailsGrid);
                                
                                addDetailRowToPanel("Tour Price:", formatCurrency(rs.getBigDecimal("tour_price")), detailsGrid);
                            } else {
                                addDetailRowToPanel("Information:", "No boat tour reserved", detailsGrid);
                            }
                            
                            return detailsGrid;
                        } catch (SQLException ex) {
                            Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                            return new JPanel();
                        }
                    });
                } else {
                    addCardToPanel("Boat Tour", rightColumn, () -> {
                        JPanel detailsGrid = new JPanel(new GridLayout(0, 1, 0, 12));
                        detailsGrid.setBackground(Color.WHITE);
                        addDetailRowToPanel("Status:", "Not Availed", detailsGrid);
                        return detailsGrid;
                    });
                }
                
                contentGrid.add(leftColumn);
                contentGrid.add(rightColumn);
                mainPanel.add(contentGrid);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

                // PAYMENT DETAILS - Full width
                addCard("Payment Summary", () -> {
                    try {
                        BigDecimal roomPrice = rs.getBigDecimal("total_room_price");
                        BigDecimal entranceFee = rs.getBigDecimal("total_entrance_fee");
                        BigDecimal ecologicalFee = rs.getBigDecimal("total_ecological_fee");
                        BigDecimal boatPrice = rs.getObject("tour_price") != null ?
                                rs.getBigDecimal("tour_price") : BigDecimal.ZERO;
                        
                        // Create a stylish payment table
                        JPanel paymentTable = new JPanel();
                        paymentTable.setLayout(new BoxLayout(paymentTable, BoxLayout.Y_AXIS));
                        paymentTable.setBackground(Color.WHITE);
                        
                        // Add payment items
                        addPaymentRow(paymentTable, "Room Charges", formatCurrency(roomPrice), false);
                        addPaymentRow(paymentTable, "Entrance Fees", formatCurrency(entranceFee), false);
                        addPaymentRow(paymentTable, "Ecological Fees", formatCurrency(ecologicalFee), false);
                        
                        if (boatPrice.compareTo(BigDecimal.ZERO) > 0) {
                            addPaymentRow(paymentTable, "Boat Tour", formatCurrency(boatPrice), false);
                        }
                        
                        // Add separator before total
                        JSeparator separator = new JSeparator();
                        separator.setForeground(new Color(222, 226, 230));
                        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
                        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
                        paymentTable.add(separator);
                        paymentTable.add(Box.createRigidArea(new Dimension(0, 12)));
                        
                        // Add total amount
                        BigDecimal totalAmount = roomPrice.add(entranceFee).add(ecologicalFee).add(boatPrice);
                        addPaymentRow(paymentTable, "Total Amount", formatCurrency(totalAmount), true);
                        
                        return paymentTable;
                    } catch (SQLException ex) {
                        Logger.getLogger(ReservationViewDetails.class.getName()).log(Level.SEVERE, null, ex);
                        return new JPanel();
                    }
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
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        
        // Reservation number and status in one row
        JPanel infoRow = new JPanel(new BorderLayout(20, 0));
        infoRow.setOpaque(false);
        
        // Reservation number
        JLabel resNumberLabel = new JLabel("Reservation #" + reservationNumber);
        resNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoRow.add(resNumberLabel, BorderLayout.WEST);
        
        // Status badge
        infoRow.add(createStatusBadge(status), BorderLayout.EAST);
        
        // Add the row to header panel
        headerPanel.add(infoRow);
        
        // Add a subtle message below
        JLabel messageLabel = new JLabel("Thank you for choosing Paradise Resort!");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        messageLabel.setForeground(SECONDARY_COLOR);
        messageLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        headerPanel.add(messageLabel);
        
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    private JPanel createStatusBadge(String status) {
        JPanel badgePanel = new JPanel();
        badgePanel.setLayout(new BorderLayout());
        
        JLabel badge = new JLabel(status.toUpperCase());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        
        Color bgColor;
        switch (status.toLowerCase()) {
            case "confirmed":
                bgColor = SUCCESS_COLOR;
                break;
            case "pending":
                bgColor = WARNING_COLOR;
                badge.setForeground(Color.BLACK);
                break;
            case "cancelled":
                bgColor = DANGER_COLOR;
                break;
            case "completed":
                bgColor = INFO_COLOR;
                break;
            default:
                bgColor = SECONDARY_COLOR;
        }
        
        badgePanel.setBackground(bgColor);
        badgePanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        badgePanel.add(badge, BorderLayout.CENTER);
        
        return badgePanel;
    }

    private void addCard(String title, CardContentProvider contentProvider) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 25, 25, 25)));
        cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));

        // Card title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(titleLabel);

        // Add content
        JComponent content = contentProvider.provide();
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(content);

        // Add card to main panel with spacing
        mainPanel.add(cardPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    private void addCardToPanel(String title, JPanel targetPanel, CardContentProvider contentProvider) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 25, 25, 25)));
        cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));

        // Card title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(titleLabel);

        // Add content
        JComponent content = contentProvider.provide();
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(content);

        // Add card to target panel with spacing
        targetPanel.add(cardPanel);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    private void addDetailRowToPanel(String label, String value, JPanel panel) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        labelComponent.setForeground(SECONDARY_COLOR);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(VALUE_FONT);
        valueComponent.setForeground(new Color(33, 37, 41));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        panel.add(rowPanel);
    }
    
    private void addPaymentRow(JPanel panel, String description, String amount, boolean isTotal) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(new EmptyBorder(6, 0, 6, 0));
        
        Font font = isTotal ? 
            new Font("Segoe UI", Font.BOLD, 16) : 
            new Font("Segoe UI", Font.PLAIN, 14);
        Color textColor = isTotal ? 
            new Color(33, 37, 41) : 
            SECONDARY_COLOR;
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(font);
        descLabel.setForeground(textColor);
        
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(font);
        amountLabel.setForeground(textColor);
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rowPanel.add(descLabel, BorderLayout.WEST);
        rowPanel.add(amountLabel, BorderLayout.EAST);
        
        panel.add(rowPanel);
    }
    
    private void addActionButtons(String status) {
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setBackground(BG_COLOR);
        buttonContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonContainer.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        if ("confirmed".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
            buttonPanel.add(createCustomButton("Cancel Reservation", DANGER_COLOR));
        }
        
        if ("confirmed".equalsIgnoreCase(status)) {
            buttonPanel.add(createCustomButton("Modify Reservation", WARNING_COLOR));
        }
        
       
        
        JPanel closeButton = createCustomButton("Close", PRIMARY_COLOR);
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
    
    private JPanel createCustomButton(String text, Color bgColor) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(label, BorderLayout.CENTER);
        
        // Hover effects
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(brighten(bgColor));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(bgColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                buttonPanel.setBorder(new EmptyBorder(11, 20, 9, 20)); // Slight press effect
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
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
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
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