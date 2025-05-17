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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JOptionPane;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 *
 * @author yeojvaldez
 */
public class adminReservation extends javax.swing.JInternalFrame {

    /**
     * Creates new form staffReservation
     */
    public adminReservation() {
        initComponents();
        removeBackground();
        DatabaseConnection();
        showReservations();
        
 
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
public final void showReservations() {
    String selectedStatus = statusComboBox.getSelectedItem().toString();

    try {
        String query = "SELECT reservation_number, guest.guest_name, r.check_in_date, " +
                      "r.check_out_date, r.total_price, r.status, r.created_at " +
                      "FROM reservation r " +
                      "JOIN guest ON r.guest_id = guest.guest_id";

        if (!selectedStatus.equalsIgnoreCase("All")) {
            query += " WHERE r.status = ?";
        }

        pst = con.prepareStatement(query);

        if (!selectedStatus.equalsIgnoreCase("All")) {
            pst.setString(1, selectedStatus);
        }

        rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) completedTable.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Set custom renderer for the status column (adjust the column index as needed)
        completedTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        while (rs.next()) {
            String status = rs.getString("status");
            // Make sure status is not null
            if (status == null) status = "Unknown";
            
            model.addRow(new Object[] {
                timestampFormat.format(rs.getTimestamp("created_at")),
                rs.getString("reservation_number"),
                rs.getString("guest_name"),
                dateFormat.format(rs.getDate("check_in_date")),
                dateFormat.format(rs.getDate("check_out_date")),
                "₱" + String.format("%.2f", rs.getDouble("total_price")),
                status // Make sure this is included
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading reservations: " + ex.getMessage(),
                                    "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

public void printTableToPDF() {
    // Create file chooser
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save PDF");
    fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
    
    // Set default file name with timestamp
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String defaultFileName = "Reservations_" + dateFormat.format(new Date()) + ".pdf";
    fileChooser.setSelectedFile(new java.io.File(defaultFileName));
    
    int userSelection = fileChooser.showSaveDialog(this);
    
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        
        // Ensure the file has .pdf extension
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }
        
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Reservation Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Add date/time of generation
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Paragraph datePara = new Paragraph("Generated on: " + new Date(), dateFont);
            datePara.setAlignment(Element.ALIGN_CENTER);
            datePara.setSpacingAfter(20f);
            document.add(datePara);
            
            // Create PDF table
            PdfPTable pdfTable = new PdfPTable(completedTable.getColumnCount());
            pdfTable.setWidthPercentage(100);
            
            // Add table headers
            for (int i = 0; i < completedTable.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(completedTable.getColumnName(i)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }
            
            // Add table rows
            for (int rows = 0; rows < completedTable.getRowCount(); rows++) {
                for (int cols = 0; cols < completedTable.getColumnCount(); cols++) {
                    Object value = completedTable.getValueAt(rows, cols);
                    String cellValue = (value == null) ? "" : value.toString();
                    
                    PdfPCell cell = new PdfPCell(new Phrase(cellValue));
                    
                    // Special formatting for status column (assuming it's column 6)
                    if (cols == 6) {
                        if (cellValue.equalsIgnoreCase("Confirmed")) {
                            cell.setBackgroundColor(BaseColor.GREEN);
                        } else if (cellValue.equalsIgnoreCase("Cancelled")) {
                            cell.setBackgroundColor(BaseColor.RED);
                        } else if (cellValue.equalsIgnoreCase("Pending")) {
                            cell.setBackgroundColor(BaseColor.YELLOW);
                        } else if (cellValue.equalsIgnoreCase("Completed")) {
                            cell.setBackgroundColor(BaseColor.BLUE);
                            cell.setPhrase(new Phrase(cellValue, FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE)));
                        }
                    }
                    
                    pdfTable.addCell(cell);
                }
            }
            
            document.add(pdfTable);
            
            // Add footer
            Paragraph footer = new Paragraph("End of Report", dateFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20f);
            document.add(footer);
            
            JOptionPane.showMessageDialog(this, "PDF created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
}



// Enhanced Status Cell Renderer
class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JLabel label = (JLabel) this;
        
        if (value == null) {
            value = "Unknown";
        }
        
        String status = value.toString().toLowerCase();
        
        // Set colors based on status with better contrast
        Color bgColor;
        Color fgColor = Color.WHITE; // Default text color
        
        switch (status.toLowerCase()) {
            case "confirmed":
                bgColor = new Color(40, 167, 69); // Green
                break;
            case "pending":
                bgColor = new Color(255, 193, 7); // Yellow
                fgColor = Color.BLACK;
                break;
            case "cancelled":
                bgColor = new Color(220, 53, 69); // Red
                break;
            case "rejected":  // New status
                bgColor = new Color(139, 0, 0); // Dark Red
                break;
            case "checked in":
                bgColor = new Color(23, 162, 184); // Teal
                break;
            case "checked out":
                bgColor = new Color(108, 117, 125); // Gray
                break;
            default:
                bgColor = Color.LIGHT_GRAY;
                fgColor = Color.BLACK;
        }

        // For light backgrounds, use dark text
        if (bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue() > 382) {
            fgColor = Color.BLACK;
        }
        
        label.setBackground(bgColor);
        label.setForeground(fgColor);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setOpaque(true);
        
        // Create oval shape with padding
        label.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15, bgColor),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        
        // Make sure text is set
        label.setText(value.toString());
        
        return label;
    }
}

// Improved Rounded Border
class RoundedBorder implements Border {
    private int radius;
    private Color color;
    
    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius+1, radius+1, radius+1, radius+1);
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
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
        completedTable = new rojerusan.RSTableMetro();
        txtsearch = new textfield_suggestion.TextFieldSuggestion();
        statusComboBox = new GUI.ComboBoxSuggestion();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 242, 242));
        jPanel1.setPreferredSize(new java.awt.Dimension(1170, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        completedTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        completedTable.setForeground(new java.awt.Color(255, 255, 255));
        completedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Date Created", "Reservation ID", "Guest Name", "Check-In", "Check-Out", "Total", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        completedTable.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        completedTable.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        completedTable.setColorBordeHead(new java.awt.Color(255, 255, 255));
        completedTable.setColorFilasBackgound2(new java.awt.Color(242, 242, 242));
        completedTable.setColorFilasForeground1(new java.awt.Color(27, 59, 95));
        completedTable.setColorFilasForeground2(new java.awt.Color(27, 59, 95));
        completedTable.setColorSelBackgound(new java.awt.Color(39, 114, 160));
        completedTable.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        completedTable.setFuenteFilas(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        completedTable.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        completedTable.setFuenteHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        completedTable.setGridColor(new java.awt.Color(255, 255, 255));
        completedTable.setRowHeight(30);
        completedTable.setSelectionBackground(new java.awt.Color(39, 114, 160));
        completedTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        completedTable.setShowGrid(false);
        completedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                completedTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(completedTable);

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

        statusComboBox.setEditable(false);
        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Pending", "Rejected", "Confirmed", "Check-in", "Check-out" }));
        statusComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                statusComboBoxItemStateChanged(evt);
            }
        });
        statusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusComboBoxActionPerformed(evt);
            }
        });
        jPanel2.add(statusComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 110, 40));

        jPanel5.setBackground(new java.awt.Color(27, 59, 95));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Print");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 40));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 20, 90, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 1160, 660));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 0, 0, 0, new java.awt.Color(39, 114, 160)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel1.setText("Reservation History");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 230, 50));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1160, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 760));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void completedTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_completedTableMouseClicked

    }//GEN-LAST:event_completedTableMouseClicked

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

        DefaultTableModel obj =(DefaultTableModel) completedTable.getModel();
        TableRowSorter<DefaultTableModel> obj1=new TableRowSorter<>(obj);
        completedTable.setRowSorter(obj1);
        obj1.setRowFilter(RowFilter.regexFilter(txtsearch.getText()));
    }//GEN-LAST:event_txtsearchKeyReleased

    private void statusComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statusComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_statusComboBoxItemStateChanged

    private void statusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusComboBoxActionPerformed
        showReservations();
    }//GEN-LAST:event_statusComboBoxActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
printTableToPDF();
        
        
    }//GEN-LAST:event_jLabel3MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSTableMetro completedTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private GUI.ComboBoxSuggestion statusComboBox;
    private textfield_suggestion.TextFieldSuggestion txtsearch;
    // End of variables declaration//GEN-END:variables
}
