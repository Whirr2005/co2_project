package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.app.config.DatabaseConnector;

class MapPanel extends JPanel {
    private BufferedImage ukMapImage;  //uk map image
    private final ArrayList<PointData> positions;  //list for coords co2 reading data


    public MapPanel(List<String[]> data) {
        positions = new ArrayList<>(); //create list

        // save results
    for (int i = 0; i < data.size() - 1; i++) {
            String[] row = data.get(i);

            double[] coordinates = postcodeCoords.getCoords(row[2]); //converting postcode from database into coords
            assert coordinates != null;

            //convert lat lon coords into xy screen coords
            double[] xyCoords = convertLatLonToXY(coordinates[0], coordinates[1]);
            //set new point on map at converted coords
            Point point = new Point((int) xyCoords[0], (int) xyCoords[1]);
            //store point and data
            positions.add(new PointData(point, row[2], row[3], row[4], row[1]));
        }

        //load uk map
        try {
            ukMapImage = ImageIO.read(new File("src/main/resources/images/uk-map.png")); // Adjust path
        } catch (Exception e) {
            e.printStackTrace();
        }

        // detect clicks for viewing data info
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickedPoint = e.getPoint();
                for (PointData positionData : positions) {
                    if (isPointClicked(clickedPoint, positionData.point())) {
                        // show data in pop up
                        String message = "\nPostcode: " + positionData.postcode() + "\n"
                                + "User ID: "+positionData.userID()+"\n"
                                +"Data: " + positionData.data() + "PPM (Parts Per Million)\n"
                                //formats time stamp into a eay to read format
                                + "Timestamp: " + positionData.timestamp().substring(5,7)+"/"+positionData.timestamp().substring(8,10)+"/"+positionData.timestamp().substring(0,4);
                        StyledFrames.newPopup(message, "Point Data");
                        break;
                    }
                }
            }
        });
    }

    // function to check if click is on a point
    private boolean isPointClicked(Point click, Point point) {
        int dx = click.x - point.x;
        int dy = click.y - point.y;
        int POINT_RADIUS = 15; //size of click "hitbox" radius
        return Math.sqrt(dx * dx + dy * dy) <= POINT_RADIUS;
    }

    //convert lat/long to x y coordinates using world map variables
    private double[] convertLatLonToXY(double lat, double lon) {
        //full world dimensions for use in calculating lat and long
        int map_width = 15000;
        int map_height = 10500;
        //offsets for moving uk area of map into frame
        int map_offset_x = 6980; // Higher number moves UK left
        int map_offset_y = 1750; // Higher number moves UK up

        //conversions
        double x = (lon + 180.0) * (map_width / 360.0) - map_offset_x;
        double y = (90.0 - lat) * (map_height / 180.0) - map_offset_y;

        return new double[]{x, y};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //background colour
        setBackground(Color.decode("#24293e"));

        //put uk map in position based on sourced coordinets from google maps of furthest N,E,S,W points
        if (ukMapImage != null) {
            //uk bounds
            double UK_TOP_LAT = 59.586128178;
            double UK_LEFT_LON = -10.4415;
            double UK_BOTTOM_LAT = 49.957122;
            double UK_RIGHT_LON = 1.76297;
            //change geo coordinates to screen coords (x, y)
            double[] ukTopLeft = convertLatLonToXY(UK_TOP_LAT, UK_LEFT_LON);
            double[] ukBottomRight = convertLatLonToXY(UK_BOTTOM_LAT, UK_RIGHT_LON);

            //width and height of uk map on screen
            int ukScreenWidth = (int) (ukBottomRight[0] - ukTopLeft[0]);
            int ukScreenHeight = (int) (ukBottomRight[1] - ukTopLeft[1]);

            //draw uk image
            g.drawImage(ukMapImage, (int) ukTopLeft[0], (int) ukTopLeft[1], ukScreenWidth, ukScreenHeight, this);
        }

        // plot positions
        g.setColor(Color.decode("#f4a3a6")); //color of  points
        for (PointData positionData : positions) {
            Point position = positionData.point();
            g.fillOval(position.x - 5, position.y - 5, 15, 15); // Draw a circle for each point
        }
    }

    // method to call in app.java
    static void create(List<String[]> allData) {
        JFrame mapWindow = new JFrame("Co2 Data Visualiser Map Diagram");
        mapWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //close (dispose only does current) window
        mapWindow.setSize(700, 800);
        mapWindow.setLocationRelativeTo(null);
        mapWindow.setResizable(false);

        //create panel for map
        MapPanel mapPanel = new MapPanel(allData);

        //panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.decode("#24293e"));

        //buttons
        JButton backButton = new roundedButton("");
        JButton refreshButton = new roundedButton("");
        JButton dlButton = new roundedButton("");

        setButtonAttributes(backButton);
        setButtonAttributes(refreshButton);
        refreshButton.setPreferredSize(new Dimension(refreshButton.getFontMetrics(refreshButton.getFont()).stringWidth(refreshButton.getText())+180, 90));
        setButtonAttributes(dlButton);

        //load icon images
        //in main/recorces to make paths work on teammates laptops
        ImageIcon backImage = new ImageIcon("src/main/resources/images/backButton.png");
        ImageIcon refreshImage = new ImageIcon("src/main/resources/images/refreshButton.png");
        ImageIcon dlImage = new ImageIcon("src/main/resources/images/downloadButton.png");

        //image sizing
        Image tmpImage = backImage.getImage();
        backImage = new ImageIcon(tmpImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH));
        tmpImage = refreshImage.getImage();
        refreshImage = new ImageIcon(tmpImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH));
        tmpImage = dlImage.getImage();
        dlImage = new ImageIcon(tmpImage.getScaledInstance(70, 70, java.awt.Image.SCALE_SMOOTH));

        //place icon on buttons
        backButton.setIcon(backImage);
        refreshButton.setIcon(refreshImage);
        dlButton.setIcon(dlImage);

        //back button function
        backButton.addActionListener(_ -> mapWindow.dispose()); //closes window

        //refresh button function
        refreshButton.addActionListener(_ -> {
            create(allData);
            mapWindow.dispose();
        });

        //call download button function
        dlButton.addActionListener(_ -> {
            // call method to save table as a csv
            try {
                saveToCSV(allData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //add buttons to panel
        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(dlButton);

        //add to frame
        mapWindow.setLayout(new BorderLayout());
        mapWindow.add(mapPanel, BorderLayout.CENTER);
        mapWindow.add(buttonPanel, BorderLayout.SOUTH);
        mapWindow.setVisible(true);
    }

    //button styling function
    public static void setButtonAttributes(JButton button) {
        //set button width 100 larger then content
        button.setPreferredSize(new Dimension(button.getFontMetrics(button.getFont()).stringWidth(button.getText())+100, 75));
        button.setFont(FontLoader.getSatoshiFont(28f));
        button.setForeground(Color.decode("#24293e"));
        button.setBackground(Color.decode("#8ebbff"));
    }

    public static void saveToCSV(List<String[]> allData) throws IOException {
        // file chooser destination window set file lcoation
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");

        // default file name
        fileChooser.setSelectedFile(new File("table_data.csv"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();
            try (FileWriter csvWriter = new FileWriter(csvFile)) {

                // Define custom headers
                String[] headers = {"id", "user_id", "postcode", "data", "timeStamp"};

                // Write headers to CSV
                for (int i = 0; i < headers.length; i++) {
                    csvWriter.append(headers[i]);
                    if (i < headers.length - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");

                // Write data rows (excluding the first row, which was used as headers)
                for (int i = 1; i < allData.size(); i++) {
                    String[] row = allData.get(i);
                    for (int j = 0; j < row.length; j++) {
                        csvWriter.append(row[j]);
                        if (j < row.length - 1) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }

                // Show success message
                JOptionPane.showMessageDialog(null, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}

// class for point coords and its info
record PointData(Point point, String postcode, String data, String timestamp, String userID) {
}

