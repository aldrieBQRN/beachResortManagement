package Login;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;

public class Contact extends JInternalFrame {
    
    String papayaCoveCoords = "14.177193234590122,120.61285088887435"; // Papaya Cove coordinates
    String mapUrl = String.format(
        "https://www.google.com/maps/search/?api=1&query=%s",
        papayaCoveCoords.replace(",", "%2C")
    );
    
    // Font definitions - using web-friendly fonts
    private Font titleFont = new Font("Montserrat", Font.BOLD, 42);
    private Font headingFont = new Font("Montserrat", Font.BOLD, 28);
    private Font subheadingFont = new Font("Montserrat", Font.BOLD, 20);
    private Font bodyFont = new Font("Open Sans", Font.PLAIN, 16);
    private Font captionFont = new Font("Open Sans", Font.ITALIC, 14);
    private Font buttonFont = new Font("Montserrat", Font.BOLD, 16);
    
    // Modern resort color scheme
    private Color primaryBlue = new Color(0, 119, 182);    // Deeper blue for primary elements
    private Color darkBlue = new Color(3, 47, 87);         // Dark blue for headings
    private Color lightBlue = new Color(230, 243, 252);    // Light blue for backgrounds
    private Color accentTeal = new Color(0, 180, 197);     // Teal accent for highlights
    private Color accentOrange = new Color(255, 147, 38);  // Warm orange for CTAs
    private Color sandColor = new Color(252, 248, 232);    // Warm sand color
    private Color textDark = new Color(45, 55, 72);        // Slate for text
    private Color textLight = new Color(255, 255, 255);    // White text
    private Color borderColor = new Color(226, 232, 240);  // Light gray for borders
    
    // Icons map for contact methods
    private Map<String, String> methodIcons = new HashMap<>();
    
    public Contact() {
        super("Contact Azure Waves Resort", true, true, true, true);
        setSize(1440, 820);
        setBackground(Color.WHITE);
        removeBackground();
        
       
        
        JPanel contactContentPanel = createContactContentPanel();
        
        JScrollPane scrollPane = new JScrollPane(contactContentPanel);
        scrollPane.setBorder(null);
        customizeScrollBar(scrollPane);
        
        getContentPane().add(scrollPane);
        setVisible(true);
    }
    
