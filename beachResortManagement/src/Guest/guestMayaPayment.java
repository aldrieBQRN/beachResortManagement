/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Login.landingPage;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;




/**
 *
 * @author yeojvaldez
 */
public class guestMayaPayment extends javax.swing.JFrame {

    // Fields with consistent indentation
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
    private int numAdults;
    private int numChildren;
    private int userID;
    private double grandTotal;
    private double downPayment;
    private String guestName;
    private String email;
    private String contact;
    private String address;
    private String reservationNumber;
    
    // Database connection objects
    private java.sql.Connection con; 
    private PreparedStatement pst;
    private ResultSet rs;
    
   
    // Constructor with boat reservation
    public guestMayaPayment(Date checkInDate, Date checkOutDate, String roomNumber, String roomType, 
                           String roomDescription, double roomPrice, java.sql.Date sqlDate, 
                           java.sql.Time sqlStartTime, java.sql.Time sqlEndTime, String boatName, 
                           double boatPrice, int numAdults, int numChildren, int userID, 
                           double grandTotal, double downPayment, String guestName, 
                           String email, String contact, String address, String reservationNumber) {
        initializeFields(checkInDate, checkOutDate, roomNumber, roomType, roomDescription, roomPrice,
                       numAdults, numChildren, userID, grandTotal, downPayment, guestName,
                       email, contact, address, reservationNumber);
        
        this.sqlDate = sqlDate;
        this.sqlStartTime = sqlStartTime;
        this.sqlEndTime = sqlEndTime;
        this.boatName = boatName;
        this.boatPrice = boatPrice;

        initializeUI();
    }
    
    // Constructor without boat reservation
    public guestMayaPayment(Date checkInDate, Date checkOutDate, String roomNumber, String roomType, 
                           String roomDescription, double roomPrice, int numAdults, 
                           int numChildren, int userID, double grandTotal, double downPayment, 
                           String guestName, String email, String contact, String address, 
                           String reservationNumber) {
        initializeFields(checkInDate, checkOutDate, roomNumber, roomType, roomDescription, roomPrice,
                       numAdults, numChildren, userID, grandTotal, downPayment, guestName,
                       email, contact, address, reservationNumber);
     
        initializeUI();
        
    }
    
    private void initializeFields(Date checkInDate, Date checkOutDate, String roomNumber, 
                                String roomType, String roomDescription, double roomPrice,
                                int numAdults, int numChildren, int userID, double grandTotal, 
                                double downPayment, String guestName, String email, 
                                String contact, String address, String reservationNumber) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomDescription = roomDescription;
        this.roomPrice = roomPrice;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.userID = userID;
        this.grandTotal = grandTotal;
        this.downPayment = downPayment;
        this.guestName = guestName;
        this.email = email;
        this.contact = contact;
        this.address = address;
        this.reservationNumber = reservationNumber;
    }
    
    private void initializeUI() {
        String formattedDownPayment = String.format("₱%.2f", downPayment);
        initComponents(); // Assuming this initializes the UI components
        txtAmount.setText(formattedDownPayment);
        txtAmount2.setText(formattedDownPayment);
        DatabaseConnection();
    }
    
    // Database connection method
    public final void DatabaseConnection() {
        String url = "jdbc:mysql://localhost:3307/beachResortManagement";
        String user = "root";
        String password = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage(), 
                                        "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database driver not found: " + e.getMessage(), 
                                        "Driver Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Main insertion method that decides which path to take
    public void processReservation(String referenceNumber) {
        if (boatName != null && !boatName.isEmpty()) {
            insertGuestAndRoomReservation(referenceNumber);
        } else {
            insertGuestAndRoomReservationWithoutBoat(referenceNumber);
        }
    }
    
    // Methods for reservations with boat tour
    private void insertGuestAndRoomReservation(String referenceNumber) {
        try {
            String guestQuery = "INSERT INTO guest (user_id, guest_name, email, contact, address) "
                             + "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement guestStmt = con.prepareStatement(guestQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            
            guestStmt.setInt(1, this.userID);
            guestStmt.setString(2, guestName);
            guestStmt.setString(3, email);
            guestStmt.setString(4, contact);
            guestStmt.setString(5, address);
            
            int affectedRows = guestStmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = guestStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int guestId = generatedKeys.getInt(1);
                    insertRoomReservation(guestId, referenceNumber);
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Error inserting guest and room reservation", e);
        }
    }
    
   private void insertRoomReservation(int guestId, String referenceNumber) {
    try {
        // Calculate stay duration and fees
        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        double totalRoomPrice = roomPrice * numberOfNights;
        int totalGuests = numAdults + numChildren;
        double entranceFee = 100.0 * totalGuests;
        double ecologicalFee = 20.0 * totalGuests;

        // SQL query matching your schema
        String roomReservationQuery = "INSERT INTO room_reservation " +
            "(reservation_number, user_id, guest_id, room_number, adult, child, " +
            "total_guests, check_in_date, check_out_date, total_room_price, " +
            "total_entrance_fee, total_ecological_fee, boat_tour_status, status, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        PreparedStatement roomStmt = con.prepareStatement(roomReservationQuery, 
            PreparedStatement.RETURN_GENERATED_KEYS);

        // Set parameters
        roomStmt.setString(1, reservationNumber);
        roomStmt.setInt(2, userID);       // From your class field (references user_details.user_id)
        roomStmt.setInt(3, guestId);      // From parameter (references guest.guest_id)
        roomStmt.setString(4, roomNumber);
        roomStmt.setInt(5, numAdults);
        roomStmt.setInt(6, numChildren);
        roomStmt.setInt(7, totalGuests);
        roomStmt.setDate(8, new java.sql.Date(checkInDate.getTime()));
        roomStmt.setDate(9, new java.sql.Date(checkOutDate.getTime()));
        roomStmt.setDouble(10, totalRoomPrice);
        roomStmt.setDouble(11, entranceFee);
        roomStmt.setDouble(12, ecologicalFee);
        roomStmt.setString(13, "Availed"); // For boat reservation
        roomStmt.setString(14, "Reserved");

        int affectedRows = roomStmt.executeUpdate();

        if (affectedRows > 0) {
            ResultSet generatedKeys = roomStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int roomReservationId = generatedKeys.getInt(1);
                insertBoatReservation(roomReservationId, guestId, referenceNumber);
            }
        }
    } catch (SQLException e) {
        handleDatabaseError("Error inserting room reservation", e);
        // Consider rolling back any transactions here if you're using them
    }
}

