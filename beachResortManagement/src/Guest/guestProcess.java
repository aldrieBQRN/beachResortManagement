/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Guest.guestSelectRoom;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.raven.datechooser.EventDateChooser;
import com.raven.datechooser.SelectedAction;
import com.raven.datechooser.SelectedDate;
import com.toedter.calendar.JCalendar;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



/**
 *
 * @author yeojvaldez
 */
public class guestProcess extends javax.swing.JFrame {

   private Date checkInDate;
    private Date checkOutDate;
    private String roomNumber;
    private String roomType;
    private String roomDescription;
    private double roomPrice;
    private java.sql.Date sqlDate;
    private java.sql.Time sqlStartTime;
    private java.sql.Time sqlEndTime;
    private String boatName;
    private double boatPrice;

    // Constructor to initialize guestProcess with all the parameters
    public guestProcess(Date checkInDate, Date checkOutDate, String roomNumber, String roomType, 
                        String roomDescription, double roomPrice, java.sql.Date sqlDate, 
                        java.sql.Time sqlStartTime, java.sql.Time sqlEndTime, String boatName, 
                        double boatPrice) {
        
        initComponents();  // Initialize UI components (if any)

        // Store the parameters in the instance variables
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomDescription = roomDescription;
        this.roomPrice = roomPrice;
        this.sqlDate = sqlDate;
        this.sqlStartTime = sqlStartTime;
        this.sqlEndTime = sqlEndTime;
        this.boatName = boatName;
        this.boatPrice = boatPrice;
        
       SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); // "hh" for 12-hour format with leading zero, "a" for AM/PM


// Format the check-in and check-out dates
        lblCheckIn.setText(checkInDate != null ? dateFormat.format(checkInDate) : "N/A");
        lblCheckOut.setText(checkOutDate != null ? dateFormat.format(checkOutDate) : "N/A");
        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();

