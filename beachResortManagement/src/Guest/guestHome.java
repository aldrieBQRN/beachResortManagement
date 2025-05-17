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
import java.awt.HeadlessException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import com.raven.datechooser.EventDateChooser;
import com.raven.datechooser.SelectedAction;
import com.raven.datechooser.SelectedDate;


import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;






/**
 *
 * @author yeojvaldez
 */
public class guestHome extends javax.swing.JFrame {

   private int userID;
   
  
    
    private int currentImageIndex = 0;
   
    public guestHome(int userID) {
        this.userID = userID;
        initComponents();
      
        hoverEffect();
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
    
    
    private void searchAvailableRooms(java.sql.Date checkIn, java.sql.Date checkOut, 
                                 int totalGuests, int adults, int children) {
    try {

        

       String query = "SELECT r.room_number, r.room_type, r.max_occupancy, r.room_price " +
                       "FROM room r " +
                       "WHERE r.max_occupancy >= ? " +
                       "AND r.room_number NOT IN (" +
                       "   SELECT room_number FROM room_reservation " +
                       "   WHERE status = 'Reserved' " +
                       "   AND (? < check_out_date AND ? > check_in_date)" +
                       ") " +
                       "ORDER BY r.room_price ASC";

        pst = con.prepareStatement(query);
        pst.setInt(1, totalGuests);
        pst.setDate(2, checkIn);
        pst.setDate(3, checkOut);

        rs = pst.executeQuery();

        boolean found = false;

        while (rs.next()) {
            found = true;
            String roomNumber = rs.getString("room_number");
            String roomType = rs.getString("room_type");
            int maxOccupancy = rs.getInt("max_occupancy");
            double price = rs.getDouble("room_price");
            
             System.out.println("Room: " + roomNumber + " | Type: " + roomType + " | Capacity: " + maxOccupancy + " | ₱" + price);

            // You can display the result in a table or console for now
            
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No available rooms found.");
        } else {
             
            new guestSelectRoom( checkIn, checkOut, adults, children, userID).setVisible(true);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
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
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        childSpinner = new spinner.Spinner();
        jLabel11 = new javax.swing.JLabel();
        txtCheckout = new textfield_suggestion.TextFieldSuggestion();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCheckin = new textfield_suggestion.TextFieldSuggestion();
        adultSpinner = new spinner.Spinner();
        panelRound4 = new GUI.PanelRound();
        jLabel13 = new javax.swing.JLabel();
        panelRound1 = new GUI.PanelRound();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtHome = new javax.swing.JLabel();
        txtReservation = new javax.swing.JLabel();
        txtProfile = new javax.swing.JLabel();
        panelRound5 = new GUI.PanelRound();
        jLabel8 = new javax.swing.JLabel();
        panelRound3 = new GUI.PanelRound();
        jLabel14 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();

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

        jPanel3.setBackground(new java.awt.Color(242, 242, 242));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Adult");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Check-out Date");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, -1, -1));

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("Check-in Date");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        childSpinner.setBackground(new java.awt.Color(255, 255, 255));
        childSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        childSpinner.setLabelText("");
        jPanel2.add(childSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 240, 40));

