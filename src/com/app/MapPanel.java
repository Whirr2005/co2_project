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

    // Approximate map boundaries based on the new center
    private final double CENTER_LAT = 54.02505809693575; // Center latitude
    private final double CENTER_LON = -4.567324686395687; // Center longitude
    private final double LAT_SPAN = 10.8967; // Latitude range from top to bottom (approximate for the UK)
    private final double LON_SPAN = 10.4154; // Longitude range from left to right (approximate for the UK)

    // Reference geographical point and its screen position (to refine scale)
    private final double REF_LAT = 53.483959; // Latitude of the reference point (e.g., Manchester)
    private final double REF_LON = -2.244644; // Longitude of the reference point
    private final int REF_SCREEN_X = 345;     // Screen X position of the reference point
    private final int REF_SCREEN_Y = 124;     // Screen Y position of the reference point

    // Predefined latitude/longitude coordinates (example points in the UK)
    private final double[][] predefinedLatLon = {
            {51.41156386364611, -2.5266483505178243},    // London
            {53.49259345417181, -2.258080035641775},     // Manchester
            {55.935256239963024, -3.185745200085697}     // Edinburgh
    };

    public MapPanel() {
        // Load the map image
        try {
            mapImage = ImageIO.read(new File("C:\\Users\\mrfoo\\IdeaProjects\\co2_project\\src\\uk-countries.png")); // Update this path as necessary
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

                // Convert geographical coordinates back to screen coordinates
                double[] geoPoint = convertLatLonToXY(latLon[0], latLon[1]);

                // Print the required outputs
                System.out.println("Screen coordinates: (" + x + ", " + y + ")");
                System.out.println("Geographical coordinates: (" + latLon[0] + ", " + latLon[1] + ")");
                System.out.println("Geo coordinates converted into Screen coordinates: (" + geoPoint[0] + ", " + geoPoint[1] + ")");

                // Add the clicked point to the positions list as a geographical point
                positions.add(new Point(x, y)); // Store the screen position

                // Repaint the panel to show the new point
                repaint();
            }
        });
    }

    // Method to convert latitude/longitude to screen coordinates based on new map center and reference point
    private double[] convertLatLonToXY(double lat, double lon) {
        int mapWidth = getWidth();   // Width of the displayed map
        int mapHeight = getHeight(); // Height of the displayed map

        // First, calculate the relative screen position based on the map's width and height
        int x = (int) (((lon - CENTER_LON) / LON_SPAN) * mapWidth + (mapWidth / 2));
        int y = (int) ((-1 * (lat - CENTER_LAT) / LAT_SPAN) * mapHeight + (mapHeight / 2));

        // Calculate the offset based on the reference point
        double refLatOffset = (REF_LAT - CENTER_LAT) / LAT_SPAN * mapHeight;
        double refLonOffset = (REF_LON - CENTER_LON) / LON_SPAN * mapWidth;

        // Adjust x and y based on how far the reference point is from the screen center
        x += (REF_SCREEN_X - (int) refLonOffset);
        y += (REF_SCREEN_Y - (int) refLatOffset);

        return new double[]{x, y}; // Return the point in screen coordinates
    }

    // Method to convert screen coordinates to latitude/longitude
    private double[] convertXYToLatLon(int x, int y) {
        int mapWidth = getWidth();   // Width of the displayed map
        int mapHeight = getHeight(); // Height of the displayed map

        // Calculate longitude and latitude based on screen coordinates
        double lon = ((x - (mapWidth / 2)) / (double) mapWidth) * LON_SPAN + CENTER_LON;
        double lat = ((mapHeight / 2 - y) / (double) mapHeight) * LAT_SPAN + CENTER_LAT;

        return new double[]{lat, lon}; // Return the geographical coordinates
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the map image
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Plot predefined positions (convert them to screen coordinates here)
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
        JFrame newframe = new JFrame("Map Plotter");
        newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newframe.setSize(800, 800); // Frame size: 800x800
        newframe.setLocationRelativeTo(null); // Center the frame on the screen
        newframe.setResizable(false);

        // Create an instance of the MapPanel and add it to the frame
        MapPanel mapPanel = new MapPanel();
        newframe.add(mapPanel); // Add the MapPanel to the JFrame

        newframe.setVisible(true); // Make the frame visible
    }
}
