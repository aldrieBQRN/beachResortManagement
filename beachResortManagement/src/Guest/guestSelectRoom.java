/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Login.landingPage;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.raven.datechooser.EventDateChooser;
import com.raven.datechooser.SelectedAction;
import com.raven.datechooser.SelectedDate;
import com.toedter.calendar.JCalendar;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;



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
        rsDateChooserCheckIn.setDatoFecha(checkInDate);
        rsDateChooserCheckOut.setDatoFecha(checkOutDate);
        adultsSpinner.setValue(adults);
        childrenSpinner.setValue(children); 
        
      
        
        // You can display these values in JLabel or any other component
    }
    
  private void searchAvailableRooms() {
    try {
        // Ensure check-in and check-out dates are not null
        if (this.checkInDate == null || this.checkOutDate == null) {
            JOptionPane.showMessageDialog(this, "Please select valid check-in and check-out dates.");
            return;
        }

        java.sql.Date sqlCheckIn = new java.sql.Date(this.checkInDate.getTime());
        java.sql.Date sqlCheckOut = new java.sql.Date(this.checkOutDate.getTime());
        int totalGuests = this.adults + this.children;

        // SQL query to find rooms available based on occupancy and reservation status
        String query = "SELECT r.room_number, r.room_type, r.description, r.room_price " +
                       "FROM room r " +
                       "WHERE r.max_occupancy >= ? " +  // Ensure max occupancy matches
                       "AND r.room_number NOT IN (" +
                       "   SELECT room_number FROM room_reservation " +
                       "   WHERE status = 'Reserved' " +  // Check for reserved rooms
                       "   AND (? <= check_out_date AND ? >= check_in_date)" +  // Ensure no overlapping reservations
                       ") " +
                       "ORDER BY r.room_price ASC";  // Sort by room price

        // Prepare the SQL statement and set parameters
        pst = con.prepareStatement(query);
        pst.setInt(1, totalGuests);  // Number of guests should match room capacity
        pst.setDate(2, sqlCheckIn);   // Set check-in date
        pst.setDate(3, sqlCheckOut);  // Set check-out date

        rs = pst.executeQuery();

        // Create table model with columns for room details and a "Select" button
        DefaultTableModel roomModel = new DefaultTableModel(
            new Object[]{"Room Number", "Room Type", "Description", "Price", "Action"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only make the "Select" column editable (button column)
                return column == 4; // Index 4 corresponds to the "Select" button column
            }
        };

        // Set the table model for displaying room details
        tblRoomDetails.setModel(roomModel);

        // Add button renderer and editor for the "Select" column (index 4)
        tblRoomDetails.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tblRoomDetails.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        boolean found = false;

        // Add data rows for available rooms
        while (rs.next()) {
            found = true;

            String roomNumber = rs.getString("room_number");
            String roomType = rs.getString("room_type");
             String roomDdescription = rs.getString("description");
            
            double price = rs.getDouble("room_price");

            System.out.println("Room: " + roomNumber + " | Type: " + roomType + " | Description: " + roomDdescription + " | ₱" + price);

            // Add room data to table
            roomModel.addRow(new Object[]{
                roomNumber, 
                roomType, 
                roomDdescription,
                "₱" + String.format("%.2f", price), // Format price with currency
                "Select" // Action button for selection
            });
        }

        // Inform the user if no rooms are available
        if (!found) {
            JOptionPane.showMessageDialog(this, "No available rooms found for your criteria.");
        }

    } catch (SQLException ex) {
        // Handle SQL exceptions
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    } catch (Exception ex) {
        // Handle general exceptions
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}




// Button Renderer Class
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
class ButtonEditor extends DefaultCellEditor {
    private String label;
    private JButton button;
    private int clickedRow;
    
    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                selectRoom(clickedRow);
            }
        });
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        clickedRow = row;
        return button;
    }
    
    public Object getCellEditorValue() {
        return label;
    }
}

