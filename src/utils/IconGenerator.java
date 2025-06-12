package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Utility class to generate a custom icon for the Expense Tracker application.
 */
public class IconGenerator {
    
    /**
     * Creates a simple expense icon and saves it as a PNG file.
     * 
     * @param path The path where the icon should be saved
     * @param size The size of the icon (width and height)
     * @return true if the icon was successfully created, false otherwise
     */
    public static boolean createExpenseIcon(String path, int size) {
        try {
            // Create a buffered image with transparency
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // Set anti-aliasing for better quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Fill background with a gradient
            Color startColor = new Color(100, 150, 220);
            Color endColor = new Color(76, 175, 80);
            g2d.setPaint(new java.awt.GradientPaint(0, 0, startColor, size, size, endColor));
            g2d.fillRoundRect(0, 0, size - 1, size - 1, size / 4, size / 4);
            
            // Draw the border
            g2d.setColor(new Color(60, 60, 60));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(0, 0, size - 1, size - 1, size / 4, size / 4);
            
            // Draw a dollar sign or "ET" for Expense Tracker
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
            g2d.drawString("ET", size / 5, size * 2 / 3);
            
            // Draw a coin or circle for money representation
            g2d.setColor(new Color(255, 215, 0, 220)); // Gold with transparency
            g2d.fillOval(size / 2, size / 5, size / 3, size / 3);
            
            // Outline for the coin
            g2d.setColor(new Color(210, 180, 20));
            g2d.drawOval(size / 2, size / 5, size / 3, size / 3);
            
            // Clean up
            g2d.dispose();
            
            // Save the icon as a PNG file
            File iconFile = new File(path);
            // Create directories if they don't exist
            File parentDir = iconFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("Creating directory: " + parentDir.getAbsolutePath() + " - Success: " + dirCreated);
            }
            
            boolean saved = ImageIO.write(image, "png", iconFile);
            System.out.println("Icon successfully created at: " + iconFile.getAbsolutePath() + " - Save success: " + saved);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating icon: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Main method to generate the icon.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create icons in different sizes
        createExpenseIcon("resources/expense_icon.png", 64);
    }
} 