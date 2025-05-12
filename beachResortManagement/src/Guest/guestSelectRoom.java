/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Login.landingPage;
import com.mysql.cj.jdbc.Blob;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.toedter.calendar.JCalendar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;



/**
 *
 * @author yeojvaldez
 */
public class guestSelectRoom extends javax.swing.JFrame {

    private Date checkInDate;

    private Date checkOutDate;
    private int adults;
    private int children;
    private int userID;
    
    
    public guestSelectRoom(Date checkInDate, Date checkOutDate, int adults, int children, int userID) {
        
        initComponents();
        hoverEffect();
        
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.adults = adults;
        this.children = children;
        this.userID = userID;
        displayValues();
         
        DatabaseConnection();
        searchAvailableRooms();

        
        
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
    
    private void displayValues() {
    try {
        // Format dates before displaying in text fields
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy");
        
        if (checkInDate != null) {
            txtCheckin.setText(displayFormat.format(checkInDate));
        } else {
            txtCheckin.setText("");
        }
        
        if (checkOutDate != null) {
            txtCheckout.setText(displayFormat.format(checkOutDate));
        } else {
            txtCheckout.setText("");
        }
        
        // Set spinner values
        adultsSpinner.setValue(adults);
        childrenSpinner.setValue(children);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error displaying values: " + e.getMessage(),
            "Display Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

public void searchAvailableRooms() {
    try {
        // Validate dates
        if (this.checkInDate == null || this.checkOutDate == null) {
            JOptionPane.showMessageDialog(this, "Please select valid check-in and check-out dates.");
            return;
        }

        if (!this.checkOutDate.after(this.checkInDate)) {
            JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.");
            return;
        }

        // Convert to SQL dates
        java.sql.Date sqlCheckIn = new java.sql.Date(this.checkInDate.getTime());
        java.sql.Date sqlCheckOut = new java.sql.Date(this.checkOutDate.getTime());
        int totalGuests = this.adults + this.children;

        // Build and execute query with ratings
        String query = "SELECT r.room_number, r.room_type, r.description, r.room_price, r.room_image, r.max_occupancy,\n" +
                        "       COALESCE(AVG(rr.rating_value), 0) AS average_rating,\n" +
                        "       COUNT(rr.rating_id) AS rating_count\n" +
                        "FROM room r\n" +
                        "LEFT JOIN room_reservation res ON res.room_number = r.room_number\n" +
                        "LEFT JOIN reservation_ratings rr ON rr.room_reservation_id = res.room_reservation_id\n" +
                        "WHERE r.max_occupancy >= ?\n" +
                        "AND r.room_number NOT IN (\n" +
                        "    SELECT room_number FROM room_reservation\n" +
                        "    WHERE status = 'Reserved'\n" +
                        "    AND (? <= check_out_date AND ? >= check_in_date)\n" +
                        ")\n" +
                        "GROUP BY r.room_number, r.room_type, r.description, r.room_price, r.room_image, r.max_occupancy\n" +
                        "ORDER BY average_rating DESC, r.room_price DESC;";

        pst = con.prepareStatement(query);
        pst.setInt(1, totalGuests);
        pst.setDate(2, sqlCheckIn);
        pst.setDate(3, sqlCheckOut);
        rs = pst.executeQuery();

        // Create main container with 4 columns
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setBackground(new Color(248, 248, 252));

        // Add search summary header
        mainContainer.add(createSearchSummaryHeader(sqlCheckIn, sqlCheckOut, totalGuests), BorderLayout.NORTH);

        // Create grid panel for room cards (4 columns)
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 30, 30));
        gridPanel.setBackground(new Color(248, 248, 252));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Create cards for each room
        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;
            gridPanel.add(createRoomCard(
                rs.getString("room_number"),
                rs.getString("room_type"),
                rs.getDouble("room_price"),
                rs.getString("description"),
                rs.getInt("max_occupancy"),
                rs.getBytes("room_image"),
                rs.getDouble("average_rating"),
                rs.getInt("rating_count")
            ));
        }

        if (!hasResults) {
            mainContainer.add(createNoResultsPanel(), BorderLayout.CENTER);
        } else {
            // Wrap grid panel in scroll pane
            JScrollPane scrollPane = new JScrollPane(gridPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(new Color(248, 248, 252));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            // Minimalist, narrow scrollbar
            scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                private final int SCROLLBAR_WIDTH = 6;

                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = new Color(180, 180, 190);
                    this.trackColor = new Color(248, 248, 252);
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

                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                    if (!scrollbar.isEnabled() || thumbBounds.width > thumbBounds.height) return;

                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(new Color(150, 150, 160));
                    g2.fillRoundRect(thumbBounds.x, thumbBounds.y, SCROLLBAR_WIDTH, thumbBounds.height, 10, 10);
                    g2.dispose();
                }

                @Override
                protected Dimension getMinimumThumbSize() {
                    return new Dimension(SCROLLBAR_WIDTH, 30);
                }

                @Override
                protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                    // Keep it clean
                }

                @Override
                protected void setThumbBounds(int x, int y, int width, int height) {
                    super.setThumbBounds(x, y, SCROLLBAR_WIDTH, height);
                    scrollbar.repaint();
                }
            });

