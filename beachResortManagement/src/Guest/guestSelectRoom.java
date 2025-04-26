/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Guest;

import Login.landingPage;
import com.mysql.cj.jdbc.Blob;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.toedter.calendar.JCalendar;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;



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
    try {
        // Format dates before displaying in text fields
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy");
        
        if (checkInDate != null) {
            txtCheckin.setText(displayFormat.format(checkInDate));
        } else {
            txtCheckin.setText("");
        }
        
        if (checkOutDate != null) {
            txtCheckout.setText(displayFormat.format(checkOutDate));
        } else {
            txtCheckout.setText("");
        }
        
        // Set spinner values
        adultsSpinner.setValue(adults);
        childrenSpinner.setValue(children);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error displaying values: " + e.getMessage(),
            "Display Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void searchAvailableRooms() {
    try {
        // Validate dates
        if (this.checkInDate == null || this.checkOutDate == null) {
            JOptionPane.showMessageDialog(this, "Please select valid check-in and check-out dates.");
            return;
        }

        if (!this.checkOutDate.after(this.checkInDate)) {
            JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.");
            return;
        }

        java.sql.Date sqlCheckIn = new java.sql.Date(this.checkInDate.getTime());
        java.sql.Date sqlCheckOut = new java.sql.Date(this.checkOutDate.getTime());
        int totalGuests = this.adults + this.children;

        String query = "SELECT r.room_number, r.room_type, r.description, r.room_price, r.room_image " +
                       "FROM room r " +
                       "WHERE r.max_occupancy >= ? " +
                       "AND r.room_number NOT IN (" +
                       "   SELECT room_number FROM room_reservation " +
                       "   WHERE status = 'Reserved' " +
                       "   AND (? <= check_out_date AND ? >= check_in_date)" +
                       ") " +
                       "ORDER BY r.room_price ASC";

        pst = con.prepareStatement(query);
        pst.setInt(1, totalGuests);
        pst.setDate(2, sqlCheckIn);
        pst.setDate(3, sqlCheckOut);

        rs = pst.executeQuery();

        DefaultTableModel roomModel = new DefaultTableModel(
            new Object[]{"Room Number", "Room Image", "Room Type", "Description", "Price", "Action"}, 
            0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 1: return ImageIcon.class; // Image column
                    default: return Object.class;
                }
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };

        // Set model first
        tblRoomDetails.setModel(roomModel);

        // Configure table appearance
        tblRoomDetails.setRowHeight(80);
        tblRoomDetails.getColumnModel().getColumn(1).setPreferredWidth(100);

        // Set image renderer
        tblRoomDetails.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel((ImageIcon) value);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setOpaque(true);
                    if (isSelected) {
                        label.setBackground(table.getSelectionBackground());
                    }
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, 
                        hasFocus, row, column);
            }
        });

        // Configure button column
        TableColumn buttonColumn = tblRoomDetails.getColumnModel().getColumn(5);
        buttonColumn.setCellRenderer(new ButtonRenderer());
        buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        boolean found = false;

        while (rs.next()) {
            found = true;
            String roomNumber = rs.getString("room_number");
            String roomType = rs.getString("room_type");
            String roomDescription = rs.getString("description");
            double price = rs.getDouble("room_price");

            // Handle image
            ImageIcon roomImage;
            try {
                Blob imageBlob = (Blob) rs.getBlob("room_image");
                if (imageBlob != null && imageBlob.length() > 0) {
                    byte[] imgBytes = imageBlob.getBytes(1, (int)imageBlob.length());
                    ImageIcon originalIcon = new ImageIcon(imgBytes);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                    roomImage = new ImageIcon(scaledImage);
                } else {
                    roomImage = createPlaceholderImage();
                }
            } catch (SQLException e) {
                roomImage = createPlaceholderImage();
                System.err.println("Error loading image: " + e.getMessage());
            }

            roomModel.addRow(new Object[]{
                roomNumber,
                roomImage,
                roomType,
                roomDescription,
                "₱" + String.format("%.2f", price),
                "Select"
            });
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No available rooms found for your criteria.");
            return;
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        ex.printStackTrace();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        ex.printStackTrace();
    }
}

// Helper method to create placeholder image
private ImageIcon createPlaceholderImage() {
    BufferedImage placeholder = new BufferedImage(100, 80, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = placeholder.createGraphics();
    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(0, 0, 100, 80);
    g2d.setColor(Color.DARK_GRAY);
    g2d.drawString("No Image", 30, 40);
    g2d.dispose();
    return new ImageIcon(placeholder);
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
    private JButton button;
    private boolean isPushed;
    private int clickedRow;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        button.setText(value == null ? "" : value.toString());
        clickedRow = row;
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            // Call selectRoom with the clicked row
            selectRoom(clickedRow);
        }
        isPushed = false;
        return button.getText();
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}


