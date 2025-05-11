package Login;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class About extends JInternalFrame {
    
    // Font definitions
    private Font titleFont = new Font("Poppins", Font.BOLD, 36);
    private Font headingFont = new Font("Poppins", Font.BOLD, 26);
    private Font subheadingFont = new Font("Poppins", Font.BOLD, 20);
    private Font bodyFont = new Font("Roboto", Font.PLAIN, 16);
    private Color accentTeal = new Color(0, 180, 197);
    private Font captionFont = new Font("Roboto", Font.ITALIC, 14);
    
    // Color scheme - More vibrant and sophisticated
    private Color primaryBlue = new Color(0, 149, 200);       // Bright blue
    private Color darkBlue = new Color(5, 68, 94);           // Dark blue
    private Color lightBlue = new Color(212, 241, 252);      // Very light blue
    private Color accentOrange = new Color(255, 153, 51);    // Orange for accents
    private Color sandColor = new Color(255, 248, 231);      // Light sand
    private Color textDark = new Color(51, 51, 51);          // Dark text
    private Color textLight = new Color(250, 250, 250);      // Light text
    
    public About() {
        // Set up the JInternalFrame
        super("About Azure Waves Resort", true, true, true, true);
        setSize(1440, 730);
        setBackground(Color.WHITE);
        
        // Remove the title bar and border
        removeBackground();
        
        // Create the about content panel
        JPanel aboutContentPanel = createAboutContentPanel();
        
        // Add scroll capabilities
        JScrollPane scrollPane = new JScrollPane(aboutContentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Apply custom scroll bar styling
        customizeScrollBar(scrollPane);
        
        // Add to frame
        getContentPane().add(scrollPane);
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void customizeScrollBar(JScrollPane scrollPane) {
        // Customize the vertical scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setOpaque(false);
        
        // Customize the horizontal scroll bar (if needed)
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new ModernScrollBarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 10));
        horizontalScrollBar.setOpaque(false);
        
        // Set scroll pane properties
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
    }
    
    private class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int SCROLL_BAR_ALPHA_ROLLOVER = 150;
        private static final int SCROLL_BAR_ALPHA = 100;
        private static final int THUMB_SIZE = 8;
        private final Color THUMB_COLOR = accentTeal; // Same as primaryBlue
        
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
            // No track painting
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
            int orientation = scrollbar.getOrientation();
            int x = thumbBounds.x;
            int y = thumbBounds.y;
            
            int width = orientation == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width;
            width = Math.max(width, THUMB_SIZE);
            
            int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height : THUMB_SIZE;
            height = Math.max(height, THUMB_SIZE);
            
            Graphics2D graphics2D = (Graphics2D) g.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
            graphics2D.fillRoundRect(x, y, width, height, 10, 10);
            graphics2D.dispose();
        }
        
        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            scrollbar.repaint();
        }
    }
    
    /**
     * Removes the title bar and border from the internal frame
     */
    public final void removeBackground() {
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI UI = (BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null);
    }
    
    private JPanel createAboutContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Our Story Section
        JPanel ourStoryPanel = createOurStoryPanel();
        
        // Our Activities Section with improved cards
        JPanel activitiesPanel = createActivitiesPanel();
        
        // Add all components to main panel
        panel.add(ourStoryPanel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(activitiesPanel);
        panel.add(Box.createVerticalStrut(40));
        
        return panel;
    }
    
    private JPanel createOurStoryPanel() {
        JPanel ourStoryPanel = new JPanel();
        ourStoryPanel.setLayout(new BoxLayout(ourStoryPanel, BoxLayout.Y_AXIS));
        ourStoryPanel.setBackground(Color.WHITE);
        ourStoryPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 40, 80));
        
        // Section title with accent
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleContainer.setBackground(Color.WHITE);
        
        JLabel sectionIcon = new JLabel("✦");
        sectionIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        sectionIcon.setForeground(accentOrange);
        
        JLabel sectionTitle = new JLabel("About Us");
        sectionTitle.setFont(headingFont);
        sectionTitle.setForeground(darkBlue);
        
        JLabel sectionIcon2 = new JLabel("✦");
        sectionIcon2.setFont(new Font("Arial", Font.PLAIN, 24));
        sectionIcon2.setForeground(accentOrange);
        
        titleContainer.add(sectionIcon);
        titleContainer.add(Box.createHorizontalStrut(15));
        titleContainer.add(sectionTitle);
        titleContainer.add(Box.createHorizontalStrut(15));
        titleContainer.add(sectionIcon2);
        
        // Main content with image and text in modern layout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Create rounded panel for image
        JPanel imagePanel = new RoundedPanel(20);
imagePanel.setLayout(new BorderLayout());
imagePanel.setBackground(lightBlue);
imagePanel.setPreferredSize(new Dimension(600, 350));
imagePanel.setMaximumSize(new Dimension(600, 350));

