package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;

class MapPanel extends JPanel {
    private BufferedImage mapImage; // To hold the map image
    private ArrayList<Point> positions; // To store points that will be plotted

    // World Map bounds
    private final double MIN_LAT = -90.0; // Minimum latitude
    private final double MAX_LAT = 90.0;  // Maximum latitude
    private final double MIN_LON = -180.0; // Minimum longitude
    private final double MAX_LON = 180.0;  // Maximum longitude
    private final int map_width = 1600;  // Maximum longitude
    private final int map_height = 800;  // Maximum longitude
    private final int map_offset_x = 200;  // Maximum longitude

    // Predefined positions (UK outline)
    private final double[][] predefinedLatLon = {
            {51.5074, -0.1278},  // London
            {52.4862, -1.8904},  // Birmingham
            {53.4808, -2.2426},  // Manchester
            {53.4084, -2.9916},  // Liverpool
            {55.9533, -3.1883},  // Edinburgh
            {55.8642, -4.2518},  // Glasgow
            {54.5973, -5.9301},  // Belfast
            {51.4816, -3.1791},   // Cardiff
            {-1.289798175528336, 32.93535678638415}, // eye of aftrica
            {41.556458030741055, -97.53642771952767}//USA BABYYYY
    };

    public MapPanel() {
        // Load the map image
        try {
            mapImage = ImageIO.read(new File("C:\\Users\\mrfoo\\IdeaProjects\\co2_project\\src\\world-map.jpg")); // Update this path as necessary
        } catch (Exception e) {
            e.printStackTrace();
        }

        positions = new ArrayList<>(); // Initialize the list of positions

        // Add mouse listener to handle clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Get the screen coordinates of the click
                int x = e.getX();
                int y = e.getY();

                // Convert the clicked screen coordinates to geographical coordinates
                double[] latLon = convertXYToLatLon(x, y);

                // Print the required outputs
                System.out.println("Screen coordinates: (" + x + ", " + y + ")");
                System.out.println("Geographical coordinates: (" + latLon[0] + ", " + latLon[1] + ")");

                // Add the clicked point to the positions list as a geographical point
                positions.add(new Point(x, y)); // Store the screen position

                // Repaint the panel to show the new point
                repaint();
            }
        });
    }

    // Method to convert latitude/longitude to screen coordinates based on the world map
    private double[] convertLatLonToXY(double lat, double lon) {
        int mapWidth = map_width;   // Width of the displayed map
        int mapHeight = map_height; // Height of the displayed map

        // Map latitude and longitude to pixel coordinates
        double x = (lon + 180.0 ) * (mapWidth / 360.0) -200; // Convert longitude to pixel x
        double y = (90.0 - lat) * (mapHeight / 180.0); // Convert latitude to pixel y

        return new double[]{x, y}; // Return the point in screen coordinates
    }

    // Method to convert screen coordinates to latitude/longitude
    private double[] convertXYToLatLon(int x, int y) {
        int mapWidth = map_width;   // Width of the displayed map
        int mapHeight = map_height; // Height of the displayed map

        // Map pixel coordinates back to latitude and longitude
        double lon = ((x+200) / (double) mapWidth ) * 360.0 - 180.0; // Convert pixel x to longitude
        double lat = 90.0 - (y / (double) mapHeight) * 180.0; // Convert pixel y to latitude

        return new double[]{lat, lon}; // Return the geographical coordinates
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the map image
        if (mapImage != null) {
            g.drawImage(mapImage, -200, 0, map_width, map_height, this);
        }

        // Plot predefined positions (UK outline)
        g.setColor(Color.BLUE); // Color for predefined points
        for (double[] latLon : predefinedLatLon) {
            double[] screenCoords = convertLatLonToXY(latLon[0], latLon[1]);
            g.fillOval((int) screenCoords[0] - 5, (int) screenCoords[1] - 5, 10, 10); // Draw a circle for each point
        }

        // Plot newly clicked positions
        g.setColor(Color.RED); // Color for clicked points
        for (Point position : positions) {
            g.fillOval(position.x - 5, position.y - 5, 10, 10); // Draw a circle for each point
        }
    }

    // Static method to create and show the frame with the map
    static void create() {
        JFrame newframe = new JFrame("World Map Plotter");
        newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newframe.setSize(400, 400); // Adjust frame size as needed
        newframe.setLocationRelativeTo(null); // Center the frame on the screen
        newframe.setResizable(false);

        // Create an instance of the MapPanel and add it to the frame
        MapPanel mapPanel = new MapPanel();
        newframe.add(mapPanel); // Add the MapPanel to the JFrame

        newframe.setVisible(true); // Make the frame visible
    }
}
