/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Staff;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
  import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author yeojvaldez
 */
public final class staffReservation extends javax.swing.JInternalFrame {

    /**
     * Creates new form staffReservation
     */
    public staffReservation() {
        initComponents();
        removeBackground();
        DatabaseConnection();
        fetchPendingRoomReservations();
        
 
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
    
  
 private void fetchPendingRoomReservations() {
    try {
        pst = con.prepareStatement("SELECT reservation_number, guest.guest_name, r.check_in_date, r.check_out_date, r.total_price, r.created_at, r.reservation_id "
                                  + "FROM reservation r "
                                  + "JOIN guest ON r.guest_id = guest.guest_id "
                                  + "WHERE r.status = 'Pending'");

        rs = pst.executeQuery();

        DefaultTableModel reservationModel = new DefaultTableModel(
            new Object[]{"Reservation Number", "Guest Name", "Check-In Date", "Check-Out Date", "Total Price", "Created At", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only make the actions column editable
                return column == 6;
            }
        };

        tblReservation.setModel(reservationModel);
        
        // Set up the combined panel renderer and editor
        TableColumn actionColumn = tblReservation.getColumnModel().getColumn(6);
        actionColumn.setCellRenderer(new DualPanelRenderer());
        actionColumn.setCellEditor(new DualPanelEditor(new JCheckBox()));

        boolean found = false;

        while (rs.next()) {
            found = true;

            String reservationNumber = rs.getString("reservation_number");
            String guestName = rs.getString("guest_name");
            Date checkInDate = rs.getDate("check_in_date");
            Date checkOutDate = rs.getDate("check_out_date");
            Timestamp createdAt = rs.getTimestamp("created_at");
            double totalPrice = rs.getDouble("total_price");
            int reservationId = rs.getInt("reservation_id");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedCheckInDate = sdf.format(checkInDate);
            String formattedCheckOutDate = sdf.format(checkOutDate);
            String formattedCreatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);

            reservationModel.addRow(new Object[]{
                reservationNumber,
                guestName,
                formattedCheckInDate,
                formattedCheckOutDate,
                "₱" + String.format("%.2f", totalPrice),
                formattedCreatedAt,
                reservationId // Store the reservation ID in the actions column
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}

// Updated DualPanelRenderer class with white borders
// Updated DualPanelRenderer with new View color
class DualPanelRenderer extends JPanel implements TableCellRenderer {
    private JPanel viewPanel;
    private JPanel confirmPanel;
    private JLabel viewLabel;
    private JLabel confirmLabel;
    
    // Define colors as constants
    private static final Color VIEW_COLOR = new Color(27, 59, 95);
    private static final Color VIEW_HOVER = new Color(47, 79, 115);
    private static final Color VIEW_SELECTED = new Color(67, 99, 135);
    private static final Color CONFIRM_COLOR = new Color(51,204,0);
    private static final Color CONFIRM_HOVER = new Color(54, 159, 54);
    private static final Color CONFIRM_SELECTED = new Color(50, 160, 50);

    public DualPanelRenderer() {
        setLayout(new GridLayout(1, 2, 0, 0));
        setOpaque(true);
        
        // View Panel
        viewPanel = new JPanel(new GridBagLayout());
        viewPanel.setBackground(VIEW_COLOR);
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));
        
        viewLabel = new JLabel("View");
        viewLabel.setForeground(Color.WHITE);
        viewLabel.setFont(viewLabel.getFont().deriveFont(Font.BOLD));
        viewPanel.add(viewLabel, new GridBagConstraints());
        
        // Confirm Panel
        confirmPanel = new JPanel(new GridBagLayout());
        confirmPanel.setBackground(CONFIRM_COLOR);
        confirmPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        confirmLabel = new JLabel("Confirm");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(confirmLabel.getFont().deriveFont(Font.BOLD));
        confirmPanel.add(confirmLabel, new GridBagConstraints());
        
        add(viewPanel);
        add(confirmPanel);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            viewPanel.setBackground(VIEW_SELECTED);
            confirmPanel.setBackground(CONFIRM_SELECTED);
        } else {
            setBackground(table.getBackground());
            viewPanel.setBackground(VIEW_COLOR);
            confirmPanel.setBackground(CONFIRM_COLOR);
        }
        
        // Maintain white borders
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));
        confirmPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        return this;
    }
}

