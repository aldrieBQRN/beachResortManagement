/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Staff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import Database.DatabaseConnection; 
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author yeojvaldez
 */
public class staffCheckin extends javax.swing.JInternalFrame {

    private static final String CONFIRMED_STATUS = "Confirmed";
    private static final String CHECKIN_STATUS = "Check-in";
    private int userID;
    
    public staffCheckin(int userID) {
        this.userID = userID;
        initComponents();
        removeBackground();
        DatabaseConnection();
        DatabaseConnection();
        fetchRoomReservations(checkInTable, CONFIRMED_STATUS);
        fetchRoomReservations(checkOutTable, CHECKIN_STATUS);
    
 
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

    private void fetchRoomReservations(JTable table, String status) {
    try {
        DatabaseConnection();
        String query = "SELECT reservation_number, guest.guest_name, r.check_in_date, " +
                "r.check_out_date, r.total_price, r.created_at, r.reservation_id " +
                "FROM reservation r " +
                "JOIN guest ON r.guest_id = guest.guest_id " +
                "WHERE r.status = ?";

        pst = con.prepareStatement(query);
        pst.setString(1, status);
        rs = pst.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Reservation Number", "Guest Name", "Check-In Date",
                        "Check-Out Date", "Total Price", "Created At", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table.setModel(model);

        TableColumn actionColumn = table.getColumnModel().getColumn(6);
        actionColumn.setCellRenderer(new DualPanelRenderer());
        actionColumn.setCellEditor(new DualPanelEditor(new JCheckBox(), table));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString("reservation_number"),
                    rs.getString("guest_name"),
                    dateFormat.format(rs.getDate("check_in_date")),
                    dateFormat.format(rs.getDate("check_out_date")),
                    "₱" + String.format("%.2f", rs.getDouble("total_price")),
                    timestampFormat.format(rs.getTimestamp("created_at")),
                    rs.getInt("reservation_id")
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pst != null) pst.close(); } catch (SQLException e) {}
        try { if (con != null) con.close(); } catch (SQLException e) {}
    }
}

class DualPanelRenderer extends JPanel implements TableCellRenderer {
    private JPanel viewPanel;
    private JPanel actionPanel;
    private JLabel viewLabel;
    private JLabel actionLabel;
    
    private static final Color VIEW_COLOR = new Color(27, 59, 95);
    private static final Color VIEW_HOVER = new Color(47, 79, 115);
    private static final Color VIEW_SELECTED = new Color(67, 99, 135);
    private static final Color ACTION_COLOR = new Color(51,204,0); 
    private static final Color ACTION_HOVER = new Color(54, 159, 54);
    private static final Color ACTION_SELECTED = new Color(50, 160, 50);

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
        
        // Action Panel
        actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(ACTION_COLOR);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        actionLabel = new JLabel("Action");
        actionLabel.setForeground(Color.WHITE);
        actionLabel.setFont(actionLabel.getFont().deriveFont(Font.BOLD));
        actionPanel.add(actionLabel, new GridBagConstraints());
        
        add(viewPanel);
        add(actionPanel);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
        String status = "";
        if (table == checkInTable) {
            status = "Check-in";
        } else if (table == checkOutTable) {
            status = "Check-out";
        } else {
            status = "Confirm";
        }
        actionLabel.setText(status);
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            viewPanel.setBackground(VIEW_SELECTED);
            actionPanel.setBackground(ACTION_SELECTED);
        } else {
            setBackground(table.getBackground());
            viewPanel.setBackground(VIEW_COLOR);
            actionPanel.setBackground(ACTION_COLOR);
        }
        
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        return this;
    }
}

class DualPanelEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel mainPanel;
    private JPanel viewPanel;
    private JPanel actionPanel;
    private JLabel viewLabel;
    private JLabel actionLabel;
    private JTable table;
    private int currentRow;
    private Object currentValue;
    
    private static final Color VIEW_COLOR = new Color(27, 59, 95);
    private static final Color VIEW_HOVER = new Color(47, 79, 115);
    private static final Color VIEW_SELECTED = new Color(67, 99, 135);
    private static final Color ACTION_COLOR = new Color(51,204,0);
    private static final Color ACTION_HOVER = new Color(54, 159, 54);
    private static final Color ACTION_SELECTED = new Color(50, 160, 50);

    public DualPanelEditor(JCheckBox checkBox, JTable tableRef) {
        this.table = tableRef;
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
        
        viewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fireEditingStopped();
                handleButtonClick(currentRow, "View", table);
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
        
        // Action Panel
        actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(ACTION_COLOR);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        actionLabel = new JLabel("Action");
        actionLabel.setForeground(Color.WHITE);
        actionLabel.setFont(actionLabel.getFont().deriveFont(Font.BOLD));
        actionPanel.add(actionLabel, new GridBagConstraints());
        
        actionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fireEditingStopped();
                String action = "";
                if (table == checkInTable) {
                    action = "Check-in";
                } else if (table == checkOutTable) {
                    action = "Check-out";
                } else {
                    action = "Confirm";
                }
                handleButtonClick(currentRow, action, table);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                actionPanel.setBackground(ACTION_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                actionPanel.setBackground(ACTION_COLOR);
            }
        });
        
        mainPanel.add(viewPanel);
        mainPanel.add(actionPanel);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                             boolean isSelected, int row, int column) {
        currentRow = row;
        currentValue = value;
        
        String status = "";
        if (table == checkInTable) {
            status = "Check-in";
        } else if (table == checkOutTable) {
            status = "Check-out";
        } else {
            status = "Confirm";
        }
        actionLabel.setText(status);
        
        if (isSelected) {
            mainPanel.setBackground(table.getSelectionBackground());
            viewPanel.setBackground(VIEW_SELECTED);
            actionPanel.setBackground(ACTION_SELECTED);
        } else {
            mainPanel.setBackground(table.getBackground());
            viewPanel.setBackground(VIEW_COLOR);
            actionPanel.setBackground(ACTION_COLOR);
        }
        
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 10)
        ));
        
        return mainPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }
}

private void handleButtonClick(int row, String action, JTable sourceTable) {
    String reservationNumber = (String) sourceTable.getValueAt(row, 0);
    int reservationId = (int) sourceTable.getValueAt(row, 6); // reservation_id is in column 6

    if ("View".equals(action)) {
        viewReservationDetails(reservationNumber);
        return;
    }

    String newStatus = "";
    boolean requiresPayment = false;
    double remainingBalance = 0;

    if ("Check-in".equals(action)) {
        newStatus = "Check-in";
        requiresPayment = true;
    } else if ("Check-out".equals(action)) {
        newStatus = "Check-out";

        // Confirm check-out before proceeding
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to check out this reservation?",
                "Confirm Check-out",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // Cancel operation if user chose NO
        }
    }

    try {
        // Calculate remaining balance
        DatabaseConnection();
        String balanceQuery = "SELECT r.total_price, COALESCE(SUM(p.amount), 0) as paid " +
                            "FROM reservation r " +
                            "LEFT JOIN payment p ON r.reservation_id = p.reservation_id AND p.status = 'Paid' " +
                            "WHERE r.reservation_id = ?";
        pst = con.prepareStatement(balanceQuery);
        pst.setInt(1, reservationId);
        rs = pst.executeQuery();

        if (rs.next()) {
            double totalPrice = rs.getDouble("total_price");
            double paidAmount = rs.getDouble("paid");
            remainingBalance = totalPrice - paidAmount;
        }

        // For check-in, ensure payment
        if (requiresPayment && remainingBalance > 0) {
            int option = JOptionPane.showConfirmDialog(this,
                    "This reservation has ₱" + String.format("%.2f", remainingBalance) + " remaining balance.\n" +
                    "Process payment now?",
                    "Payment Required",
                    JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return; // Don't proceed with check-in if payment not made
            }
            
            // Process payment - if this fails, we shouldn't proceed with status update
            if (!processOnSitePayment(reservationId, remainingBalance)) {
                return; // Payment failed or was canceled
            }
        }

        // Update status (Check-in or Check-out)
        updateReservationStatus(reservationNumber, newStatus);

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pst != null) pst.close(); } catch (SQLException e) {}
        try { if (con != null) con.close(); } catch (SQLException e) {}
    }
}

