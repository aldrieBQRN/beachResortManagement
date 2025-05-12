/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Login.landingPage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;




/**
 *
 * @author yeojvaldez
 */
public class guestSelectBoat extends javax.swing.JFrame {

    private Date checkInDate;
    private Date checkOutDate;
    private int adults;
    private int children;
    
    private String roomNumber;
    private String roomType;
    private String roomDescription;
    private double roomPrice;
    private int userID;
    
    
    public guestSelectBoat(Date checkInDate, Date checkOutDate, int adults, int children, 
                           String roomNumber, String roomType, String description, double price, int userID) {
        
        initComponents();
        hoverEffect();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.adults = adults;
        this.children = children;   
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomDescription = description;
        this.roomPrice = price;
        this.userID = userID;

        displayValues();
         
        DatabaseConnection();
        
     
     
       

        
        
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
    dateComboBox.removeAllItems();
    
    // Add default/placeholder item
    dateComboBox.addItem("Select a date");

    // Return if dates are invalid
    if (checkInDate == null || checkOutDate == null || checkOutDate.before(checkInDate)) {
        return;
    }

    try {
        // Convert java.util.Date to LocalDate (safe conversion)
        LocalDate startDate = checkInDate.toInstant()
                                  .atZone(ZoneId.systemDefault())
                                  .toLocalDate();
        LocalDate endDate = checkOutDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

        // Format for display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)");

        // Add each date in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dateComboBox.addItem(date.format(formatter));
        }
    } catch (UnsupportedOperationException e) {
        // Fallback for java.sql.Date (which doesn't support toInstant())
        LocalDate startDate = new java.sql.Date(checkInDate.getTime()).toLocalDate();
        LocalDate endDate = new java.sql.Date(checkOutDate.getTime()).toLocalDate();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)");
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dateComboBox.addItem(date.format(formatter));
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error displaying dates: " + e.getMessage(),
            "Date Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

// Button Editor Class


// Method to handle boat selection
public void SearchBoat() {
    try {
        // Get selected date from combo box
        String selectedDateString = (String) dateComboBox.getSelectedItem();
        if (selectedDateString == null || selectedDateString.equals("Select a date")) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.");
            return;
        }

        // Parse selected date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)", Locale.ENGLISH);
        LocalDate selectedDate = LocalDate.parse(selectedDateString, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

        // Get selected time from text field
        String timeInput = txtTime.getText().trim();
        LocalTime localStartTime;

        try {
            // Try AM/PM format first
            DateTimeFormatter amPmFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
            localStartTime = LocalTime.parse(timeInput.toUpperCase(), amPmFormatter);
        } catch (DateTimeParseException e1) {
            try {
                // Try 24-hour format
                DateTimeFormatter twentyFourHrFormatter = DateTimeFormatter.ofPattern("HH:mm");
                localStartTime = LocalTime.parse(timeInput, twentyFourHrFormatter);
            } catch (DateTimeParseException e2) {
                JOptionPane.showMessageDialog(this, "Invalid time format. Please use hh:mm AM/PM or HH:mm (24hr).");
                return;
            }
        }

        // Validate time range: 7:00 AM to 2:00 PM only
        LocalTime minTime = LocalTime.of(7, 0);
        LocalTime maxTime = LocalTime.of(14, 0);
        if (localStartTime.isBefore(minTime) || localStartTime.isAfter(maxTime)) {
            JOptionPane.showMessageDialog(this, "Please select a time between 7:00 AM and 2:00 PM.");
            return;
        }

        // Convert to SQL time
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(localStartTime);
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(localStartTime.plusHours(3));

        int guestTotal = this.adults + this.children;

        // Query for available boats
        String boatQuery = "SELECT b.boat_number, b.boat_name, b.description, b.tour_price\n" +
            "FROM boat b\n" +
            "WHERE b.capacity >= ?\n" +
            "AND b.boat_id NOT IN (\n" +
            "    SELECT br.boat_id\n" +
            "    FROM boat_reservation br\n" +
            "    WHERE br.status = 'Reserved'\n" +
            "    AND br.boat_tour_date = ?\n" +
            "    AND (? < br.boat_tour_end_time AND ? > br.boat_tour_start_time)\n" +
            ")\n" +
            "ORDER BY b.tour_price ASC;";

        pst = con.prepareStatement(boatQuery);
        pst.setInt(1, guestTotal);
        pst.setDate(2, sqlDate);
        pst.setTime(3, sqlStartTime);
        pst.setTime(4, sqlEndTime);
        rs = pst.executeQuery();

        // Clear the main panel first
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(242,242,242));

        // Create content container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 35, 35));
        contentPanel.setBackground(new Color(242,242,242));

        // Add header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(242,242,242));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Available Boat Tours");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(new Color(33, 37, 41));
        
        JLabel subtitleLabel = new JLabel(
            "<html><div style='color:#6c757d; font-size:11px;'>" +
            "For " + selectedDateString + " at " + 
            localStartTime.format(DateTimeFormatter.ofPattern("hh:mm a")) + 
            "</div></html>"
        );

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        contentPanel.add(headerPanel);

        // Create cards container
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(new Color(242,242,242));
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        boolean foundBoats = false;
        while (rs.next()) {
            foundBoats = true;
            String boatNumber = rs.getString("boat_number");
            String boatName = rs.getString("boat_name");
            String description = rs.getString("description");
            double rate = rs.getDouble("tour_price");

            // Create card panel
            JPanel cardPanel = new JPanel(new BorderLayout(15, 0));
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20))
            );
            cardPanel.setBackground(Color.WHITE);
            cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Boat info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nameLabel = new JLabel(boatName);
            nameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
            nameLabel.setForeground(new Color(33, 37, 41));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            // Price label on left side
            JLabel priceLabel = new JLabel("₱" + String.format("%.2f", rate) + " per ride");
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            priceLabel.setForeground(new Color(40, 167, 69));
            priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            JLabel descLabel = new JLabel("<html><div style='width:400px; color:#495057;'>" + description + "</div></html>");
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            infoPanel.add(nameLabel);
            infoPanel.add(priceLabel);
            infoPanel.add(descLabel);

            // Smaller select button panel
            JPanel selectButtonPanel = new JPanel();
            selectButtonPanel.setLayout(new BorderLayout());
            selectButtonPanel.setBackground(new Color(13, 110, 253));
            selectButtonPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            selectButtonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            selectButtonPanel.setMaximumSize(new Dimension(120, 35));

            JLabel selectLabel = new JLabel("SELECT", SwingConstants.CENTER);
            selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            selectLabel.setForeground(Color.WHITE);
            selectButtonPanel.add(selectLabel, BorderLayout.CENTER);

            // Store boat details
            selectButtonPanel.putClientProperty("boatName", boatName);
            selectButtonPanel.putClientProperty("boatPrice", rate);

            // Add hover effects
            selectButtonPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    selectButtonPanel.setBackground(new Color(11, 94, 215));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    selectButtonPanel.setBackground(new Color(13, 110, 253));
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    String selectedBoatName = (String) selectButtonPanel.getClientProperty("boatName");
                    double selectedBoatPrice = (double) selectButtonPanel.getClientProperty("boatPrice");
                    handleBoatSelection(selectedBoatName, selectedBoatPrice);
                }
            });

            // Add components to card
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBackground(Color.WHITE);
            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(selectButtonPanel);
            rightPanel.add(Box.createVerticalGlue());

            cardPanel.add(infoPanel, BorderLayout.CENTER);
            cardPanel.add(rightPanel, BorderLayout.EAST);

            // Add card to container
            cardsContainer.add(cardPanel);
            cardsContainer.add(Box.createVerticalStrut(15));
        }

        if (!foundBoats) {
            JPanel noResultsPanel = new JPanel(new BorderLayout());
            noResultsPanel.setBackground(new Color(242,242,242));
            noResultsPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

            JLabel noResultsLabel = new JLabel("No available boats found for the selected date and time.");
            noResultsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noResultsLabel.setForeground(new Color(108, 117, 125));
            noResultsLabel.setHorizontalAlignment(JLabel.CENTER);

            noResultsPanel.add(noResultsLabel, BorderLayout.CENTER);
            cardsContainer.add(noResultsPanel);
        }

        // Modern scroll pane
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(242,242,242));
        
        // Custom scroll bar UI
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
                this.trackColor = new Color(242, 242, 242);
                this.thumbDarkShadowColor = new Color(180, 180, 180);
                this.thumbHighlightColor = new Color(220, 220, 220);
                this.thumbLightShadowColor = new Color(210, 210, 210);
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
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 5, 5);
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isDragging) {
                    g2.setColor(thumbColor.darker());
                } else if (isThumbRollover()) {
                    g2.setColor(thumbColor.brighter());
                } else {
                    g2.setColor(thumbColor);
                }
                
                g2.fillRoundRect(thumbBounds.x+1, thumbBounds.y, thumbBounds.width-2, thumbBounds.height, 5, 5);
            }
        });

        contentPanel.add(scrollPane);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Refresh UI
        mainPanel.revalidate();
        mainPanel.repaint();

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}

