/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResortAdminDashboard extends JFrame {
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(0, 105, 148); // Ocean blue
    private final Color SECONDARY_COLOR = new Color(255, 165, 0); // Sunset orange
    private final Color ACCENT_COLOR = new Color(0, 150, 136); // Teal
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color CARD_COLOR = Color.WHITE;
    
    public ResortAdminDashboard() {
        // Frame setup
        setTitle("Beachfront Resort - Admin Dashboard");
        setSize(1170, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create sidebar
        JPanel sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Create content area
        JPanel contentPanel = createContentArea();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Beachfront Resort Admin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome, Admin");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel notificationIcon = new JLabel(new ImageIcon("icons/notification.png"));
        JLabel userIcon = new JLabel(new ImageIcon("icons/user.png"));
        
        userPanel.add(notificationIcon);
        userPanel.add(welcomeLabel);
        userPanel.add(userIcon);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(PRIMARY_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Menu items
        String[] menuItems = {"Dashboard", "Reservations", "Guests", "Rooms", 
                             "Boat Rides", "Payments", "Reports", "Settings"};
        String[] iconNames = {"dashboard", "reservations", "guests", "rooms", 
                             "boat", "payments", "reports", "settings"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = new JButton(menuItems[i]);
            menuButton.setIcon(new ImageIcon("icons/" + iconNames[i] + ".png"));
            menuButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            menuButton.setForeground(Color.WHITE);
            menuButton.setBackground(PRIMARY_COLOR);
            menuButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setFocusPainted(false);
            menuButton.setMaximumSize(new Dimension(200, 40));
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Add hover effect
            menuButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(0, 85, 128));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(PRIMARY_COLOR);
                }
            });
            
            sidebarPanel.add(menuButton);
            
            // Add spacing between buttons except after last one
            if (i < menuItems.length - 1) {
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        // Add flexible space to push the logout button to bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setIcon(new ImageIcon("icons/logout.png"));
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setFocusPainted(false);
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        logoutButton.addActionListener(e -> {
            // Handle logout
            JOptionPane.showMessageDialog(this, "Logging out...");
            System.exit(0);
        });
        
        sidebarPanel.add(logoutButton);
        
        return sidebarPanel;
    }
    
    private JPanel createContentArea() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Dashboard title
        JLabel dashboardTitle = new JLabel("Dashboard Overview");
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(dashboardTitle, BorderLayout.NORTH);
        
        // Main content with cards
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        cardsPanel.setBackground(BACKGROUND_COLOR);
        
        // Create cards
        String[] cardTitles = {"Today's Reservations", "Active Guests", "Available Rooms", 
                              "Boat Ride Bookings", "Revenue Today", "Occupancy Rate"};
        String[] cardValues = {"12", "45", "23", "8", "$4,850", "78%"};
        Color[] cardColors = {PRIMARY_COLOR, ACCENT_COLOR, SECONDARY_COLOR, 
                             PRIMARY_COLOR, ACCENT_COLOR, SECONDARY_COLOR};
        
        for (int i = 0; i < cardTitles.length; i++) {
            JPanel card = createDashboardCard(cardTitles[i], cardValues[i], cardColors[i]);
            cardsPanel.add(card);
        }
        
        contentPanel.add(cardsPanel, BorderLayout.CENTER);
        
        // Recent activity section
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(CARD_COLOR);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));
        
        JLabel activityTitle = new JLabel("Recent Activities");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Sample activity data
        String[] activities = {
            "New reservation from John Doe (Room 203)",
            "Boat ride booked by Jane Smith (2:00 PM)",
            "Check-out processed for Room 105",
            "Payment received from Robert Johnson",
            "New guest registration - Alice Williams"
        };
        
        String[] times = {"10 mins ago", "25 mins ago", "1 hour ago", "2 hours ago", "3 hours ago"};
        
        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setBackground(CARD_COLOR);
        
        for (int i = 0; i < activities.length; i++) {
            JPanel activityItem = new JPanel(new BorderLayout());
            activityItem.setBackground(CARD_COLOR);
            activityItem.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            JLabel activityLabel = new JLabel(activities[i]);
            activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            JLabel timeLabel = new JLabel(times[i]);
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            timeLabel.setForeground(Color.GRAY);
            
            activityItem.add(activityLabel, BorderLayout.CENTER);
            activityItem.add(timeLabel, BorderLayout.EAST);
            activityList.add(activityItem);
            
            if (i < activities.length - 1) {
                activityList.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }
        
        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(new JScrollPane(activityList), BorderLayout.CENTER);
        
        contentPanel.add(activityPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        // Add icon based on title
        String iconName = "";
        if (title.contains("Reservation")) iconName = "reservations";
        else if (title.contains("Guest")) iconName = "guests";
        else if (title.contains("Room")) iconName = "rooms";
        else if (title.contains("Boat")) iconName = "boat";
        else if (title.contains("Revenue")) iconName = "payments";
        else if (title.contains("Occupancy")) iconName = "reports";
        
        JLabel iconLabel = new JLabel(new ImageIcon("icons/" + iconName + "_large.png"));
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);
        
        return card;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ResortAdminDashboard dashboard = new ResortAdminDashboard();
            dashboard.setVisible(true);
        });
    }
} 

