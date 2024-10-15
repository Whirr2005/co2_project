package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;

class MapPanel extends JPanel {
    private BufferedImage mapImage; // To hold the map image
    private ArrayList<Point> positions; // To store points that will be plotted

    public MapPanel() {
        // Load the map image
        try {
            mapImage = ImageIO.read(new File("src/map.png")); // Update this path as necessary
        } catch (Exception e) {
            e.printStackTrace();
        }

        positions = new ArrayList<>(); // Initialize the list of positions

        // Predefined coordinates to plot (example coordinates)
        Point[] predefinedPoints = {
                new Point(150, 100), // Point 1
                new Point(300, 200), // Point 2
                new Point(450, 150), // Point 3
        };

        // Add predefined points to the positions list
        for (Point point : predefinedPoints) {
            positions.add(point);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the map image
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Plot positions
        g.setColor(Color.RED); // Color for points
        for (Point position : positions) {
            g.fillOval(position.x - 5, position.y - 5, 10, 10); // Draw a circle for each point
        }
    }

    // Static method to create and show the frame with the map
    static void create() {
        JFrame newframe = new JFrame("Map Plotter");
        newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newframe.setSize(600, 400); // Frame size
        newframe.setLocationRelativeTo(null); // Center the frame on the screen
        newframe.setResizable(false);

        // Create an instance of the MapPanel and add it to the frame
        MapPanel mapPanel = new MapPanel();
        newframe.add(mapPanel); // Add the MapPanel to the JFrame

        newframe.setVisible(true); // Make the frame visible
    }
}
