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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



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
    

    // Constructor to initialize guestProcess with all the parameters
     public guestProcess2(Date checkInDate, Date checkOutDate, String roomNumber, String roomType,
                         String description, double price, int adult, int numChildren, int userID){
   


        
        initComponents();  // Initialize UI components (if any)
         pnlPayment.setVisible(false);
        DatabaseConnection();

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
            pst = con.prepareStatement(sql);
            pst.setString(1, roomNumber);
            
            // Execute the query
            rs = pst.executeQuery();

            // Check if the room exists in the database
            if (rs.next()) {


                // Load and display the room image
                loadRoomImage(rs.getBytes("room_image"));
            } else {
                JOptionPane.showMessageDialog(this, "Room not found in database", "Error", JOptionPane.WARNING_MESSAGE);

}       } catch (SQLException ex) {   
            Logger.getLogger(guestProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    
   public String generateReservationNumber() {
    String reservationNumber = "";
    
    // Ensure that the connection is not null
    if (con == null) {
        System.out.println("Database connection is not initialized!");
        return reservationNumber;
    }

    try {
        // Start with generating the initial reservation number
        reservationNumber = "RES-" + getCurrentDateString() + "-001";

        // Query to check if the generated reservation number already exists
        String query = "SELECT COUNT(*) FROM room_reservation WHERE reservation_number = ?";
        PreparedStatement pst = con.prepareStatement(query);
        
        while (true) {
            // Set the generated reservation number
            pst.setString(1, reservationNumber);
            
            // Execute query to check if the number already exists
            ResultSet rs = pst.executeQuery();
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
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return reservationNumber;
}

   
   
       public final void checkGuestDetails() {
  
    
    try {
       
        String sql = "SELECT * FROM guest WHERE user_id = ?";
        pst = con.prepareStatement(sql);
        pst.setInt(1, userID);
        rs = pst.executeQuery();
        
        if (rs.next()) {
            // Guest exists, populate the fields
            String fullName = rs.getString("guest_name");
            String email = rs.getString("email");
            String contact = rs.getString("contact");
            String address = rs.getString("address");
            
            // Split name into first and last name if possible
            String[] names = fullName.split(" ", 2);
            if (names.length > 0) txtFName.setText(names[0]);
            if (names.length > 1) txtLName.setText(names[1]);
            
            txtEmail.setText(email);
            txtContact.setText(contact);
            txtAddress.setText(address);
           
        }
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
        jLabel4 = new javax.swing.JLabel();
        txtAddress = new textfield.TextField();
        txtEmail = new textfield.TextField();
        txtContact = new textfield.TextField();
        txtLName = new textfield.TextField();
        txtFName = new textfield.TextField();
        panelRound4 = new GUI.PanelRound();
        lbl = new javax.swing.JLabel();
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
        panelRound1 = new GUI.PanelRound();
        panelRound2 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
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

        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Who's the lead Guest?");
        jPanel14.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        txtAddress.setBackground(new java.awt.Color(255, 255, 255));
        txtAddress.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtAddress.setLabelText("Address");
        txtAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressActionPerformed(evt);
            }
        });
        jPanel14.add(txtAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, 780, 45));

        txtEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtEmail.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtEmail.setLabelText("Email");
        jPanel14.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 790, 45));

        txtContact.setBackground(new java.awt.Color(255, 255, 255));
        txtContact.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtContact.setLabelText("Contact Number");
        txtContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContactActionPerformed(evt);
            }
        });
        jPanel14.add(txtContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 790, 45));

        txtLName.setBackground(new java.awt.Color(255, 255, 255));
        txtLName.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtLName.setLabelText("Last Name");
        jPanel14.add(txtLName, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 80, 380, 45));

        txtFName.setBackground(new java.awt.Color(255, 255, 255));
        txtFName.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtFName.setLabelText("First Name");
        jPanel14.add(txtFName, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 380, 45));

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

        jPanel14.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 335, 790, 40));

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

        panelRound1.setBackground(new java.awt.Color(27, 59, 95));
        panelRound1.setRoundBottomLeft(50);
        panelRound1.setRoundBottomRight(50);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Welcome,");
        panelRound1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 180, 60));

        jLabel21.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("to Papaya Beach Resort");
        panelRound1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, -1, 30));

        jLabel25.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("HOME");
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 0, -1, 60));

        jLabel27.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("RESERVATION");
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 0, -1, 60));

        jLabel26.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("PROFILE");
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel26MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 0, 60, 60));

        jPanel1.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 60));

        jPanel4.setBackground(new java.awt.Color(39, 114, 160));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("© 2025 Papaya Beach Resort. All rights reserved.");
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 13, -1, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 790, 1440, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1440, 720));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked

       

    }//GEN-LAST:event_jLabel10MouseClicked

    private void txtAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressActionPerformed

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

        // TODO add your handling code here:
    }//GEN-LAST:event_paymentMethodComboBoxItemStateChanged

    private void paymentMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentMethodComboBoxActionPerformed

    }//GEN-LAST:event_paymentMethodComboBoxActionPerformed

    private void lblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMouseClicked
            String reservationNumber = lblReservationNumber.getText();
            String guestName = txtFName.getText().trim() + " " + txtLName.getText().trim();
            String email = txtEmail.getText().trim();
            String contact = txtContact.getText().trim();
            String address = txtAddress.getText().trim();

            // Basic validation
            if (reservationNumber.isEmpty() || txtFName.getText().trim().isEmpty() || 
                txtLName.getText().trim().isEmpty() || email.isEmpty() || 
                contact.isEmpty() || address.isEmpty()) {

                JOptionPane.showMessageDialog(this, "Please fill in all the required fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return; // Stop here if validation fails
            }

            // You can add more checks (e.g., email format, contact number length) if needed

            pnlPayment.setVisible(true); // Show payment panel only if validation passes
    }//GEN-LAST:event_lblMouseClicked

    private void lblPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPaymentMouseClicked
      String reservationNumber = lblReservationNumber.getText();
String guestName = txtFName.getText() + " " + txtLName.getText();
String email = txtEmail.getText();
String contact = txtContact.getText();
String address = txtAddress.getText();

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

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        this.dispose();
        new landingPage().setVisible(true);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked

    }//GEN-LAST:event_panelRound2MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        new guestHome(userID).setVisible(true);
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
        new guestReservation(userID).setVisible(true);
    }//GEN-LAST:event_jLabel27MouseClicked

    private void jLabel26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseClicked
        guestProfile user = new guestProfile(userID);
        user.setVisible(true);
    }//GEN-LAST:event_jLabel26MouseClicked

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
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
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
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound2;
    private GUI.PanelRound panelRound4;
    private GUI.PanelRound panelRound5;
    private rojerusan.RSComboMetro paymentMethodComboBox;
    private javax.swing.JPanel pnlPayment;
    private textfield.TextField txtAddress;
    private textfield.TextField txtContact;
    private textfield.TextField txtEmail;
    private textfield.TextField txtFName;
    private textfield.TextField txtLName;
    private javax.swing.JLabel txtmessage;
    // End of variables declaration//GEN-END:variables
}