// Updated DualPanelEditor with new View color
class DualPanelEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel mainPanel;
    private JPanel viewPanel;
    private JPanel confirmPanel;
    private JLabel viewLabel;
    private JLabel confirmLabel;
    private int currentRow;
    private Object currentValue;
    
    // Reuse the same color constants
    private static final Color VIEW_COLOR = new Color(27, 59, 95);
    private static final Color VIEW_HOVER = new Color(47, 79, 115);
    private static final Color VIEW_SELECTED = new Color(67, 99, 135);
    private static final Color CONFIRM_COLOR = new Color(51,204,0);
    private static final Color CONFIRM_HOVER = new Color(54, 159, 54);
    private static final Color CONFIRM_SELECTED = new Color(50, 160, 50);

    public DualPanelEditor(JCheckBox checkBox) {
        mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setOpaque(true);
        
        // View Panel
        viewPanel = new JPanel(new GridBagLayout());
        viewPanel.setBackground(VIEW_COLOR);
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));
        
        viewLabel = new JLabel("View");
        viewLabel.setForeground(Color.WHITE);
        viewLabel.setFont(viewLabel.getFont().deriveFont(Font.BOLD));
        viewPanel.add(viewLabel, new GridBagConstraints());
        
        // Mouse listeners for View panel
        viewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fireEditingStopped();
                handleButtonClick(currentRow, "View");
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                viewPanel.setBackground(VIEW_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                viewPanel.setBackground(VIEW_COLOR);
            }
        });
        
        // Confirm Panel
        confirmPanel = new JPanel(new GridBagLayout());
        confirmPanel.setBackground(CONFIRM_COLOR);
        confirmPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        confirmLabel = new JLabel("Confirm");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(confirmLabel.getFont().deriveFont(Font.BOLD));
        confirmPanel.add(confirmLabel, new GridBagConstraints());
        
        // Mouse listeners for Confirm panel
        confirmPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fireEditingStopped();
                handleButtonClick(currentRow, "Confirm");
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                confirmPanel.setBackground(CONFIRM_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                confirmPanel.setBackground(CONFIRM_COLOR);
            }
        });
        
        mainPanel.add(viewPanel);
        mainPanel.add(confirmPanel);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                              boolean isSelected, int row, int column) {
        currentRow = row;
        currentValue = value;
        
        if (isSelected) {
            mainPanel.setBackground(table.getSelectionBackground());
            viewPanel.setBackground(VIEW_SELECTED);
            confirmPanel.setBackground(CONFIRM_SELECTED);
        } else {
            mainPanel.setBackground(table.getBackground());
            viewPanel.setBackground(VIEW_COLOR);
            confirmPanel.setBackground(CONFIRM_COLOR);
        }
        
        // Maintain white borders
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));
        confirmPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        return mainPanel;
    }

    public Object getCellEditorValue() {
        return currentValue;
    }
}

