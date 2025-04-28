// Imports you need
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReservationDetailsView {

    private JFrame frame;
    private JPanel detailsPanel;
    private int roomReservationId; // Set this when calling the constructor

    public ReservationDetailsView(int roomReservationId) {
        this.roomReservationId = roomReservationId;
        initialize();
        loadReservationDetails();
    }

    private void initialize() {
        frame = new JFrame("Reservation Details");
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private void loadReservationDetails() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/beachResortManagement", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT rr.*, br.boat_id, br.boat_tour_date, br.boat_tour_start_time, br.boat_tour_end_time, br.tour_price " +
                             "FROM room_reservation rr " +
                             "LEFT JOIN boat_reservation br ON rr.room_reservation_id = br.room_reservation_id " +
                             "WHERE rr.room_reservation_id = ?")) {

            stmt.setInt(1, roomReservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // ROOM RESERVATION DETAILS
                addTitle("Room Reservation Details");

                addLabel("Reservation Number", rs.getString("reservation_number"));
                addLabel("Room Number", rs.getString("room_number"));
                addLabel("Adults", String.valueOf(rs.getInt("adult")));
                addLabel("Children", String.valueOf(rs.getInt("child")));
                addLabel("Total Guests", String.valueOf(rs.getInt("total_guests")));
                addLabel("Check-in Date", rs.getString("check_in_date"));
                addLabel("Check-out Date", rs.getString("check_out_date"));
                addLabel("Total Room Price", "₱" + rs.getBigDecimal("total_room_price"));
                addLabel("Total Entrance Fee", "₱" + rs.getBigDecimal("total_entrance_fee"));
                addLabel("Total Ecological Fee", "₱" + rs.getBigDecimal("total_ecological_fee"));
                addLabel("Boat Tour Status", rs.getString("boat_tour_status"));
                addLabel("Status", rs.getString("status"));

                // BOAT RESERVATION DETAILS (only if boat was availed)
                String boatTourStatus = rs.getString("boat_tour_status");

                if ("Availed".equalsIgnoreCase(boatTourStatus)) {
                    addSpace();
                    addTitle("Boat Reservation Details");

                    if (rs.getObject("boat_id") != null) { // If boat reservation exists
                        addLabel("Boat ID", String.valueOf(rs.getInt("boat_id")));
                        addLabel("Boat Tour Date", rs.getString("boat_tour_date"));
                        addLabel("Tour Start Time", rs.getString("boat_tour_start_time"));
                        addLabel("Tour End Time", rs.getString("boat_tour_end_time"));
                        addLabel("Tour Price", "₱" + rs.getBigDecimal("tour_price"));
                    } else {
                        addLabel("Boat Reservation", "No boat reservation record found.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Reservation not found.", "Error", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }
    }

    private void addTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(new Color(39, 114, 160));
        detailsPanel.add(titleLabel);
        addSpace();
    }

    private void addLabel(String fieldName, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel(fieldName + ": ");
        fieldLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(fieldLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);

        detailsPanel.add(panel);
        addSpace();
    }

    private void addSpace() {
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // Example to open the view
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReservationDetailsView(62); // Pass the room_reservation_id you want to view
        });
    }
}