        // Convert the difference from milliseconds to days
        long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillies);

        // Display the number of nights (you can set it to a label or print it)
        System.out.println("Number of Nights: " + numberOfNights);
        lblNumberOfNights.setText("Number of Nights: " + numberOfNights);

    // Format and display the room details
    lblRoomNumber.setText(roomNumber != null ? roomNumber : "N/A");
        lblRoomNumber.setText(roomNumber != null ? roomNumber : "N/A");
        lblRoomType.setText(roomType != null ? roomType : "N/A");
        lblRoomDescription.setText(roomDescription != null ? roomDescription : "N/A");
        lblBoatName.setText(boatName != null ? boatName : "N/A");
        lblSqlDate.setText(sqlDate != null ? dateFormat.format(sqlDate) : "N/A");
        lblStartTime.setText(sqlStartTime != null ? timeFormat.format(sqlStartTime) : "N/A");
        lblEndTime.setText(sqlEndTime != null ? timeFormat.format(sqlEndTime) : "N/A");

        
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
    
    
    private void searchAvailableRooms(Date checkIn, Date checkOut, int totalGuests, int adults, int children) {
    try {
        java.sql.Date sqlCheckIn = new java.sql.Date(checkIn.getTime());
        java.sql.Date sqlCheckOut = new java.sql.Date(checkOut.getTime());
        

        // Fixed SQL query
        String query = "SELECT r.room_number, r.room_type, r.max_occupancy, r.room_price " +
                       "FROM room r " +
                       "WHERE r.max_occupancy >= ? " +
                       "AND r.room_number NOT IN (" +
                       "   SELECT room_number FROM room_reservation " +
                       "   WHERE (? < check_out_date AND ? > check_in_date)" +
                       ") " + // <== don't forget to close subquery
                       "ORDER BY r.room_price ASC";

        pst = con.prepareStatement(query);
        pst.setInt(1, totalGuests);
        pst.setDate(2, sqlCheckIn);
        pst.setDate(3, sqlCheckOut);

        rs = pst.executeQuery();

        boolean found = false;

        while (rs.next()) {
            found = true;
            String roomNumber = rs.getString("room_number");
            String roomType = rs.getString("room_type");
            int maxOccupancy = rs.getInt("max_occupancy");
            double price = rs.getDouble("room_price");

            // You can display the result in a table or console for now
            System.out.println("Room: " + roomNumber + " | Type: " + roomType + " | Capacity: " + maxOccupancy + " | ₱" + price);
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No available rooms found.");
        } else {
             
            new guestSelectRoom(checkIn, checkOut, adults, children).setVisible(true);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
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
        jPanel4 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        rSButtonHover1 = new rojeru_san.complementos.RSButtonHover();
        cmbRoomType = new rojerusan.RSComboMetro();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblBoatName = new javax.swing.JLabel();
        lblSqlDate = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblStartTime = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblEndTime = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        txtRoomPrice = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txtEcological = new javax.swing.JLabel();
        txtWaterActivityPrice = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtEntrace = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        textReservation = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblNumberOfNights = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblCheckOut = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblCheckIn = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        lblRoomNumber = new javax.swing.JLabel();
        lblRoomType = new javax.swing.JLabel();
        lblRoomDescription = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        textField4 = new textfield.TextField();
        textField5 = new textfield.TextField();
        textField2 = new textfield.TextField();
        textField3 = new textfield.TextField();
        textField6 = new textfield.TextField();
        textField7 = new textfield.TextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        jPanel11.setBackground(new java.awt.Color(242, 242, 242));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Digital Payment");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 30));

        rSButtonHover1.setText("Pay with Gcash");
        jPanel2.add(rSButtonHover1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 820, -1));

        cmbRoomType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gcash", "Maya", "PayPal", " " }));
        cmbRoomType.setColorArrow(new java.awt.Color(27, 59, 95));
        cmbRoomType.setColorBorde(new java.awt.Color(39, 114, 160));
        cmbRoomType.setColorFondo(new java.awt.Color(39, 114, 160));
        cmbRoomType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRoomTypeActionPerformed(evt);
            }
        });
        jPanel2.add(cmbRoomType, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 820, 30));

        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("The total amount you will be charged is: ₱ 5,236.85");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 810, -1));

        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("You have chosen to pay by GCash. You will be forwarded to the GCash website to proceed with this transaction.");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 810, -1));

        jPanel11.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, 870, 220));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBoatName.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblBoatName.setForeground(new java.awt.Color(0, 0, 0));
        lblBoatName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBoatName.setText("Default");
        jPanel3.add(lblBoatName, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 130, -1));

        lblSqlDate.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblSqlDate.setForeground(new java.awt.Color(0, 0, 0));
        lblSqlDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSqlDate.setText("Thu, April 22");
        jPanel3.add(lblSqlDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 110, -1));

        jLabel15.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Date");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 110, -1));

        jLabel25.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(102, 102, 102));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Boat Name");
        jPanel3.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 90, -1));

        lblStartTime.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblStartTime.setForeground(new java.awt.Color(0, 0, 0));
        lblStartTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStartTime.setText("00:00 AM");
        jPanel3.add(lblStartTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 20, 70, -1));

        jLabel27.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Start Time");
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 70, -1));

        lblEndTime.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblEndTime.setForeground(new java.awt.Color(0, 0, 0));
        lblEndTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEndTime.setText("00:00 AM");
        jPanel3.add(lblEndTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 70, -1));

        jLabel29.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(102, 102, 102));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("End Time");
        jPanel3.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, 70, -1));

        jPanel11.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 380, 470, 80));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtRoomPrice.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        txtRoomPrice.setForeground(new java.awt.Color(0, 0, 0));
        txtRoomPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtRoomPrice.setText("00.00");
        jPanel6.add(txtRoomPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 40, 110, -1));

        jLabel35.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 0, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Others:");
        jPanel6.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 150, -1));

        txtEcological.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        txtEcological.setForeground(new java.awt.Color(0, 0, 0));
        txtEcological.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtEcological.setText("00.00");
        jPanel6.add(txtEcological, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 120, 110, -1));

        txtWaterActivityPrice.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        txtWaterActivityPrice.setForeground(new java.awt.Color(0, 0, 0));
        txtWaterActivityPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtWaterActivityPrice.setText("00.00");
        jPanel6.add(txtWaterActivityPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, 110, -1));

        jLabel40.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 0, 0));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("Reservation Number");
        jPanel6.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 150, -1));

        txtEntrace.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        txtEntrace.setForeground(new java.awt.Color(0, 0, 0));
        txtEntrace.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtEntrace.setText("00.00");
        jPanel6.add(txtEntrace, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 110, -1));

        jLabel45.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 0, 0));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setText("Total Room Price");
        jPanel6.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 110, -1));

        textReservation.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        textReservation.setForeground(new java.awt.Color(0, 0, 0));
        textReservation.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        textReservation.setText("00.00");
        jPanel6.add(textReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 110, -1));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("00.00");
        jPanel7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 100, -1));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Total Price");
        jPanel7.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 470, 40));

        jLabel42.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 0, 0));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("Total Ecological Fee");
        jPanel6.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 190, -1));

        jLabel47.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(0, 0, 0));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel47.setText("Total Entrance Fee");
        jPanel6.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 190, -1));

        jLabel36.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(0, 0, 0));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Water Activity Price");
        jPanel6.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 150, -1));

        jPanel11.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 470, 470, 220));

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

        jPanel11.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 50, 470, 90));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 240, 180));

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

        jPanel11.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 150, 470, 220));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Who's the lead Guest?");
        jPanel14.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        textField4.setBackground(new java.awt.Color(255, 255, 255));
        textField4.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField4.setLabelText("Fiste Name");
        jPanel14.add(textField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 790, 45));

        textField5.setBackground(new java.awt.Color(255, 255, 255));
        textField5.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField5.setLabelText("Last Name");
        jPanel14.add(textField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 790, 45));

        textField2.setBackground(new java.awt.Color(255, 255, 255));
        textField2.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField2.setLabelText("Address");
        textField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textField2ActionPerformed(evt);
            }
        });
        jPanel14.add(textField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 790, 45));

        textField3.setBackground(new java.awt.Color(255, 255, 255));
        textField3.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField3.setLabelText("Email");
        jPanel14.add(textField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 790, 45));

        textField6.setBackground(new java.awt.Color(255, 255, 255));
        textField6.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField6.setLabelText("Contact Number");
        jPanel14.add(textField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, 790, 45));

        textField7.setBackground(new java.awt.Color(255, 255, 255));
        textField7.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        textField7.setLabelText("Contact Number");
        jPanel14.add(textField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 790, 45));

        jPanel11.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 51, 870, 410));

        jPanel1.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1440, 730));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("CONTACT");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 0, -1, 60));

        jLabel8.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("HOME");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 0, -1, 60));

        jLabel9.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("ABOUT");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 0, -1, 60));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Welcome,");
        jPanel5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 180, 60));

        jLabel12.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("enjoy and have fun!");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, -1, 30));

        jPanel10.setBackground(new java.awt.Color(0, 153, 255));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 15)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Log out");
        jPanel10.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 100, 20));

        jPanel5.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 10, 100, 40));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1490, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1440, 720));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked

       

    }//GEN-LAST:event_jLabel10MouseClicked

    private void textField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textField2ActionPerformed

    private void cmbRoomTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoomTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbRoomTypeActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked

    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel9MouseClicked

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
            java.util.logging.Logger.getLogger(guestProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            
            
           Date checkInDate = null;         // Null check-in date
    Date checkOutDate = null;        // Null check-out date
    String roomNumber = null;        // Null room number
    String roomType = null;          // Null room type
    String roomDescription = null;   // Null room description
    double roomPrice = 0.0;         // Null room price
    java.sql.Date sqlDate = null;    // Null SQL date
    java.sql.Time sqlStartTime = null; // Null start time
    java.sql.Time sqlEndTime = null;   // Null end time
    String boatName = null;          // Null boat name
    double boatPrice = 0.0;          // Default boat price as 0.0 (since boatPrice should be a double)

    // Create a new instance of guestProcess with the initialized parameters
    new guestProcess(checkInDate, checkOutDate, roomNumber, roomType, roomDescription, 
                     roomPrice, sqlDate, sqlStartTime, sqlEndTime, boatName, boatPrice).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSComboMetro cmbRoomType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblBoatName;
    private javax.swing.JLabel lblCheckIn;
    private javax.swing.JLabel lblCheckOut;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblNumberOfNights;
    private javax.swing.JLabel lblRoomDescription;
    private javax.swing.JLabel lblRoomNumber;
    private javax.swing.JLabel lblRoomType;
    private javax.swing.JLabel lblSqlDate;
    private javax.swing.JLabel lblStartTime;
    private rojeru_san.complementos.RSButtonHover rSButtonHover1;
    private textfield.TextField textField2;
    private textfield.TextField textField3;
    private textfield.TextField textField4;
    private textfield.TextField textField5;
    private textfield.TextField textField6;
    private textfield.TextField textField7;
    private javax.swing.JLabel textReservation;
    private javax.swing.JLabel txtEcological;
    private javax.swing.JLabel txtEntrace;
    private javax.swing.JLabel txtRoomPrice;
    private javax.swing.JLabel txtWaterActivityPrice;
    // End of variables declaration//GEN-END:variables
}
