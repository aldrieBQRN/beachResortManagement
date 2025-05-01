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

        // Build and execute query
        String query = "SELECT r.room_number, r.room_type, r.description, r.room_price, r.room_image, max_occupancy " +
                     "FROM room r " +
                     "WHERE r.max_occupancy >= ? " +
                     "AND r.room_number NOT IN (" +
                     "   SELECT room_number FROM room_reservation " +
                     "   WHERE status = 'Reserved' " +
                     "   AND (? <= check_out_date AND ? >= check_in_date)" +
                     ") " +
                     "ORDER BY r.room_price ASC";

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
                rs.getBytes("room_image")
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
    private final int SCROLLBAR_WIDTH = 6; // narrower width

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

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new Color(150, 150, 160)); // subtle dark thumb
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
                             String description, int maxOccupancy, byte[] imageBytes) {
    JPanel roomCard = new JPanel();
    roomCard.setLayout(new BorderLayout());
    roomCard.setPreferredSize(new Dimension(320, 480));
    roomCard.setBackground(Color.WHITE);
    roomCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    roomCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    roomCard.add(createImagePanel(imageBytes, roomType), BorderLayout.NORTH);
    roomCard.add(createInfoPanel(roomType, description, maxOccupancy), BorderLayout.CENTER);
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

private JPanel createInfoPanel(String roomType, String description, int maxOccupancy) {
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

    // Room type label
    JLabel lblRoomType = new JLabel(roomType);
    lblRoomType.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblRoomType.setForeground(new Color(50, 50, 50));
    lblRoomType.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Occupancy label
    JLabel lblOccupancy = new JLabel("Max Guests: " + maxOccupancy);
    lblOccupancy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblOccupancy.setForeground(new Color(100, 100, 100));
    lblOccupancy.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblOccupancy.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

    // Description label with HTML formatting for better text wrapping
    JLabel lblDescription = new JLabel("<html><div style='text-align:center;color:#555;padding:0 5px;'>" 
        + description + "</div></html>");
    lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

    infoPanel.add(lblRoomType);
    infoPanel.add(lblOccupancy);
    infoPanel.add(lblDescription);
    
    return infoPanel;
}

private JPanel createBookingPanel(String roomNumber, String roomType, String description, double roomPrice) {
    JPanel bookingPanel = new JPanel();
    bookingPanel.setLayout(new GridBagLayout());
    bookingPanel.setBackground(new Color(39, 114, 160)); // Primary color
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
            bookingPanel.setBackground(new Color(29, 94, 140)); // darker when hover
        }

        @Override
        public void mouseExited(MouseEvent e) {
            bookingPanel.setBackground(new Color(39, 114, 160)); // original color
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Call your selectRoom function correctly
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
            // Add debug logging
            System.out.println("Creating guestSelectBoat with:");
            System.out.println("checkInDate: " + checkInDate);
            System.out.println("checkOutDate: " + checkOutDate);
            System.out.println("roomNumber: " + roomNumber);
            System.out.println("userID: " + userID);
            
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
        // Existing error han
    JOptionPane.showMessageDialog(this, 
        "<html>Unexpected error while processing booking:<br>" + ex.getMessage() + "</html>",
        "Booking Error", 
        JOptionPane.ERROR_MESSAGE);
    ex.printStackTrace();
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
        jLabel13 = new javax.swing.JLabel();
        adultsSpinner = new spinner.Spinner();
        txtCheckin = new textfield_suggestion.TextFieldSuggestion();
        jLabel7 = new javax.swing.JLabel();
        txtCheckout = new textfield_suggestion.TextFieldSuggestion();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCheckout1 = new textfield_suggestion.TextFieldSuggestion();
        jLabel15 = new javax.swing.JLabel();
        panelRound4 = new GUI.PanelRound();
        jLabel18 = new javax.swing.JLabel();
        panelRound5 = new GUI.PanelRound();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

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

        panelRound1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 30, 180, 40));

        childrenSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        childrenSpinner.setLabelText("");
        panelRound1.add(childrenSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 30, 170, 40));

        jLabel14.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Child");
        panelRound1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, -1, -1));

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Duration");
        panelRound1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, -1, -1));

        adultsSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        adultsSpinner.setLabelText("");
        panelRound1.add(adultsSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, 170, 40));

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

        txtCheckout1.setText("2 Nigths");
        txtCheckout1.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckout1.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtCheckout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCheckout1ActionPerformed(evt);
            }
        });
        panelRound1.add(txtCheckout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 30, 110, 40));

        jLabel15.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("Adult");
        panelRound1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, -1, -1));

        jPanel2.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 1260, 85));

        panelRound4.setBackground(new java.awt.Color(242, 242, 242));
        panelRound4.setRoundTopLeft(50);
        panelRound4.setRoundTopRight(50);
        panelRound4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 1440, 670));

        jLabel18.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("to Papaya Beach Resort");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, -1, 30));

        panelRound5.setBackground(new java.awt.Color(27, 59, 95));
        panelRound5.setRoundBottomLeft(50);
        panelRound5.setRoundBottomRight(50);
        panelRound5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/iconHome.png"))); // NOI18N
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        panelRound5.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 0, -1, 60));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/profile.png"))); // NOI18N
        panelRound5.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 0, 30, 60));

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

        panelRound5.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jLabel27.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-book-25.png"))); // NOI18N
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });
        panelRound5.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 0, -1, 60));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Welcome,");
        panelRound5.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 180, 60));

        jPanel2.add(panelRound5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 60));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 200));

        jPanel4.setBackground(new java.awt.Color(39, 114, 160));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1440, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

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

    private void txtCheckout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCheckout1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCheckout1ActionPerformed

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        new guestHome(userID).setVisible(true);
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        this.dispose();
        new landingPage().setVisible(true);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked

    }//GEN-LAST:event_panelRound2MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
        new guestReservation(userID).setVisible(true);
    }//GEN-LAST:event_jLabel27MouseClicked

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
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound2;
    private GUI.PanelRound panelRound3;
    private GUI.PanelRound panelRound4;
    private GUI.PanelRound panelRound5;
    private textfield_suggestion.TextFieldSuggestion txtCheckin;
    private textfield_suggestion.TextFieldSuggestion txtCheckout;
    private textfield_suggestion.TextFieldSuggestion txtCheckout1;
    private javax.swing.JPanel yourMainPanelOrFrame;
    // End of variables declaration//GEN-END:variables
}