            mainContainer.add(scrollPane, BorderLayout.CENTER);
        }

        // Update the main display
        yourMainPanelOrFrame.removeAll();
        yourMainPanelOrFrame.setLayout(new BorderLayout());
        yourMainPanelOrFrame.add(mainContainer, BorderLayout.CENTER);
        yourMainPanelOrFrame.revalidate();
        yourMainPanelOrFrame.repaint();

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Error loading rooms: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}




// Helper method to create star icons
private JLabel createStarIcon(boolean filled) {
    return createStarIcon(filled, false);
}

private JLabel createStarIcon(boolean filled, boolean half) {
    JLabel star = new JLabel();
    star.setPreferredSize(new Dimension(16, 16));
    if (half) {
        star.setIcon(new ImageIcon(getClass().getResource("/icons/star-half.png")));
    } else {
        star.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/star-filled.png" : "/icons/star-empty.png")));
    }
    return star;
}


private JPanel createSearchSummaryHeader(Date checkIn, Date checkOut, int totalGuests) {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(240, 245, 250));
    headerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(15, 25, 15, 25)
    ));

    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
    String dateRange = sdf.format(checkIn) + " to " + sdf.format(checkOut);
    String guestText = totalGuests + " guest" + (totalGuests > 1 ? "s" : "");

    JLabel summaryLabel = new JLabel("<html><div style='font-size:15px;color:#555;'>" +
        "Available rooms for <b>" + guestText + "</b> from <b>" + dateRange + "</b></div></html>");
    
    headerPanel.add(summaryLabel, BorderLayout.WEST);
    
    return headerPanel;
}

private JPanel createNoResultsPanel() {
    JPanel noResultsPanel = new JPanel(new BorderLayout());
    noResultsPanel.setBackground(new Color(248, 248, 252));
    noResultsPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

    JLabel noResultsLabel = new JLabel("<html><div style='text-align:center;color:#666;font-size:16px;'>" +
        "No available rooms found for your selected dates.<br>" +
        "Please try different dates or adjust your guest count.</div></html>");
    noResultsLabel.setHorizontalAlignment(JLabel.CENTER);

    noResultsPanel.add(noResultsLabel, BorderLayout.CENTER);
    return noResultsPanel;
}

// Keep all the existing createRoomCard(), createImagePanel(), createInfoPanel(), and createBookingPanel() methods
// exactly as you have them in your original code

