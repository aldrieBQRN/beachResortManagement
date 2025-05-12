/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Staff;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import Database.DatabaseConnection; 
import java.awt.Insets;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author yeojvaldez
 */
public class staffMainHome extends javax.swing.JInternalFrame {

    /**
     * Creates new form staffReservation
     */
    public staffMainHome() {
        initComponents();
        removeBackground();
        DatabaseConnection();
        shwoDetails();
        displayCurrentDateTime();
        displayUpcomingReservations();
       
   
        
 
    }
    
    Connection con; 
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
    
    public final void removeBackground(){
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI UI = (BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null); 
    }

    
    public void shwoDetails(){
    try {
    // Total Rooms
    String sql1 = "SELECT total_rooms FROM view_total_rooms";
    pst = con.prepareStatement(sql1);
    rs = pst.executeQuery();
    if (rs.next()) {
        int count = rs.getInt("total_rooms");
        txtRoomCount.setText(String.valueOf(count));
    }
    rs.close();
    pst.close();

    // Total Boats
    String sql2 = "SELECT total_boats FROM view_total_boats";
    pst = con.prepareStatement(sql2);
    rs = pst.executeQuery();
    if (rs.next()) {
        int count = rs.getInt("total_boats");
        txtBoatCount.setText(String.valueOf(count));
    }
    rs.close();
    pst.close();

    // Total Room Reservations
    String sql3 = "SELECT total_room_reservations FROM view_total_room_reservations";
    pst = con.prepareStatement(sql3);
    rs = pst.executeQuery();
    if (rs.next()) {
        int count = rs.getInt("total_room_reservations");
        txtReservationCount.setText(String.valueOf(count));
    }
    rs.close();
    pst.close();
    
    String sql = "SELECT total_guests FROM view_guest_users";
    pst = con.prepareStatement(sql);
    rs = pst.executeQuery();
    if (rs.next()) {
        int count = rs.getInt("total_guests");
        txtGuestUserCount.setText(String.valueOf(count));  // Use your JTextField or JLabel
    }
    rs.close();
    pst.close();

    // Total Boat Reservations
    String sql4 = "SELECT total_boat_reservations FROM view_total_boat_reservations";
    pst = con.prepareStatement(sql4);
    rs = pst.executeQuery();
    if (rs.next()) {
        int count = rs.getInt("total_boat_reservations");
        txtBoatReservationCount.setText(String.valueOf(count)); // Assuming 'label' is a JLabel
    }
    rs.close();
    pst.close();

    // Cancelled Reservations
    String sql5 = "SELECT cancelled_reservation_count FROM view_count_cancelled_reservations";
    pst = con.prepareStatement(sql5);
    rs = pst.executeQuery();
    if (rs.next()) {
        int count = rs.getInt("cancelled_reservation_count");
        txtCancelledCount.setText(String.valueOf(count));
    }
    rs.close();
    pst.close();

    // Confirmed Reservations
    String sql6 = "SELECT confirmed_reservation_count FROM view_count_confirmed_reservations";
    pst = con.prepareStatement(sql6);
    rs = pst.executeQuery();  // FIXED: You used 'stmt' instead of 'pst' before
    if (rs.next()) {
        int count = rs.getInt("confirmed_reservation_count");
        txtConfirmedCount.setText(String.valueOf(count));
    }
    
    rs.close();
pst.close();
    
   

    String sql8 = "SELECT pending_reservations FROM view_pending_reservations";
pst = con.prepareStatement(sql8);
rs = pst.executeQuery();
if (rs.next()) {
    int count = rs.getInt("pending_reservations");
    txtPendingReservations.setText(String.valueOf(count));
}

} catch (SQLException ex) {
    Logger.getLogger(staffMainHome.class.getName()).log(Level.SEVERE, null, ex);
} finally {
    try {
        if (rs != null) rs.close();
        if (pst != null) pst.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    }
    
 
    public void displayUpcomingReservations() {
    String[] columnNames = {
        "Reservation Number", "Guest Name", "Check-In Date",
        "Check-Out Date", "Total Price", "Room Number", "Boat Tour Status"
    };

    DefaultTableModel tableModel = new DefaultTableModel();
    for (String columnName : columnNames) {
        tableModel.addColumn(columnName);
    }

    reservationsTable.setModel(tableModel);

    try {
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3307/beachResortManagement", "root", ""
        );

        String sql = """
            SELECT 
                reservation_number, guest_name, check_in_date,
                check_out_date, total_price, room_number, boat_tour_status
            FROM upcoming_confirmed_reservations
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            tableModel.addRow(row);
        }

        rs.close();
        ps.close();
        conn.close();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

  
}

    public void displayCurrentDateTime() {
    // Define the date and time format
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy    HH:mm:ss");

    // Create a timer to update the label every second
    Timer timer = new Timer(1000, e -> {
        // Get the current date and time
        String currentDateTime = dateFormat.format(new Date());

        // Update the JLabel with the current date and time
        txtDateTime.setText(currentDateTime);
    });

    // Start the timer
    timer.start();
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlmain = new javax.swing.JPanel();
        txtHead = new javax.swing.JLabel();
        pnlCheckin = new javax.swing.JPanel();
        txtPendingReservations = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pnlWaterActivities = new javax.swing.JPanel();
        txtGuestUserCount = new javax.swing.JLabel();
        Guest = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtReservationCount = new javax.swing.JLabel();
        pnlConfirmReservation = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtConfirmedCount = new javax.swing.JLabel();
        pnlAvailableRoom = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtRoomCount = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pnlAvailableBoat = new javax.swing.JPanel();
        txtBoatCount = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pnlReserveBoat = new javax.swing.JPanel();
        txtBoatReservationCount = new javax.swing.JLabel();
        label = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pnlCancelReservation = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        txtCancelledCount = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pnlUpcomming = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        reservationsTable = new rojerusan.RSTableMetro();
        txtDateTime = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlmain.setBackground(new java.awt.Color(242, 242, 242));
        pnlmain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtHead.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        txtHead.setForeground(new java.awt.Color(0, 0, 0));
        txtHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtHead.setText("Beach-Front Resort Management System");
        txtHead.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(39, 114, 160)));
        pnlmain.add(txtHead, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 1140, 80));

        pnlCheckin.setBackground(new java.awt.Color(255, 255, 255));
        pnlCheckin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtPendingReservations.setForeground(new java.awt.Color(0, 0, 0));
        txtPendingReservations.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtPendingReservations.setText("00");
        pnlCheckin.add(txtPendingReservations, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        jLabel9.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Pending Reservation");
        pnlCheckin.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-pending-50.png"))); // NOI18N
        pnlCheckin.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        pnlmain.add(pnlCheckin, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 100, 270, 70));

        pnlWaterActivities.setBackground(new java.awt.Color(255, 255, 255));
        pnlWaterActivities.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtGuestUserCount.setForeground(new java.awt.Color(0, 0, 0));
        txtGuestUserCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtGuestUserCount.setText("00");
        pnlWaterActivities.add(txtGuestUserCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        Guest.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        Guest.setForeground(new java.awt.Color(0, 0, 0));
        Guest.setText("Total Guest");
        pnlWaterActivities.add(Guest, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-user-50.png"))); // NOI18N
        pnlWaterActivities.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        pnlmain.add(pnlWaterActivities, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 190, 270, 70));

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Reserve Room");
        panel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, 20));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-room-50 2.png"))); // NOI18N
        panel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        txtReservationCount.setForeground(new java.awt.Color(0, 0, 0));
        txtReservationCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtReservationCount.setText("00");
        panel.add(txtReservationCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        pnlmain.add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 270, 70));

        pnlConfirmReservation.setBackground(new java.awt.Color(255, 255, 255));
        pnlConfirmReservation.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Confirm Reservation");
        pnlConfirmReservation.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-checked-checkbox-50.png"))); // NOI18N
        pnlConfirmReservation.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        txtConfirmedCount.setForeground(new java.awt.Color(0, 0, 0));
        txtConfirmedCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtConfirmedCount.setText("00");
        pnlConfirmReservation.add(txtConfirmedCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        pnlmain.add(pnlConfirmReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, 270, 70));

        pnlAvailableRoom.setBackground(new java.awt.Color(255, 255, 255));
        pnlAvailableRoom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Available Room");
        pnlAvailableRoom.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        txtRoomCount.setForeground(new java.awt.Color(0, 0, 0));
        txtRoomCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtRoomCount.setText("00");
        pnlAvailableRoom.add(txtRoomCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-room-50 (1).png"))); // NOI18N
        pnlAvailableRoom.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        pnlmain.add(pnlAvailableRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 270, 70));

        pnlAvailableBoat.setBackground(new java.awt.Color(255, 255, 255));
        pnlAvailableBoat.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtBoatCount.setForeground(new java.awt.Color(0, 0, 0));
        txtBoatCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtBoatCount.setText("00");
        pnlAvailableBoat.add(txtBoatCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        jLabel13.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Available Boat");
        pnlAvailableBoat.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-boat-50.png"))); // NOI18N
        pnlAvailableBoat.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        pnlmain.add(pnlAvailableBoat, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 270, 70));

        pnlReserveBoat.setBackground(new java.awt.Color(255, 255, 255));
        pnlReserveBoat.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtBoatReservationCount.setForeground(new java.awt.Color(0, 0, 0));
        txtBoatReservationCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtBoatReservationCount.setText("00");
        pnlReserveBoat.add(txtBoatReservationCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        label.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        label.setForeground(new java.awt.Color(0, 0, 0));
        label.setText("Reserve Boat");
        pnlReserveBoat.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-boat-50 (1).png"))); // NOI18N
        pnlReserveBoat.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        pnlmain.add(pnlReserveBoat, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 190, 270, 70));

        pnlCancelReservation.setBackground(new java.awt.Color(255, 255, 255));
        pnlCancelReservation.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("Cancel Reservation");
        pnlCancelReservation.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        txtCancelledCount.setForeground(new java.awt.Color(0, 0, 0));
        txtCancelledCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtCancelledCount.setText("00");
        pnlCancelReservation.add(txtCancelledCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 40, -1));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-cancel-50.png"))); // NOI18N
        pnlCancelReservation.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 50));

        pnlmain.add(pnlCancelReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 190, 270, 70));

        pnlUpcomming.setBackground(new java.awt.Color(255, 255, 255));
        pnlUpcomming.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        reservationsTable.setBackground(new java.awt.Color(242, 242, 242));
        reservationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Guest", "Room Number", "Check-in", "Check-out", "Boat Tour"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        reservationsTable.setColorBackgoundHead(new java.awt.Color(255, 255, 255));
        reservationsTable.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        reservationsTable.setColorBordeHead(new java.awt.Color(255, 255, 255));
        reservationsTable.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        reservationsTable.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        reservationsTable.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        reservationsTable.setColorForegroundHead(new java.awt.Color(0, 0, 0));
        reservationsTable.setColorSelBackgound(new java.awt.Color(27, 59, 95));
        reservationsTable.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        reservationsTable.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        reservationsTable.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        reservationsTable.setGridColor(new java.awt.Color(204, 204, 204));
        reservationsTable.setRowHeight(30);
        reservationsTable.setSelectionBackground(new java.awt.Color(27, 59, 95));
        reservationsTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        reservationsTable.setShowGrid(false);
        reservationsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reservationsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(reservationsTable);

        pnlUpcomming.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 1100, 380));

        txtDateTime.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        txtDateTime.setForeground(new java.awt.Color(0, 0, 0));
        txtDateTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtDateTime.setText("Date and Time");
        pnlUpcomming.add(txtDateTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 20, 270, 30));

        jLabel24.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setText("Upcomming Check-In");
        pnlUpcomming.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, -1, 30));

        pnlmain.add(pnlUpcomming, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 1140, 460));

        getContentPane().add(pnlmain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 760));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reservationsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reservationsTableMouseClicked

    }//GEN-LAST:event_reservationsTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Guest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel pnlAvailableBoat;
    private javax.swing.JPanel pnlAvailableRoom;
    private javax.swing.JPanel pnlCancelReservation;
    private javax.swing.JPanel pnlCheckin;
    private javax.swing.JPanel pnlConfirmReservation;
    private javax.swing.JPanel pnlReserveBoat;
    private javax.swing.JPanel pnlUpcomming;
    private javax.swing.JPanel pnlWaterActivities;
    private javax.swing.JPanel pnlmain;
    private rojerusan.RSTableMetro reservationsTable;
    private javax.swing.JLabel txtBoatCount;
    private javax.swing.JLabel txtBoatReservationCount;
    private javax.swing.JLabel txtCancelledCount;
    private javax.swing.JLabel txtConfirmedCount;
    private javax.swing.JLabel txtDateTime;
    private javax.swing.JLabel txtGuestUserCount;
    private javax.swing.JLabel txtHead;
    private javax.swing.JLabel txtPendingReservations;
    private javax.swing.JLabel txtReservationCount;
    private javax.swing.JLabel txtRoomCount;
    // End of variables declaration//GEN-END:variables
}
