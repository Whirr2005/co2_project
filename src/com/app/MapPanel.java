package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

import com.app.FontLoader;
import com.app.postcodeCoords;
import com.app.config.DatabaseConnector;

class MapPanel extends JPanel {
    private BufferedImage ukMapImage;  // UK map image
    private ArrayList<PointData> positions;  // List to store points and associated row data

    // Full world dimensions for use in calculating lat and long
    private final int map_width = 15000;
    private final int map_height = 10500;
    // Offsets for moving the UK area into frame
    private final int map_offset_x = 6980; // Higher number moves UK left
    private final int map_offset_y = 1750; // Higher number moves UK up

    // UK bounds (approximate)
    private final double UK_TOP_LAT = 59.586128178;
    private final double UK_BOTTOM_LAT = 49.957122;
    private final double UK_LEFT_LON = -10.4415;
    private final double UK_RIGHT_LON = 1.76297;

    // Proximity radius to detect clicks near points
    private final int POINT_RADIUS = 15;

    public MapPanel() {
        positions = new ArrayList<>(); // Init list of points and associated data

        // Reading data from the table "data_table"
        List<String[]> data = DatabaseConnector.readData("data_table");
        // Save results
        for (String[] row : data) {
            double[] coordinates = postcodeCoords.getCoords(row[2]);

            double[] xyCoords = convertLatLonToXY(coordinates[0], coordinates[1]);
            Point point = new Point((int) xyCoords[0], (int) xyCoords[1]);

            // Store the point and its associated data
            positions.add(new PointData(point, row[2], row[3], row[4]));
        }

        // Load the UK map
        try {
            ukMapImage = ImageIO.read(new File("src/main/resources/images/uk-map.png")); // Adjust path
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add a mouse listener for detecting clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickedPoint = e.getPoint();
                for (PointData positionData : positions) {
                    if (isPointClicked(clickedPoint, positionData.getPoint())) {
                        // Show the associated data in a message dialog
                        String message = "Postcode: " + positionData.getPostcode() + "\n"
                                + "Data: " + positionData.getData() + "\n"
                                + "Timestamp: " + positionData.getTimestamp();
                        JOptionPane.showMessageDialog(null, message, "Point Data", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            }
        });
    }

    // Method to check if a click is near a plotted point (using proximity radius)
    private boolean isPointClicked(Point click, Point point) {
        int dx = click.x - point.x;
        int dy = click.y - point.y;
        return Math.sqrt(dx * dx + dy * dy) <= POINT_RADIUS;
    }

    // Convert lat/long to x y coordinates using world map variables
    private double[] convertLatLonToXY(double lat, double lon) {
        double x = (lon + 180.0) * (map_width / 360.0) - map_offset_x;
        double y = (90.0 - lat) * (map_height / 180.0) - map_offset_y;

        return new double[]{x, y};
    }

    // Screen coordinates to lat/long by doing the reverse of previous
    private double[] convertXYToLatLon(int x, int y) {
        double lon = ((x + map_offset_x) / (double) map_width) * 360.0 - 180.0;
        double lat = 90.0 - ((y + map_offset_y) / (double) map_height) * 180.0;

        return new double[]{lat, lon};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background
        setBackground(Color.decode("#24293e"));

        // Place UK map in the correct position based on the furthest points of UK
        if (ukMapImage != null) {
            // Change geo coordinates to screen coords (x, y)
            double[] ukTopLeft = convertLatLonToXY(UK_TOP_LAT, UK_LEFT_LON);
            double[] ukBottomRight = convertLatLonToXY(UK_BOTTOM_LAT, UK_RIGHT_LON);

            // Width and height of the UK map on the screen
            int ukScreenWidth = (int) (ukBottomRight[0] - ukTopLeft[0]);
            int ukScreenHeight = (int) (ukBottomRight[1] - ukTopLeft[1]);

            // Draw UK map image
            g.drawImage(ukMapImage, (int) ukTopLeft[0], (int) ukTopLeft[1], ukScreenWidth, ukScreenHeight, this);
        }

        // Plot clicked positions
        g.setColor(Color.decode("#f4a3a6")); // Color for clicked points
        for (PointData positionData : positions) {
            Point position = positionData.getPoint();
            g.fillOval(position.x - 5, position.y - 5, 15, 15); // Draw a circle for each point
        }
    }

    // Create method called from app.java
    static void create() {
        JFrame mapWindow = new JFrame("UK Map Plotter");
        mapWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only removes map window
        mapWindow.setSize(700, 800); // Adjust frame size as needed
        mapWindow.setLocationRelativeTo(null); // Center the frame on the screen
        mapWindow.setResizable(false);

        // Create the mapPanel
        MapPanel mapPanel = new MapPanel();

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.decode("#24293e"));

        // Create buttons
        JButton backButton = new roundedButton("");
        JButton refreshButton = new roundedButton("");
        JButton dlButton = new roundedButton("");

        setButtonAttributes(backButton);
        setButtonAttributes(refreshButton);
        refreshButton.setPreferredSize(new Dimension(refreshButton.getFontMetrics(refreshButton.getFont()).stringWidth(refreshButton.getText())+130, 80));
        setButtonAttributes(dlButton);

        // Load images as ImageIcons
        ImageIcon backImage = new ImageIcon("src/main/resources/images/backButton.png"); // Replace with your image path
        ImageIcon refreshImage = new ImageIcon("src/main/resources/images/refreshButton.png"); // Replace with your image path
        ImageIcon dlImage = new ImageIcon("src/main/resources/images/downloadButton.png"); // Replace with your image path

        Image tmpImage = backImage.getImage();
        backImage = new ImageIcon(tmpImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH));

        tmpImage = refreshImage.getImage();
        refreshImage = new ImageIcon(tmpImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH));

        tmpImage = dlImage.getImage();
        dlImage = new ImageIcon(tmpImage.getScaledInstance(70, 70, java.awt.Image.SCALE_SMOOTH));

        // Set the icon on the buttons
        backButton.setIcon(backImage);
        refreshButton.setIcon(refreshImage);
        dlButton.setIcon(dlImage);

        // Add back button functionality
        backButton.addActionListener(e -> mapWindow.dispose());

        // Add refresh button functionality
        refreshButton.addActionListener(e -> {
            create();
            mapWindow.dispose();
        });

        // Add dlButton functionality for downloading CSV
        dlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the method to save the table as a CSV file
                DatabaseConnector.saveTableToCSV("data_table");
            }
        });

        // Add buttons to the panel
        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(dlButton);

        // Add components to the frame
        mapWindow.setLayout(new BorderLayout());
        mapWindow.add(mapPanel, BorderLayout.CENTER);  // Map panel in the center
        mapWindow.add(buttonPanel, BorderLayout.SOUTH); // Buttons at the bottom

        mapWindow.setVisible(true);
    }

    // Helper method to set the attributes
    public static void setButtonAttributes(JButton button) {
        // Set the width of the button to be 100 larger than the width of the text
        button.setPreferredSize(new Dimension(button.getFontMetrics(button.getFont()).stringWidth(button.getText())+130, 60));
        button.setFont(FontLoader.getSatoshiFont(28f));
        button.setForeground(Color.decode("#24293e"));
        button.setBackground(Color.decode("#8ebbff"));
    }
}

// Helper class to store the point and its associated data
class PointData {
    private Point point;
    private String postcode;
    private String data;
    private String timestamp;

    public PointData(Point point, String postcode, String data, String timestamp) {
        this.point = point;
        this.postcode = postcode;
        this.data = data;
        this.timestamp = timestamp;
    }

    public Point getPoint() {
        return point;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

