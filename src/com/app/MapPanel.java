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
    private BufferedImage ukMapImage;    // uk map image
    private ArrayList<Point> positions;  // array for points to be plotted

    // full world dimensions for use calculating lat and long
    private final int map_width = 15000;
    private final int map_height = 10500;
    //offsets for moving the uk area into frame
    private final int map_offset_x = 6980; // higher number moves uk left
    private final int map_offset_y = 1750; // higher number moves uk up

    // UK bounds (approximate)
    private final double UK_TOP_LAT = 59.586128178;
    private final double UK_BOTTOM_LAT = 49.957122;
    private final double UK_LEFT_LON = -10.4415;
    private final double UK_RIGHT_LON = 1.76297;

    // Predefined positions (UK outline and more)
    private final double[][] predefinedLatLon = {
            //for testing plotting points
            {51.5074, -0.1278},  // London
            {52.4862, -1.8904},  // Birmingham
            {53.4808, -2.2426},  // Manchester
            {53.4084, -2.9916},  // Liverpool
            {55.9533, -3.1883},  // Edinburgh
            {55.8642, -4.2518},  // Glasgow
            {54.5973, -5.9301},  // Belfast
            {51.4816, -3.1791},   // Cardiff
            {50.081587108944056, -5.640682437188373} // Cornwall
    };

    public MapPanel() {
        // load the uk map
        try {
            ukMapImage = ImageIO.read(new File("C:\\Users\\mrfoo\\IdeaProjects\\co2_project\\src\\uk-map.png")); // Adjust path
        } catch (Exception e) {
            e.printStackTrace();
        }

        positions = new ArrayList<>(); // init list of positions

        // mouse listener for clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                double[] latLon = convertXYToLatLon(x, y);

                System.out.println("Screen coordinates: (" + x + ", " + y + ")");
                System.out.println("Geographical coordinates: (" + latLon[0] + ", " + latLon[1] + ")");

                positions.add(new Point(x, y)); // Store the screen position
                repaint();
            }
        });
    }

    // convert lat/long to x y coordinates using world map variables
    private double[] convertLatLonToXY(double lat, double lon) {
        double x = (lon + 180.0) * (map_width / 360.0) - map_offset_x;
        double y = (90.0 - lat) * (map_height / 180.0) - map_offset_y;

        return new double[]{x, y};
    }

    //  screen coordinates to lat/long by doing the reverse of previous
    private double[] convertXYToLatLon(int x, int y) {
        double lon = ((x + map_offset_x) / (double) map_width) * 360.0 - 180.0;
        double lat = 90.0 - ((y + map_offset_y) / (double) map_height) * 180.0;

        return new double[]{lat, lon};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // set background
        setBackground(Color.decode("#24293e"));

        // place uk map in correct position based on the furthest points of uk
        if (ukMapImage != null) {
            // chnage geo coordinates to screen coords (x,y)
            double[] ukTopLeft = convertLatLonToXY(UK_TOP_LAT, UK_LEFT_LON);
            double[] ukBottomRight = convertLatLonToXY(UK_BOTTOM_LAT, UK_RIGHT_LON);

            // width and height of the uk map on the screen
            int ukScreenWidth = (int) (ukBottomRight[0] - ukTopLeft[0]);
            int ukScreenHeight = (int) (ukBottomRight[1] - ukTopLeft[1]);

            // draw uk-map image
            g.drawImage(ukMapImage, (int) ukTopLeft[0], (int) ukTopLeft[1], ukScreenWidth, ukScreenHeight, this);
        }

        // plot predefined positions
        g.setColor(Color.decode("#f4a3a6")); // Color for predefined points
        for (double[] latLon : predefinedLatLon) {
            double[] screenCoords = convertLatLonToXY(latLon[0], latLon[1]);
            g.fillOval((int) screenCoords[0] - 5, (int) screenCoords[1] - 5, 15, 15); // Draw a circle for each point
        }

        // plot clicked positions
        g.setColor(Color.decode("#8ef2e4")); // Color for clicked points
        for (Point position : positions) {
            g.fillOval(position.x - 5, position.y - 5, 10, 10); // Draw a circle for each point
        }
    }

    // create method called from app.java
    static void create() {
        JFrame newframe = new JFrame("UK Map Plotter");
        newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newframe.setSize(700, 800); // Adjust frame size as needed
        newframe.setLocationRelativeTo(null); // Center the frame on the screen
        newframe.setResizable(false);

        // create the mapPanel
        MapPanel mapPanel = new MapPanel();
        newframe.add(mapPanel);

        newframe.setVisible(true);
    }
}