try {
    // Load the image
      ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Image/brach_front.png"));
    
    // Scale the image to fit the panel while maintaining aspect ratio
    Image scaledImage = originalIcon.getImage().getScaledInstance(
        580, 330, Image.SCALE_SMOOTH);
    ImageIcon scaledIcon = new ImageIcon(scaledImage);
    
    // Create image label
    JLabel resortImage = new JLabel(scaledIcon);
    resortImage.setHorizontalAlignment(JLabel.CENTER);
    resortImage.setVerticalAlignment(JLabel.CENTER);
    
    // Add tooltip in case image fails to load
    resortImage.setToolTipText("Papaya Beach Resort Front View");
    
    imagePanel.add(resortImage, BorderLayout.CENTER);
    
} catch (Exception e) {
    // Fallback in case image fails to load
    JLabel errorLabel = new JLabel("Image not found");
    errorLabel.setFont(subheadingFont);
    errorLabel.setForeground(Color.RED);
    errorLabel.setHorizontalAlignment(JLabel.CENTER);
    imagePanel.add(errorLabel, BorderLayout.CENTER);
    
    e.printStackTrace();
}
        
        // About Text Panel with styled text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome to Paradise");
        welcomeLabel.setFont(subheadingFont);
        welcomeLabel.setForeground(primaryBlue);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Orange accent line
        JPanel accentLine = new JPanel();
        accentLine.setMaximumSize(new Dimension(100, 3));
        accentLine.setPreferredSize(new Dimension(100, 3));
        accentLine.setBackground(accentOrange);
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Styled description text
        JTextPane descriptionText = new JTextPane();
        descriptionText.setContentType("text/html");
        descriptionText.setText(
            "<html><body style='font-family:Roboto; font-size:15pt; width:450px'>" +
            "<p>Established in 2010, <b>Papaya Beach Resort</b> has been providing unforgettable beach experiences " +
            "for over 15 years. Our luxurious beachfront property spans 12 acres of pristine coastline, offering " +
            "breathtaking views and direct access to crystal-clear waters and white sandy beaches.</p>" +
            "<p>We pride ourselves on offering <span style='color:#FF9933'>world-class accommodations</span> paired with " +
            "<span style='color:#FF9933'>exceptional service</span>. Our dedicated staff ensures that each guest experiences " +
            "the perfect blend of relaxation and adventure during their stay.</p>" +
            "<p>From thrilling water activities to serene spa treatments, we cater to all types of travelers seeking " +
            "a memorable beach getaway. Our commitment to sustainability ensures that the natural beauty of our " +
            "surroundings is preserved for generations to come.</p>" +
            "</body></html>"
        );
        descriptionText.setEditable(false);
        descriptionText.setBackground(Color.WHITE);
        descriptionText.setBorder(null);
        descriptionText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(accentLine);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(descriptionText);
        
        contentPanel.add(imagePanel);
        contentPanel.add(textPanel);
        
        ourStoryPanel.add(titleContainer);
        ourStoryPanel.add(contentPanel);
        
        return ourStoryPanel;
    }
    
    private JPanel createActivitiesPanel() {
        JPanel activitiesPanel = new JPanel();
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));
        activitiesPanel.setBackground(sandColor);
        activitiesPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        
        // Section title
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleContainer.setBackground(sandColor);
        
        JLabel activitiesTitle = new JLabel("WATER ACTIVITIES");
        activitiesTitle.setFont(headingFont);
        activitiesTitle.setForeground(darkBlue);
        titleContainer.add(activitiesTitle);
        
        // Subtitle
        JLabel subtitle = new JLabel("Explore the Best Aquatic Adventures");
        subtitle.setFont(new Font("Roboto", Font.ITALIC, 18));
        subtitle.setForeground(primaryBlue);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Activities Grid with modern cards
        JPanel activitiesGrid = new JPanel(new GridLayout(1, 4, 30, 0));
        activitiesGrid.setBackground(sandColor);
        activitiesGrid.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        String[] activities = {"Cove Tour", "Fish Feeding", "Snorkeling", "Cliff Diving"};
        Color[] cardColors = {
            new Color(232, 245, 253), // Light blue
            new Color(255, 243, 224), // Light orange
            new Color(232, 245, 233), // Light green
            new Color(239, 246, 255)  // Very light blue
        };
        
        for (int i = 0; i < activities.length; i++) {
            activitiesGrid.add(createActivityCard(activities[i], cardColors[i]));
        }
        
        activitiesPanel.add(titleContainer);
        activitiesPanel.add(Box.createVerticalStrut(10));
        activitiesPanel.add(subtitle);
        activitiesPanel.add(activitiesGrid);
        
        return activitiesPanel;
    }
    
    private JPanel createActivityCard(String activity, Color bgColor) {
        RoundedPanel card = new RoundedPanel(15);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        
        // Activity Icon (placeholder as a circle)
        JPanel iconContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(primaryBlue);
                g2d.fillOval(25, 5, 60, 60);
                
                // Draw icon text
                g2d.setColor(Color.WHITE);
                Font iconFont = new Font("Arial", Font.BOLD, 24);
                g2d.setFont(iconFont);
                FontMetrics fm = g2d.getFontMetrics();
                String iconText = activity.substring(0, 1);
                int textWidth = fm.stringWidth(iconText);
                int textHeight = fm.getHeight();
                g2d.drawString(iconText, 25 + (60 - textWidth) / 2, 5 + (60 - textHeight) / 2 + fm.getAscent());
            }
        };
        iconContainer.setPreferredSize(new Dimension(110, 70));
        iconContainer.setMaximumSize(new Dimension(110, 70));
        iconContainer.setOpaque(false);
        iconContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Activity Title
        JLabel activityTitle = new JLabel(activity);
        activityTitle.setFont(subheadingFont);
        activityTitle.setForeground(darkBlue);
        activityTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Small divider
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(60, 2));
        divider.setPreferredSize(new Dimension(60, 2));
        divider.setBackground(accentOrange);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Activity description
        String descriptions = switch(activity) {
            case "Cove Tour" -> "Discover hidden coves and pristine beaches on our guided boat tours. Explore secluded spots only accessible by water and learn about the area's rich marine ecosystem from our knowledgeable guides.";
            case "Fish Feeding" -> "Interact with colorful tropical fish in their natural habitat. Our supervised feeding sessions allow you to observe diverse marine life up close while learning about local species and conservation efforts.";
            case "Snorkeling" -> "Immerse yourself in the vibrant underwater world with our guided snorkeling excursions. Perfect for all skill levels, explore coral gardens teeming with marine life in crystal-clear waters.";
            case "Cliff Diving" -> "Challenge yourself with our thrilling cliff diving experiences at carefully selected natural platforms. Our certified instructors provide safety training and guidance for all levels, from beginners to adrenaline seekers.";
            default -> "Experience unforgettable aquatic adventures with our professional guides and premium equipment, tailored to create lasting memories of your coastal getaway.";
        };
        
        JTextArea activityDesc = new JTextArea(descriptions);
        activityDesc.setFont(bodyFont);
        activityDesc.setForeground(textDark);
        activityDesc.setLineWrap(true);
        activityDesc.setWrapStyleWord(true);
        activityDesc.setEditable(false);
        activityDesc.setBackground(new Color(0, 0, 0, 0));
        activityDesc.setOpaque(false);
        activityDesc.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));
        activityDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Learn More button (compact version)
        JPanel learnMorePanel = new JPanel();
        learnMorePanel.setLayout(new BorderLayout());
        learnMorePanel.setBackground(primaryBlue);
        learnMorePanel.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12)); // Tight padding
        learnMorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        learnMorePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        learnMorePanel.setMaximumSize(new Dimension(100, 30)); // Smaller fixed size

        // Compact label
        JLabel learnMoreLabel = new JLabel("Learn More", SwingConstants.CENTER);
        learnMoreLabel.setFont(new Font("Roboto", Font.BOLD, 11)); // Smaller font
        learnMoreLabel.setForeground(Color.WHITE);
        learnMorePanel.add(learnMoreLabel, BorderLayout.CENTER);

        // Subtle rounded corners
        learnMorePanel.setBorder(new RoundedBorder(8, primaryBlue)); // Smaller radius

        // Keep hover effects
        learnMorePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                learnMorePanel.setBackground(primaryBlue.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                learnMorePanel.setBackground(primaryBlue);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Learn More clicked for " + activity);
            }
        });
        
        card.add(Box.createVerticalStrut(20));
        card.add(iconContainer);
        card.add(Box.createVerticalStrut(15));
        card.add(activityTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(divider);
        card.add(Box.createVerticalStrut(15));
        card.add(activityDesc);
        card.add(Box.createVerticalGlue());
        card.add(learnMorePanel);
        card.add(Box.createVerticalStrut(20));
        
        return card;
    }
    
    // Custom rounded panel class
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        
        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            RoundRectangle2D roundedRect = new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            g2.setColor(getBackground());
            g2.fill(roundedRect);
            g2.dispose();
        }
    }
    
    private class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+1, this.radius+1);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.bottom = insets.top = this.radius+1;
            return insets;
        }
    }
    
    // Demo application with JDesktopPane to show the internal frame
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            // Create main application frame
            JFrame mainFrame = new JFrame("Beach Resort Management System");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1500, 800);
            
            // Create desktop pane
            JDesktopPane desktopPane = new JDesktopPane();
            desktopPane.setBackground(new Color(240, 240, 240));
            mainFrame.add(desktopPane);
            
            // Create and add internal frame
            About aboutFrame = new About();
            desktopPane.add(aboutFrame);
            
            // Center the internal frame in the desktop pane
            Dimension desktopSize = desktopPane.getSize();
            Dimension frameSize = aboutFrame.getSize();
            aboutFrame.setLocation((desktopSize.width - frameSize.width) / 2, 
                                  (desktopSize.height - frameSize.height) / 2);
            
            // Display the main frame
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
            
            // Try to select the internal frame
            try {
                aboutFrame.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
                e.printStackTrace();
            }
        });
    }
}