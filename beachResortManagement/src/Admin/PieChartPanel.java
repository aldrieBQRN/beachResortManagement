/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;

public class PieChartPanel extends JPanel {
    private Map<String, Double> data;
    private Map<String, Color> colorMap;
    
    public PieChartPanel() {
        data = new HashMap<>();
        colorMap = new HashMap<>();
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
    }
    
    /**
     * Add data to the pie chart
     * @param label The label for this data segment
     * @param value The value for this data segment
     * @param color The color for this data segment
     */
    public void addData(String label, double value, Color color) {
        data.put(label, value);
        colorMap.put(label, color);
        repaint();
    }
    
    /**
     * Clear all data from the pie chart
     */
    public void clearData() {
        data.clear();
        colorMap.clear();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate total value
        double total = 0;
        for (double value : data.values()) {
            total += value;
        }
        
        if (total <= 0) {
            g2d.drawString("No data to display", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }
        
        // Draw pie chart
        double currentAngle = 0;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 3;
        
        // Draw segments
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();
            double angle = (value / total) * 360;
            
            g2d.setColor(colorMap.getOrDefault(label, getRandomColor()));
            
            Arc2D.Double arc = new Arc2D.Double(
                centerX - radius, 
                centerY - radius, 
                radius * 2, 
                radius * 2, 
                currentAngle, 
                angle, 
                Arc2D.PIE
            );
            g2d.fill(arc);
            g2d.setColor(Color.BLACK);
            g2d.draw(arc);
            
            // Draw label
            double labelAngle = Math.toRadians(currentAngle + angle / 2);
            int labelX = (int) (centerX + (radius * 1.2) * Math.cos(labelAngle));
            int labelY = (int) (centerY + (radius * 1.2) * Math.sin(labelAngle));
            
            String labelText = label + ": " + value + " (" + String.format("%.1f", (value / total) * 100) + "%)";
            g2d.drawString(labelText, labelX, labelY);
            
            currentAngle += angle;
        }
        
        // Draw legend
        int legendX = 10;
        int legendY = 20;
        int boxSize = 10;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = entry.getKey();
            g2d.setColor(colorMap.getOrDefault(label, Color.BLACK));
            g2d.fillRect(legendX, legendY - boxSize, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, legendY - boxSize, boxSize, boxSize);
            g2d.drawString(label, legendX + boxSize + 5, legendY);
            legendY += 20;
        }
    }
    
    private Color getRandomColor() {
        return new Color(
            (int) (Math.random() * 256),
            (int) (Math.random() * 256),
            (int) (Math.random() * 256)
        );
    }
    
    // Example of how to use this panel
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pie Chart Example");
        PieChartPanel pieChart = new PieChartPanel();
        
        // Add sample data
        pieChart.addData("Category A", 25.0, Color.RED);
        pieChart.addData("Category B", 35.0, Color.BLUE);
        pieChart.addData("Category C", 15.0, Color.GREEN);
        pieChart.addData("Category D", 25.0, Color.ORANGE);
        
        frame.add(pieChart);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