private JPanel createRoomCard(String roomNumber, String roomType, double roomPrice, 
                             String description, int maxOccupancy, byte[] imageBytes,
                             double averageRating, int ratingCount) {
    JPanel roomCard = new JPanel();
    roomCard.setLayout(new BorderLayout());
    roomCard.setPreferredSize(new Dimension(320, 520)); // Increased height for ratings
    roomCard.setBackground(Color.WHITE);
    roomCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    roomCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    roomCard.add(createImagePanel(imageBytes, roomType), BorderLayout.NORTH);
    roomCard.add(createInfoPanel(roomType, description, maxOccupancy, averageRating, ratingCount), BorderLayout.CENTER);
    roomCard.add(createBookingPanel(roomNumber, roomType, description, roomPrice), BorderLayout.SOUTH);

    return roomCard;
}



private JPanel createImagePanel(byte[] imageBytes, String roomType) {
    JPanel imagePanel = new JPanel(new BorderLayout());
    imagePanel.setPreferredSize(new Dimension(300, 220));
    imagePanel.setBackground(new Color(245, 245, 245));

    JLabel lblImage = new JLabel();
    lblImage.setHorizontalAlignment(JLabel.CENTER);
    
    if (imageBytes != null) {
        ImageIcon roomImageIcon = new ImageIcon(imageBytes);
        Image scaledImage = roomImageIcon.getImage()
            .getScaledInstance(300, 220, Image.SCALE_SMOOTH);
        lblImage.setIcon(new ImageIcon(scaledImage));
    } else {
        lblImage.setIcon(new ImageIcon(getClass().getResource("/images/default_room.png")));
        lblImage.setText("No Image Available");
        lblImage.setHorizontalTextPosition(JLabel.CENTER);
        lblImage.setVerticalTextPosition(JLabel.BOTTOM);
        lblImage.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }
    
    imagePanel.add(lblImage);
    return imagePanel;
}

private JPanel createInfoPanel(String roomType, String description, int maxOccupancy,
                              double averageRating, int ratingCount) {
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Reduced padding

    // Room type label
    JLabel lblRoomType = new JLabel(roomType);
    lblRoomType.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Slightly smaller font
    lblRoomType.setForeground(new Color(50, 50, 50));
    lblRoomType.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Rating panel with reduced spacing
    JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0)); // Reduced horizontal spacing
    ratingPanel.setBackground(Color.WHITE);
    ratingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    ratingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Reduced vertical padding

    // Add stars based on average rating
    int fullStars = (int) averageRating;
    boolean hasHalfStar = (averageRating - fullStars) >= 0.5;

    // Full stars (yellow)
    for (int i = 0; i < fullStars; i++) {
        ratingPanel.add(createStarLabel(true));
    }

    // Half star if needed (yellow)
    if (hasHalfStar) {
        ratingPanel.add(createStarLabel(false, true));
    }

    // Empty stars (gray)
    int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
    for (int i = 0; i < emptyStars; i++) {
        ratingPanel.add(createStarLabel(false));
    }

    // Rating text with smaller font and less spacing
    JLabel ratingText = new JLabel(String.format(" %.1f (%d)", averageRating, ratingCount));
    ratingText.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font
    ratingText.setForeground(new Color(120, 120, 120));
    ratingPanel.add(ratingText);

    // Occupancy label with reduced spacing
    JLabel lblOccupancy = new JLabel("Max Guests: " + maxOccupancy);
    lblOccupancy.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Slightly smaller
    lblOccupancy.setForeground(new Color(100, 100, 100));
    lblOccupancy.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblOccupancy.setBorder(BorderFactory.createEmptyBorder(3, 0, 10, 0)); // Reduced spacing

    // Description label
    JLabel lblDescription = new JLabel("<html><div style='text-align:center;color:#555;padding:0 5px;font-size:13px'>" 
        + description + "</div></html>"); // Smaller font size
    lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

    infoPanel.add(lblRoomType);
    infoPanel.add(ratingPanel);
    infoPanel.add(lblOccupancy);
    infoPanel.add(lblDescription);
    
    return infoPanel;
}



private JLabel createStarLabel(boolean filled) {
    return createStarLabel(filled, false);
}

