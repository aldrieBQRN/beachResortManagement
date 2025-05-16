/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Guest.guestSelectRoom;
import Login.landingPage;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.toedter.calendar.JCalendar;
import java.awt.HeadlessException;
import java.awt.Image;
import java.beans.Statement;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Image;
import javax.swing.DefaultComboBoxModel;
import java.sql.DriverManager;





/**
 *
 * @author yeojvaldez
 */
public class guestProcess2 extends javax.swing.JFrame {

    private int numChildren;
    private int numAdults;
    private int totalGuests;
    private Date checkInDate;
    private Date checkOutDate;
    private String roomNumber;
    private String roomType;
    private String roomDescription;
    private double roomPrice; 
    private int userID;
    
    Connection resortCon; // Connection for beachResortManagement
    Connection locationCon; // Connection for the location database
    PreparedStatement pst;
    ResultSet rs;
    

    // Constructor to initialize guestProcess with all the parameters
     public guestProcess2(Date checkInDate, Date checkOutDate, String roomNumber, String roomType,
                         String description, double price, int adult, int numChildren, int userID){
   


        
        initComponents();  // Initialize UI components (if any)
        hoverEffect();
         pnlPayment.setVisible(false);
           DatabaseConnection(); // Initialize the connection for the resort database
        DatabaseLocationConnection(); 
            DatabaseConnection();
            loadRegions(regionCombo);
            regionCombo.setSelectedIndex(-1);
            provinceCombo.removeAllItems();
            provinceCombo.setSelectedIndex(-1);  // reset selection
            municipalCombo.removeAllItems();      // clear old data
            municipalCombo.setSelectedIndex(-1); // reset selection
            brgyCombo.removeAllItems();
            brgyCombo.setSelectedIndex(-1);

        // Store the parameters in the instance variables
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomDescription = description;
        this.roomPrice = price;
        this.numAdults = adult;
        this.numChildren = numChildren;  
        this.userID = userID;
        
      
      
       
      
        
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.ENGLISH);
    