// Method to handle room selection
// Method to handle room selection
private void selectRoom(int row) {
    // Retrieve room information from the selected row
    String roomNumber = (String) tblRoomDetails.getValueAt(row, 0); // Room number is in column 0
    String roomType = (String) tblRoomDetails.getValueAt(row, 1); // Room type is in column 1
    String description = (String) tblRoomDetails.getValueAt(row, 2); // Description is in column 2

    String priceString = (String) tblRoomDetails.getValueAt(row, 3); // Price is in column 3
    priceString = priceString.replaceAll("[^0-9.]", ""); 
    double price = 0;
    
    try {
        price = Double.parseDouble(priceString); // Parse price
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Debug: print the selected room information (Remove in production)
    System.out.println("Selected Room: ");
    System.out.println("Room Number: " + roomNumber);
    System.out.println("Room Type: " + roomType);
    System.out.println("Description: " + description);
    System.out.println("Price: " + price);

    // Ask user if they want to add water activities to their booking
    int response = JOptionPane.showConfirmDialog(
        this,
        "Would you like to add water activities to your booking?",
        "Water Activities",
        JOptionPane.YES_NO_OPTION
    );

    boolean wantsWaterActivities = (response == JOptionPane.YES_OPTION);

    // Get check-in and check-out dates
    Date checkInDate = rsDateChooserCheckIn.getDatoFecha();
    Date checkOutDate = rsDateChooserCheckOut.getDatoFecha();

 
    // Get number of guests
    int adults = (Integer) adultsSpinner.getValue();
    int children = (Integer) childrenSpinner.getValue();
    int totalGuests = adults + children;

    // Debug: print the number of guests
    System.out.println("Total Guests: " + totalGuests);

    // Pass the details to the next form or action
    if (wantsWaterActivities) {
        new guestSelectBoat(checkInDate, checkOutDate, adults, children, roomNumber, roomType, description, price, userID).setVisible(true);
    } else {
        new guestProcess2(checkInDate, checkOutDate, roomNumber, roomType, description, price, adults, children, userID).setVisible(true);
    }
}

// Method to validate check-in and check-out dates
private boolean validateDates(Date checkInDate, Date checkOutDate) {
    if (checkInDate == null || checkOutDate == null) {
        JOptionPane.showMessageDialog(this, "Please select valid check-in and check-out dates.", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    if (checkOutDate.before(checkInDate)) {
        JOptionPane.showMessageDialog(this, "Check-out date cannot be before check-in date.", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    return true;
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
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRoomDetails = new rojerusan.RSTableMetro();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        rsDateChooserCheckIn = new rojeru_san.componentes.RSDateChooser();
        rsDateChooserCheckOut = new rojeru_san.componentes.RSDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        adultsSpinner = new spinner.Spinner();
        jLabel14 = new javax.swing.JLabel();
        childrenSpinner = new spinner.Spinner();
        panelRound3 = new GUI.PanelRound();
        jLabel12 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        panelRound1 = new GUI.PanelRound();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(242, 242, 242));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblRoomDetails.setBackground(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setForeground(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Room Number", "Room Category", "Description", "Price/Night"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRoomDetails.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        tblRoomDetails.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setColorBordeHead(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        tblRoomDetails.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        tblRoomDetails.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        tblRoomDetails.setColorSelBackgound(new java.awt.Color(27, 59, 95));
        tblRoomDetails.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        tblRoomDetails.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblRoomDetails.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblRoomDetails.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tblRoomDetails.setGridColor(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setRowHeight(50);
        tblRoomDetails.setSelectionBackground(new java.awt.Color(39, 114, 160));
        tblRoomDetails.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setShowGrid(false);
        tblRoomDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRoomDetailsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRoomDetails);

        jPanel9.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 1360, 510));

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 1440, 590));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("Check-in Date");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, -1, -1));

        rsDateChooserCheckIn.setColorBackground(new java.awt.Color(0, 153, 255));
        rsDateChooserCheckIn.setColorButtonHover(new java.awt.Color(0, 153, 255));
        rsDateChooserCheckIn.setColorForeground(new java.awt.Color(0, 0, 0));
        rsDateChooserCheckIn.setPlaceholder("");
        rsDateChooserCheckIn.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                rsDateChooserCheckInPropertyChange(evt);
            }
        });
        jPanel2.add(rsDateChooserCheckIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, -1, -1));

        rsDateChooserCheckOut.setColorBackground(new java.awt.Color(0, 153, 255));
        rsDateChooserCheckOut.setColorButtonHover(new java.awt.Color(0, 153, 255));
        rsDateChooserCheckOut.setColorForeground(new java.awt.Color(0, 0, 0));
        rsDateChooserCheckOut.setPlaceholder("");
        jPanel2.add(rsDateChooserCheckOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, -1));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Check-out Date");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, -1, -1));

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Adult");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, -1, -1));

        adultsSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        adultsSpinner.setLabelText("");
        jPanel2.add(adultsSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 60, 240, 40));

        jLabel14.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Child");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 40, -1, -1));

        childrenSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        childrenSpinner.setLabelText("");
        jPanel2.add(childrenSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 60, 240, 40));

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
        panelRound3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 180, 40));

        jPanel2.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 60, 180, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1440, 140));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound1.setBackground(new java.awt.Color(27, 59, 95));
        panelRound1.setRoundBottomLeft(50);
        panelRound1.setRoundBottomRight(50);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Welcome,");
        panelRound1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 180, 60));

        jLabel18.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("enjoy and have fun!");
        panelRound1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, -1, 30));

        jLabel20.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/iconHome.png"))); // NOI18N
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 0, -1, 60));

        jLabel19.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/iconNotif.png"))); // NOI18N
        panelRound1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 0, -1, 60));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/profile.png"))); // NOI18N
        panelRound1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 0, 30, 60));

        panelRound2.setBackground(new java.awt.Color(255, 255, 255));
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

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Logout");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        panelRound2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jPanel6.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 60));

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1490, 60));

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

    private void tblRoomDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRoomDetailsMouseClicked

    }//GEN-LAST:event_tblRoomDetailsMouseClicked

    private void rsDateChooserCheckInPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_rsDateChooserCheckInPropertyChange
        
    }//GEN-LAST:event_rsDateChooserCheckInPropertyChange

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        Date checkInDate = rsDateChooserCheckIn.getDatoFecha();
        Date checkOutDate = rsDateChooserCheckOut.getDatoFecha();

        // 2. Get number of guests
        int adults = (Integer) adultsSpinner.getValue();
        int children = (Integer) childrenSpinner.getValue();
       
        
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.adults = adults;
        this.children = children;
        
        searchAvailableRooms();
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel20MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
           this.dispose();
       new landingPage().setVisible(true);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked

    }//GEN-LAST:event_panelRound2MouseClicked

    private void panelRound3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelRound3MouseClicked

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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound2;
    private GUI.PanelRound panelRound3;
    private rojeru_san.componentes.RSDateChooser rsDateChooserCheckIn;
    private rojeru_san.componentes.RSDateChooser rsDateChooserCheckOut;
    private rojerusan.RSTableMetro tblRoomDetails;
    // End of variables declaration//GEN-END:variables
}