private JLabel createStarLabel(boolean filled, boolean half) {
    JLabel star = new JLabel();
    star.setPreferredSize(new Dimension(14, 14)); // Slightly smaller stars
    
    if (half) {
        // Half star (yellow left, gray right)
        star.setIcon(new ImageIcon(createHalfStarIcon()));
    } else if (filled) {
        // Full yellow star
        star.setIcon(new ImageIcon(createStarIcon(new Color(255, 215, 0)))); // Gold color
    } else {
        // Empty gray star
        star.setIcon(new ImageIcon(createStarIcon(new Color(200, 200, 200)))); // Light gray
    }
    
    return star;
}

// Creates a star icon with specified color
private BufferedImage createStarIcon(Color color) {
    BufferedImage image = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // Star polygon coordinates
    int[] xPoints = {7, 9, 13, 10, 11, 7, 3, 4, 1, 5};
    int[] yPoints = {1, 5, 5, 8, 12, 10, 12, 8, 5, 5};
    
    g2.setColor(color);
    g2.fillPolygon(xPoints, yPoints, xPoints.length);
    g2.dispose();
    
    return image;
}

// Creates a half-filled star icon (yellow left, gray right)
private BufferedImage createHalfStarIcon() {
    BufferedImage image = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // Star polygon coordinates
    int[] xPoints = {7, 9, 13, 10, 11, 7, 3, 4, 1, 5};
    int[] yPoints = {1, 5, 5, 8, 12, 10, 12, 8, 5, 5};
    
    // Left half (yellow)
    g2.setClip(new Rectangle(0, 0, 7, 14));
    g2.setColor(new Color(255, 215, 0)); // Gold color
    g2.fillPolygon(xPoints, yPoints, xPoints.length);
    
    // Right half (gray)
    g2.setClip(new Rectangle(7, 0, 7, 14));
    g2.setColor(new Color(200, 200, 200)); // Light gray
    g2.fillPolygon(xPoints, yPoints, xPoints.length);
    
    g2.dispose();
    
    return image;
}


private JPanel createBookingPanel(String roomNumber, String roomType, String description, double roomPrice) {
    JPanel bookingPanel = new JPanel();
    bookingPanel.setLayout(new GridBagLayout());
    bookingPanel.setBackground(new Color(39, 114, 160));
    bookingPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    bookingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    JLabel lblPrice = new JLabel("BOOK NOW • ₱" + String.format("%,.2f", roomPrice));
    lblPrice.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
    lblPrice.setForeground(Color.WHITE);

    bookingPanel.add(lblPrice);

    // Add hover and click
    bookingPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            bookingPanel.setBackground(new Color(29, 94, 140));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            bookingPanel.setBackground(new Color(39, 114, 160));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            selectRoom(roomNumber, roomType, description, roomPrice);
        }
    });

    return bookingPanel;
}