    private void customizeScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setOpaque(false);
        
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new ModernScrollBarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));
        horizontalScrollBar.setOpaque(false);
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
    }
    
    private class ModernScrollBarUI extends BasicScrollBarUI {
        private final int SCROLL_BAR_ALPHA_ROLLOVER = 180;
        private final int SCROLL_BAR_ALPHA = 120;
        private final int THUMB_SIZE = 8;
        private final Color THUMB_COLOR = accentTeal;
        
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(240, 240, 240)); // Light grey track
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 5, 5);
            g2.dispose();
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                           Math.max(THUMB_SIZE, thumbBounds.width), 
                           Math.max(THUMB_SIZE, thumbBounds.height), 10, 10);
            g2.dispose();
        }
    }
    
    public final void removeBackground() {
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI ui = (BasicInternalFrameUI)this.getUI();
        ui.setNorthPane(null);
    }
    
    private JPanel createContactContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
     
        
        // Contact Header
        panel.add(createContactHeaderPanel());
        
        // Contact Information Section
        panel.add(createContactInfoPanel());
        panel.add(Box.createVerticalStrut(40));
        
        // Contact Form Section
        panel.add(createContactFormPanel());
        panel.add(Box.createVerticalStrut(40));
      
        
        return panel;
    }
    

    
    private JPanel createContactHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 40, 80));
        
        // Title with accent
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleContainer.setBackground(Color.WHITE);
        
        JLabel sectionTitle = new JLabel("How Can We Help You?");
        sectionTitle.setFont(headingFont);
        sectionTitle.setForeground(darkBlue);
        
        titleContainer.add(sectionTitle);
        
        // Subtitle with decorative elements
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subtitlePanel.setBackground(Color.WHITE);
        
        // Left decorator
        JPanel leftDivider = new JPanel();
        leftDivider.setBackground(accentTeal);
        leftDivider.setPreferredSize(new Dimension(50, 2));
        
        JLabel subtitle = new JLabel("We'd love to hear from you");
        subtitle.setFont(new Font("Open Sans", Font.ITALIC, 18));
        subtitle.setForeground(primaryBlue);
        
        // Right decorator
        JPanel rightDivider = new JPanel();
        rightDivider.setBackground(accentTeal);
        rightDivider.setPreferredSize(new Dimension(50, 2));
        
        subtitlePanel.add(leftDivider);
        subtitlePanel.add(Box.createHorizontalStrut(20));
        subtitlePanel.add(subtitle);
        subtitlePanel.add(Box.createHorizontalStrut(20));
        subtitlePanel.add(rightDivider);
        
        headerPanel.add(titleContainer);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(subtitlePanel);
        
        return headerPanel;
    }
    
    private JPanel createContactInfoPanel() {
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
    containerPanel.setBackground(Color.WHITE);
    containerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
    
    // Section header
    JLabel sectionTitle = new JLabel("Contact Information");
    sectionTitle.setFont(new Font("Montserrat", Font.BOLD, 28));
    sectionTitle.setForeground(darkBlue);
    sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
    
    containerPanel.add(sectionTitle);
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridLayout(1, 4, 30, 0));
    infoPanel.setBackground(Color.WHITE);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
    String[] methods = {"Phone", "Email", "Location", "Hours"};
    String[] details = {
        "<html><center><b>+1 (555) 123-4567</b><br>24/7 Customer Support<br><br><small>For emergencies: +1 (555) 987-6543</small></center></html>",
        "<html><center><b>papayabeach@gmail.com</b><br>General inquiries<br><br><b>papayareservation@gmail.com</b><br>Booking requests</center></html>",
        "<html><center><b>Papaya Beach Resort</b><br>St. Dagundong, Brgy. Papaya,<br>Nasugbu, Batangas</center></html>",
        "<html><center><b>Front Desk:</b> 24/7<br><b>Dining:</b> 7AM-11PM<br><b>Spa:</b> 9AM-8PM<br><b>Pool:</b> 6AM-10PM</center></html>"
    };
    
    for (int i = 0; i < methods.length; i++) {
        JPanel card = createModernContactCard(methods[i], details[i]);
        if (methods[i].equals("Location")) {
            // Create panel that will act as button
            JPanel locationPanel = new JPanel();
            locationPanel.setLayout(new BorderLayout());
            locationPanel.setBackground(lightBlue); // Button background color
            locationPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            locationPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Create label that will act as button text
            JLabel locationLabel = new JLabel("View Location", SwingConstants.CENTER);
            locationLabel.setFont(new Font("Montserrat", Font.BOLD, 12));
            locationLabel.setForeground(Color.BLACK);
            
            locationPanel.add(locationLabel, BorderLayout.CENTER);
            
            // Add mouse listener to make it interactive
            locationPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    try {
                        openMapInBrowser();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                public void mouseEntered(MouseEvent e) {
                    locationPanel.setBackground(Color.WHITE); // Darken on hover
                }
                
                public void mouseExited(MouseEvent e) {
                    locationPanel.setBackground(lightBlue); // Restore original color
                }
            });
            
            // Add some space and the "button" panel to the card
            card.add(Box.createVerticalStrut(10));
            card.add(locationPanel);
        }
        infoPanel.add(card);
    }
    
    containerPanel.add(infoPanel);
    
    return containerPanel;
}
    
    private void openMapInBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(mapUrl));
            } else {
                // Fallback for Linux or unsupported systems
                Runtime.getRuntime().exec("xdg-open " + mapUrl);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Could not open browser: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


private JPanel createModernContactCard(String method, String details) {
    RoundedPanel card = new RoundedPanel(20); // Increased corner radius
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(new Color(245, 250, 255)); // Slightly darker background
    card.setBorder(new CompoundBorder(
        new MatteBorder(0, 0, 3, 0, accentTeal), // Bottom border accent
        new EmptyBorder(30, 25, 30, 25) // Increased padding
    ));
    
    // Icon with modern styling
    JLabel iconLabel = new JLabel(methodIcons.get(method));
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
    iconLabel.setForeground(primaryBlue);
    iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Method Title with better typography
    JLabel titleLabel = new JLabel(method);
    titleLabel.setFont(new Font("Montserrat", Font.BOLD, 22));
    titleLabel.setForeground(darkBlue);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Divider with animation
    JPanel divider = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(accentOrange);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        }
    };
    divider.setMaximumSize(new Dimension(80, 4)); // Wider divider
    divider.setPreferredSize(new Dimension(80, 4));
    divider.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Details with HTML formatting
    JLabel detailsLabel = new JLabel(details);
    detailsLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
    detailsLabel.setForeground(new Color(70, 80, 90)); // Darker text for better readability
    detailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    card.add(iconLabel);
    card.add(Box.createVerticalStrut(15)); // More spacing
    card.add(titleLabel);
    card.add(Box.createVerticalStrut(15));
    card.add(divider);
    card.add(Box.createVerticalStrut(20)); // Increased spacing
    card.add(detailsLabel);
    
    // Enhanced hover effect with animation
    card.addMouseListener(new MouseAdapter() {
        private Timer timer;
        private Color startColor = new Color(245, 250, 255);
        private Color endColor = new Color(230, 243, 252);
        
        @Override
        public void mouseEntered(MouseEvent e) {
            if (timer != null && timer.isRunning()) timer.stop();
            timer = new Timer(10, evt -> {
                float ratio = Math.min(1f, ((float)evt.getWhen() - e.getWhen()) / 200f);
                card.setBackground(blendColors(startColor, endColor, ratio));
                if (ratio == 1f) timer.stop();
            });
            timer.start();
            
            titleLabel.setForeground(primaryBlue);
            iconLabel.setForeground(accentOrange);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            if (timer != null && timer.isRunning()) timer.stop();
            timer = new Timer(10, evt -> {
                float ratio = 1f - Math.min(1f, ((float)evt.getWhen() - e.getWhen()) / 200f);
                card.setBackground(blendColors(startColor, endColor, ratio));
                if (ratio == 0f) timer.stop();
            });
            timer.start();
            
            titleLabel.setForeground(darkBlue);
            iconLabel.setForeground(primaryBlue);
        }
    });
    
    return card;
}

