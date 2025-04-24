/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Admin;

import Staff.*;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author yeojvaldez
 */
public class adminUpdateRoom extends javax.swing.JFrame {

    /**
     * Creates new form roomAdd
     */
    
    File f = null;
    String path = null;
    private ImageIcon format = null;
    String fname = null;
    int s = 0;
    byte[] pimage = null;
    public adminUpdateRoom() {
        initComponents();
        DatabaseConnection();
        populateRoomNumbersComboBox();
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
    
    private void populateRoomNumbersComboBox() {
        // Clear the combo box before adding items
        cmbRoomNumber.removeAllItems();

        // Add the default entry (null or a placeholder)
        cmbRoomNumber.addItem("Select a Room Number");

        try {
            // SQL query to fetch all room numbers from the room table
            String sql = "SELECT room_number FROM room";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            // Populate combo box with room numbers
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                cmbRoomNumber.addItem(roomNumber); // Add each room number to the combo box
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching room numbers: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   /**
 * Loads and displays an image from the database result set
 */
public void loadImage() {
    
        try {
            // Make sure ResultSet is valid
            if (rs == null || rs.isClosed()) {
                System.err.println("ResultSet is not available or closed");
                return;
            }
            
            // Use the correct column name that exists in your database
            byte[] imagedata = rs.getBytes("room_image");  // Changed from "imageFile" to "room_image"
            
            if (imagedata == null || imagedata.length == 0) {
                labelDisplayImage.setIcon(null);
                System.out.println("No image data found for this record");
                return;
            }
            
            // Load and scale the image
            ImageIcon format = new ImageIcon(imagedata);
            Image mm = format.getImage();
            Image img2 = mm.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon image = new ImageIcon(img2);
            labelDisplayImage.setIcon(image);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        spinnerMaxOccupancy = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtRoomPrice = new javax.swing.JTextField();
        rSButtonHover1 = new rojeru_san.complementos.RSButtonHover();
        rSButtonHover2 = new rojeru_san.complementos.RSButtonHover();
        jLabel7 = new javax.swing.JLabel();
        labelDisplayImage = new javax.swing.JLabel();
        timePickerButton1 = new com.raven.swing.TimePickerButton();
        cmbRoomType = new GUI.ComboBoxSuggestion();
        cmbRoomNumber = new GUI.ComboBoxSuggestion();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(39, 114, 160), 7));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(27, 59, 95)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel1.setText("ROOM NUMBER");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 122, 30));

        jLabel2.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel2.setText("ROOM TYPE");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 300, 122, 30));

        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel4.setText("MAX OCCUPANCY");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 440, 140, 30));

        txtDescription.setBackground(new java.awt.Color(242, 242, 242));
        txtDescription.setColumns(20);
        txtDescription.setForeground(new java.awt.Color(102, 102, 102));
        txtDescription.setRows(5);
        txtDescription.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jScrollPane1.setViewportView(txtDescription);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 340, 240, -1));
        jPanel2.add(spinnerMaxOccupancy, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 440, 240, 30));

        jLabel5.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel5.setText("DESCRIPTION");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 340, 122, 30));

        jLabel3.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel3.setText("PRICE/DAY");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 480, 122, 30));

        txtRoomPrice.setBackground(new java.awt.Color(242, 242, 242));
        txtRoomPrice.setForeground(new java.awt.Color(102, 102, 102));
        txtRoomPrice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel2.add(txtRoomPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 480, 240, 30));

        rSButtonHover1.setBackground(new java.awt.Color(27, 59, 95));
        rSButtonHover1.setText("CANCEL");
        rSButtonHover1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSButtonHover1ActionPerformed(evt);
            }
        });
        jPanel2.add(rSButtonHover1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 530, 160, -1));

        rSButtonHover2.setBackground(new java.awt.Color(27, 59, 95));
        rSButtonHover2.setText("CONTINUE");
        rSButtonHover2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSButtonHover2ActionPerformed(evt);
            }
        });
        jPanel2.add(rSButtonHover2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 530, 240, -1));

        jLabel7.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("ROOM IMAGE");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 122, 30));

        labelDisplayImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDisplayImage.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel2.add(labelDisplayImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, 240, 160));

        timePickerButton1.setBackground(new java.awt.Color(27, 59, 95));
        timePickerButton1.setText("Choose Image");
        timePickerButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timePickerButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(timePickerButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 260, 240, -1));

        cmbRoomType.setEditable(false);
        cmbRoomType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Single", "Double" }));
        cmbRoomType.setSelectedIndex(-1);
        jPanel2.add(cmbRoomType, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 300, 240, -1));

        cmbRoomNumber.setEditable(false);
        cmbRoomNumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Single", "Double" }));
        cmbRoomNumber.setSelectedIndex(-1);
        cmbRoomNumber.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbRoomNumberItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbRoomNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 240, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 560, 610));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Update Room Details");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 560, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 720));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rSButtonHover1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonHover1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_rSButtonHover1ActionPerformed

    private void rSButtonHover2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonHover2ActionPerformed
    try {                                               
        InputStream is = null;
        
        // Get input values
        String roomNumber = (String) cmbRoomNumber.getSelectedItem();
        String roomType = (String) cmbRoomType.getSelectedItem();
        String description = txtDescription.getText().trim();
        String roomPriceText = txtRoomPrice.getText().trim();
        int maxOccupancy = (int) spinnerMaxOccupancy.getValue();
        
        // Validate required fields
        if (roomNumber == null || roomNumber.isEmpty() ||
                roomType == null || roomType.isEmpty() ||
                description.isEmpty() || roomPriceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate room price
        double roomPrice;
        try {
            roomPrice = Double.parseDouble(roomPriceText);
            if (roomPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Room price must be a positive number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid room price format!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate max occupancy
        if (maxOccupancy <= 0) {
            JOptionPane.showMessageDialog(this, "Max occupancy must be greater than 0!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm update
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to update the room information?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Prepare image stream if path exists
        File f = new File(path);
        if (f.exists()) {
            try {
                is = new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        
        // Prepare SQL query
        String sql;
        if (is != null) {
            sql = "UPDATE room SET room_type = ?, room_image = ?, description = ?, " +
                    "room_price = ?, max_occupancy = ? WHERE room_number = ?";
        } else {
            sql = "UPDATE room SET room_type = ?, description = ?, " +
                    "room_price = ?, max_occupancy = ? WHERE room_number = ?";
        }
        
        // Execute update
        pst = con.prepareStatement(sql);
        
        if (is != null) {
            try {
                pst.setString(1, roomType);
                pst.setBinaryStream(2, is, (int)f.length());
                pst.setString(3, description);
                pst.setDouble(4, roomPrice);
                pst.setInt(5, maxOccupancy);
                pst.setString(6, roomNumber);
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
            try {
                pst.setString(1, roomType);
                pst.setString(2, description);
                pst.setDouble(3, roomPrice);
                pst.setInt(4, maxOccupancy);
                pst.setString(5, roomNumber);
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        
        int rowsUpdated = pst.executeUpdate();
        
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Room updated successfully!");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Room number not found or no changes made.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
        
    } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }



    }//GEN-LAST:event_rSButtonHover2ActionPerformed

    private void timePickerButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timePickerButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fnwf = new FileNameExtensionFilter("PNG AND JPEG", "png", "jpeg", "jpg");
        fileChooser.addChoosableFileFilter(fnwf);
        int load = fileChooser.showOpenDialog(null);

        if(load == fileChooser.APPROVE_OPTION){
            f = fileChooser.getSelectedFile();
            path = f.getAbsolutePath();

            ImageIcon ii = new ImageIcon(path);
            Image img = ii.getImage().getScaledInstance(labelDisplayImage.getWidth(), 
                            labelDisplayImage.getHeight(),  Image.SCALE_SMOOTH);
            labelDisplayImage.setIcon(new ImageIcon(img));
}       
    }//GEN-LAST:event_timePickerButton1ActionPerformed

    private void cmbRoomNumberItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbRoomNumberItemStateChanged
        try {
            // Only proceed if an item is selected and it's not the default empty value
            String selectedRoomNumber = (String) cmbRoomNumber.getSelectedItem();
            
// If no room number is selected (empty or null), do nothing
if (selectedRoomNumber == null || selectedRoomNumber.isEmpty() || selectedRoomNumber.equals("Select a Room Number")) {
   
    return;
}



// Prepare the SQL query to fetch all room details including image
String sql = "SELECT room_type, description, room_price, max_occupancy, room_image FROM room WHERE room_number = ?";
pst = con.prepareStatement(sql);
pst.setString(1, selectedRoomNumber);

// Execute the query
rs = pst.executeQuery();

// Check if the room exists in the database
if (rs.next()) {
    // Set the values of the form fields
    cmbRoomType.setSelectedItem(rs.getString("room_type"));
    txtDescription.setText(rs.getString("description"));
    txtRoomPrice.setText(String.format("%.2f", rs.getDouble("room_price"))); // Format price
    spinnerMaxOccupancy.setValue(rs.getInt("max_occupancy"));
    
    // Load and display the room image
    loadRoomImage(rs.getBytes("room_image"));
} else {
    JOptionPane.showMessageDialog(this, "Room not found in database", "Error", JOptionPane.WARNING_MESSAGE);
    
}   } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_cmbRoomNumberItemStateChanged

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
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adminUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminUpdateRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private GUI.ComboBoxSuggestion cmbRoomNumber;
    private GUI.ComboBoxSuggestion cmbRoomType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelDisplayImage;
    private rojeru_san.complementos.RSButtonHover rSButtonHover1;
    private rojeru_san.complementos.RSButtonHover rSButtonHover2;
    private javax.swing.JSpinner spinnerMaxOccupancy;
    private com.raven.swing.TimePickerButton timePickerButton1;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtRoomPrice;
    // End of variables declaration//GEN-END:variables
}