        lblCheckIn.setText(checkInDate != null ? dateFormat.format(checkInDate) : "N/A");
        lblCheckOut.setText(checkOutDate != null ? dateFormat.format(checkOutDate) : "N/A");
        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillies);
       
        lblNumberOfNights.setText("" + numberOfNights);
        lblRoomNumber.setText(roomNumber != null ? roomNumber : "N/A");
        lblRoomNumber.setText(roomNumber != null ? roomNumber : "N/A");
        lblRoomType.setText(roomType != null ? roomType : "N/A");
        lblRoomDescription.setText(roomDescription != null ? roomDescription : "N/A");
        String reservationNumber = generateReservationNumber();
        lblReservationNumber.setText("" + reservationNumber);
        int totalGuests = numAdults + numChildren;
        String guestInfo;
        if (numChildren > 0) {
            guestInfo = numAdults + " Adult" + (numAdults > 1 ? "s" : "") + ", " + numChildren + " Child" + (numChildren > 1 ? "ren" : "");
        } else {
            guestInfo = numAdults + " Adult" + (numAdults > 1 ? "s" : "");
        }
        lblGuestInfo.setText(guestInfo);  // Make sure lblGuestInfo exists in your form
        double totalRoomPrice = roomPrice * numberOfNights;
        String formattedTotalRoomPrice = String.format("₱%.2f", totalRoomPrice);
        lblTotalRoomPrice.setText(formattedTotalRoomPrice);
       
     
        double entranceFee = 100.0 * totalGuests;
        String formattedEntranceFee = String.format("₱%.2f", entranceFee);
        lblEntranceFee.setText(formattedEntranceFee); 
        double ecologicalFee = 20.0 * totalGuests;
        String formattedEcoFee = String.format("₱%.2f", ecologicalFee);
        lblEcologicalFee.setText(formattedEcoFee);
        double grandTotal = totalRoomPrice + entranceFee + ecologicalFee;
        lblGrandTotal.setText(String.format("₱%.2f", grandTotal));
        double downPayment = grandTotal * 0.30;
        String formattedDownPayment = String.format("₱%.2f", downPayment);
        lblDownPayment.setText(formattedDownPayment);
        lblDownPayment2.setText(formattedDownPayment);
        
        checkGuestDetails();
        
            try {
            // Prepare the SQL query to fetch all room details including image
            String sql = "SELECT room_type, description, room_price, max_occupancy, room_image FROM room WHERE room_number = ?";
            pst = resortCon.prepareStatement(sql); // Use the resort database connection
            pst.setString(1, roomNumber);

            // Execute the query
            rs = pst.executeQuery();

            // Check if the room exists in the database
            if (rs.next()) {
                // Load and display the room image
                loadRoomImage(rs.getBytes("room_image"));
            } else {
                JOptionPane.showMessageDialog(this, "Room not found in database", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(guestProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
          
          regionCombo.addActionListener(e -> {
            guestProcess2.ComboItem selected = (guestProcess2.ComboItem) regionCombo.getSelectedItem();
            if (selected != null) {
                String regCode = selected.getCode();
                loadProvinces(provinceCombo, regCode);
                provinceCombo.setSelectedIndex(-1);
                municipalCombo.removeAllItems();
                municipalCombo.setSelectedIndex(-1);
                brgyCombo.removeAllItems();
                brgyCombo.setSelectedIndex(-1);
            }
        });

        provinceCombo.addActionListener(e -> {
            guestProcess2.ComboItem selected = (guestProcess2.ComboItem) provinceCombo.getSelectedItem();
            if (selected != null) {
                String provCode = selected.getCode();
                loadCities(municipalCombo, provCode);
                municipalCombo.setSelectedIndex(-1);
                brgyCombo.removeAllItems();
                brgyCombo.setSelectedIndex(-1);
            }
        });

        municipalCombo.addActionListener(e -> {
            guestProcess2.ComboItem selected = (guestProcess2.ComboItem) municipalCombo.getSelectedItem();
            if (selected != null) {
                String citymunCode = selected.getCode();
                loadBarangays(brgyCombo, citymunCode);
                brgyCombo.setSelectedIndex(-1);
            }
        });

    }

       
    

    
  


  
     public final void DatabaseConnection() {
        String url = "jdbc:mysql://localhost:3307/beachResortManagement";
        String user = "root"; // MySQL username
        String password = ""; // MySQL password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            resortCon = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to beachResortManagement database successfully!");
        } catch (SQLException e) {
            System.out.println("Error connecting to beachResortManagement database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found for beachResortManagement: " + e.getMessage());
        }
    }

    // Method to establish connection to the location database
    public final void DatabaseLocationConnection() {
        String url = "jdbc:mysql://localhost:3307/location";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            locationCon = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to location database successfully!");
        } catch (SQLException e) {
            System.out.println("Error connecting to location database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found for location database: " + e.getMessage());
        }
    }

public class ComboItem {
    private String code;
    private String description;

    public ComboItem(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return description; // Shown in combo box
    }

    public String getCode() {
        return code;
    }
}


  public void loadRegions(JComboBox<ComboItem> regionCombo) {
        try {
            regionCombo.removeAllItems();
            String sql = "SELECT regCode, regDesc FROM refregion ORDER BY regDesc";
            pst = locationCon.prepareStatement(sql); // Use the location database connection
            rs = pst.executeQuery();
            while (rs.next()) {
                String code = rs.getString("regCode");
                String desc = rs.getString("regDesc");
                regionCombo.addItem(new ComboItem(code, desc));
            }
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(guestProcess2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
  
  public void loadProvinces(JComboBox<ComboItem> provinceCombo, String regCode) {
    Connection con = null; // Declare connection here
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
        provinceCombo.removeAllItems();
        con = locationCon; // Use the class-level locationCon
        String sql = "SELECT provCode, provDesc FROM refprovince WHERE regCode = ? ORDER BY provDesc";
        pst = con.prepareStatement(sql); // Ensure you're using the locationCon
        pst.setString(1, regCode);
        rs = pst.executeQuery();
        while (rs.next()) {
            String code = rs.getString("provCode");
            String desc = rs.getString("provDesc");
            provinceCombo.addItem(new ComboItem(code, desc));
        }
    } catch (SQLException ex) {
        Logger.getLogger(guestProcess2.class.getName()).log(Level.SEVERE, null, ex);
        // Important: Consider displaying an error message to the user
        JOptionPane.showMessageDialog(this, "Error loading provinces: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Ensure resources are closed in the finally block
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Note: We are NOT closing the locationCon here, as it's managed at the class level
    }
}

    public void loadCities(JComboBox<ComboItem> cityCombo, String provCode) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
        cityCombo.removeAllItems();
        con = locationCon; // Use the class-level locationCon
        String sql = "SELECT citymunCode, citymunDesc FROM refcitymun WHERE provCode = ? ORDER BY citymunDesc";
        pst = con.prepareStatement(sql);
        pst.setString(1, provCode);
        rs = pst.executeQuery();
        while (rs.next()) {
            String code = rs.getString("citymunCode");
            String desc = rs.getString("citymunDesc");
            cityCombo.addItem(new ComboItem(code, desc));
        }
    } catch (SQLException ex) {
        Logger.getLogger(guestProcess2.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Error loading municipalities: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Note: We are NOT closing the locationCon here
    }
}

public void loadBarangays(JComboBox<ComboItem> brgyCombo, String citymunCode) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
        brgyCombo.removeAllItems();
        con = locationCon; // Use the class-level locationCon
        String sql = "SELECT brgyCode, brgyDesc FROM refbrgy WHERE citymunCode = ? ORDER BY brgyDesc";
        pst = con.prepareStatement(sql);
        pst.setString(1, citymunCode);
        rs = pst.executeQuery();
        while (rs.next()) {
            String code = rs.getString("brgyCode");
            String desc = rs.getString("brgyDesc");
            brgyCombo.addItem(new ComboItem(code, desc));
        }
    } catch (SQLException ex) {
        Logger.getLogger(guestProcess2.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Error loading barangays: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Note: We are NOT closing the locationCon here
    }
}


   

    

    
    
   public String generateReservationNumber() {
        String reservationNumber = "";

        // Ensure that the connection is not null
        if (resortCon == null) {
            System.out.println("Resort database connection is not initialized!");
            return reservationNumber;
        }

        try {
            // Start with generating the initial reservation number
            reservationNumber = "RES-" + getCurrentDateString() + "-001";

            // Query to check if the generated reservation number already exists
            String query = "SELECT COUNT(*) FROM room_reservation WHERE reservation_number = ?";
            pst = resortCon.prepareStatement(query); // Use the resort database connection

            while (true) {
                // Set the generated reservation number
                pst.setString(1, reservationNumber);

                // Execute query to check if the number already exists
                rs = pst.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) {
                        // If the count is zero, the number is unique
                        break;
                    } else {
                        // If the reservation number exists, increment the number part and try again
                        String lastNumberPart = reservationNumber.substring(reservationNumber.lastIndexOf("-") + 1);
                        int lastNumber = Integer.parseInt(lastNumberPart);
                        lastNumber++;
                        reservationNumber = "RES-" + getCurrentDateString() + "-" + String.format("%03d", lastNumber);
                    }
                }
            }
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservationNumber;
    }
   
   public void checkGuestDetails() {
        try {
            String sql = "SELECT * FROM user_details WHERE user_id = ?";
            pst = resortCon.prepareStatement(sql); // Use the resort database connection
            pst.setInt(1, userID);
            rs = pst.executeQuery();

            if (rs.next()) {
                // Get values from DB
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String contact = rs.getString("phone");
             

                // Split name
                String[] names = fullName.split(" ", 2);
                if (names.length > 0) txtFName.setText(names[0]);  // First name
                if (names.length > 1) txtLName.setText(names[1]);  // Last name (if exists)

                // Set other fields
                txtEmail.setText(email);
                txtContact.setText(contact);

            }
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
       
   
    // Get the current date in the format YYYYMMDD
    private String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(new Date());
    }
    
    

      
    private void loadRoomImage(byte[] imageData) {
    try {
        if (imageData == null || imageData.length == 0) {
            labelDisplayImage.setIcon(null);
            return;
        }

        ImageIcon originalIcon = new ImageIcon(imageData);
        Image scaledImage = originalIcon.getImage()
            .getScaledInstance(labelDisplayImage.getWidth(), 
                            labelDisplayImage.getHeight(), 
                            Image.SCALE_SMOOTH);
        labelDisplayImage.setIcon(new ImageIcon(scaledImage));
    } catch (Exception e) {
        labelDisplayImage.setIcon(null);
        System.err.println("Error loading image: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        txtEmail = new textfield.TextField();
        txtContact = new textfield.TextField();
        txtLName = new textfield.TextField();
        txtFName = new textfield.TextField();
        panelRound4 = new GUI.PanelRound();
        lbl = new javax.swing.JLabel();
        regionCombo = new GUI.Combobox();
        jLabel9 = new javax.swing.JLabel();
        provinceCombo = new GUI.Combobox();
        municipalCombo = new GUI.Combobox();
        brgyCombo = new GUI.Combobox();
        jLabel10 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        lblRoomNumber = new javax.swing.JLabel();
        lblRoomType = new javax.swing.JLabel();
        lblRoomDescription = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        labelDisplayImage = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblNumberOfNights = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblCheckOut = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblCheckIn = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblTotalRoomPrice = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        lblEcologicalFee = new javax.swing.JLabel();
        lblEntranceFee = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblGrandTotal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblDownPayment = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        lblReservationNumber = new javax.swing.JLabel();
        lblGuestInfo = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        pnlPayment = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        paymentMethodComboBox = new rojerusan.RSComboMetro();
        lblDownPayment2 = new javax.swing.JLabel();
        txtmessage = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        panelRound5 = new GUI.PanelRound();
        lblPayment = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        panelRound1 = new GUI.PanelRound();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtHome = new javax.swing.JLabel();
        txtReservation = new javax.swing.JLabel();
        txtProfile = new javax.swing.JLabel();
        panelRound6 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(242, 242, 242));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtEmail.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtEmail.setLabelText("Email");
        jPanel14.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 790, 45));

        txtContact.setBackground(new java.awt.Color(255, 255, 255));
        txtContact.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtContact.setLabelText("Contact Number");
        txtContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContactActionPerformed(evt);
            }
        });
        jPanel14.add(txtContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 790, 45));

        txtLName.setBackground(new java.awt.Color(255, 255, 255));
        txtLName.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtLName.setLabelText("Last Name");
        jPanel14.add(txtLName, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 60, 380, 45));

        txtFName.setBackground(new java.awt.Color(255, 255, 255));
        txtFName.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtFName.setLabelText("First Name");
        jPanel14.add(txtFName, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 380, 45));

        panelRound4.setBackground(new java.awt.Color(0, 153, 255));
        panelRound4.setRoundBottomLeft(20);
        panelRound4.setRoundBottomRight(20);
        panelRound4.setRoundTopLeft(20);
        panelRound4.setRoundTopRight(20);
        panelRound4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lbl.setForeground(new java.awt.Color(255, 255, 255));
        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl.setText("Procced to Payment");
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMouseClicked(evt);
            }
        });
        panelRound4.add(lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 770, 40));

        jPanel14.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 790, 40));

        regionCombo.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        regionCombo.setLabeText("Region");
        regionCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regionComboActionPerformed(evt);
            }
        });
        jPanel14.add(regionCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 280, 190, -1));

        jLabel9.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Address Details");
        jPanel14.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, -1, -1));

        provinceCombo.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        provinceCombo.setLabeText("Province");
        jPanel14.add(provinceCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 280, 190, -1));

        municipalCombo.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        municipalCombo.setLabeText("Municipal");
        jPanel14.add(municipalCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 280, 190, -1));

        brgyCombo.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        brgyCombo.setLabeText("Barangay");
        jPanel14.add(brgyCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 280, 180, -1));

        jLabel10.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Lead Guest Details");
        jPanel14.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jPanel11.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 870, 410));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Inclution:");
        jPanel12.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, 120, -1));

        lblRoomNumber.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        lblRoomNumber.setForeground(new java.awt.Color(0, 0, 0));
        lblRoomNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRoomNumber.setText("Null");
        jPanel12.add(lblRoomNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 70, -1));

        lblRoomType.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        lblRoomType.setForeground(new java.awt.Color(0, 0, 0));
        lblRoomType.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRoomType.setText("Null");
        jPanel12.add(lblRoomType, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 120, -1));

        lblRoomDescription.setForeground(new java.awt.Color(0, 0, 0));
        lblRoomDescription.setText("jLabel16");
        jPanel12.add(lblRoomDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 80, 160, 120));

        jLabel39.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(0, 0, 0));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel39.setText("Number: ");
        jPanel12.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 120, -1));

        jLabel41.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(0, 0, 0));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Category:");
        jPanel12.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 120, -1));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel5.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                jPanel5ComponentAdded(evt);
            }
        });
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelDisplayImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDisplayImage.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel5.add(labelDisplayImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 180));

        jPanel12.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 240, 180));

        jPanel11.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 130, 470, 220));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNumberOfNights.setBackground(new java.awt.Color(0, 0, 0));
        lblNumberOfNights.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblNumberOfNights.setForeground(new java.awt.Color(0, 0, 0));
        lblNumberOfNights.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfNights.setText("2");
        jPanel8.add(lblNumberOfNights, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 50, -1));

        jLabel18.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Check-out");
        jPanel8.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 120, -1));

        lblCheckOut.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblCheckOut.setForeground(new java.awt.Color(0, 0, 0));
        lblCheckOut.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCheckOut.setText("Thu, April 22");
        jPanel8.add(lblCheckOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 120, -1));

        jLabel20.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(102, 102, 102));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Nigths");
        jPanel8.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 50, -1));

        lblCheckIn.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblCheckIn.setForeground(new java.awt.Color(0, 0, 0));
        lblCheckIn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCheckIn.setText("Thu, April 22");
        jPanel8.add(lblCheckIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 120, -1));

        jLabel22.setBackground(new java.awt.Color(153, 153, 153));
        jLabel22.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(102, 102, 102));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Check-in");
        jPanel8.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 130, -1));

        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setText(">>>");
        jPanel8.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, -1, 40));

        jPanel11.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 30, 470, 90));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotalRoomPrice.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblTotalRoomPrice.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalRoomPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalRoomPrice.setText("00.00");
        jPanel6.add(lblTotalRoomPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 70, 110, -1));

        jLabel35.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 0, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Others:");
        jPanel6.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 150, -1));

        lblEcologicalFee.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblEcologicalFee.setForeground(new java.awt.Color(0, 0, 0));
        lblEcologicalFee.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEcologicalFee.setText("00.00");
        jPanel6.add(lblEcologicalFee, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 110, -1));

        lblEntranceFee.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblEntranceFee.setForeground(new java.awt.Color(0, 0, 0));
        lblEntranceFee.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEntranceFee.setText("00.00");
        jPanel6.add(lblEntranceFee, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, 110, -1));

        jLabel45.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 0, 0));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setText("Total Room Price");
        jPanel6.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 110, -1));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGrandTotal.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        lblGrandTotal.setForeground(new java.awt.Color(0, 0, 0));
        lblGrandTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrandTotal.setText("00.00");
        jPanel7.add(lblGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 100, -1));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Total Price");
        jPanel7.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jLabel12.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Downpayment");
        jPanel7.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        lblDownPayment.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblDownPayment.setForeground(new java.awt.Color(0, 0, 0));
        lblDownPayment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDownPayment.setText("00.00");
        jPanel7.add(lblDownPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 100, -1));

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 470, 60));

        jLabel42.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 0, 0));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("Total Ecological Fee");
        jPanel6.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 190, -1));

        jLabel47.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(0, 0, 0));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel47.setText("Total Entrance Fee");
        jPanel6.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, 190, -1));

        lblReservationNumber.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblReservationNumber.setForeground(new java.awt.Color(0, 0, 0));
        lblReservationNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblReservationNumber.setText("0000001");
        jPanel6.add(lblReservationNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 270, -1));

        lblGuestInfo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblGuestInfo.setForeground(new java.awt.Color(0, 0, 0));
        lblGuestInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGuestInfo.setText("2 Adults, 1 Children");
        jPanel6.add(lblGuestInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 140, -1));

        jLabel46.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 0, 0));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setText("Guest Count:");
        jPanel6.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 90, -1));

        jLabel48.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(0, 0, 0));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel48.setText("Reservation Number:");
        jPanel6.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 130, -1));

        jPanel11.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 360, 470, 250));

        pnlPayment.setBackground(new java.awt.Color(255, 255, 255));
        pnlPayment.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        pnlPayment.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Digital Payment");
        pnlPayment.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 60));

        paymentMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GCash", "Maya", "PayPal" }));
        paymentMethodComboBox.setColorArrow(new java.awt.Color(27, 59, 95));
        paymentMethodComboBox.setColorBorde(new java.awt.Color(39, 114, 160));
        paymentMethodComboBox.setColorFondo(new java.awt.Color(39, 114, 160));
        paymentMethodComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                paymentMethodComboBoxItemStateChanged(evt);
            }
        });
        paymentMethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentMethodComboBoxActionPerformed(evt);
            }
        });
        pnlPayment.add(paymentMethodComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 790, 30));

        lblDownPayment2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        lblDownPayment2.setForeground(new java.awt.Color(102, 102, 102));
        lblDownPayment2.setText("₱ 5,236.85");
        pnlPayment.add(lblDownPayment2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, 310, -1));

        txtmessage.setForeground(new java.awt.Color(102, 102, 102));
        txtmessage.setText("You have chosen to pay by GCash. You will be forwarded to the GCash website to proceed with this transaction.");
        pnlPayment.add(txtmessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 800, -1));

        jLabel11.setForeground(new java.awt.Color(102, 102, 102));
        jLabel11.setText("The total amount to be pay is:");
        pnlPayment.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 180, -1));

        panelRound5.setBackground(new java.awt.Color(0, 153, 255));
        panelRound5.setRoundBottomLeft(20);
        panelRound5.setRoundBottomRight(20);
        panelRound5.setRoundTopLeft(20);
        panelRound5.setRoundTopRight(20);
        panelRound5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPayment.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblPayment.setForeground(new java.awt.Color(255, 255, 255));
        lblPayment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPayment.setText("Pay with GCash");
        lblPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPaymentMouseClicked(evt);
            }
        });
        panelRound5.add(lblPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 770, 40));

        pnlPayment.add(panelRound5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 790, 40));

        jPanel11.add(pnlPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 870, 250));

        jPanel1.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1440, 730));

        jPanel4.setBackground(new java.awt.Color(39, 114, 160));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("© 2025 Papaya Beach Resort. All rights reserved.");
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 13, -1, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 790, 1440, 40));

        panelRound1.setBackground(new java.awt.Color(27, 59, 95));
        panelRound1.setRoundBottomLeft(50);
        panelRound1.setRoundBottomRight(50);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal copy.png"))); // NOI18N
        jLabel17.setText(" PAPAYA BEACH RESORT,");
        panelRound1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 310, 60));

        jLabel19.setFont(new java.awt.Font("Arial Rounded MT Bold", 2, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Escape to Paradise");
        panelRound1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 11, -1, 40));

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

        panelRound6.setBackground(new java.awt.Color(0, 153, 255));
        panelRound6.setRoundBottomLeft(20);
        panelRound6.setRoundBottomRight(20);
        panelRound6.setRoundTopLeft(20);
        panelRound6.setRoundTopRight(20);
        panelRound6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRound6MouseClicked(evt);
            }
        });
        panelRound6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Logout");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        panelRound6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        panelRound1.add(panelRound6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jPanel1.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1440, 720));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked

       

    }//GEN-LAST:event_jLabel10MouseClicked

    private void panelRound1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound1MouseClicked
       guestHome gh = new guestHome(userID);  // Ensure that guestHome is a valid class
    gh.setVisible(true);
    }//GEN-LAST:event_panelRound1MouseClicked

    private void paymentMethodComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_paymentMethodComboBoxItemStateChanged
         String selectedPaymentMethod = (String) paymentMethodComboBox.getSelectedItem();

       if (selectedPaymentMethod != null) {
        if (selectedPaymentMethod.equals("GCash")) {
            // Action for GCash
            txtmessage.setText("You have chosen to pay by GCash. You will be forwarded to the GCash website to proceed with this transaction.");
             lblPayment.setText("Pay with GCash");
        } else if (selectedPaymentMethod.equals("Maya")) {
            // Action for Maya
            txtmessage.setText("You have chosen to pay by Maya. You will be forwarded to the Maya website to proceed with this transaction.");
             lblPayment.setText("Pay with Maya");
        } else if (selectedPaymentMethod.equals("PayPal")) {
            // Action for PayMaya
            txtmessage.setText("You have chosen to pay by PayPal. You will be forwarded to the PayPal website to proceed with this transaction.");
             lblPayment.setText("Pay with PayPal");
        } else {
            // Handle the case where no valid payment method is selected
            txtmessage.setText("Invalid Payment Method");
        }
    }
    }//GEN-LAST:event_paymentMethodComboBoxItemStateChanged

    private void paymentMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentMethodComboBoxActionPerformed

    }//GEN-LAST:event_paymentMethodComboBoxActionPerformed

    private void lblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMouseClicked
          // Get form field values
    String reservationNumber = lblReservationNumber.getText();
    String guestName = txtFName.getText().trim() + " " + txtLName.getText().trim();
    String email = txtEmail.getText().trim();
    String contact = txtContact.getText().trim();

    // Get the description from the selected ComboItem
    String region = (regionCombo.getSelectedItem() != null) ? regionCombo.getSelectedItem().toString() : "";
    String province = (provinceCombo.getSelectedItem() != null) ? provinceCombo.getSelectedItem().toString() : "";
    String municipality = (municipalCombo.getSelectedItem() != null) ? municipalCombo.getSelectedItem().toString() : "";
    String barangay = (brgyCombo.getSelectedItem() != null) ? brgyCombo.getSelectedItem().toString() : "";

    // Basic validation
    if (reservationNumber.isEmpty() || txtFName.getText().trim().isEmpty() ||
        txtLName.getText().trim().isEmpty() || email.isEmpty() ||
        contact.isEmpty() || region.isEmpty() || province.isEmpty() || municipality.isEmpty() || barangay.isEmpty()) {

        JOptionPane.showMessageDialog(this, "Please fill in all the required fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
        return; // Stop here if validation fails
    }

    // Additional validation: Email format check
    if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9s_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
        JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Additional validation: Contact number length check (assuming a 10-digit number)
    if (!contact.matches("^\\d{10,15}$")) {
        JOptionPane.showMessageDialog(this, "Phone must be 10-15 digits!", 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // If all validations pass, show the payment panel
    pnlPayment.setVisible(true);  // Show the payment panel only if validation passes


    }//GEN-LAST:event_lblMouseClicked

    private void lblPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPaymentMouseClicked
      String reservationNumber = lblReservationNumber.getText();
String guestName = txtFName.getText() + " " + txtLName.getText();
String email = txtEmail.getText();
String contact = txtContact.getText();
  // Get the description from the selected ComboItem
    String region = (regionCombo.getSelectedItem() != null) ? regionCombo.getSelectedItem().toString() : "";
    String province = (provinceCombo.getSelectedItem() != null) ? provinceCombo.getSelectedItem().toString() : "";
    String municipality = (municipalCombo.getSelectedItem() != null) ? municipalCombo.getSelectedItem().toString() : "";
    String barangay = (brgyCombo.getSelectedItem() != null) ? brgyCombo.getSelectedItem().toString() : "";

    String address = barangay + ", " + municipality + ", " + province + ", " + region;

double totalPrice = roomPrice * TimeUnit.MILLISECONDS.toDays(checkOutDate.getTime() - checkInDate.getTime())
        + (100.0 + 20.0) * (numAdults + numChildren);
double downPayment = totalPrice * 0.30;

String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();

if ("GCash".equalsIgnoreCase(paymentMethod)) {
    new guestGcashPayment(
        checkInDate,
        checkOutDate,
        roomNumber,
        roomType,
        roomDescription,
        roomPrice,
        numAdults,
        numChildren,
        userID,
        totalPrice,
        downPayment,
        guestName,
        email,
        contact,
        address,
        reservationNumber
    ).setVisible(true);
} else if ("Maya".equalsIgnoreCase(paymentMethod)) {
    new guestMayaPayment(
        checkInDate,
        checkOutDate,
        roomNumber,
        roomType,
        roomDescription,
        roomPrice,
        numAdults,
        numChildren,
        userID,
        totalPrice,
        downPayment,
        guestName,
        email,
        contact,
        address,
        reservationNumber
    ).setVisible(true);
} else if ("Paypal".equalsIgnoreCase(paymentMethod)) {
    new guestPaypalPayment(
        checkInDate,
        checkOutDate,
        roomNumber,
        roomType,
        roomDescription,
        roomPrice,
        numAdults,
        numChildren,
        userID,
        totalPrice,
        downPayment,
        guestName,
        email,
        contact,
        address,
        reservationNumber
    ).setVisible(true);
} else {
    JOptionPane.showMessageDialog(this, "Please select a valid payment method.");
}

    }//GEN-LAST:event_lblPaymentMouseClicked

    private void txtContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContactActionPerformed

    private void jPanel5ComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_jPanel5ComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel5ComponentAdded

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
            try (PreparedStatement pst = resortCon.prepareStatement(logSql)) { // Use resortCon
                pst.setInt(1, userID);  // Assuming userID is available after login
                pst.setString(2, "LOGOUT");
                pst.setString(3, "User logged out successfully");

                // Execute the update to log the action
                pst.executeUpdate();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(guestProcess2.class.getName()).log(java.util.logging.Level.SEVERE, "Error logging logout activity", ex);
            }

            // Close the current window and open the landing page (logout action)
            this.dispose();
            new landingPage().setVisible(true);

        } catch (Exception ex) {
            // Handle any other unforeseen exceptions
            java.util.logging.Logger.getLogger(guestProcess2.class.getName()).log(java.util.logging.Level.SEVERE, "Unexpected error during logout", ex);
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound6MouseClicked

    }//GEN-LAST:event_panelRound6MouseClicked

    private void regionComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regionComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_regionComboActionPerformed

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
            java.util.logging.Logger.getLogger(guestProcess2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestProcess2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestProcess2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestProcess2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            
           Date checkInDate = new Date(); // Example: current date
            Date checkOutDate = new Date(System.currentTimeMillis() + 86400000L); // Example: 1 day after check-in date
            String roomNumber = "101";
            String roomType = "Deluxe";
            String roomDescription = "Ocean view room with modern amenities";
            double roomPrice = 200.00;
            int numAdult = 2;       // Example values
            int numChildren = 1;  
            int userID = 12345;  // Example values

            // Pass the initialized data to the guestProcess2 constructor
            new guestProcess2(checkInDate, checkOutDate, roomNumber, roomType, roomDescription, 
                              roomPrice, numAdult, numChildren, userID).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private GUI.Combobox brgyCombo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel labelDisplayImage;
    private javax.swing.JLabel lbl;
    private javax.swing.JLabel lblCheckIn;
    private javax.swing.JLabel lblCheckOut;
    private javax.swing.JLabel lblDownPayment;
    private javax.swing.JLabel lblDownPayment2;
    private javax.swing.JLabel lblEcologicalFee;
    private javax.swing.JLabel lblEntranceFee;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblGuestInfo;
    private javax.swing.JLabel lblNumberOfNights;
    private javax.swing.JLabel lblPayment;
    private javax.swing.JLabel lblReservationNumber;
    private javax.swing.JLabel lblRoomDescription;
    private javax.swing.JLabel lblRoomNumber;
    private javax.swing.JLabel lblRoomType;
    private javax.swing.JLabel lblTotalRoomPrice;
    private GUI.Combobox municipalCombo;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound4;
    private GUI.PanelRound panelRound5;
    private GUI.PanelRound panelRound6;
    private rojerusan.RSComboMetro paymentMethodComboBox;
    private javax.swing.JPanel pnlPayment;
    private GUI.Combobox provinceCombo;
    private GUI.Combobox regionCombo;
    private textfield.TextField txtContact;
    private textfield.TextField txtEmail;
    private textfield.TextField txtFName;
    private javax.swing.JLabel txtHome;
    private textfield.TextField txtLName;
    private javax.swing.JLabel txtProfile;
    private javax.swing.JLabel txtReservation;
    private javax.swing.JLabel txtmessage;
    // End of variables declaration//GEN-END:variables
}