// Helper method for color animation
private Color blendColors(Color c1, Color c2, float ratio) {
    int r = (int)(c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
    int g = (int)(c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
    int b = (int)(c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
    return new Color(r, g, b);
}
    

    
private JPanel createContactFormPanel() {
    // Main form container with centered content
    RoundedPanel formPanel = new RoundedPanel(15);
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBackground(sandColor);
    formPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
    
    // Center the form contents using a wrapper panel
    JPanel centerWrapper = new JPanel();
    centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
    centerWrapper.setOpaque(false);
    centerWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
    centerWrapper.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
    
    // Form Header Section
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
    headerPanel.setOpaque(false);
    headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

    // Form Title
    JLabel formTitle = new JLabel("Send Us a Message");
    formTitle.setFont(new Font("Montserrat", Font.BOLD, 24));
    formTitle.setForeground(darkBlue);
    formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Underline accent
    JPanel titleUnderline = new JPanel();
    titleUnderline.setPreferredSize(new Dimension(60, 3));
    titleUnderline.setMaximumSize(new Dimension(60, 3));
    titleUnderline.setBackground(accentTeal);
    titleUnderline.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Form description
    JLabel formDesc = new JLabel("Have questions or special requests? We'll respond within 24 hours.");
    formDesc.setFont(new Font("Open Sans", Font.PLAIN, 14));
    formDesc.setForeground(new Color(100, 110, 120));
    formDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
    formDesc.setBorder(new EmptyBorder(10, 0, 0, 0));

    headerPanel.add(formTitle);
    headerPanel.add(Box.createVerticalStrut(10));
    headerPanel.add(titleUnderline);
    headerPanel.add(formDesc);
    
    // Form Fields Container
    JPanel fieldsPanel = new JPanel();
    fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
    fieldsPanel.setOpaque(false);
    fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Form fields
    fieldsPanel.add(createCenteredFormField("Full Name", "Enter your full name"));
    fieldsPanel.add(Box.createVerticalStrut(20));
    fieldsPanel.add(createCenteredFormField("Email Address", "Enter your email"));
    fieldsPanel.add(Box.createVerticalStrut(20));
    fieldsPanel.add(createCenteredFormField("Subject", "Enter message subject"));
    fieldsPanel.add(Box.createVerticalStrut(20));
    
    // Message Area
    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    messagePanel.setOpaque(false);
    messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JLabel messageLabel = new JLabel("Your Message");
    messageLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
    messageLabel.setForeground(textDark);
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JTextArea messageArea = new JTextArea(5, 20);
    messageArea.setFont(new Font("Open Sans", Font.PLAIN, 14));
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);
    messageArea.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    messageArea.setMaximumSize(new Dimension(600, 150));
    
    // Add placeholder to message area
    messageArea.setText("Type your message here...");
    messageArea.setForeground(new Color(170, 170, 170));
    messageArea.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (messageArea.getText().equals("Type your message here...")) {
                messageArea.setText("");
                messageArea.setForeground(textDark);
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if (messageArea.getText().isEmpty()) {
                messageArea.setText("Type your message here...");
                messageArea.setForeground(new Color(170, 170, 170));
            }
        }
    });
    
    JScrollPane scrollPane = new JScrollPane(messageArea);
    scrollPane.setBorder(null);
    scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    messagePanel.add(messageLabel);
    messagePanel.add(Box.createVerticalStrut(8));
    messagePanel.add(scrollPane);
    
    fieldsPanel.add(messagePanel);
    fieldsPanel.add(Box.createVerticalStrut(30));
    
    // Centered Submit Button
    JButton submitButton = createCenteredButton("Send Message");
    
    // Add all components to the center wrapper
    centerWrapper.add(headerPanel);
    centerWrapper.add(fieldsPanel);
    centerWrapper.add(submitButton);
    
    // Add the centered wrapper to the main panel
    formPanel.add(centerWrapper);
    
    return formPanel;
}

