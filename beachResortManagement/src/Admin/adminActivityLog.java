/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
/**
 *
 * @author yeojvaldez
 */
public class adminActivityLog extends javax.swing.JInternalFrame {

    /**
     * Creates new form staffReservation
     */
    public adminActivityLog() {
        initComponents();
        removeBackground();
        DatabaseConnection();
        fetchActivityLog();
        
 
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
    
private void fetchActivityLog() {
    String selectedRole = roleComboBox.getSelectedItem().toString();

    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        if (con == null || con.isClosed()) {
            DatabaseConnection(); 
            if (con == null || con.isClosed()) {
                throw new SQLException("Failed to establish database connection");
            }
        }

        String query = """
            SELECT 
                log.log_id, 
                ud.full_name, 
                ud.role,
                log.action_type, 
                log.action_timestamp,
                log.action_description
            FROM activity_log log
            JOIN user_details ud ON log.user_id = ud.user_id
            """;

        // Add WHERE clause if role is not "All"
        if (!selectedRole.equalsIgnoreCase("All")) {
            query += " WHERE ud.role = ? ";
        }

        query += " ORDER BY log.action_timestamp DESC";

        pst = con.prepareStatement(query);

        // Set role parameter only if needed
        if (!selectedRole.equalsIgnoreCase("All")) {
            pst.setString(1, selectedRole);
        }

        rs = pst.executeQuery();

        if (activityTable.getModel() == null) {
            activityTable.setModel(new DefaultTableModel());
        }

        DefaultTableModel model = (DefaultTableModel) activityTable.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(new String[]{
            "Log ID", 
            "Full Name", 
            "Role", 
            "Action Type", 
            "Description", 
            "Timestamp", 
            "Description"
        });

        while (rs.next()) {
            Object[] row = {
                rs.getInt("log_id"),
                rs.getString("full_name"),
                rs.getString("role"),
                rs.getString("action_type"),
                "View",
                rs.getTimestamp("action_timestamp"),
                rs.getString("action_description")
            };
            model.addRow(row);
        }

        setupTable();

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error fetching activity logs: " + ex.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}



private void setupTable() {
    if (activityTable.getColumnCount() < 7) {
        System.err.println("Table doesn't have enough columns");
        return;
    }

    try {
        // Enable row selection highlighting
        activityTable.setRowSelectionAllowed(true);
        activityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Remove existing mouse listeners to prevent duplicates
        MouseListener[] listeners = activityTable.getMouseListeners();
        for (MouseListener listener : listeners) {
            activityTable.removeMouseListener(listener);
        }

        // Configure View button column (index 4)
        TableColumn buttonColumn = activityTable.getColumnModel().getColumn(4);
        buttonColumn.setCellRenderer(new ButtonRenderer());
        buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        // Add mouse listener to handle clicks
        activityTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = activityTable.rowAtPoint(e.getPoint());
                int col = activityTable.columnAtPoint(e.getPoint());
                
                // Highlight the row when clicked anywhere
                if (row >= 0 && col >= 0) {
                    activityTable.setRowSelectionInterval(row, row);
                    
                    // Handle View button click
                    if (col == 4) {
                        String description = activityTable.getModel().getValueAt(row, 6).toString();
                        showDescriptionDialog(description);
                    }
                }
            }
        });

        // Hide description column (index 6)
        TableColumn hiddenCol = activityTable.getColumnModel().getColumn(6);
        hiddenCol.setMinWidth(0);
        hiddenCol.setMaxWidth(0);
        hiddenCol.setPreferredWidth(0);
        hiddenCol.setResizable(false);

    } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("Column index out of bounds: " + e.getMessage());
    }
}

// Simplified ButtonRenderer
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText("View");
        return this;
    }
}


// Modified ButtonEditor to preserve selection
class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private int row;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton("View");
        button.setOpaque(true);
        button.addActionListener(e -> {
            fireEditingStopped();
            String description = activityTable.getModel().getValueAt(row, 6).toString();
            showDescriptionDialog(description);
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.row = row;
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(UIManager.getColor("Button.background"));
        }
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "View";
    }
}

