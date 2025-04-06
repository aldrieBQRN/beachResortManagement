/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Guest;

import Staff.*;
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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author yeojvaldez
 */
public class test3 extends javax.swing.JInternalFrame {

    /**
     * Creates new form staffReservation
     */
    public test3() {
        initComponents();
        removeBackground();
        DatabaseConnection();
        showRoom();
        
 
    }
    
    Connection con; 
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
    
    public final void removeBackground(){
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI UI = (BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null); 
    }
    
    public final void showRoom() {
    try {
        pst = con.prepareStatement("SELECT * FROM room");
        rs = pst.executeQuery();
        
        // Create table model with delete column
        DefaultTableModel roomModel = new DefaultTableModel(
            new Object[]{"Room Number", "Room Type", "Description", "Max Occupancy", "Price", "Action"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only make the action column editable
                return column == 5;
            }
        };
        
        tblroom.setModel(roomModel);
        
        while (rs.next()) {
            String roomNumber = rs.getString("room_number");
            String roomType = rs.getString("room_type");
            double price = rs.getDouble("room_price");
            String description = rs.getString("description");
            int maxOccupancy = rs.getInt("max_occupancy");
            
            // Add row with delete button
            roomModel.addRow(new Object[]{
                roomNumber, 
                roomType, 
                description, 
                maxOccupancy, 
                price,
                "Delete" // This will be rendered as a button
            });
        }
        
        // Add button renderer and editor
        tblroom.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tblroom.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
    } catch (SQLException ex) {
        Logger.getLogger(test3.class.getName()).log(Level.SEVERE, null, ex);
        System.out.println("Error fetching room data: " + ex.getMessage());
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
                deleteRoom(clickedRow);
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

// Delete Room Method
private void deleteRoom(int row) {
    String roomNumber = (String) tblroom.getValueAt(row, 0);
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to delete room " + roomNumber + "?", 
        "Confirm Delete", 
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            pst = con.prepareStatement("DELETE FROM room WHERE room_number = ?");
            pst.setString(1, roomNumber);
            pst.executeUpdate();
            showRoom(); // Refresh the table
            JOptionPane.showMessageDialog(this, "Room deleted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting room: " + ex.getMessage());
        }
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

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblroom = new rojerusan.RSTableMetro();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblroom.setBackground(new java.awt.Color(255, 255, 255));
        tblroom.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        tblroom.setForeground(new java.awt.Color(255, 255, 255));
        tblroom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Room Number", "Room Type", "Description", "Max Occupancy", "Price/Day"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 1120, 430));

        jPanel6.setBackground(new java.awt.Color(51, 204, 0));
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
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 40));

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 20, 120, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1160, 660));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblroomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblroomMouseClicked

    }//GEN-LAST:event_tblroomMouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        new roomReservation().setVisible(true);
    }//GEN-LAST:event_jLabel4MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private rojerusan.RSTableMetro tblroom;
    // End of variables declaration//GEN-END:variables
}
