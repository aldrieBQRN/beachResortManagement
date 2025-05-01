package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ResortDashboard extends JInternalFrame {
    private static final int DASHBOARD_WIDTH = 1200;
    private static final int DASHBOARD_HEIGHT = 800;
    private static final Color PRIMARY_COLOR = new Color(39, 114, 160);
    private static final Color LIGHT_GRAY = new Color(242, 242, 242);
    private static final Color METRIC_BG_COLOR = new Color(250, 250, 250);
    
    private Connection connection;
    private JPanel chartsPanel;
    private JPanel metricsPanel;
    private JPanel mainPanel;

    public ResortDashboard() {
        super("", true, false, true, true);
        setSize(DASHBOARD_WIDTH, DASHBOARD_HEIGHT);
        setLocation(0, 0);
        setVisible(true);
        removeBackground();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/beachresortmanagement", 
                "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed");
            return;
        }

        initUI();
        
        // Add resize listener to handle responsive layout
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshLayout();
            }
        });
    }

    public final void removeBackground() {
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
    }
    
    private void refreshLayout() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void initUI() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(LIGHT_GRAY);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel (with metrics and charts)
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(LIGHT_GRAY);
        
        // Create metrics panel
        metricsPanel = createMetricsPanel();
        contentPanel.add(metricsPanel, BorderLayout.NORTH);
        
        // Create charts panel with scroll pane
        JPanel chartsContainer = new JPanel(new BorderLayout());
        chartsContainer.setBackground(Color.WHITE);
        chartsContainer.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        
        chartsPanel = createChartsPanel();
        
        JScrollPane scrollPane = new JScrollPane(chartsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Apply minimalist scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new MinimalistScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        
        chartsContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(chartsContainer, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setPreferredSize(new Dimension(DASHBOARD_WIDTH, 90));
        
        // Add resort logo/icon on the left
        JLabel logoLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(logoLabel, BorderLayout.WEST);
        
        // Center panel for title and subtitle
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Beach Front Resort Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Administration Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 230, 240));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        
        // Add date panel on the right
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setOpaque(false);
        
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        JLabel dateLabel = new JLabel(currentDate);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        
        datePanel.add(dateLabel);
        panel.add(datePanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createMetricsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(LIGHT_GRAY);
        
        JLabel metricsTitle = new JLabel("Key Performance Metrics");
        metricsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        metricsTitle.setForeground(new Color(60, 60, 60));
        metricsTitle.setBorder(new EmptyBorder(0, 5, 10, 0));
        outerPanel.add(metricsTitle, BorderLayout.NORTH);
        
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(2, 3, 15, 15));
        cardPanel.setBackground(LIGHT_GRAY);
        
        // Row 1
        // Total Rooms
        int totalRooms = getTotalRooms();
        cardPanel.add(createMetricCard("Total Rooms", String.valueOf(totalRooms), 
                       new Color(70, 130, 180), "bed"));
        
        // Total Water Activities
        int totalBoats = getTotalBoats();
        cardPanel.add(createMetricCard("Water Activities", String.valueOf(totalBoats), 
                       new Color(60, 179, 113), "boat"));
        
        // Total Guests
        int totalGuests = getTotalGuests();
        cardPanel.add(createMetricCard("Total Guests", String.valueOf(totalGuests), 
                       new Color(138, 43, 226), "users"));
        
        // Row 2
        // Active Reservations
        int activeReservations = getActiveReservations();
        cardPanel.add(createMetricCard("Active Reservations", String.valueOf(activeReservations), 
                       new Color(30, 144, 255), "calendar-check"));
        
        // Pending Reservations
        int pendingReservations = getPendingReservations();
        cardPanel.add(createMetricCard("Pending Reservations", String.valueOf(pendingReservations), 
                       new Color(255, 140, 0), "clock"));
        
        // Revenue Today
        double revenueToday = getRevenueToday();
        cardPanel.add(createMetricCard("Today's Revenue", String.format("₱%.2f", revenueToday), 
                       new Color(46, 139, 87), "money-bill"));
        
        outerPanel.add(cardPanel, BorderLayout.CENTER);
        return outerPanel;
    }
    
    private JPanel createMetricCard(String title, String value, Color color, String iconName) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(METRIC_BG_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Create icon panel (placeholder for now)
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        
        // Replace with actual icon if available
        JLabel iconLabel = new JLabel("•");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        iconLabel.setForeground(color);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        contentPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(80, 80, 80));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        
        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createChartsPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel chartsTitle = new JLabel("Performance Analytics");
        chartsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        chartsTitle.setForeground(new Color(60, 60, 60));
        chartsTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        container.add(chartsTitle, BorderLayout.NORTH);
        
        JPanel chartsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        chartsGrid.setBackground(Color.WHITE);
        
        // Reservation Status Chart
        JPanel reservationStatusPanel = new JPanel(new BorderLayout());
        reservationStatusPanel.setBackground(Color.WHITE);
        reservationStatusPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JFreeChart reservationStatusChart = createReservationStatusChart();
        ChartPanel reservationStatusChartPanel = new ChartPanel(reservationStatusChart);
        reservationStatusChartPanel.setPreferredSize(new Dimension(540, 300));
        reservationStatusPanel.add(reservationStatusChartPanel, BorderLayout.CENTER);
        chartsGrid.add(reservationStatusPanel);
        
        // Boat Popularity Chart
        JPanel boatPopularityPanel = new JPanel(new BorderLayout());
        boatPopularityPanel.setBackground(Color.WHITE);
        boatPopularityPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JFreeChart boatPopularityChart = createBoatPopularityChart();
        ChartPanel boatPopularityChartPanel = new ChartPanel(boatPopularityChart);
        boatPopularityChartPanel.setPreferredSize(new Dimension(540, 300));
        boatPopularityPanel.add(boatPopularityChartPanel, BorderLayout.CENTER);
        chartsGrid.add(boatPopularityPanel);
        
        // Monthly Revenue Chart
        JPanel revenuePanel = new JPanel(new BorderLayout());
        revenuePanel.setBackground(Color.WHITE);
        revenuePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JFreeChart revenueChart = createRevenueChart();
        ChartPanel revenueChartPanel = new ChartPanel(revenueChart);
        revenueChartPanel.setPreferredSize(new Dimension(540, 300));
        revenuePanel.add(revenueChartPanel, BorderLayout.CENTER);
        chartsGrid.add(revenuePanel);
        
        // Room Occupancy Chart
        JPanel occupancyPanel = new JPanel(new BorderLayout());
        occupancyPanel.setBackground(Color.WHITE);
        occupancyPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JFreeChart occupancyChart = createOccupancyChart();
        ChartPanel occupancyChartPanel = new ChartPanel(occupancyChart);
        occupancyChartPanel.setPreferredSize(new Dimension(540, 300));
        occupancyPanel.add(occupancyChartPanel, BorderLayout.CENTER);
        chartsGrid.add(occupancyPanel);
        
        container.add(chartsGrid, BorderLayout.CENTER);
        return container;
    }
    
    private JFreeChart createReservationStatusChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        Map<String, Integer> statusCounts = getReservationStatusCounts();
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Reservation Status Distribution",
            dataset,
            true,
            true,
            false
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
        
        // Custom colors for pie slices
        plot.setSectionPaint("Confirmed", new Color(46, 139, 87));
        plot.setSectionPaint("Pending", new Color(255, 140, 0));
        plot.setSectionPaint("Check-in", new Color(30, 144, 255));
        plot.setSectionPaint("Completed", new Color(60, 179, 113));
        plot.setSectionPaint("Cancelled", new Color(220, 20, 60));
        
        return chart;
    }
    
    private JFreeChart createBoatPopularityChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Integer> boatReservations = getBoatReservationCounts();
        for (Map.Entry<String, Integer> entry : boatReservations.entrySet()) {
            dataset.addValue(entry.getValue(), "Reservations", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Water Activity Popularity",
            "Boat Name",
            "Number of Reservations",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // No legend
            true,   // Show tooltips
            false   // No URLs
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setDomainGridlinesVisible(false);
        
        // Customize bars
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(70, 130, 180));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        
        return chart;
    }
    
    private JFreeChart createRevenueChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Double> monthlyRevenue = getMonthlyRevenue();
        for (Map.Entry<String, Double> entry : monthlyRevenue.entrySet()) {
            dataset.addValue(entry.getValue(), "Revenue", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "Monthly Revenue",
            "Month",
            "Amount (₱)",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // No legend
            true,   // Show tooltips
            false   // No URLs
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setDomainGridlinesVisible(false);
        
        // Customize line
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 139, 87));
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));
        
        // Set font for domain and range axis
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        return chart;
    }
    
    private JFreeChart createOccupancyChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Integer> roomOccupancy = getRoomOccupancy();
        for (Map.Entry<String, Integer> entry : roomOccupancy.entrySet()) {
            dataset.addValue(entry.getValue(), "Days Occupied", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Room Occupancy",
            "Room Number",
            "Days Occupied",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // No legend
            true,   // Show tooltips
            false   // No URLs
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setDomainGridlinesVisible(false);
        
        // Customize bars
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(30, 144, 255));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        
        return chart;
    }
    
    // Database Access Methods
    
    private int getTotalRooms() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM room")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getTotalBoats() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM boat")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getTotalGuests() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM guest")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getActiveReservations() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM reservation WHERE status IN ('Confirmed', 'Check-in')")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getPendingReservations() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM reservation WHERE status = 'Pending'")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private double getRevenueToday() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT SUM(total_price) FROM reservation WHERE DATE(created_at) = CURDATE()")) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private Map<String, Integer> getReservationStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT status, COUNT(*) FROM reservation GROUP BY status")) {
            while (rs.next()) {
                counts.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }
    
    private Map<String, Integer> getBoatReservationCounts() {
        Map<String, Integer> counts = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT b.boat_name, COUNT(*) FROM boat_reservation br " +
                 "JOIN boat b ON br.boat_id = b.boat_id GROUP BY b.boat_name")) {
            while (rs.next()) {
                counts.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }
    
    private Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> revenue = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT DATE_FORMAT(check_in_date, '%Y-%m'), SUM(total_price) " +
                 "FROM reservation GROUP BY DATE_FORMAT(check_in_date, '%Y-%m')")) {
            while (rs.next()) {
                revenue.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }
    
    private Map<String, Integer> getRoomOccupancy() {
        Map<String, Integer> occupancy = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT room_number, SUM(DATEDIFF(check_out_date, check_in_date)) " +
                 "FROM room_reservation GROUP BY room_number")) {
            while (rs.next()) {
                occupancy.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return occupancy;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Resort Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(DASHBOARD_WIDTH, DASHBOARD_HEIGHT);
            frame.setLocationRelativeTo(null);
            
            ResortDashboard dashboard = new ResortDashboard();
            frame.add(dashboard);
            frame.setVisible(true);
        });
    }
}

/**
 * Custom ScrollBar UI implementation for minimalist design
 */
class MinimalistScrollBarUI extends BasicScrollBarUI {
    private static final int THUMB_SIZE = 8;
    private static final Color THUMB_COLOR = new Color(120, 120, 120, 100);
    private static final Color THUMB_ROLLOVER_COLOR = new Color(160, 160, 160, 120);
    private static final Color TRACK_COLOR = new Color(0, 0, 0, 0); // Transparent
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
    
    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(TRACK_COLOR);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set color based on mouse hover state
        if (isDragging || isThumbRollover()) {
            g2.setColor(THUMB_ROLLOVER_COLOR);
        } else {
            g2.setColor(THUMB_COLOR);
        }
        
        // Draw rounded rectangle for thumb
        g2.fillRoundRect(
            thumbBounds.x + 1,
            thumbBounds.y,
            thumbBounds.width - 2,
            thumbBounds.height,
            8, 8
        );
    }
    
    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }
}