private boolean processOnSitePayment(int reservationId, double balanceAmount) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    try {
        DatabaseConnection();
        con = this.con; // Assuming DatabaseConnection() sets this.con

        // Prompt for amount paid
        String input = JOptionPane.showInputDialog(this,
            "Remaining balance: ₱" + String.format("%.2f", balanceAmount) + "\n\nEnter amount paid:",
            "Process On-Site Payment",
            JOptionPane.PLAIN_MESSAGE);

        // Check if canceled or blank
        if (input == null) {
            return false; // User canceled
        }
        
        input = input.trim();
        
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Input cannot be empty.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate using regex: only digits and optional decimal
        if (!input.matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(this,
                "Invalid amount entered. Please enter a valid number (e.g., 100 or 100.50).",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double amountPaid = Double.parseDouble(input);

        // Negative or insufficient
        if (amountPaid <= 0) {
            JOptionPane.showMessageDialog(this,
                "Amount must be greater than zero.",
                "Invalid Amount",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (amountPaid < balanceAmount) {
            JOptionPane.showMessageDialog(this,
                "Amount paid is less than the remaining balance.\nPlease collect full payment.",
                "Insufficient Payment",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        double change = amountPaid - balanceAmount;

        // Check if 'Balance' payment already exists
        String checkQuery = "SELECT payment_id FROM payment WHERE reservation_id = ? AND payment_type = 'Balance'";
        pst = con.prepareStatement(checkQuery);
        pst.setInt(1, reservationId);
        rs = pst.executeQuery();

        if (rs.next()) {
            // Update existing
            String updateQuery = "UPDATE payment SET amount = ?, status = 'Paid' WHERE payment_id = ?";
            try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                updateStmt.setDouble(1, balanceAmount);
                updateStmt.setInt(2, rs.getInt("payment_id"));
                updateStmt.executeUpdate();
            }
        } else {
            // Insert new
            String insertQuery = "INSERT INTO payment (reservation_id, payment_type, payment_method, amount, status) VALUES (?, 'Balance', 'On Site', ?, 'Paid')";
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, reservationId);
                insertStmt.setDouble(2, balanceAmount);
                insertStmt.executeUpdate();
            }
        }

        // Confirmation
        JOptionPane.showMessageDialog(this,
            "Payment of ₱" + String.format("%.2f", balanceAmount) + " recorded.\n\n" +
            "Amount received: ₱" + String.format("%.2f", amountPaid) + "\n" +
            "Change due: ₱" + String.format("%.2f", change),
            "Payment Successful",
            JOptionPane.INFORMATION_MESSAGE);

        return true;

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error processing payment: " + ex.getMessage(),
            "Payment Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pst != null) pst.close(); } catch (SQLException e) {}
        try { if (con != null) con.close(); } catch (SQLException e) {}
    }
}





private void updateReservationStatus(String reservationNumber, String newStatus) {
    try {
        PreparedStatement logStmt = null;

        DatabaseConnection();
        String query = "UPDATE reservation SET status = ? WHERE reservation_number = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, newStatus);
        pst.setString(2, reservationNumber);
        int rows = pst.executeUpdate();

        if (rows > 0) {
           String logQuery = "INSERT INTO activity_log (user_id, action_type, action_description) VALUES (?, ?, ?)";
            logStmt = con.prepareStatement(logQuery);
            logStmt.setInt(1, userID); // Ensure userId is available in this class
            logStmt.setString(2, "UPDATE_RESERVATION_STATUS");
            logStmt.setString(3, "Updated reservation #" + reservationNumber + " to status: " + newStatus);
            logStmt.executeUpdate();
            fetchRoomReservations(checkInTable, CONFIRMED_STATUS);
            fetchRoomReservations(checkOutTable, CHECKIN_STATUS);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update reservation.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating reservation: " + e.getMessage());
    } finally {
        try { if (pst != null) pst.close(); } catch (SQLException e) {}
        try { if (con != null) con.close(); } catch (SQLException e) {}
    }
}


 private void viewReservationDetails(String reservationNumber) {
    try {
        DatabaseConnection();
        
        // Get reservation details with guest name
        String reservationQuery = "SELECT r.*, g.guest_name FROM reservation r " +
                                "JOIN guest g ON r.guest_id = g.guest_id " +
                                "WHERE r.reservation_number = ?";
        pst = con.prepareStatement(reservationQuery);
        pst.setString(1, reservationNumber);
        rs = pst.executeQuery();

        if (rs.next()) {
            String guestName = rs.getString("guest_name");
            Date checkInDate = rs.getDate("check_in_date");
            Date checkOutDate = rs.getDate("check_out_date");
            double totalPrice = rs.getDouble("total_price");
            String reservationStatus = rs.getString("status");
            int reservationId = rs.getInt("reservation_id");

            // Get payment details
            String paymentQuery = "SELECT * FROM payment WHERE reservation_id = ?";
            PreparedStatement paymentStmt = con.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, reservationId);
            ResultSet paymentRs = paymentStmt.executeQuery();

            StringBuilder paymentInfo = new StringBuilder();
            double totalPaid = 0;
            double remainingBalance = totalPrice;

            while (paymentRs.next()) {
                String paymentType = paymentRs.getString("payment_type");
                double amount = paymentRs.getDouble("amount");
                String status = paymentRs.getString("status");
                String method = paymentRs.getString("payment_method");
                String refNum = paymentRs.getString("reference_number");

                paymentInfo.append("\n- ").append(paymentType).append(": ₱").append(String.format("%.2f", amount))
                          .append(" (").append(method).append(") - ").append(status);
                if (refNum != null) {
                    paymentInfo.append(" (Ref: ").append(refNum).append(")");
                }

                if ("Paid".equals(status)) {
                    totalPaid += amount;
                }
            }
            remainingBalance = totalPrice - totalPaid;
            paymentRs.close();
            paymentStmt.close();

            // Show reservation details with payment info
            String message = "Reservation Details:\n" +
                           "Number: " + reservationNumber + "\n" +
                           "Guest: " + guestName + "\n" +
                           "Check-In: " + checkInDate + "\n" +
                           "Check-Out: " + checkOutDate + "\n" +
                           "Total Price: ₱" + String.format("%.2f", totalPrice) + "\n" +
                           "Amount Paid: ₱" + String.format("%.2f", totalPaid) + "\n" +
                           "Remaining Balance: ₱" + String.format("%.2f", remainingBalance) + "\n" +
                           "Status: " + reservationStatus + "\n\n" +
                           "Payment History:" + paymentInfo.toString();

            JOptionPane.showMessageDialog(this, message, "Reservation Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Reservation not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage(), 
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pst != null) pst.close(); } catch (SQLException e) {}
        try { if (con != null) con.close(); } catch (SQLException e) {}
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
        dateChooserCheckin1 = new com.raven.datechooser.DateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        materialTabbed1 = new GUI.MaterialTabbed();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        checkInTable = new rojerusan.RSTableMetro();
        txtsearch = new textfield_suggestion.TextFieldSuggestion();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        checkOutTable = new rojerusan.RSTableMetro();
        txtsearch2 = new textfield_suggestion.TextFieldSuggestion();

        dateChooserCheckin.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckin.setDateFormat("MMMM dd, yyyy");

        dateChooserCheckin1.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckin1.setDateFormat("MMMM dd, yyyy");

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setPreferredSize(new java.awt.Dimension(1170, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(39, 114, 160)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        materialTabbed1.setForeground(new java.awt.Color(0, 0, 0));
        materialTabbed1.setFont(new java.awt.Font("Helvetica Neue", 1, 15)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkInTable.setBackground(new java.awt.Color(255, 255, 255));
        checkInTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        checkInTable.setForeground(new java.awt.Color(255, 255, 255));
        checkInTable.setModel(new javax.swing.table.DefaultTableModel(
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
                "Reservation ID", "Guest Name", "Check-In", "Check-Out", "Total", "Date Created"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        checkInTable.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        checkInTable.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        checkInTable.setColorBordeHead(new java.awt.Color(255, 255, 255));
        checkInTable.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        checkInTable.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        checkInTable.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        checkInTable.setColorSelBackgound(new java.awt.Color(39, 114, 160));
        checkInTable.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        checkInTable.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkInTable.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkInTable.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        checkInTable.setGridColor(new java.awt.Color(255, 255, 255));
        checkInTable.setRowHeight(30);
        checkInTable.setSelectionBackground(new java.awt.Color(39, 114, 160));
        checkInTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        checkInTable.setShowGrid(false);
        checkInTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkInTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(checkInTable);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1120, 560));

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
        jPanel4.add(txtsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 270, 40));

        materialTabbed1.addTab("Check-in", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkOutTable.setBackground(new java.awt.Color(255, 255, 255));
        checkOutTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        checkOutTable.setForeground(new java.awt.Color(255, 255, 255));
        checkOutTable.setModel(new javax.swing.table.DefaultTableModel(
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
                "Reservation ID", "Guest Name", "Check-In", "Check-Out", "Total", "Date Created"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        checkOutTable.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        checkOutTable.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        checkOutTable.setColorBordeHead(new java.awt.Color(255, 255, 255));
        checkOutTable.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        checkOutTable.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        checkOutTable.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        checkOutTable.setColorSelBackgound(new java.awt.Color(39, 114, 160));
        checkOutTable.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        checkOutTable.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkOutTable.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkOutTable.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        checkOutTable.setGridColor(new java.awt.Color(255, 255, 255));
        checkOutTable.setRowHeight(30);
        checkOutTable.setSelectionBackground(new java.awt.Color(39, 114, 160));
        checkOutTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        checkOutTable.setShowGrid(false);
        checkOutTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkOutTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(checkOutTable);

        jPanel5.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1120, 560));

        txtsearch2.setForeground(new java.awt.Color(102, 102, 102));
        txtsearch2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtsearch2.setText("Search here...");
        txtsearch2.setSelectedTextColor(new java.awt.Color(102, 102, 102));
        txtsearch2.setSelectionColor(new java.awt.Color(102, 102, 102));
        txtsearch2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtsearch2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtsearch2FocusLost(evt);
            }
        });
        txtsearch2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtsearch2KeyReleased(evt);
            }
        });
        jPanel5.add(txtsearch2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 270, 40));

        materialTabbed1.addTab("Check-out", jPanel5);

        jPanel2.add(materialTabbed1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1120, 680));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1160, 720));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 760));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkInTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkInTableMouseClicked

    }//GEN-LAST:event_checkInTableMouseClicked

    private void checkOutTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkOutTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_checkOutTableMouseClicked

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

        DefaultTableModel obj =(DefaultTableModel) checkInTable.getModel();
        TableRowSorter<DefaultTableModel> obj1=new TableRowSorter<>(obj);
        checkInTable.setRowSorter(obj1);
        obj1.setRowFilter(RowFilter.regexFilter(txtsearch.getText()));
    }//GEN-LAST:event_txtsearchKeyReleased

    private void txtsearch2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsearch2FocusGained
          if(txtsearch2.getText().equals("Search here...")){
            txtsearch2.setText("");

        }
    }//GEN-LAST:event_txtsearch2FocusGained

    private void txtsearch2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsearch2FocusLost
       if(txtsearch2.getText().equals("")){
            txtsearch2.setText("Search here...");

        }
    }//GEN-LAST:event_txtsearch2FocusLost

    private void txtsearch2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtsearch2KeyReleased
          DefaultTableModel obj =(DefaultTableModel) checkOutTable.getModel();
        TableRowSorter<DefaultTableModel> obj1=new TableRowSorter<>(obj);
        checkOutTable.setRowSorter(obj1);
        obj1.setRowFilter(RowFilter.regexFilter(txtsearch2.getText()));
    }//GEN-LAST:event_txtsearch2KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSTableMetro checkInTable;
    private rojerusan.RSTableMetro checkOutTable;
    private com.raven.datechooser.DateChooser dateChooserCheckin;
    private com.raven.datechooser.DateChooser dateChooserCheckin1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private GUI.MaterialTabbed materialTabbed1;
    private textfield_suggestion.TextFieldSuggestion txtsearch;
    private textfield_suggestion.TextFieldSuggestion txtsearch2;
    // End of variables declaration//GEN-END:variables
}