private void handleBoatSelection(String boatName, double boatPrice) {
   


    // Prompt the user about water activities
    int response = JOptionPane.showConfirmDialog(
            this,
            "Would you like to add " + boatName + " to your booking?",
            "Add Boat Activity",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

    boolean wantsWaterActivities = (response == JOptionPane.YES_OPTION);
   
    
    if (wantsWaterActivities) {
    try {
        // Get the selected date and parse it
        String selectedDateString = (String) dateComboBox.getSelectedItem();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)", Locale.ENGLISH);
        LocalDate selectedDate = LocalDate.parse(selectedDateString, dateFormatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

        // Get time from text field and parse it
        String timeText = txtTime.getText().trim();
        SimpleDateFormat timeFormat12hr = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat timeFormat24hr = new SimpleDateFormat("HH:mm");
        SimpleDateFormat timeFormat24hrWithAMPM = new SimpleDateFormat("HH:mm a");
        
        Date timeDate;
        
        try {
            // Try parsing with AM/PM first (12-hour format)
            timeDate = timeFormat12hr.parse(timeText);
        } catch (ParseException e1) {
            try {
                // Try parsing as 24-hour format without AM/PM
                timeDate = timeFormat24hr.parse(timeText);
            } catch (ParseException e2) {
                try {
                    // Try parsing as 24-hour format with AM/PM (unlikely but possible)
                    timeDate = timeFormat24hrWithAMPM.parse(timeText);
                } catch (ParseException e3) {
                    JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:mm (24-hour) or hh:mm a (12-hour) format.");
                    return;
                }
            }
        }

        // Convert to SQL Time
        java.sql.Time sqlStartTime = new java.sql.Time(timeDate.getTime());
        
        // Add 3 hours to get end time
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlStartTime);
        cal.add(Calendar.HOUR_OF_DAY, 3);
        java.sql.Time sqlEndTime = new java.sql.Time(cal.getTimeInMillis());

        // Proceed with creating a new instance
        new guestProcess(checkInDate, checkOutDate, roomNumber, roomType, roomDescription, 
                roomPrice, sqlDate, sqlStartTime, sqlEndTime, boatName, 
                boatPrice, adults, children, userID).setVisible(true);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error processing time: " + ex.getMessage());
        ex.printStackTrace();
    }
} else {
    new guestProcess2(checkInDate, checkOutDate, roomNumber, roomType, 
            roomDescription, roomPrice, adults, children, userID).setVisible(true);
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

        timePicker = new com.raven.swing.TimePicker();
        jPanel1 = new javax.swing.JPanel();
        panelRound5 = new GUI.PanelRound();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        panelRound3 = new GUI.PanelRound();
        jLabel12 = new javax.swing.JLabel();
        txtTime = new textfield_suggestion.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        dateComboBox = new GUI.ComboBoxSuggestion();
        panelRound4 = new GUI.PanelRound();
        mainPanel = new javax.swing.JPanel();
        panelRound1 = new GUI.PanelRound();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtHome = new javax.swing.JLabel();
        txtReservation = new javax.swing.JLabel();
        txtProfile = new javax.swing.JLabel();
        panelRound7 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();

        timePicker.setDisplayText(txtTime);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound5.setBackground(new java.awt.Color(255, 255, 255));
        panelRound5.setRoundBottomLeft(10);
        panelRound5.setRoundBottomRight(10);
        panelRound5.setRoundTopLeft(10);
        panelRound5.setRoundTopRight(10);
        panelRound5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Date");
        panelRound5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        jLabel16.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Time");
        panelRound5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, -1, -1));

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
        jLabel12.setText("SEACH");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        panelRound3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 90, 40));

        panelRound5.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, 130, 40));

        txtTime.setEditable(false);
        txtTime.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtTime.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtTime.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTimeMouseClicked(evt);
            }
        });
        txtTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimeActionPerformed(evt);
            }
        });
        panelRound5.add(txtTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, 100, -1));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/clock.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        panelRound5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 28, 40, 40));

        dateComboBox.setEditable(false);
        panelRound5.add(dateComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 180, 40));

        jPanel1.add(panelRound5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 80, 570, 80));

        panelRound4.setBackground(new java.awt.Color(242, 242, 242));
        panelRound4.setRoundTopLeft(50);
        panelRound4.setRoundTopRight(50);
        panelRound4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setBackground(new java.awt.Color(242, 242, 242));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelRound4.add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1440, 610));

        jPanel1.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 1440, 670));

        panelRound1.setBackground(new java.awt.Color(27, 59, 95));
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal copy.png"))); // NOI18N
        jLabel17.setText(" PAPAYA BEACH RESORT,");
        panelRound1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 310, 60));

        jLabel21.setFont(new java.awt.Font("Arial Rounded MT Bold", 2, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Escape to Paradise");
        panelRound1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 11, -1, 40));

        txtHome.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        txtHome.setForeground(new java.awt.Color(255, 255, 255));
        txtHome.setText("HOME");
        txtHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHomeMouseClicked(evt);
            }
        });
        panelRound1.add(txtHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 0, 40, 60));

        txtReservation.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        txtReservation.setForeground(new java.awt.Color(255, 255, 255));
        txtReservation.setText("RESERVATION");
        txtReservation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReservationMouseClicked(evt);
            }
        });
        panelRound1.add(txtReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 0, -1, 60));

        txtProfile.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        txtProfile.setForeground(new java.awt.Color(255, 255, 255));
        txtProfile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProfile.setText("PROFILE");
        txtProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProfileMouseClicked(evt);
            }
        });
        panelRound1.add(txtProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 0, 60, 60));

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

        panelRound1.add(panelRound7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jPanel1.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 160));

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

    private void panelRound3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelRound3MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
  
        SearchBoat();
        
        
        
        
        
        

    }//GEN-LAST:event_jLabel12MouseClicked

    private void txtTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimeActionPerformed
         
    }//GEN-LAST:event_txtTimeActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
 timePicker.showPopup(this, 100, 100);  
    }//GEN-LAST:event_jLabel1MouseClicked

    private void txtTimeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTimeMouseClicked
         timePicker.showPopup(this, 100, 100);  
    }//GEN-LAST:event_txtTimeMouseClicked

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
                java.util.logging.Logger.getLogger(guestSelectBoat.class.getName()).log(java.util.logging.Level.SEVERE, "Error logging logout activity", ex);
            }

            // Close the current window and open the landing page (logout action)
            this.dispose();
            new landingPage().setVisible(true);

        }catch (Exception ex) {
            // Handle any other unforeseen exceptions
            java.util.logging.Logger.getLogger(guestSelectBoat.class.getName()).log(java.util.logging.Level.SEVERE, "Unexpected error during logout", ex);
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound7MouseClicked

    }//GEN-LAST:event_panelRound7MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Date checkInDate = null;
             Date checkOutDate = null;
             int adults = 0;
             int children = 0;
             String roomNumber = "";
              String roomType = "";
                String description = "";
                double price = 0.0;
                int userID = 0;
              
            
            new guestSelectBoat(
                checkInDate, 
                checkOutDate, 
                adults, 
                children, 
                roomNumber, 
                roomType, 
                description, 
                price, 
                userID
            ).setVisible(true);
            
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private GUI.ComboBoxSuggestion dateComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel mainPanel;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound3;
    private GUI.PanelRound panelRound4;
    private GUI.PanelRound panelRound5;
    private GUI.PanelRound panelRound7;
    private com.raven.swing.TimePicker timePicker;
    private javax.swing.JLabel txtHome;
    private javax.swing.JLabel txtProfile;
    private javax.swing.JLabel txtReservation;
    private textfield_suggestion.TextFieldSuggestion txtTime;
    // End of variables declaration//GEN-END:variables
}