private void showDescriptionDialog(String description) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Action Details");
    dialog.setPreferredSize(new Dimension(380, 200));
    dialog.setModal(true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo(null);

    // Outer padding
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    contentPanel.setBackground(Color.WHITE);

    // Text area with centered horizontal text
    JTextArea textArea = new JTextArea(description);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    textArea.setBackground(Color.WHITE);
    textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Center horizontally using styled document (if you want true centered lines, but only for plain text)
    SimpleAttributeSet center = new SimpleAttributeSet();
    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
    
    // Optional: Use JTextPane if you want alignment effects
    JTextPane textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setBackground(Color.WHITE);
    textPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    textPane.setText(description);
    StyledDocument doc = textPane.getStyledDocument();
    doc.setParagraphAttributes(0, doc.getLength(), center, false);

    JScrollPane scrollPane = new JScrollPane(textPane);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());

    // Bottom panel with "Close" label
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    buttonPanel.setOpaque(false);

    JLabel closeLabel = new JLabel("Close", SwingConstants.CENTER);
    closeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    closeLabel.setForeground(new Color(0, 120, 215));
    closeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    closeLabel.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));

    closeLabel.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            closeLabel.setForeground(new Color(0, 80, 180));
            closeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 80, 180)),
                BorderFactory.createEmptyBorder(8, 25, 7, 25)
            ));
        }

        public void mouseExited(MouseEvent e) {
            closeLabel.setForeground(new Color(0, 120, 215));
            closeLabel.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        }

        public void mouseClicked(MouseEvent e) {
            dialog.dispose();
        }
    });

    buttonPanel.add(closeLabel);

    contentPanel.add(scrollPane, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.add(contentPanel);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
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
        activityTable = new rojerusan.RSTableMetro();
        txtsearch = new textfield_suggestion.TextFieldSuggestion();
        roleComboBox = new GUI.ComboBoxSuggestion();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setPreferredSize(new java.awt.Dimension(1170, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        activityTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        activityTable.setForeground(new java.awt.Color(255, 255, 255));
        activityTable.setModel(new javax.swing.table.DefaultTableModel(
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
                "Log ID", "User Name", "User Role", "Action Type", "Action", "Timestamp"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        activityTable.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        activityTable.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        activityTable.setColorBordeHead(new java.awt.Color(255, 255, 255));
        activityTable.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        activityTable.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        activityTable.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        activityTable.setColorSelBackgound(new java.awt.Color(39, 114, 160));
        activityTable.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        activityTable.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        activityTable.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        activityTable.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        activityTable.setGridColor(new java.awt.Color(255, 255, 255));
        activityTable.setRowHeight(30);
        activityTable.setSelectionBackground(new java.awt.Color(39, 114, 160));
        activityTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        activityTable.setShowGrid(false);
        activityTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                activityTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(activityTable);
        if (activityTable.getColumnModel().getColumnCount() > 0) {
            activityTable.getColumnModel().getColumn(0).setPreferredWidth(10);
            activityTable.getColumnModel().getColumn(1).setPreferredWidth(10);
            activityTable.getColumnModel().getColumn(2).setPreferredWidth(10);
            activityTable.getColumnModel().getColumn(3).setPreferredWidth(10);
            activityTable.getColumnModel().getColumn(5).setPreferredWidth(10);
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
        jPanel2.add(txtsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 290, 40));

        roleComboBox.setEditable(false);
        roleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Admin", "Staff", "Guest" }));
        roleComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                roleComboBoxItemStateChanged(evt);
            }
        });
        roleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roleComboBoxActionPerformed(evt);
            }
        });
        jPanel2.add(roleComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 110, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 1160, 660));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(39, 114, 160)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel1.setText("Activity Log");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 230, 40));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1160, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 760));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void activityTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_activityTableMouseClicked

    }//GEN-LAST:event_activityTableMouseClicked

    private void roleComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_roleComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_roleComboBoxItemStateChanged

    private void roleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roleComboBoxActionPerformed
       fetchActivityLog();
    }//GEN-LAST:event_roleComboBoxActionPerformed

    private void txtsearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtsearchKeyReleased

        DefaultTableModel obj =(DefaultTableModel) activityTable.getModel();
        TableRowSorter<DefaultTableModel> obj1=new TableRowSorter<>(obj);
        activityTable.setRowSorter(obj1);
        obj1.setRowFilter(RowFilter.regexFilter(txtsearch.getText()));
    }//GEN-LAST:event_txtsearchKeyReleased

    private void txtsearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsearchFocusLost
        if(txtsearch.getText().equals("")){
            txtsearch.setText("Search here...");

        }
    }//GEN-LAST:event_txtsearchFocusLost

    private void txtsearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsearchFocusGained

        if(txtsearch.getText().equals("Search here...")){
            txtsearch.setText("");

        }
    }//GEN-LAST:event_txtsearchFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSTableMetro activityTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private GUI.ComboBoxSuggestion roleComboBox;
    private textfield_suggestion.TextFieldSuggestion txtsearch;
    // End of variables declaration//GEN-END:variables
}
