
import Admin.adminRoom;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author yeojvaldez
 */
public final class NewJFrame1 extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame1
     */
    public NewJFrame1() {
        initComponents();
         DatabaseConnection();
        showPendingRoomReservations();
       
        
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
    
public final void showPendingRoomReservations() {
    pendingReservationPanel.removeAll();
    pendingReservationPanel.setLayout(new BoxLayout(pendingReservationPanel, BoxLayout.Y_AXIS));
    pendingReservationPanel.setBackground(new Color(240, 242, 245));

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/beachResortManagement", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT r.reservation_number, r.check_in_date, rr.room_reservation_id, r.check_out_date, rr.room_number, rm.room_image " +
                                                      "FROM reservation r " +
                                                      "JOIN room_reservation rr ON r.reservation_number = rr.reservation_number " +
                                                      "JOIN room rm ON rr.room_number = rm.room_number " +
                                                      "WHERE r.user_id = ? AND r.status = 'Pending'")) {
        
        pst.setInt(1, 1); // Use dynamic user ID
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            // Create a reservation card
            JPanel reservationCard = new JPanel(new BorderLayout());
            reservationCard.setBackground(Color.WHITE);
            reservationCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            reservationCard.setPreferredSize(new Dimension(900, 220));
            reservationCard.setMaximumSize(new Dimension(1000, 220));
            reservationCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Left panel (Image + Details)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
            leftPanel.setOpaque(false);

            // Image
            byte[] imgBytes = rs.getBytes("room_image");
            JLabel lblImage = new JLabel();
            lblImage.setPreferredSize(new Dimension(250, 170));
            lblImage.setHorizontalAlignment(JLabel.CENTER);
            lblImage.setVerticalAlignment(JLabel.CENTER);

            if (imgBytes != null) {
                ImageIcon imageIcon = new ImageIcon(imgBytes);
                Image img = imageIcon.getImage().getScaledInstance(250, 170, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } else {
                lblImage.setText("No Image");
                lblImage.setForeground(Color.GRAY);
                lblImage.setFont(new Font("Segoe UI", Font.BOLD, 16));
            }

            // Details Panel
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setOpaque(false);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

            JLabel lblReservationNumber = new JLabel(rs.getString("reservation_number"));
            lblReservationNumber.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblReservationNumber.setForeground(new Color(30, 30, 30));

            JLabel lblCheckIn = new JLabel("Check-in: " + rs.getDate("check_in_date"));
            lblCheckIn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblCheckIn.setForeground(new Color(100, 100, 100));

            JLabel lblCheckOut = new JLabel("Check-out: " + rs.getDate("check_out_date"));
            lblCheckOut.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblCheckOut.setForeground(new Color(100, 100, 100));

            JLabel lblRoomNumber = new JLabel("Room: " + rs.getString("room_number"));
            lblRoomNumber.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblRoomNumber.setForeground(new Color(100, 100, 100));

            detailsPanel.add(lblReservationNumber);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            detailsPanel.add(lblCheckIn);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblCheckOut);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            detailsPanel.add(lblRoomNumber);

            leftPanel.add(lblImage);
            leftPanel.add(detailsPanel);

            // Right panel (Action panels)
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setOpaque(false);

            // View Panel
            JPanel viewPanel = createActionPanel("View", new Color(0, 123, 255));
            JLabel lblView = (JLabel) viewPanel.getComponent(0); // since only label inside
            int roomReservationId = rs.getInt("room_reservation_id"); // capture the room_reservation_id here
            lblView.addMouseListener(new MouseAdapter() {
            @Override
                public void mouseClicked(MouseEvent e) {
                    // Instead of closing the current frame, just create and display the reservation details view
                    new ReservationViewDetails(roomReservationId);  // Show details view without disposing the current form
                }
            });


            // Cancel Panel
            JPanel cancelPanel = createActionPanel("Cancel", new Color(220, 53, 69));
            JLabel lblCancel = (JLabel) cancelPanel.getComponent(0);
            lblCancel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        JOptionPane.showMessageDialog(null, "Canceling reservation: " + rs.getString("reservation_number"));
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                }
            });

            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(viewPanel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            rightPanel.add(cancelPanel);
            rightPanel.add(Box.createVerticalGlue());

            reservationCard.add(leftPanel, BorderLayout.CENTER);
            reservationCard.add(rightPanel, BorderLayout.EAST);

            pendingReservationPanel.add(reservationCard);
            pendingReservationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        pendingReservationPanel.revalidate();
        pendingReservationPanel.repaint();

    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}


// Helper method to create Action Panel
private JPanel createActionPanel(String text, Color backgroundColor) {
    JPanel panel = new JPanel();
    panel.setBackground(backgroundColor);
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    panel.setMaximumSize(new Dimension(150, 40));
    panel.setLayout(new GridBagLayout());

    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.BOLD, 15));
    label.setForeground(Color.WHITE);

    panel.add(label);

    return panel;
}















    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pendingReservationPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout pendingReservationPanelLayout = new javax.swing.GroupLayout(pendingReservationPanel);
        pendingReservationPanel.setLayout(pendingReservationPanelLayout);
        pendingReservationPanelLayout.setHorizontalGroup(
            pendingReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );
        pendingReservationPanelLayout.setVerticalGroup(
            pendingReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 471, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pendingReservationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pendingReservationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pendingReservationPanel;
    // End of variables declaration//GEN-END:variables
}