// Method to handle room selection
// Method to handle room selection
// Method to handle room selection
private void selectRoom(int row) {
    try {
        DefaultTableModel model = (DefaultTableModel) tblRoomDetails.getModel();
        
        // Get room details from the selected row
        String roomNumber = model.getValueAt(row, 0).toString();
        String roomType = model.getValueAt(row, 2).toString();
        String description = model.getValueAt(row, 3).toString();
        
        // Parse price
        String priceStr = model.getValueAt(row, 4).toString().replace("₱", "");
        double price = Double.parseDouble(priceStr);

        // Get dates from text fields
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date checkInDate = displayFormat.parse(txtCheckin.getText().trim());
        Date checkOutDate = displayFormat.parse(txtCheckout.getText().trim());

        // Get guest counts
        int adults = (Integer) adultsSpinner.getValue();
        int children = (Integer) childrenSpinner.getValue();

        // Ask about water activities
        int response = JOptionPane.showConfirmDialog(
            this,
            "Would you like to add water activities to your booking?",
            "Water Activities",
            JOptionPane.YES_NO_OPTION
        );

        boolean wantsWaterActivities = (response == JOptionPane.YES_OPTION);

        // Open appropriate form
        if (wantsWaterActivities) {
            new guestSelectBoat(checkInDate, checkOutDate, adults, children, 
                roomNumber, roomType, description, price, userID).setVisible(true);
        } else {
            new guestProcess2(checkInDate, checkOutDate, roomNumber, 
                roomType, description, price, adults, children, userID).setVisible(true);
        }
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error selecting room: " + ex.getMessage());
        ex.printStackTrace();
    }
}