private void selectRoom(String roomNumber, String roomType, String description, double price) {
    try {
        // Validate fields first
        if (this.checkInDate == null || this.checkOutDate == null) {
            JOptionPane.showMessageDialog(this, "Please select valid check-in and check-out dates.");
            return;
        }

        if (!this.checkOutDate.after(this.checkInDate)) {
            JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.");
            return;
        }

        // Get guest counts
        int adult = this.adults;
        int childrens = this.children;

        // Confirm booking with user
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "<html><b>Confirm Room Booking:</b><br><br>" +
            "Room: " + roomType + " (" + roomNumber + ")<br>" +
            "Dates: " + new SimpleDateFormat("MMMM d, yyyy").format(checkInDate) + 
                     " to " + new SimpleDateFormat("MMMM d, yyyy").format(checkOutDate) + "<br>" +
            "Guests: " + adult + " adults, " + childrens + " children<br>" +
            "Total: ₱" + String.format("%,.2f", price) + "</html>",
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Ask about water activities
        int waterActivities = JOptionPane.showConfirmDialog(
            this,
            "<html>Would you like to add water activities to your booking?<br>" +
            "(Boat rentals, snorkeling gear, etc.)</html>",
            "Water Activities",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        boolean wantsWaterActivities = (waterActivities == JOptionPane.YES_OPTION);

        // Open appropriate form based on user choice
        if (wantsWaterActivities) {
            new guestSelectBoat(
                checkInDate, 
                checkOutDate, 
                adult, 
                childrens, 
                roomNumber, 
                roomType, 
                description, 
                price, 
                userID
            ).setVisible(true);
        } else {
            new guestProcess2(
                checkInDate, 
                checkOutDate, 
                roomNumber, 
                roomType, 
                description, 
                price, 
                adult, 
                childrens, 
                userID
            ).setVisible(true);
        }
        
        // Close current window if booking was successful
        this.dispose();
        
    } catch (NullPointerException ex) {
        JOptionPane.showMessageDialog(this, 
            "<html>Missing required information:<br>" +
            "Please ensure all booking details are properly selected.</html>",
            "Booking Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "<html>Unexpected error while processing booking:<br>" + ex.getMessage() + "</html>",
            "Booking Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

     public void hoverEffect(){
        txtHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                handleLabelEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                handleLabelEvent(evt);
            }
        });

        txtReservation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                handleLabelEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                handleLabelEvent(evt);
            }
        });

        txtProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                handleLabelEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                handleLabelEvent(evt);
            }
        });

       }
    
    
