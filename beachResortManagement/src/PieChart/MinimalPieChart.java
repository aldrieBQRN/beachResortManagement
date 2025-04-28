package PieChart;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MinimalPieChart extends JPanel {
    private List<PieSegment> segments = new ArrayList<>();
    private double totalValue;
    private int padding = 10;
    private boolean showLabels = true;
    private Color backgroundColor = new Color(250, 250, 250);
    private Color textColor = new Color(80, 80, 80);

    public MinimalPieChart() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void addSegment(String label, double value, Color color) {
        segments.add(new PieSegment(label, value, color));
        totalValue += value;
        repaint();
    }

    public void clearSegments() {
        segments.clear();
        totalValue = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int diameter = Math.min(getWidth(), getHeight()) - 2 * padding;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw light background circle
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillOval(centerX - diameter/2, centerY - diameter/2, diameter, diameter);

        // Draw segments with subtle spacing
        double startAngle = 0;
        for (PieSegment segment : segments) {
            double extent = 360 * (segment.value / totalValue);
            
            // Draw segment with subtle white border
            g2d.setColor(segment.color);
            g2d.fill(new Arc2D.Double(
                centerX - diameter/2 + 1, 
                centerY - diameter/2 + 1, 
                diameter - 2, 
                diameter - 2, 
                startAngle, 
                extent - 0.5, // creates subtle gap between segments
                Arc2D.PIE
            ));

            // Draw labels if enabled
            if (showLabels && extent > 10) {
                double midAngle = Math.toRadians(startAngle + extent / 2);
                double labelRadius = diameter * 0.35;
                
                int labelX = (int) (centerX + labelRadius * Math.cos(midAngle));
                int labelY = (int) (centerY - labelRadius * Math.sin(midAngle));
                
                String percentage = String.format("%.0f%%", (segment.value / totalValue) * 100);
                drawCenteredText(g2d, percentage, labelX, labelY, textColor, 12);
            }

            startAngle += extent;
        }

        // Draw center circle for modern look
        g2d.setColor(backgroundColor);
        int innerDiameter = diameter / 3;
        g2d.fill(new Ellipse2D.Double(
            centerX - innerDiameter/2, 
            centerY - innerDiameter/2, 
            innerDiameter, 
            innerDiameter
        ));

        g2d.dispose();
    }

    private void drawCenteredText(Graphics2D g2d, String text, int x, int y, Color color, int size) {
        Font font = new Font("Segoe UI", Font.PLAIN, size);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, g2d);
        
        g2d.setColor(color);
        g2d.drawString(
            text, 
            (int)(x - bounds.getWidth()/2), 
            (int)(y + bounds.getHeight()/4) // Adjust for baseline
        );
    }

    // Configuration methods
    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        repaint();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        repaint();
    }

    private static class PieSegment {
        String label;
        double value;
        Color color;

        PieSegment(String label, double value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    // Example usage
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Minimal Pie Chart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            
            MinimalPieChart pieChart = new MinimalPieChart();
            
            // Modern color palette
            pieChart.addSegment("Technology", 45, new Color(100, 149, 237)); // Cornflower blue
            pieChart.addSegment("Design", 25, new Color(255, 160, 122)); // Light salmon
            pieChart.addSegment("Marketing", 15, new Color(152, 251, 152)); // Pale green
            pieChart.addSegment("Other", 15, new Color(221, 160, 221)); // Plum
            
            frame.add(pieChart);
            frame.setVisible(true);
        });
    }
}