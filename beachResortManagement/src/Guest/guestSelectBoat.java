/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
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
public class guestSelectBoat extends javax.swing.JFrame {

    private Date checkInDate;
    private Date checkOutDate;
    private int adults;
    private int children;
    
    private String roomNumber;
    private String roomType;
    private String roomDescription;
    private double roomPrice;
    
    
    public guestSelectBoat(Date checkInDate, Date checkOutDate, int adults, int children, 
                           String roomNumber, String roomType, String description, double price) {
        
        initComponents();
        
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.adults = adults;
        this.children = children;   
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomDescription = description;
        this.roomPrice = price;

        displayValues();
         
        DatabaseConnection();
       

        
        
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
       
        dateComboBox.removeAllItems();
    
    // Add default/placeholder item
        dateComboBox.addItem("Select a date");

        // Return if dates are invalid
        if (checkInDate == null || checkOutDate == null || checkOutDate.before(checkInDate)) {
            return;
        }

        // Convert to LocalDate for easier manipulation
        LocalDate startDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Format for display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)");

        // Add each date in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dateComboBox.addItem(date.format(formatter));
        }
       
       
        
      
        
        // You can display these values in JLabel or any other component
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
                selectBoat(clickedRow);
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

// Method to handle boat selection
private void selectBoat(int row) {
    // Get selected boat details from the table
    String boatName = (String) tblBoatDetails.getValueAt(row, 0); // Boat name is in column 0
    String boatPriceString = (String) tblBoatDetails.getValueAt(row, 3); // Boat price is in column 3
    boatPriceString = boatPriceString.replaceAll("[^0-9.]", "");
    double boatPrice = 0.0;
    if (boatPriceString.isEmpty()) {
        // Handle the case where the boat price is empty
        System.out.println("Boat price is empty!");
        boatPrice = 0.0; // Default value for boat price if empty
    } else {
        boatPrice = Double.parseDouble(boatPriceString);
    }


    // Prompt the user about water activities
    int response = JOptionPane.showConfirmDialog(
        this,
        "Would you like to add water activities to your booking?",
        "Water Activities",
        JOptionPane.YES_NO_OPTION
    );

    boolean wantsWaterActivities = (response == JOptionPane.YES_OPTION);
    double waterActivityPrice = 0.0; // Default value for water activities, can be updated based on user selection
    
    if (wantsWaterActivities) {
        // Get the selected date and parse it
        String selectedDateString = (String) dateComboBox.getSelectedItem();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)", Locale.ENGLISH);
        LocalDate selectedDate = LocalDate.parse(selectedDateString, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

        // Get selected time and convert it to 24-hour format
        int hour = (int) spinnerTime.getValue();
        String period = (String) comboBoxPeriod.getSelectedItem();

        // Adjust the hour for AM/PM format
        if ("PM".equals(period) && hour < 12) {
            hour += 12; // Convert PM time to 24-hour format
        } else if ("AM".equals(period) && hour == 12) {
            hour = 0; // Convert 12 AM to 0
        }

        String timeString = String.format("%02d:00:00", hour);
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(timeString);

        // Add 3 hours to the start time to calculate the end time for the water activity
        java.sql.Time sqlEndTime = new java.sql.Time(sqlStartTime.getTime() + (3 * 60 * 60 * 1000)); // 3 hours in milliseconds

        // Proceed with creating a new instance of guestProcess with all details
        new guestProcess(checkInDate, checkOutDate, roomNumber, roomType, roomDescription, 
                         roomPrice, sqlDate, sqlStartTime, sqlEndTime, boatName, boatPrice).setVisible(true);
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
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBoatDetails = new rojerusan.RSTableMetro();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        comboBoxPeriod = new rojerusan.RSComboMetro();
        jLabel16 = new javax.swing.JLabel();
        dateComboBox = new rojerusan.RSComboMetro();
        spinnerTime = new spinner.Spinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblBoatDetails.setBackground(new java.awt.Color(255, 255, 255));
        tblBoatDetails.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        tblBoatDetails.setForeground(new java.awt.Color(255, 255, 255));
        tblBoatDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Boat Name", "Description", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBoatDetails.setColorBackgoundHead(new java.awt.Color(27, 59, 95));
        tblBoatDetails.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        tblBoatDetails.setColorBordeHead(new java.awt.Color(255, 255, 255));
        tblBoatDetails.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        tblBoatDetails.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        tblBoatDetails.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        tblBoatDetails.setColorSelBackgound(new java.awt.Color(27, 59, 95));
        tblBoatDetails.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        tblBoatDetails.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblBoatDetails.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblBoatDetails.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tblBoatDetails.setGridColor(new java.awt.Color(255, 255, 255));
        tblBoatDetails.setRowHeight(50);
        tblBoatDetails.setSelectionBackground(new java.awt.Color(39, 114, 160));
        tblBoatDetails.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblBoatDetails.setShowGrid(false);
        tblBoatDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBoatDetailsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBoatDetails);

        jPanel9.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 620));

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 1440, 620));

        jPanel4.setBackground(new java.awt.Color(27, 59, 95));

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

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Welcome,");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 180, 60));

        jLabel5.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("enjoy and have fun!");
        jPanel5.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, -1, 30));

        jPanel10.setBackground(new java.awt.Color(0, 153, 255));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 15)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Log out");
        jPanel10.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 100, 20));

        jPanel5.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 10, 100, 40));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1490, 60));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("DATE");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, -1, -1));

        jPanel8.setBackground(new java.awt.Color(0, 153, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("SEACH");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        jPanel8.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, -1, 40));

        jPanel2.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 40, 230, 40));

        comboBoxPeriod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PM", "AM", " " }));
        comboBoxPeriod.setColorArrow(new java.awt.Color(27, 59, 95));
        comboBoxPeriod.setColorBorde(new java.awt.Color(39, 114, 160));
        comboBoxPeriod.setColorFondo(new java.awt.Color(39, 114, 160));
        comboBoxPeriod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPeriodActionPerformed(evt);
            }
        });
        comboBoxPeriod.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxPeriodPropertyChange(evt);
            }
        });
        jPanel2.add(comboBoxPeriod, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, 100, 40));

        jLabel16.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Time");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, -1, -1));

        dateComboBox.setColorArrow(new java.awt.Color(27, 59, 95));
        dateComboBox.setColorBorde(new java.awt.Color(39, 114, 160));
        dateComboBox.setColorFondo(new java.awt.Color(39, 114, 160));
        dateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateComboBoxActionPerformed(evt);
            }
        });
        dateComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateComboBoxPropertyChange(evt);
            }
        });
        jPanel2.add(dateComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 220, 40));

        spinnerTime.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        spinnerTime.setLabelText("");
        jPanel2.add(spinnerTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 120, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1440, 110));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 830));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked

       

    }//GEN-LAST:event_jLabel10MouseClicked

    private void tblBoatDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBoatDetailsMouseClicked

    }//GEN-LAST:event_tblBoatDetailsMouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked

    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
   try {
    // Get selected date and time
    String selectedDateString = (String) dateComboBox.getSelectedItem();
    if (selectedDateString == null || selectedDateString.equals("Select a date")) {
        JOptionPane.showMessageDialog(this, "Please select a valid date.");
        return;
    }

    // Parse selected date
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)", Locale.ENGLISH);
    LocalDate selectedDate = LocalDate.parse(selectedDateString, formatter);
    java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);
    
    // Debug: Print selected date
  

    // Get selected time and convert to 24-hour format
    int hour = (int) spinnerTime.getValue();
    String period = (String) comboBoxPeriod.getSelectedItem();

    if ("PM".equals(period) && hour < 12) {
        hour += 12;
    } else if ("AM".equals(period) && hour == 12) {
        hour = 0;
    }

    String timeString = String.format("%02d:00:00", hour);
    java.sql.Time sqlStartTime = java.sql.Time.valueOf(timeString);

    // Add 3 hours to the start time to calculate the end time
    java.sql.Time sqlEndTime = new java.sql.Time(sqlStartTime.getTime() + (3 * 60 * 60 * 1000)); // 3 hours in milliseconds
    
    int guestTotal = this.adults + this.children;

  

    // Query for available boats with the updated condition for reservation overlap
    String boatQuery = """
        SELECT b.boat_name, b.description, b.tour_price 
        FROM boat b 
        WHERE b.capacity >= ? 
        AND b.boat_name NOT IN (
            SELECT br.boat_name 
            FROM boat_reservation br 
            WHERE br.status = 'Reserved' 
            AND br.boat_tour_date = ? 
            AND (
                ? < br.boat_tour_end_time AND ? > br.boat_tour_start_time
            )
        ) 
        ORDER BY b.tour_price ASC;
    """;

    pst = con.prepareStatement(boatQuery);
    pst.setInt(1, guestTotal);     // Capacity check (number of guests)
    pst.setDate(2, sqlDate);       // Selected date for reservation
    pst.setTime(3, sqlStartTime);  // Start time for reservation
    pst.setTime(4, sqlEndTime);    // End time for reservation

    rs = pst.executeQuery();

    // Create table model for boats
    DefaultTableModel boatModel = new DefaultTableModel(
        new Object[]{"Boat Name", "Description", "Price/Ride", "Select"}, 
        0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3;  // Only the 'Select' button column is editable
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) {
                return JButton.class;  // Select column to be a button
            }
            return super.getColumnClass(columnIndex);
        }
    };

    tblBoatDetails.setModel(boatModel); // Set the model to the table
    tblBoatDetails.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
    tblBoatDetails.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

    boolean foundBoats = false;

    // Process result set and populate the table with available boats
    while (rs.next()) {
        foundBoats = true;
        String boatName = rs.getString("boat_name");
        String description = rs.getString("description");
        double rate = rs.getDouble("tour_price");

        System.out.println("Boat Name: " + boatName + " | Description: " + description + " | ₱" + rate);

        boatModel.addRow(new Object[]{
            boatName,
            description,
            "₱" + String.format("%.2f", rate),
            "Select"
        });
    }
    
      // Debug: Print start and end times
    System.out.println("Start Time: " + sqlStartTime);
    System.out.println("End Time: " + sqlEndTime);
      System.out.println("Selected Date: " + sqlDate);

    // If no available boats found, show message to the user
    if (!foundBoats) {
        JOptionPane.showMessageDialog(this, "No available boats found for the selected date and time.");
    }

} catch (SQLException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
} catch (DateTimeParseException ex) {
    JOptionPane.showMessageDialog(this, "Invalid date format: " + ex.getMessage());
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}




   

     
   
   
    }//GEN-LAST:event_jLabel12MouseClicked

    private void comboBoxPeriodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxPeriodActionPerformed
         String selected = (String) dateComboBox.getSelectedItem();
               if ("AM".equals(selected)) {
        spinnerTime.setModel(new javax.swing.SpinnerNumberModel(7, 7, 11, 1));
    } else if ("PM".equals(selected)) {
        spinnerTime.setModel(new javax.swing.SpinnerNumberModel(12, 12, 15, 1)); // 12 to 3 PM
    }
            

    }//GEN-LAST:event_comboBoxPeriodActionPerformed

    private void comboBoxPeriodPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxPeriodPropertyChange
     
    }//GEN-LAST:event_comboBoxPeriodPropertyChange

    private void dateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateComboBoxActionPerformed

    private void dateComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateComboBoxPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_dateComboBoxPropertyChange

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
            java.util.logging.Logger.getLogger(guestSelectBoat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guestSelectBoat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guestSelectBoat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guestSelectBoat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        java.awt.EventQueue.invokeLater(() -> {
            
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSComboMetro comboBoxPeriod;
    private rojerusan.RSComboMetro dateComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private spinner.Spinner spinnerTime;
    private rojerusan.RSTableMetro tblBoatDetails;
    // End of variables declaration//GEN-END:variables
}
