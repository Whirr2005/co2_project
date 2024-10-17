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
    private final int TILE_ROWS = 30;  // Number of vertical tiles (latitude)
    private final int TILE_COLS = 20;  // Number of horizontal tiles (longitude)
    private final double TILE_LAT_SPAN = 180.0 / TILE_ROWS; // Each tile covers 6 degrees of latitude
    private final double TILE_LON_SPAN = 360.0 / TILE_COLS; // Each tile covers 18 degrees of longitude

    private final double MIN_LAT_ZOOM = 50.0;  // Minimum latitude for UK zoom
    private final double MAX_LAT_ZOOM = 60.0;  // Maximum latitude for UK zoom
    private final double MIN_LON_ZOOM = -10.0; // Minimum longitude for UK zoom
    private final double MAX_LON_ZOOM = 5.0;   // Maximum longitude for UK zoom

    private final int TILE_SIZE = 256; // Assuming each tile is 256x256 pixels
    private ArrayList<Point> positions; // To store clicked positions

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
    };

    public MapPanel() {
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

    // Method to load the tile image by its row and column indices
    private BufferedImage loadTileImage(int row, int col) {
        try {
            String tileName = String.format("C:\\path\\to\\tiles\\image%dx%d.jpg", row + 1, col + 1); // Adjust path as necessary
            return ImageIO.read(new File(tileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to convert latitude/longitude to screen coordinates for zoomed UK region
    private double[] convertLatLonToXY(double lat, double lon) {
        int mapWidth = getWidth();   // Width of the displayed map
        int mapHeight = getHeight(); // Height of the displayed map

        // Map latitude and longitude to pixel coordinates based on the zoomed-in region
        double x = (lon - MIN_LON_ZOOM) / (MAX_LON_ZOOM - MIN_LON_ZOOM) * mapWidth; // Convert longitude to pixel x
        double y = (MAX_LAT_ZOOM - lat) / (MAX_LAT_ZOOM - MIN_LAT_ZOOM) * mapHeight; // Convert latitude to pixel y

        return new double[]{x, y}; // Return the point in screen coordinates
    }

    // Method to convert screen coordinates to latitude/longitude for zoomed UK region
    private double[] convertXYToLatLon(int x, int y) {
        int mapWidth = getWidth();   // Width of the displayed map
        int mapHeight = getHeight(); // Height of the displayed map

        // Map pixel coordinates back to latitude and longitude based on the zoomed-in region
        double lon = (x / (double) mapWidth) * (MAX_LON_ZOOM - MIN_LON_ZOOM) + MIN_LON_ZOOM; // Convert pixel x to longitude
        double lat = MAX_LAT_ZOOM - (y / (double) mapHeight) * (MAX_LAT_ZOOM - MIN_LAT_ZOOM); // Convert pixel y to latitude

        return new double[]{lat, lon}; // Return the geographical coordinates
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Calculate the tile range for the UK zoomed region
        int startRow = (int) ((90 - MAX_LAT_ZOOM) / TILE_LAT_SPAN);
        int endRow = (int) ((90 - MIN_LAT_ZOOM) / TILE_LAT_SPAN);
        int startCol = (int) ((MIN_LON_ZOOM + 180) / TILE_LON_SPAN);
        int endCol = (int) ((MAX_LON_ZOOM + 180) / TILE_LON_SPAN);

        // Loop over the tiles that intersect the UK zoom area
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                BufferedImage tile = loadTileImage(row, col);
                if (tile != null) {
                    // Calculate the x, y position to draw the tile on the screen
                    int x = (int) ((col - startCol) * TILE_SIZE);
                    int y = (int) ((row - startRow) * TILE_SIZE);
                    g.drawImage(tile, x, y, TILE_SIZE, TILE_SIZE, this);
                }
            }
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
        JFrame newframe = new JFrame("Zoomed-in UK Map Plotter");
        newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newframe.setSize(800, 600); // Adjust frame size as needed
        newframe.setLocationRelativeTo(null); // Center the frame on the screen
        newframe.setResizable(false);

        // Create an instance of the MapPanel and add it to the frame
        MapPanel mapPanel = new MapPanel();
        newframe.add(mapPanel); // Add the MapPanel to the JFrame

        newframe.setVisible(true); // Make the frame visible
    }
}
