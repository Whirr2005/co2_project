package com.app;

import java.time.LocalDateTime;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import com.app.config.DatabaseConnector;
import java.awt.geom.RoundRectangle2D;

    public class app {

        // Main method
        public static void main(String[] args) {
            // Call the login method to display the login screen
            showLoginScreen();
        }

        // Method to display the login screen
        private static void showLoginScreen() {
            // Create a login frame
            JFrame loginFrame = new JFrame("Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

            // Create user ID and port number input fields
            JLabel userIdLabel = new JLabel("User ID:");
            RoundedTextField userIdField = new RoundedTextField();
            JLabel portLabel = new JLabel("Port Number:");
            RoundedTextField portField = new RoundedTextField();

            // Create login button
            JButton loginButton = new roundedButton("Login");

            // Add components to the login frame
            loginFrame.add(userIdLabel);
            loginFrame.add(userIdField);
            loginFrame.add(portLabel);
            loginFrame.add(portField);
            loginFrame.add(new JLabel()); // Empty cell for layout spacing
            loginFrame.add(loginButton);

            // Set frame properties
            loginFrame.setSize(400, 200);
            loginFrame.setLocationRelativeTo(null);  // Center the frame
            loginFrame.setVisible(true);

            // Add login button action listener
            loginButton.addActionListener(e -> {
                try {
                    // Get user input
                    int userId = Integer.parseInt(userIdField.getText());
                    int port = Integer.parseInt(portField.getText());

                    // Close the login window
                    loginFrame.dispose();

                    // Show the main input window
                    showMainWindow(userId, port);

                } catch (NumberFormatException ex) {
                    // Show error message if the input is invalid
                    JOptionPane.showMessageDialog(loginFrame, "Please enter valid values for User ID and Port Number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        // Method to display the main window (after login)
        private static void showMainWindow(int userId, int port) {
            JFrame frame;
            final RoundedTextField postcodeField = new RoundedTextField();
            final RoundedTextField dataField = new RoundedTextField();

            // Creates JFrame
            frame = new JFrame("CO2 Program Input Data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Style fields and labels
            JLabel userIdLabel = new JLabel("User ID: " + userId);  // Display the user ID from login
            userIdLabel.setFont(FontLoader.getSatoshiFont(28f));
            userIdLabel.setForeground(Color.decode("#f4f5fc"));

            JLabel portLabel = new JLabel("Port Number: " +port);  // Display the user ID from login
            portLabel.setFont(FontLoader.getSatoshiFont(28f));
            portLabel.setForeground(Color.decode("#f4f5fc"));


            // Postcode
            JLabel postcodeLabel = new JLabel("Enter Postcode:");
            postcodeField.setBackground(Color.decode("#f4f5fc"));
            postcodeField.setPreferredSize(new Dimension(200, 25)); // Sets width
            postcodeLabel.setFont(FontLoader.getSatoshiFont(18f));
            postcodeLabel.setForeground(Color.decode("#f4f5fc"));

            // CO2 data readings
            JLabel dataLabel = new JLabel("CO2 Data (PPM):");
            dataLabel.setFont(FontLoader.getSatoshiFont(18f));
            dataLabel.setForeground(Color.decode("#f4f5fc"));
            dataField.setBackground(Color.decode("#f4f5fc"));
            dataField.setPreferredSize(new Dimension(200, 25)); // Sets width

            // Creates panel
            JPanel Inputpanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Grid gap
            Inputpanel.add(userIdLabel);  // displayed user id
            Inputpanel.add(portLabel); // port label
            Inputpanel.add(postcodeLabel);
            Inputpanel.add(postcodeField);
            Inputpanel.add(dataLabel);
            Inputpanel.add(dataField);
            Inputpanel.setBackground(Color.decode("#24293e"));
            Inputpanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            frame.add(Inputpanel, BorderLayout.CENTER);

            // Second panel for the buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.decode("#24293e"));
            JButton submitButton = new roundedButton("Submit");
            JButton mapButton = new roundedButton("View Map");
            mapButton.setBackground(new Color(10, 25, 86));
            mapButton.setForeground(new Color(255, 255, 255));
            mapButton.setPreferredSize(new Dimension(200, 40));
            submitButton.setBackground(new Color(10, 25, 86));
            submitButton.setForeground(new Color(255, 255, 255));
            submitButton.setPreferredSize(new Dimension(200, 40));
            submitButton.setHorizontalAlignment(SwingConstants.CENTER);
            buttonPanel.add(submitButton);
            buttonPanel.add(mapButton);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            frame.getContentPane().add(Box.createVerticalStrut(20), BorderLayout.PAGE_END);
            frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

            // Set frame size and rules
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);

            // Submit button action listener
            submitButton.addActionListener(_ -> {
                try {
                    // Collect user input
                    String postcode = postcodeField.getText();
                    String data = dataField.getText();
                    LocalDateTime currentTime = LocalDateTime.now();
                    String timestamp = currentTime.toString();

                    // Send data to the server
                    try (Socket socket = new Socket("localhost", port); // Use port from login
                         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                         ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                        // Send insert operation to server
                        out.writeObject("insert");
                        out.writeInt(userId);  // Use userId from login
                        out.writeObject(postcode);
                        out.writeObject(data);
                        out.writeObject(timestamp);
                        out.flush();

                        // Get response from server
                        boolean success = in.readBoolean();

                        // Show feedback to user
                        if (success) {
                            StyledFrames.newPopup("Data inserted successfully!", "Success");
                        } else {
                            StyledFrames.newPopup("Error inserting data. Please try again.", "Error");
                        }
                    }

                    // Clear input fields
                    postcodeField.setText("");
                    dataField.setText("");

                } catch (Exception ex) {
                    StyledFrames.newPopup("An unexpected error occurred: " + ex.getMessage(), "Error");
                }
            });

            // Map button action listener
            mapButton.addActionListener(_ -> {
                // Calls create() function from MapPanel class
                MapPanel.create();
            });
        }
    }

//data object for preparing data for database
class DataHandler {
    static int USERID;
    static String POSTCODE;
    static String DATA;
}
//class for rounded text fields
class RoundedTextField extends JTextField {
    private static final int RADIUS = 15;  //curve radius

    public RoundedTextField() {
        super();
        setOpaque(false);  // transparent background
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //padding
        setFont(FontLoader.getSatoshiFont(18f)); // uses fontloader class
        setForeground(Color.DARK_GRAY); //font color
    }

    @Override
    protected void paintComponent(Graphics g) {
        //creates rounded rectangle shape
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255)); // background color
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RADIUS, RADIUS)); // Draw rounded rectangle

        super.paintComponent(g);
    }

}
//similar class converted for use on buttons
class roundedButton extends JButton {
    private static final int RADIUS = 15;  //curve radius

    public roundedButton(String text) {
        super(text);
        setOpaque(false);  //transparent background
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //padding
        setFont(FontLoader.getSatoshiFont(22f)); // uses fontloader class
        setForeground(Color.DARK_GRAY); //font color
        setContentAreaFilled(false); //remove default button background
        setFocusPainted(false); //remove hover border
    }

    @Override
    protected void paintComponent(Graphics g) {
        // rounded rectangle shape
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color (change it to your desired button background)
        g2.setColor(Color.decode("#97b9f9")); // background color
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RADIUS, RADIUS)); //rounded rectangle

        super.paintComponent(g);  //paint button text
    }
}