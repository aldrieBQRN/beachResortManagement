/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Staff;

import Admin.adminRoom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import Database.DatabaseConnection; 
import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author yeojvaldez
 */
public class staffRooms extends javax.swing.JInternalFrame {

    /**
     * Creates new form staffReservation
     */
    public staffRooms() {
        initComponents();
        removeBackground();
        DatabaseConnection();
        showRoomDetails();
        
 
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
    
 public final void showRoomDetails() {
    try {
        pst = con.prepareStatement("SELECT * FROM room");
        rs = pst.executeQuery();

        DefaultTableModel roomModel = (DefaultTableModel) tblroom.getModel();
        roomModel.setRowCount(0); // clear table

        // Add custom renderer just for the image column (column 1)
        tblroom.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel((ImageIcon) value);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    if (isSelected) {
                        label.setBackground(table.getSelectionBackground());
                        label.setOpaque(true);
                    }
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, 
                        hasFocus, row, column);
            }
        });

        while (rs.next()) {
            String roomNumber = rs.getString("room_number");
            String roomType = rs.getString("room_type");
            double price = rs.getDouble("room_price");
            String description = rs.getString("description");
            int maxOccupancy = rs.getInt("max_occupancy");

            // Get the image as bytes
            byte[] imgBytes = rs.getBytes("room_image");
            ImageIcon imageIcon = null;

            if (imgBytes != null) {
                Image img = new ImageIcon(imgBytes).getImage();
                img = img.getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(img);
            } else {
                // Set a default blank icon if no image exists
                imageIcon = new ImageIcon(new BufferedImage(100, 80, BufferedImage.TYPE_INT_ARGB));
            }

            // Add row to model
            roomModel.addRow(new Object[] {
                roomNumber,
                imageIcon,
                roomType,
                description,
                maxOccupancy,
                price
            });
        }

        // Set the row height to fit the image
        tblroom.setRowHeight(80);

    } catch (SQLException ex) {
        Logger.getLogger(adminRoom.class.getName()).log(Level.SEVERE, null, ex);
        System.out.println("Error fetching room data: " + ex.getMessage());
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblroom = new rojerusan.RSTableMetro();
        txtsearch = new textfield_suggestion.TextFieldSuggestion();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setPreferredSize(new java.awt.Dimension(1170, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblroom.setBackground(new java.awt.Color(255, 255, 255));
        tblroom.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        tblroom.setForeground(new java.awt.Color(255, 255, 255));
        tblroom.setModel(new javax.swing.table.DefaultTableModel(
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
                "Room Number", "Room Image", "Type", "Description", "Max Occupancy", "Price/Day"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblroom.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        tblroom.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        tblroom.setColorBordeHead(new java.awt.Color(255, 255, 255));
        tblroom.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        tblroom.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        tblroom.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        tblroom.setColorSelBackgound(new java.awt.Color(39, 114, 160));
        tblroom.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        tblroom.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblroom.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblroom.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tblroom.setGridColor(new java.awt.Color(255, 255, 255));
        tblroom.setRowHeight(30);
        tblroom.setSelectionBackground(new java.awt.Color(39, 114, 160));
        tblroom.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblroom.setShowGrid(false);
        tblroom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblroomMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblroom);
        if (tblroom.getColumnModel().getColumnCount() > 0) {
            tblroom.getColumnModel().getColumn(0).setResizable(false);
            tblroom.getColumnModel().getColumn(0).setPreferredWidth(5);
            tblroom.getColumnModel().getColumn(1).setResizable(false);
            tblroom.getColumnModel().getColumn(1).setPreferredWidth(5);
            tblroom.getColumnModel().getColumn(2).setResizable(false);
            tblroom.getColumnModel().getColumn(2).setPreferredWidth(5);
            tblroom.getColumnModel().getColumn(3).setResizable(false);
            tblroom.getColumnModel().getColumn(4).setResizable(false);
            tblroom.getColumnModel().getColumn(4).setPreferredWidth(5);
            tblroom.getColumnModel().getColumn(5).setResizable(false);
            tblroom.getColumnModel().getColumn(5).setPreferredWidth(5);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 1120, 570));

        txtsearch.setForeground(new java.awt.Color(102, 102, 102));
        txtsearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtsearch.setText("Search here...");
        txtsearch.setSelectedTextColor(new java.awt.Color(102, 102, 102));
        txtsearch.setSelectionColor(new java.awt.Color(102, 102, 102));
        txtsearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtsearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtsearchFocusLost(evt);
            }
        });
        txtsearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtsearchKeyReleased(evt);
            }
        });
        jPanel2.add(txtsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 270, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 1160, 660));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(39, 114, 160)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("List of Rooms");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 40));

        jPanel6.setBackground(new java.awt.Color(102, 102, 102));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(51, 51, 51));
        jLabel4.setFont(new java.awt.Font("Arial Unicode MS", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("View Reservation");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 30));

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 20, 120, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1160, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 760));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        new staffReservationRoom().setVisible(true);
    }//GEN-LAST:event_jLabel4MouseClicked

    private void tblroomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblroomMouseClicked

    }//GEN-LAST:event_tblroomMouseClicked

    private void txtsearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsearchFocusGained

        if(txtsearch.getText().equals("Search here...")){
            txtsearch.setText("");

        }
    }//GEN-LAST:event_txtsearchFocusGained

    private void txtsearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsearchFocusLost
        if(txtsearch.getText().equals("")){
            txtsearch.setText("Search here...");

        }
    }//GEN-LAST:event_txtsearchFocusLost

    private void txtsearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtsearchKeyReleased

        DefaultTableModel obj =(DefaultTableModel) tblroom.getModel();
        TableRowSorter<DefaultTableModel> obj1=new TableRowSorter<>(obj);
        tblroom.setRowSorter(obj1);
        obj1.setRowFilter(RowFilter.regexFilter(txtsearch.getText()));

    }//GEN-LAST:event_txtsearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private rojerusan.RSTableMetro tblroom;
    private textfield_suggestion.TextFieldSuggestion txtsearch;
    // End of variables declaration//GEN-END:variables
}
