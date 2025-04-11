package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class progressBar extends JProgressBar {

    public Color getColorString() {
        return colorString;
    }

    public void setColorString(Color colorString) {
        this.colorString = colorString;
    }

    private Color colorString = new Color(200, 200, 200);

    public progressBar() {
        // Set string painted to false to remove the percentage text
        setStringPainted(false);
        setPreferredSize(new Dimension(100, 15));
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(69, 124, 235));
        setUI(new BasicProgressBarUI() {
            @Override
            protected void paintString(Graphics grphcs, int i, int i1, int i2, int i3, int i4, Insets insets) {
                // Override to prevent drawing of text
                // Do nothing in this method
            }
        });
    }
}