// Keep your existing handleButtonClick, viewReservationDetails, and confirmReservation methods

   private void handleButtonClick(int row, String action) {
    String reservationNumber = (String) tblReservation.getValueAt(row, 0);
    
    if ("View".equals(action)) {
        viewReservationDetails(reservationNumber);
    } else if ("Confirm".equals(action)) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to confirm reservation number " + reservationNumber + "?",
                "Confirm Reservation",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            confirmReservation(reservationNumber);
        }
    }
}
   
   
    private void viewReservationDetails(String reservationNumber) {
        try {
            pst = con.prepareStatement("SELECT * FROM reservation WHERE reservation_number = ?");
            pst.setString(1, reservationNumber);
            rs = pst.executeQuery();

            if (rs.next()) {
                String guestName = rs.getString("guest_id");
                Date checkInDate = rs.getDate("check_in_date");
                Date checkOutDate = rs.getDate("check_out_date");
                double totalPrice = rs.getDouble("total_price");
                String reservationStatus = rs.getString("status");

                JOptionPane.showMessageDialog(this,
                        "Reservation Number: " + reservationNumber + "\n"
                                + "Guest Name: " + guestName + "\n"
                                + "Check-In Date: " + checkInDate + "\n"
                                + "Check-Out Date: " + checkOutDate + "\n"
                                + "Total Price: ₱" + totalPrice + "\n"
                                + "Status: " + reservationStatus);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching reservation details: " + ex.getMessage());
        }
    }
    

    private void confirmReservation(String reservationNumber) {
        try {
            String updateQuery = "UPDATE reservation SET status = 'Confirmed' WHERE reservation_number = ?";
            pst = con.prepareStatement(updateQuery);
            pst.setString(1, reservationNumber);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Reservation confirmed successfully!");
                fetchPendingRoomReservations();
            } else {
                JOptionPane.showMessageDialog(this, "Error confirming reservation.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error confirming reservation: " + ex.getMessage());
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
        txtsearch = new javax.swing.JTextField();
        rSComboMetro1 = new rojerusan.RSComboMetro();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservation = new rojerusan.RSTableMetro();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setPreferredSize(new java.awt.Dimension(1170, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtsearch.setBackground(new java.awt.Color(255, 255, 255));
        txtsearch.setForeground(new java.awt.Color(102, 102, 102));
        txtsearch.setText("Seach here...");
        txtsearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        txtsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsearchActionPerformed(evt);
            }
        });
        jPanel2.add(txtsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 270, 40));

        rSComboMetro1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Room Number", "Type", "Occupancy", "Price", " ", " " }));
        rSComboMetro1.setColorArrow(new java.awt.Color(27, 59, 95));
        rSComboMetro1.setColorBorde(new java.awt.Color(39, 114, 160));
        rSComboMetro1.setColorFondo(new java.awt.Color(39, 114, 160));
        rSComboMetro1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSComboMetro1ActionPerformed(evt);
            }
        });
        jPanel2.add(rSComboMetro1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 100, 40));

        tblReservation.setBackground(new java.awt.Color(255, 255, 255));
        tblReservation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        tblReservation.setForeground(new java.awt.Color(255, 255, 255));
        tblReservation.setModel(new javax.swing.table.DefaultTableModel(
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
                "Date Created", "Reservation ID", "Guest Name", "Check-In", "Check-Out", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblReservation.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        tblReservation.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        tblReservation.setColorBordeHead(new java.awt.Color(255, 255, 255));
        tblReservation.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        tblReservation.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        tblReservation.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        tblReservation.setColorSelBackgound(new java.awt.Color(39, 114, 160));
        tblReservation.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        tblReservation.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblReservation.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblReservation.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tblReservation.setGridColor(new java.awt.Color(255, 255, 255));
        tblReservation.setRowHeight(30);
        tblReservation.setSelectionBackground(new java.awt.Color(39, 114, 160));
        tblReservation.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblReservation.setShowGrid(true);
        tblReservation.setShowHorizontalLines(false);
        tblReservation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReservationMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblReservation);
        if (tblReservation.getColumnModel().getColumnCount() > 0) {
            tblReservation.getColumnModel().getColumn(0).setResizable(false);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 1120, 570));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 1160, 660));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(39, 114, 160)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel1.setText("Reservation");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 180, 40));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1160, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 760));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rSComboMetro1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSComboMetro1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSComboMetro1ActionPerformed

    private void txtsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsearchActionPerformed

    private void tblReservationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReservationMouseClicked

    }//GEN-LAST:event_tblReservationMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private rojerusan.RSComboMetro rSComboMetro1;
    private rojerusan.RSTableMetro tblReservation;
    private javax.swing.JTextField txtsearch;
    // End of variables declaration//GEN-END:variables
}