private JPanel createCenteredFormField(String labelText, String placeholder) {
    JPanel fieldPanel = new JPanel();
    fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
    fieldPanel.setOpaque(false);
    fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Label
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("Open Sans", Font.PLAIN, 14));
    label.setForeground(textDark);
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Text Field
    JTextField textField = new JTextField(placeholder);
    textField.setFont(new Font("Open Sans", Font.PLAIN, 14));
    textField.setForeground(new Color(170, 170, 170));
    textField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(12, 15, 12, 15)
    ));
    textField.setMaximumSize(new Dimension(600, 45)); // Fixed width
    textField.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Placeholder behavior
    textField.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (textField.getText().equals(placeholder)) {
                textField.setText("");
                textField.setForeground(textDark);
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if (textField.getText().isEmpty()) {
                textField.setText(placeholder);
                textField.setForeground(new Color(170, 170, 170));
            }
        }
    });
    
    fieldPanel.add(label);
    fieldPanel.add(Box.createVerticalStrut(8));
    fieldPanel.add(textField);
    
    return fieldPanel;
}

private JButton createCenteredButton(String text) {
    JButton button = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(accentOrange.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(accentOrange.brighter());
            } else {
                g2.setColor(accentOrange);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(getText(), g2);
            int x = (getWidth() - (int) r.getWidth()) / 2;
            int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            
            g2.dispose();
        }
    };
    
    button.setFont(new Font("Montserrat", Font.BOLD, 14));
    button.setForeground(Color.WHITE);
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(180, 45));
    button.setMaximumSize(new Dimension(180, 45));
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    return button;
}



// Custom ComboBox class for modern styling
private class ModernComboBox<E> extends JComboBox<E> {
    public ModernComboBox(E[] items) {
        super(items);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        
        // Paint border
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
        
        // Paint arrow
        g2.setColor(new Color(100, 100, 100));
        int arrowX = getWidth() - 20;
        int arrowY = getHeight() / 2;
        int[] xPoints = {arrowX, arrowX + 10, arrowX + 5};
        int[] yPoints = {arrowY - 5, arrowY - 5, arrowY + 5};
        g2.fillPolygon(xPoints, yPoints, 3);
        
        g2.dispose();
        
        super.paintComponent(g);
    }
}
    
    // Inner class for rounded panels
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, 
                getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame frame = new JFrame("Azure Waves Resort");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1480, 900);
        frame.setLocationRelativeTo(null);
        
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.WHITE);
        
        Contact contactScreen = new Contact();
        contactScreen.setLocation(10, 10);
        desktopPane.add(contactScreen);
        
        frame.add(desktopPane);
        frame.setVisible(true);
    }
}