// Supporting method for error handling
private void handleDatabaseError(String message, SQLException e) {
    System.err.println(message + ": " + e.getMessage());
    e.printStackTrace();
    
    // Show user-friendly error message
    String errorMsg = "Database operation failed.\n";
    if (e.getMessage().contains("foreign key constraint")) {
        errorMsg += "Data integrity error: Invalid reference to another record.";
    } else {
        errorMsg += "Error: " + e.getMessage();
    }
    
    JOptionPane.showMessageDialog(null, 
        errorMsg, 
        "Database Error", 
        JOptionPane.ERROR_MESSAGE);
}
    
    private void insertBoatReservation(int roomReservationId, int guestId, String referenceNumber) {
        try {
            String boatQuery = "SELECT boat_id FROM boat WHERE boat_name = ?";
            PreparedStatement boatStmt = con.prepareStatement(boatQuery);
            boatStmt.setString(1, boatName);

            ResultSet boatRs = boatStmt.executeQuery();

            if (boatRs.next()) {
                int boatId = boatRs.getInt("boat_id");

                String insertQuery = "INSERT INTO boat_reservation (room_reservation_id, guest_id, boat_id, boat_tour_date, boat_tour_start_time, boat_tour_end_time, tour_price, status, created_at) "
                                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

                PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                setBoatReservationParameters(insertStmt, roomReservationId, guestId, boatId);

                int inserted = insertStmt.executeUpdate();
                if (inserted > 0) {
                    insertMainReservation(guestId, roomReservationId, referenceNumber);
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Error inserting boat reservation", e);
        }
    }
    
    // Methods for reservations without boat tour
    private void insertGuestAndRoomReservationWithoutBoat(String referenceNumber) {
        try {
            String guestQuery = "INSERT INTO guest (user_id, guest_name, email, contact, address) "
                              + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement guestStmt = con.prepareStatement(guestQuery, PreparedStatement.RETURN_GENERATED_KEYS);

            guestStmt.setInt(1, this.userID);
            guestStmt.setString(2, guestName);
            guestStmt.setString(3, email);
            guestStmt.setString(4, contact);
            guestStmt.setString(5, address);

            int affectedRows = guestStmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = guestStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int guestId = generatedKeys.getInt(1);
                    insertRoomReservationWithoutBoat(guestId, referenceNumber);
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Error inserting guest and room reservation (no boat)", e);
        }
    }
    
 private void insertRoomReservationWithoutBoat(int guestId, String referenceNumber) {
    try {
        // Calculate stay duration and fees
        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        double totalRoomPrice = roomPrice * numberOfNights;
        int totalGuests = numAdults + numChildren;
        double entranceFee = 100.0 * totalGuests;
        double ecologicalFee = 20.0 * totalGuests;

        // SQL with proper column names matching your schema
        String roomReservationQuery = "INSERT INTO room_reservation " +
            "(reservation_number, user_id, guest_id, room_number, adult, child, " +
            "total_guests, check_in_date, check_out_date, total_room_price, " +
            "total_entrance_fee, total_ecological_fee, boat_tour_status, status, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        PreparedStatement roomStmt = con.prepareStatement(roomReservationQuery, 
            PreparedStatement.RETURN_GENERATED_KEYS);

        // Set parameters - note both userID and guestId are included
        roomStmt.setString(1, reservationNumber);
        roomStmt.setInt(2, userID);     // From your class field (references user_details.user_id)
        roomStmt.setInt(3, guestId);    // From method parameter (references guest.guest_id)
        roomStmt.setString(4, roomNumber);
        roomStmt.setInt(5, numAdults);
        roomStmt.setInt(6, numChildren);
        roomStmt.setInt(7, totalGuests);
        roomStmt.setDate(8, new java.sql.Date(checkInDate.getTime()));
        roomStmt.setDate(9, new java.sql.Date(checkOutDate.getTime()));
        roomStmt.setDouble(10, totalRoomPrice);
        roomStmt.setDouble(11, entranceFee);
        roomStmt.setDouble(12, ecologicalFee);
        roomStmt.setString(13, "Not Availed");
        roomStmt.setString(14, "Reserved");

        int affectedRows = roomStmt.executeUpdate();

        if (affectedRows > 0) {
            ResultSet generatedKeys = roomStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int roomReservationId = generatedKeys.getInt(1);
                insertMainReservationWithoutBoat(guestId, roomReservationId, referenceNumber);
            }
        }
    } catch (SQLException e) {
        handleDatabaseError("Error inserting room reservation without boat", e);
    }
}


    // Common reservation methods
    private void insertMainReservation(int guestId, int roomReservationId, String referenceNumber) {
        try {
            double totalPrice = calculateTotalPrice(true);

            String query = "INSERT INTO reservation (reservation_number, guest_id, user_id, room_reservation_id, check_in_date, check_out_date, total_price, status, created_at) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            setMainReservationParameters(pst, guestId, roomReservationId, totalPrice);

            int inserted = pst.executeUpdate();
            if (inserted > 0) {
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int reservationId = generatedKeys.getInt(1);
                    insertPayment(reservationId, totalPrice, referenceNumber);
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Error inserting main reservation", e);
        }
    }
    
   private void insertMainReservationWithoutBoat(int guestId, int roomReservationId, String referenceNumber) {
    try {
        double totalPrice = calculateTotalPrice(false);

        String query = "INSERT INTO reservation (reservation_number, guest_id, user_id, room_reservation_id, check_in_date, check_out_date, total_price, status, created_at) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1, reservationNumber);
        pst.setInt(2, guestId);
        pst.setInt(3, userID);  // Added this missing parameter
        pst.setInt(4, roomReservationId);
        pst.setDate(5, new java.sql.Date(checkInDate.getTime()));
        pst.setDate(6, new java.sql.Date(checkOutDate.getTime()));
        pst.setDouble(7, totalPrice);
        pst.setString(8, "Pending");

        int inserted = pst.executeUpdate();
        if (inserted > 0) {
            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                int reservationId = keys.getInt(1);
                insertPayment(reservationId, totalPrice, referenceNumber);
            }
        }
    } catch (SQLException e) {
        handleDatabaseError("Error inserting main reservation (no boat)", e);
    }
}
    
    private void insertPayment(int reservationId, double totalAmount, String referenceNumber) {
        try {
            String paymentQuery = "INSERT INTO payment (reservation_id, payment_type, payment_method, amount, reference_number, status) "
                              + "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement paymentStmt = con.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, reservationId);
            paymentStmt.setString(2, "Downpayment");
            paymentStmt.setString(3, "Maya");
            paymentStmt.setDouble(4, downPayment);
            paymentStmt.setString(5, referenceNumber);
            paymentStmt.setString(6, "Pending");

            int affectedRows = paymentStmt.executeUpdate();
            handlePaymentResult(affectedRows > 0);
        } catch (SQLException e) {
            handleDatabaseError("Error inserting payment", e);
        }
    }
    
    // Helper methods
  private double calculateTotalPrice(boolean includeBoat) {
    long numberOfNights = TimeUnit.MILLISECONDS.toDays(checkOutDate.getTime() - checkInDate.getTime());
    double total = roomPrice * numberOfNights
                 + (100.0 + 20.0) * (numAdults + numChildren);
    
    if (includeBoat && boatName != null && !boatName.isEmpty()) {
        total += boatPrice;
    }
    
    return total;
}
    private void setRoomReservationParameters(PreparedStatement stmt, int guestId, 
                                           int totalGuests, double totalRoomPrice,
                                           double entranceFee, double ecologicalFee) throws SQLException {
        stmt.setString(1, reservationNumber);
        stmt.setInt(2, userID);
        stmt.setInt(3, guestId);
        stmt.setString(4, roomNumber);
        stmt.setInt(5, numAdults);
        stmt.setInt(6, numChildren);
        stmt.setInt(7, totalGuests);
        stmt.setDate(8, new java.sql.Date(checkInDate.getTime()));
        stmt.setDate(9, new java.sql.Date(checkOutDate.getTime()));
        stmt.setDouble(10, totalRoomPrice);
        stmt.setDouble(11, entranceFee);
        stmt.setDouble(12, ecologicalFee);
        stmt.setString(13, (boatName != null && !boatName.isEmpty()) ? "Availed" : "Not Availed");
        stmt.setString(14, "Reserved");
    }
    
    private void setBoatReservationParameters(PreparedStatement stmt, int roomReservationId,
                                           int guestId, int boatId) throws SQLException {
        stmt.setInt(1, roomReservationId);
        stmt.setInt(2, guestId);
        stmt.setInt(3, boatId);
        stmt.setDate(4, sqlDate);
        stmt.setTime(5, sqlStartTime);
        stmt.setTime(6, sqlEndTime);
        stmt.setDouble(7, boatPrice);
        stmt.setString(8, "Reserved");
    }
    
    private void setMainReservationParameters(PreparedStatement stmt, int guestId,
                                           int roomReservationId, double totalPrice) throws SQLException {
        stmt.setString(1, reservationNumber);
        stmt.setInt(2, guestId);
        stmt.setInt(3, userID);
        stmt.setInt(4, roomReservationId);
        stmt.setDate(5, new java.sql.Date(checkInDate.getTime()));
        stmt.setDate(6, new java.sql.Date(checkOutDate.getTime()));
        stmt.setDouble(7, totalPrice);
        stmt.setString(8, "Pending");
    }
    
    private void handlePaymentResult(boolean success) {
        if (success) {
            JOptionPane.showMessageDialog(null, "Your reservation is complete! Waiting for the resort to confirm.", 
                                      "Reservation Complete", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            guestHome gh = new guestHome(userID);
            gh.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Error processing your payment. Please try again.", 
                                      "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDatabaseError(String message, Exception e) {
        System.out.println(message + ": " + e.getMessage());
        JOptionPane.showMessageDialog(null, message + ": " + e.getMessage(), 
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
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
        jPanel15 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtReferenceNumber = new GUI.TextFieldSuggestion();
        jLabel29 = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtAmount2 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        panelRound1 = new GUI.PanelRound();
        panelRound3 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(242, 242, 242));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(102, 102, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setText("Total to pay");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("PHP 9,000.00");
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 140, 190, -1));

        txtReferenceNumber.setForeground(new java.awt.Color(102, 102, 102));
        txtReferenceNumber.setText("Enter Gcash Reference No.");
        txtReferenceNumber.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtReferenceNumber.setSelectedTextColor(new java.awt.Color(102, 102, 102));
        txtReferenceNumber.setSelectionColor(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtReferenceNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 410, -1));

        jLabel29.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(102, 102, 102));
        jLabel29.setText("Reference No.");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 30));

        panelRound2.setBackground(new java.awt.Color(0, 153, 255));
        panelRound2.setRoundBottomLeft(20);
        panelRound2.setRoundBottomRight(20);
        panelRound2.setRoundTopLeft(20);
        panelRound2.setRoundTopRight(20);
        panelRound2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Submit");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        panelRound2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        jPanel2.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 85, 90, 35));

        jPanel15.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 220, 440, 130));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setForeground(new java.awt.Color(102, 102, 102));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(0, 0, 51));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("Scan QR Code to Pay");
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 200, -1));

        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("Scan the QR code with a mobile payment app");
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setBackground(new java.awt.Color(0, 51, 153));
        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 102, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Maya");
        jPanel11.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 40, 30));

        jPanel8.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 100, 50));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 0, 10)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("This QR code can be scanned once only");
        jPanel12.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 50));

        jPanel8.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 240, 240, 50));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/gcash.jpg"))); // NOI18N
        jPanel13.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 220, 190));

        jPanel8.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 240, 210));

        jPanel15.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 660, 320));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setForeground(new java.awt.Color(102, 102, 102));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("Order Summary");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel23.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 0, 102));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Papaya Beach Resort");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 60, 190, -1));

        jLabel24.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setText("Order info");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        txtAmount2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtAmount2.setForeground(new java.awt.Color(0, 0, 0));
        txtAmount2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtAmount2.setText("PHP 9,000.00");
        jPanel3.add(txtAmount2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 130, 190, -1));

        jLabel27.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setText("Order amount");
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        txtAmount.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        txtAmount.setForeground(new java.awt.Color(0, 0, 102));
        txtAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtAmount.setText("PHP 9,000.00");
        jPanel3.add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 190, -1));

        jLabel30.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(102, 102, 102));
        jLabel30.setText("Total to pay");
        jPanel3.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jPanel15.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 30, 440, 180));

        jPanel1.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1440, 730));

        panelRound1.setBackground(new java.awt.Color(27, 59, 95));
        panelRound1.setRoundBottomLeft(50);
        panelRound1.setRoundBottomRight(50);
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

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Logout");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        panelRound3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        panelRound1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Welcome,");
        panelRound1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 180, 60));

        jLabel20.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("to Papaya Beach Resort");
        panelRound1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, -1, 30));

        jLabel25.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("HOME");
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 0, -1, 60));

        jLabel31.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("RESERVATION");
        jLabel31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel31MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 0, -1, 60));

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

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("© 2025 Papaya Beach Resort. All rights reserved.");
        jLabel9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 13, -1, -1));

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

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        String referenceNumber = txtReferenceNumber.getText().trim();

