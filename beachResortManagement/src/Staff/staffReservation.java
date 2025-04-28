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
import javax.swing.RowFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

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
        txtdate3.setText("");
        txtexit.setVisible(false);
        
 
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
            new Object[]{"Created At","Reservation Number", "Guest Name", "Check-In Date", "Check-Out Date", "Total Price",  "Actions"}, 0
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
        // First get reservation details
        pst = con.prepareStatement("SELECT r.*, g.guest_name FROM reservation r " +
                                  "JOIN guest g ON r.guest_id = g.guest_id " +
                                  "WHERE r.reservation_number = ?");
        pst.setString(1, reservationNumber);
        rs = pst.executeQuery();

        if (rs.next()) {
           
            String reservationStatus = rs.getString("status");
            int reservationId = rs.getInt("reservation_id");

            // Now get payment details
            String paymentInfo = "";
            PreparedStatement paymentStmt = con.prepareStatement(
                "SELECT * FROM payment WHERE reservation_id = ? AND payment_type = 'Downpayment'");
            paymentStmt.setInt(1, reservationId);
            ResultSet paymentRs = paymentStmt.executeQuery();

            if (paymentRs.next()) {
                double downPayment = paymentRs.getDouble("amount");
                String referenceNumber = paymentRs.getString("reference_number");
                String paymentMethod = paymentRs.getString("payment_method");
                String paymentStatus = paymentRs.getString("status");

                paymentInfo = "\nDownpayment Details:\n" +
                             "Amount: ₱" + String.format("%.2f", downPayment) + "\n" +
                             "Payment Method: " + paymentMethod + "\n" +
                             "Reference Number: " + (referenceNumber != null ? referenceNumber : "N/A") + "\n" +
                             "Payment Status: " + paymentStatus;
            }

            JOptionPane.showMessageDialog(this,
               
                "Reservation Status: " + reservationStatus +
                paymentInfo,
                "Reservation Details",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No reservation found with number: " + reservationNumber);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error fetching reservation details: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } finally {
        // Close resources
        try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        try { if (pst != null) pst.close(); } catch (SQLException e) { /* ignore */ }
    }
}

private void confirmReservation(String reservationNumber) {
    PreparedStatement updateReservationStmt = null;
    PreparedStatement updatePaymentStmt = null;
    PreparedStatement getIdStmt = null;
    ResultSet rs = null;
    
    try {
        // Start transaction
        con.setAutoCommit(false);

        // 1. First get the reservation_id
        String getIdQuery = "SELECT reservation_id FROM reservation WHERE reservation_number = ?";
        getIdStmt = con.prepareStatement(getIdQuery);
        getIdStmt.setString(1, reservationNumber);
        rs = getIdStmt.executeQuery();
        
        if (!rs.next()) {
            JOptionPane.showMessageDialog(this,
                "Reservation not found with number: " + reservationNumber,
                "Not Found",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int reservationId = rs.getInt("reservation_id");

        // 2. Update reservation status
        String updateReservationQuery = "UPDATE reservation SET status = 'Confirmed' WHERE reservation_id = ?";
        updateReservationStmt = con.prepareStatement(updateReservationQuery);
        updateReservationStmt.setInt(1, reservationId);
        int reservationUpdated = updateReservationStmt.executeUpdate();

        // 3. Update payment status (only for downpayment)
        String updatePaymentQuery = "UPDATE payment SET status = 'Paid' " +
                                 "WHERE reservation_id = ? AND payment_type = 'Downpayment'";
        updatePaymentStmt = con.prepareStatement(updatePaymentQuery);
        updatePaymentStmt.setInt(1, reservationId);
        int paymentUpdated = updatePaymentStmt.executeUpdate();

        // Check if both updates were successful
        if (reservationUpdated > 0 && paymentUpdated > 0) {
            con.commit();
            JOptionPane.showMessageDialog(this, 
                "Reservation #" + reservationNumber + " confirmed successfully!\n" +
                "Payment status updated to 'Paid'.",
                "Confirmation Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            fetchPendingRoomReservations();
        } else {
            con.rollback();
            String errorMsg = "Failed to confirm reservation.\n";
            if (reservationUpdated == 0) errorMsg += "- Reservation not found\n";
            if (paymentUpdated == 0) errorMsg += "- Downpayment not found";
            
            JOptionPane.showMessageDialog(this,
                errorMsg,
                "Confirmation Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException e) {
            ex.addSuppressed(e);
        }
        JOptionPane.showMessageDialog(this,
            "Database error while confirming reservation:\n" + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } finally {
        // Restore auto-commit and close resources
        try {
            if (con != null) con.setAutoCommit(true);
            if (rs != null) rs.close();
            if (getIdStmt != null) getIdStmt.close();
            if (updateReservationStmt != null) updateReservationStmt.close();
            if (updatePaymentStmt != null) updatePaymentStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
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

        dateChooserCheckin = new com.raven.datechooser.DateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservation = new rojerusan.RSTableMetro();
        txtsearch = new textfield_suggestion.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        txtexit = new javax.swing.JLabel();
        txtdate3 = new textfield_suggestion.TextFieldSuggestion();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        dateChooserCheckin.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckin.setDateFormat("MMMM dd, yyyy");
        dateChooserCheckin.setTextRefernce(txtdate3);

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setPreferredSize(new java.awt.Dimension(1170, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        txtsearch.setForeground(new java.awt.Color(102, 102, 102));
        txtsearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtsearch.setText("Search here..");
        txtsearch.setSelectedTextColor(new java.awt.Color(102, 102, 102));
        txtsearch.setSelectionColor(new java.awt.Color(102, 102, 102));
        txtsearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtsearchKeyReleased(evt);
            }
        });
        jPanel2.add(txtsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 900, 40));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 40, 40));

        txtexit.setForeground(new java.awt.Color(102, 102, 102));
        txtexit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtexit.setText("X");
        txtexit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtexitMouseClicked(evt);
            }
        });
        jPanel2.add(txtexit, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 20, 40));

        txtdate3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtdate3.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtdate3.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtdate3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtdate3MouseClicked(evt);
            }
        });
        txtdate3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdate3ActionPerformed(evt);
            }
        });
        jPanel2.add(txtdate3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 180, 40));

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

    private void tblReservationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReservationMouseClicked

    }//GEN-LAST:event_tblReservationMouseClicked

    private void txtdate3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdate3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtdate3ActionPerformed

    private void txtsearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtsearchKeyReleased
          DefaultTableModel obj =(DefaultTableModel) tblReservation.getModel();
        TableRowSorter<DefaultTableModel> obj1=new TableRowSorter<>(obj);
        tblReservation.setRowSorter(obj1);
        obj1.setRowFilter(RowFilter.regexFilter(txtsearch.getText()));
    }//GEN-LAST:event_txtsearchKeyReleased

    private void txtexitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtexitMouseClicked
        txtdate3.setText("");
    }//GEN-LAST:event_txtexitMouseClicked

    private void txtdate3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtdate3MouseClicked
        txtexit.setVisible(true);
    }//GEN-LAST:event_txtdate3MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.datechooser.DateChooser dateChooserCheckin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private rojerusan.RSTableMetro tblReservation;
    private textfield_suggestion.TextFieldSuggestion txtdate3;
    private javax.swing.JLabel txtexit;
    private textfield_suggestion.TextFieldSuggestion txtsearch;
    // End of variables declaration//GEN-END:variables
}