        jLabel11.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(102, 102, 102));
        jLabel11.setText("Child");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 140, -1, -1));

        txtCheckout.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckout.setSelectionColor(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtCheckout, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 80, 210, -1));

        jLabel5.setBackground(new java.awt.Color(102, 102, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 78, 40, 40));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 78, 40, 40));

        txtCheckin.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckin.setSelectionColor(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtCheckin, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 210, -1));

        adultSpinner.setBackground(new java.awt.Color(255, 255, 255));
        adultSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        adultSpinner.setLabelText("");
        jPanel2.add(adultSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, 240, 40));

        panelRound4.setBackground(new java.awt.Color(0, 153, 255));
        panelRound4.setRoundBottomLeft(20);
        panelRound4.setRoundBottomRight(20);
        panelRound4.setRoundTopLeft(20);
        panelRound4.setRoundTopRight(20);
        panelRound4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRound4MouseClicked(evt);
            }
        });
        panelRound4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("SEARCH");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        panelRound4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 0, 110, 40));

        jPanel2.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 160, 140, 40));

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 740, 280));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 460, 1440, 330));

        panelRound1.setBackground(new java.awt.Color(27, 59, 95));
        panelRound1.setRoundBottomLeft(50);
        panelRound1.setRoundBottomRight(50);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logoFinal copy.png"))); // NOI18N
        jLabel17.setText(" PAPAYA BEACH RESORT,");
        panelRound1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 310, 60));

        jLabel18.setFont(new java.awt.Font("Arial Rounded MT Bold", 2, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Escape to Paradise");
        panelRound1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 11, -1, 40));

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

        panelRound5.setBackground(new java.awt.Color(0, 153, 255));
        panelRound5.setRoundBottomLeft(20);
        panelRound5.setRoundBottomRight(20);
        panelRound5.setRoundTopLeft(20);
        panelRound5.setRoundTopRight(20);
        panelRound5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRound5MouseClicked(evt);
            }
        });
        panelRound5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Logout");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        panelRound5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        panelRound1.add(panelRound5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jPanel1.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 60));

        panelRound3.setBackground(new java.awt.Color(255, 255, 255));
        panelRound3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setBackground(new java.awt.Color(242, 242, 242));
        jLabel14.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Reserve your dates");
        panelRound3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 740, 50));

        jPanel1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 410, 740, 50));

        jPanel4.setBackground(new java.awt.Color(39, 114, 160));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("© 2025 Papaya Beach Resort. All rights reserved.");
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 13, -1, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 790, 1440, 40));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/bg.jpg"))); // NOI18N
        jLabel15.setText("jLabel15");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -70, -1, 530));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 790));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        dateChooserCheckin.showPopup();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        dateChooserCheckout.showPopup();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void panelRound4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelRound4MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        try {
    // 1. Get selected dates from JTextFields (e.g., "April 15, 2025")
    String checkInStr = txtCheckin.getText().trim();
    String checkOutStr = txtCheckout.getText().trim();

    // 2. Define the correct date format that matches your text fields
    SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy");
    displayFormat.setLenient(false);

    // 3. Parse strings into Date objects using the correct format
    Date checkInDate = displayFormat.parse(checkInStr);
    Date checkOutDate = displayFormat.parse(checkOutStr);
    
   
    System.out.println("Selected Check-in Date: " + displayFormat.format(checkInDate));
    System.out.println("Selected Check-out Date: " + displayFormat.format(checkOutDate));


    // 4. Get today's date (normalized to ignore time)
    Date today = new Date();
    today = displayFormat.parse(displayFormat.format(today));

    // 5. Validate dates
    if (!checkInDate.after(today)) {
        JOptionPane.showMessageDialog(this, "Check-in date must be after today's date");
        return;
    }


    if (!checkOutDate.after(checkInDate)) {
        JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date");
        return;
    }

    // 6. Get number of guests
    int adults = (Integer) adultSpinner.getValue();
    int children = (Integer) childSpinner.getValue();
    int totalGuests = adults + children;

    if (totalGuests <= 0) {
        JOptionPane.showMessageDialog(this, "Please select at least one guest");
        return;
    }

    // 7. Convert to SQL dates
    java.sql.Date sqlCheckIn = new java.sql.Date(checkInDate.getTime());
    java.sql.Date sqlCheckOut = new java.sql.Date(checkOutDate.getTime());
    
      // Print the SQL dates here
    System.out.println("SQL Check-in Date: " + sqlCheckIn);
    System.out.println("SQL Check-out Date: " + sqlCheckOut);

    // 8. Search for available rooms
    searchAvailableRooms(sqlCheckIn, sqlCheckOut, totalGuests, adults, children);

} catch (ParseException e) {
    JOptionPane.showMessageDialog(this, "Invalid date format. Please use 'MMMM dd, yyyy'");
} catch (HeadlessException e) {
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
}

    }//GEN-LAST:event_jLabel13MouseClicked

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
                java.util.logging.Logger.getLogger(guestHome.class.getName()).log(java.util.logging.Level.SEVERE, "Error logging logout activity", ex);
            }

            // Close the current window and open the landing page (logout action)
            this.dispose();
            new landingPage().setVisible(true);

        }catch (Exception ex) {
            // Handle any other unforeseen exceptions
            java.util.logging.Logger.getLogger(guestHome.class.getName()).log(java.util.logging.Level.SEVERE, "Unexpected error during logout", ex);
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void panelRound5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound5MouseClicked

    }//GEN-LAST:event_panelRound5MouseClicked

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
            java.util.logging.Logger.getLogger(guestHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            int userID = 0;
            new guestHome(userID).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private spinner.Spinner adultSpinner;
    private spinner.Spinner childSpinner;
    private com.raven.datechooser.DateChooser dateChooserCheckin;
    private com.raven.datechooser.DateChooser dateChooserCheckout;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound3;
    private GUI.PanelRound panelRound4;
    private GUI.PanelRound panelRound5;
    private textfield_suggestion.TextFieldSuggestion txtCheckin;
    private textfield_suggestion.TextFieldSuggestion txtCheckout;
    private javax.swing.JLabel txtHome;
    private javax.swing.JLabel txtProfile;
    private javax.swing.JLabel txtReservation;
    // End of variables declaration//GEN-END:variables
}