// Validate reference number
if (referenceNumber.isEmpty()) {
    JOptionPane.showMessageDialog(this, 
        "Reference Number cannot be empty.", 
        "Validation Error", 
        JOptionPane.ERROR_MESSAGE);
    return;
}

// Check if boat details are filled
boolean isBoatAvailed = boatName != null && !boatName.trim().isEmpty()
                     && sqlDate != null
                     && sqlStartTime != null
                     && sqlEndTime != null;

if (isBoatAvailed) {
    insertGuestAndRoomReservation(referenceNumber); // With boat
} else {
    insertGuestAndRoomReservationWithoutBoat(referenceNumber); // Without boat
}

    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        this.dispose();
        new landingPage().setVisible(true);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound3MouseClicked

    }//GEN-LAST:event_panelRound3MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        new guestHome(userID).setVisible(true);
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel31MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel31MouseClicked
        new guestReservation(userID).setVisible(true);
    }//GEN-LAST:event_jLabel31MouseClicked

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
            java.util.logging.Logger.getLogger(guestMayaPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestMayaPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestMayaPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestMayaPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
       /* Create and display the form */
/* Create and display the form */
java.awt.EventQueue.invokeLater(() -> {
    // Replace these variables with the actual data you have
  // Setting to null or 0 for initialization
    Date checkInDate = null; // null for date (can be changed later)
    Date checkOutDate = null; // null for date (can be changed later)
    String roomNumber = null; // null for string (can be changed later)
    String roomType = null; // null for string (can be changed later)
    String roomDescription = null; // null for string (can be changed later)
    double roomPrice = 0.0; // 0 for price (can be changed later)
    java.sql.Date sqlDate = null; // null for SQL date (can be changed later)
    java.sql.Time sqlStartTime = null; // null for time (can be changed later)
    java.sql.Time sqlEndTime = null; // null for time (can be changed later)
    String boatName = null; // null for boat name (can be changed later)
    double boatPrice = 0.0; // 0 for boat price (can be changed later)
    int numAdults = 0; // 0 for adults
    int numChildren = 0; // 0 for children
    int userID = 0; // 0 for user ID
    double grandTotal = 0.0; // 0 for grand total (can be calculated later)
    double downPayment = 0.0; // 0 for down payment (can be calculated later)
      String guestName = null;
        String email = null;
          String contact = null;  String address = null;
           String reservationNumber = null;
          

    // Pass the parameters to the constructor of guestGcashPayment
    new guestMayaPayment(checkInDate, checkOutDate, roomNumber, roomType, 
                          roomDescription, roomPrice, sqlDate, sqlStartTime, 
                          sqlEndTime, boatName, boatPrice, numAdults, numChildren, 
                          userID, grandTotal, downPayment, guestName, email, contact, address, reservationNumber).setVisible(true);
});


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound2;
    private GUI.PanelRound panelRound3;
    private javax.swing.JLabel txtAmount;
    private javax.swing.JLabel txtAmount2;
    private GUI.TextFieldSuggestion txtReferenceNumber;
    // End of variables declaration//GEN-END:variables
}