// Helper function to handle mouse enter and mouse exit for all labels
private void handleLabelEvent(java.awt.event.MouseEvent evt) {                                     
    JLabel sourceLabel = (JLabel) evt.getSource();  // Get the label that triggered the event
    Color customColor = new Color(255, 191, 0);    // Hover color
    Color defaultColor = new Color(255, 255, 255); // Default color

    // Handle mouse enter event - change text color to customColor
    if (evt.getID() == java.awt.event.MouseEvent.MOUSE_ENTERED) {
        sourceLabel.setForeground(customColor);  // Set the hover color on the source label
    }

    // Handle mouse exit event - reset text color to default (white)
    if (evt.getID() == java.awt.event.MouseEvent.MOUSE_EXITED) {
        sourceLabel.setForeground(defaultColor);  // Reset color to white on exit
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

        dateChooserCheckin = new com.raven.datechooser.DateChooser();
        dateChooserCheckout = new com.raven.datechooser.DateChooser();
        jPanel1 = new javax.swing.JPanel();
        yourMainPanelOrFrame = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelRound1 = new GUI.PanelRound();
        panelRound3 = new GUI.PanelRound();
        jLabel12 = new javax.swing.JLabel();
        childrenSpinner = new spinner.Spinner();
        jLabel14 = new javax.swing.JLabel();
        adultsSpinner = new spinner.Spinner();
        txtCheckin = new textfield_suggestion.TextFieldSuggestion();
        jLabel7 = new javax.swing.JLabel();
        txtCheckout = new textfield_suggestion.TextFieldSuggestion();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        panelRound4 = new GUI.PanelRound();
        panelRound5 = new GUI.PanelRound();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtHome = new javax.swing.JLabel();
        txtReservation = new javax.swing.JLabel();
        txtProfile = new javax.swing.JLabel();
        panelRound7 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();

        dateChooserCheckin.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckin.setDateFormat("MMMM dd, yyyy");
        dateChooserCheckin.setTextRefernce(txtCheckin);

        dateChooserCheckout.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckout.setDateFormat(" MMMM dd, yyyy");
        dateChooserCheckout.setTextRefernce(txtCheckout);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        yourMainPanelOrFrame.setBackground(new java.awt.Color(242, 242, 242));
        yourMainPanelOrFrame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(yourMainPanelOrFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 1440, 610));

        jPanel2.setBackground(new java.awt.Color(27, 59, 95));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound1.setBackground(new java.awt.Color(255, 255, 255));
        panelRound1.setRoundBottomLeft(10);
        panelRound1.setRoundBottomRight(10);
        panelRound1.setRoundTopLeft(10);
        panelRound1.setRoundTopRight(10);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound3.setBackground(new java.awt.Color(0, 153, 255));
        panelRound3.setRoundBottomLeft(20);
        panelRound3.setRoundBottomRight(20);
        panelRound3.setRoundTopLeft(20);
        panelRound3.setRoundTopRight(20);
        panelRound3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRound3MouseClicked(evt);
            }
        });
        panelRound3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("SEACH YOUR ROOM");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        panelRound3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 0, 160, 40));

        panelRound1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 30, 180, 40));

        childrenSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        childrenSpinner.setLabelText("");
        panelRound1.add(childrenSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 30, 170, 40));

        jLabel14.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Child");
        panelRound1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, -1, -1));

        adultsSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        adultsSpinner.setLabelText("");
        panelRound1.add(adultsSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 30, 170, 40));

        txtCheckin.setEditable(false);
        txtCheckin.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckin.setSelectionColor(new java.awt.Color(255, 255, 255));
        panelRound1.add(txtCheckin, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 210, -1));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 28, 40, 40));

        txtCheckout.setEditable(false);
        txtCheckout.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckout.setSelectionColor(new java.awt.Color(255, 255, 255));
        panelRound1.add(txtCheckout, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 210, -1));

        jLabel5.setBackground(new java.awt.Color(102, 102, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 28, 40, 40));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Check-out Date");
        panelRound1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, -1, -1));

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("Check-in Date");
        panelRound1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jLabel15.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("Adult");
        panelRound1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        jPanel2.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 1140, 85));

        panelRound4.setBackground(new java.awt.Color(242, 242, 242));
        panelRound4.setRoundTopLeft(50);
        panelRound4.setRoundTopRight(50);
        panelRound4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 1440, 670));

        panelRound5.setBackground(new java.awt.Color(27, 59, 95));
        panelRound5.setRoundBottomLeft(50);
        panelRound5.setRoundBottomRight(50);
        panelRound5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal copy.png"))); // NOI18N
        jLabel17.setText(" PAPAYA BEACH RESORT,");
        panelRound5.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 310, 60));

        jLabel21.setFont(new java.awt.Font("Arial Rounded MT Bold", 2, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Escape to Paradise");
        panelRound5.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 11, -1, 40));

        txtHome.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        txtHome.setForeground(new java.awt.Color(255, 255, 255));
        txtHome.setText("HOME");
        txtHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHomeMouseClicked(evt);
            }
        });
        panelRound5.add(txtHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 0, 40, 60));

        txtReservation.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        txtReservation.setForeground(new java.awt.Color(255, 255, 255));
        txtReservation.setText("RESERVATION");
        txtReservation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReservationMouseClicked(evt);
            }
        });
        panelRound5.add(txtReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 0, -1, 60));

        txtProfile.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        txtProfile.setForeground(new java.awt.Color(255, 255, 255));
        txtProfile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProfile.setText("PROFILE");
        txtProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProfileMouseClicked(evt);
            }
        });
        panelRound5.add(txtProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 0, 60, 60));

        panelRound7.setBackground(new java.awt.Color(0, 153, 255));
        panelRound7.setRoundBottomLeft(20);
        panelRound7.setRoundBottomRight(20);
        panelRound7.setRoundTopLeft(20);
        panelRound7.setRoundTopRight(20);
        panelRound7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRound7MouseClicked(evt);
            }
        });
        panelRound7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Logout");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        panelRound7.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        panelRound5.add(panelRound7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jPanel2.add(panelRound5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 60));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 200));

        jPanel4.setBackground(new java.awt.Color(39, 114, 160));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("© 2025 Papaya Beach Resort. All rights reserved.");
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 13, -1, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 790, 1440, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked

       

    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
   try {
    String checkInStr = txtCheckin.getText().trim();
    String checkOutStr = txtCheckout.getText().trim();

    // Validate input
    if (checkInStr.isEmpty() || checkOutStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Check-in or check-out date cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Use correct date format
    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
    java.util.Date parsedCheckIn = null;
    java.util.Date parsedCheckOut = null;

    try {
        parsedCheckIn = formatter.parse(checkInStr);
        parsedCheckOut = formatter.parse(checkOutStr);
    } catch (ParseException pe) {
        JOptionPane.showMessageDialog(this, "Invalid date format. Please use: MMMM dd, yyyy (e.g., April 26, 2025)", "Date Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (parsedCheckOut.before(parsedCheckIn)) {
        JOptionPane.showMessageDialog(this, "Check-out date cannot be before check-in date.", "Date Error", JOptionPane.ERROR_MESSAGE);
        return;
    }


    // Get guest numbers
    int adults = (Integer) adultsSpinner.getValue();
    int children = (Integer) childrenSpinner.getValue();

    // Set instance variables
    this.checkInDate = parsedCheckIn;
    this.checkOutDate = parsedCheckOut;
    this.adults = adults;
    this.children = children;

    // Proceed
    searchAvailableRooms();

} catch (NullPointerException npe) {
    Logger.getLogger(guestSelectRoom.class.getName()).log(Level.SEVERE, "Null Pointer Exception: ", npe);
    JOptionPane.showMessageDialog(this, "There was an error with your dates or inputs. Please check and try again.", "Error", JOptionPane.ERROR_MESSAGE);
} catch (HeadlessException ex) {
    Logger.getLogger(guestSelectRoom.class.getName()).log(Level.SEVERE, "An error occurred while processing your request: ", ex);
    JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
}



    }//GEN-LAST:event_jLabel12MouseClicked

    private void panelRound3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelRound3MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        dateChooserCheckin.showPopup();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        dateChooserCheckout.showPopup();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void txtHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtHomeMouseClicked
        new guestHome(userID).setVisible(true);
    }//GEN-LAST:event_txtHomeMouseClicked

    private void txtReservationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtReservationMouseClicked
        new guestReservation(userID).setVisible(true);
    }//GEN-LAST:event_txtReservationMouseClicked

    private void txtProfileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProfileMouseClicked
        guestProfile user = new guestProfile(userID);
        user.setVisible(true);
    }//GEN-LAST:event_txtProfileMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
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
                java.util.logging.Logger.getLogger(guestSelectRoom.class.getName()).log(java.util.logging.Level.SEVERE, "Error logging logout activity", ex);
            }

            // Close the current window and open the landing page (logout action)
            this.dispose();
            new landingPage().setVisible(true);

        }catch (Exception ex) {
            // Handle any other unforeseen exceptions
            java.util.logging.Logger.getLogger(guestSelectRoom.class.getName()).log(java.util.logging.Level.SEVERE, "Unexpected error during logout", ex);
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound7MouseClicked

    }//GEN-LAST:event_panelRound7MouseClicked

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
            java.util.logging.Logger.getLogger(guestSelectRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestSelectRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestSelectRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestSelectRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Date checkIn = null;
             Date checkOut = null;
            int adults = 0;
            int children = 0;
            int guestID = 0;
            new guestSelectRoom(checkIn, checkOut, adults, children, guestID).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private spinner.Spinner adultsSpinner;
    private spinner.Spinner childrenSpinner;
    private com.raven.datechooser.DateChooser dateChooserCheckin;
    private com.raven.datechooser.DateChooser dateChooserCheckout;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound3;
    private GUI.PanelRound panelRound4;
    private GUI.PanelRound panelRound5;
    private GUI.PanelRound panelRound7;
    private textfield_suggestion.TextFieldSuggestion txtCheckin;
    private textfield_suggestion.TextFieldSuggestion txtCheckout;
    private javax.swing.JLabel txtHome;
    private javax.swing.JLabel txtProfile;
    private javax.swing.JLabel txtReservation;
    private javax.swing.JPanel yourMainPanelOrFrame;
    // End of variables declaration//GEN-END:variables
}