// Method to validate check-in and check-out dates




    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateChooserCheckin = new com.raven.datechooser.DateChooser();
        dateChooserCheckout = new com.raven.datechooser.DateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRoomDetails = new rojerusan.RSTableMetro();
        jPanel2 = new javax.swing.JPanel();
        panelRound1 = new GUI.PanelRound();
        panelRound3 = new GUI.PanelRound();
        jLabel12 = new javax.swing.JLabel();
        childrenSpinner = new spinner.Spinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        adultsSpinner = new spinner.Spinner();
        txtCheckin = new textfield_suggestion.TextFieldSuggestion();
        jLabel7 = new javax.swing.JLabel();
        txtCheckout = new textfield_suggestion.TextFieldSuggestion();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCheckout1 = new textfield_suggestion.TextFieldSuggestion();
        jLabel15 = new javax.swing.JLabel();
        panelRound4 = new GUI.PanelRound();
        jLabel17 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        panelRound2 = new GUI.PanelRound();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        dateChooserCheckin.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckin.setDateFormat("MMMM dd, yyyy");
        dateChooserCheckin.setTextRefernce(txtCheckin);

        dateChooserCheckout.setForeground(new java.awt.Color(0, 112, 192));
        dateChooserCheckout.setDateFormat(" MMMM dd, yyyy");
        dateChooserCheckout.setTextRefernce(txtCheckout);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(242, 242, 242));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblRoomDetails.setBackground(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setForeground(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Room Number", "Room Image", "Room Category", "Description", "Price/Night", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRoomDetails.setColorBackgoundHead(new java.awt.Color(39, 114, 160));
        tblRoomDetails.setColorBordeFilas(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setColorBordeHead(new java.awt.Color(255, 255, 255));
        tblRoomDetails.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
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
        if (tblRoomDetails.getColumnModel().getColumnCount() > 0) {
            tblRoomDetails.getColumnModel().getColumn(5).setPreferredWidth(10);
        }

        jPanel9.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 1360, 540));

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 1440, 610));

        jPanel2.setBackground(new java.awt.Color(27, 59, 95));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound1.setBackground(new java.awt.Color(255, 255, 255));
        panelRound1.setRoundBottomLeft(10);
        panelRound1.setRoundBottomRight(10);
        panelRound1.setRoundTopLeft(10);
        panelRound1.setRoundTopRight(10);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        panelRound3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 0, 160, 40));

        panelRound1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 30, 180, 40));

        childrenSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        childrenSpinner.setLabelText("");
        panelRound1.add(childrenSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 30, 170, 40));

        jLabel14.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Child");
        panelRound1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, -1, -1));

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Duration");
        panelRound1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, -1, -1));

        adultsSpinner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        adultsSpinner.setLabelText("");
        panelRound1.add(adultsSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, 170, 40));

        txtCheckin.setEditable(false);
        txtCheckin.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckin.setSelectionColor(new java.awt.Color(255, 255, 255));
        panelRound1.add(txtCheckin, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 210, -1));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, 40, 40));

        txtCheckout.setEditable(false);
        txtCheckout.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckout.setSelectionColor(new java.awt.Color(255, 255, 255));
        panelRound1.add(txtCheckout, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 210, -1));

        jLabel5.setBackground(new java.awt.Color(102, 102, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/calendarIcon.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        panelRound1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 40, 40));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Check-out Date");
        panelRound1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, -1, -1));

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("Check-in Date");
        panelRound1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        txtCheckout1.setText("2 Nigths");
        txtCheckout1.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtCheckout1.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtCheckout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCheckout1ActionPerformed(evt);
            }
        });
        panelRound1.add(txtCheckout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 30, 110, 40));

        jLabel15.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("Adult");
        panelRound1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, -1, -1));

        jPanel2.add(panelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 1260, 80));

        panelRound4.setBackground(new java.awt.Color(242, 242, 242));
        panelRound4.setRoundTopLeft(50);
        panelRound4.setRoundTopRight(50);
        panelRound4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(panelRound4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 1440, 670));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Welcome,");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 180, 60));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/profile.png"))); // NOI18N
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 0, 30, 60));

        panelRound2.setBackground(new java.awt.Color(0, 153, 255));
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

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Logout");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        panelRound2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 35));

        jPanel2.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 12, 90, -1));

        jLabel19.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/iconNotif.png"))); // NOI18N
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 0, -1, 60));

        jLabel25.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/iconHome.png"))); // NOI18N
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 0, -1, 60));

        jLabel18.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("to Papaya Beach Resort");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, -1, 30));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 200));

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

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
   try {
    String checkInStr = txtCheckin.getText().trim();
    String checkOutStr = txtCheckout.getText().trim();

    // Validate input
    if (checkInStr.isEmpty() || checkOutStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Check-in or check-out date cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Use correct date format
    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
    java.util.Date parsedCheckIn = null;
    java.util.Date parsedCheckOut = null;

    try {
        parsedCheckIn = formatter.parse(checkInStr);
        parsedCheckOut = formatter.parse(checkOutStr);
    } catch (ParseException pe) {
        JOptionPane.showMessageDialog(this, "Invalid date format. Please use: MMMM dd, yyyy (e.g., April 26, 2025)", "Date Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (parsedCheckOut.before(parsedCheckIn)) {
        JOptionPane.showMessageDialog(this, "Check-out date cannot be before check-in date.", "Date Error", JOptionPane.ERROR_MESSAGE);
        return;
    }


    // Get guest numbers
    int adults = (Integer) adultsSpinner.getValue();
    int children = (Integer) childrenSpinner.getValue();

    // Set instance variables
    this.checkInDate = parsedCheckIn;
    this.checkOutDate = parsedCheckOut;
    this.adults = adults;
    this.children = children;

    // Proceed
    searchAvailableRooms();

} catch (NullPointerException npe) {
    Logger.getLogger(guestSelectRoom.class.getName()).log(Level.SEVERE, "Null Pointer Exception: ", npe);
    JOptionPane.showMessageDialog(this, "There was an error with your dates or inputs. Please check and try again.", "Error", JOptionPane.ERROR_MESSAGE);
} catch (HeadlessException ex) {
    Logger.getLogger(guestSelectRoom.class.getName()).log(Level.SEVERE, "An error occurred while processing your request: ", ex);
    JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
}



    }//GEN-LAST:event_jLabel12MouseClicked

    private void panelRound3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelRound3MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        this.dispose();
        new landingPage().setVisible(true);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void panelRound2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseClicked

    }//GEN-LAST:event_panelRound2MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        dateChooserCheckin.showPopup();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        dateChooserCheckout.showPopup();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void txtCheckout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCheckout1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCheckout1ActionPerformed

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
    private com.raven.datechooser.DateChooser dateChooserCheckin;
    private com.raven.datechooser.DateChooser dateChooserCheckout;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private GUI.PanelRound panelRound1;
    private GUI.PanelRound panelRound2;
    private GUI.PanelRound panelRound3;
    private GUI.PanelRound panelRound4;
    private rojerusan.RSTableMetro tblRoomDetails;
    private textfield_suggestion.TextFieldSuggestion txtCheckin;
    private textfield_suggestion.TextFieldSuggestion txtCheckout;
    private textfield_suggestion.TextFieldSuggestion txtCheckout1;
    // End of variables declaration//GEN-END:variables
}
