package com.app;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import javax.swing.*;
import java.awt.*;
import com.app.config.DatabaseConnector;
import java.awt.geom.RoundRectangle2D;

public class app {

    //variables for login details
    private static int userId;
    private static String ipAddress;
    private static int port;

    public static void main(String[] args) {
        //call method for login window
        loginWindow();
    }

    private static void loginWindow() {
        //login frame
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(new GridLayout(4, 2, 10, 10));

        //user id
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setPreferredSize(new Dimension(200, 25));
        userIdLabel.setFont(FontLoader.getSatoshiFont(25f));
        userIdLabel.setForeground(Color.decode("#040404"));
        RoundedTextField userIdField = new RoundedTextField();
        //ip
        JLabel ipLabel = new JLabel("IP Address:");
        ipLabel.setPreferredSize(new Dimension(200, 25));
        ipLabel.setFont(FontLoader.getSatoshiFont(25f));
        ipLabel.setForeground(Color.decode("#040404"));
        RoundedTextField ipField = new RoundedTextField();
        ipField.setText("192.168.101.171");
        //port
        JLabel portLabel = new JLabel("Port:");
        portLabel.setPreferredSize(new Dimension(200, 25));
        portLabel.setFont(FontLoader.getSatoshiFont(25f));
        portLabel.setForeground(Color.decode("#040404"));
        RoundedTextField portField = new RoundedTextField();
        portField.setText("1234");
        //login button
        JButton loginButton = new roundedButton("Login");

        //add to frame
        loginFrame.add(userIdLabel);
        loginFrame.add(userIdField);
        loginFrame.add(ipLabel);
        loginFrame.add(ipField);
        loginFrame.add(portLabel);
        loginFrame.add(portField);
        loginFrame.add(new JLabel()); //empty frame
        loginFrame.add(loginButton);

        //show frame in middle of screen
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        //calls on login button press
        loginButton.addActionListener(_ -> {
                try {
                    //get user id from form
                    userId = Integer.parseInt(userIdField.getText().trim());
                    //save user id to data handler class
                    DataHandler.USERID = userId;
                    //get other details from form
                    ipAddress = ipField.getText().trim();
                    port = Integer.parseInt(portField.getText().trim());

                    //close login open app
                    loginFrame.dispose();
                    runApp();

                } catch (NumberFormatException ex) {
                    StyledFrames.newPopup( "Please enter valid numbers for User ID and Port.", "Input Error");
                }
        });
    }

    private static void runApp() {
        JFrame frame;

        // entry boxes
        final RoundedTextField postcodeField = new RoundedTextField();
        final RoundedTextField dataField = new RoundedTextField();

        // window for main app
        frame = new JFrame("Co2 Program Input Data");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //style fields and labels
        //welcome style
        JLabel welcomeLabel = new JLabel("Welcome, User "+DataHandler.USERID);
        welcomeLabel.setBackground(Color.decode("#f4f5fc"));
        welcomeLabel.setPreferredSize(new Dimension(200, 25));
        welcomeLabel.setFont(FontLoader.getSatoshiFont(25f));
        welcomeLabel.setForeground(Color.decode("#f4f5fc"));
        //port style
        JLabel portLabel = new JLabel("Port Number: "+port);
        portLabel.setBackground(Color.decode("#f4f5fc"));
        portLabel.setPreferredSize(new Dimension(200, 25));
        portLabel.setFont(FontLoader.getSatoshiFont(13f));
        portLabel.setForeground(Color.decode("#f4f5fc"));
        //ip style
        JLabel ipLabel = new JLabel("IP Address: "+ipAddress);
        ipLabel.setBackground(Color.decode("#f4f5fc"));
        ipLabel.setPreferredSize(new Dimension(200, 25));
        ipLabel.setFont(FontLoader.getSatoshiFont(13f));
        ipLabel.setForeground(Color.decode("#f4f5fc"));
        // postcode
        JLabel postcodeLabel = new JLabel("Enter Postcode:");
        postcodeField.setBackground(Color.decode("#f4f5fc"));
        postcodeField.setPreferredSize(new Dimension(200, 25));
        postcodeLabel.setFont(FontLoader.getSatoshiFont(18f));
        postcodeLabel.setForeground(Color.decode("#f4f5fc"));
        // data input
        JLabel dataLabel = new JLabel("CO2 Data (PPM):");
        dataLabel.setFont(FontLoader.getSatoshiFont(18f));
        dataLabel.setForeground(Color.decode("#f4f5fc"));
        dataField.setBackground(Color.decode("#f4f5fc"));
        dataField.setPreferredSize(new Dimension(200, 25));
        //logout button
        JButton logoutButton = new roundedButton("Logout", new Color(200, 50, 50), new Dimension(100, 40));
        logoutButton.setForeground(Color.WHITE);

        //create panel and add elements
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(welcomeLabel);
        inputPanel.add(logoutButton);
        inputPanel.add(ipLabel);
        inputPanel.add(portLabel);
        inputPanel.add(postcodeLabel);
        inputPanel.add(postcodeField);
        inputPanel.add(dataLabel);
        inputPanel.add(dataField);
        inputPanel.setBackground(Color.decode("#24293e"));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(inputPanel, BorderLayout.CENTER);

        // button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode("#24293e"));
        JButton submitButton = new roundedButton("Submit");
        JButton mapButton = new roundedButton("View Map");

        submitButton.setBackground(new Color(10, 25, 86));
        submitButton.setForeground(new Color(255, 255, 255));
        submitButton.setPreferredSize(new Dimension(200, 40));
        mapButton.setBackground(new Color(10, 25, 86));
        mapButton.setForeground(new Color(255, 255, 255));
        mapButton.setPreferredSize(new Dimension(200, 40));

        buttonPanel.add(submitButton);
        buttonPanel.add(mapButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

        // frame settings
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        // runs on submit
        submitButton.addActionListener(_ -> {
            try {
                //add tyo data handler
                DataHandler.POSTCODE = postcodeField.getText();
                DataHandler.DATA = dataField.getText();
                // validations
                if (DataHandler.POSTCODE.isEmpty() || DataHandler.DATA.isEmpty()) {
                    StyledFrames.newPopup("All fields are required", "Error");
                } else {
                    try (Socket socket = new Socket(ipAddress, port);
                         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        // send data to server
                        out.writeInt(DataHandler.USERID);
                        out.writeObject(DataHandler.POSTCODE);
                        out.writeObject(DataHandler.DATA);
                        out.flush();

                        // server response
                        String response = in.readLine();
                        System.out.println(response);
                        if ("success".equals(response)) {
                            StyledFrames.newPopup("Data inserted successfully!", "Success");
                        } else {
                            StyledFrames.newPopup("Error inserting data. Please try again.", "Error");
                            System.out.println("not work");
                        }
                    } catch (IOException e) {
                        StyledFrames.newPopup("Unable to connect to server: " + e.getMessage(), "Error");
                    }
                }

                //clear input boxes
                postcodeField.setText("");
                dataField.setText("");

            } catch (Exception ex) {
                StyledFrames.newPopup("An unexpected error occurred: " + ex.getMessage(), "Error");
            }
        });

        // map button action
        mapButton.addActionListener(_ -> {
            MapPanel.create();

        });

        logoutButton.addActionListener(_ -> {
            // clear variables
            userId = 0;
            ipAddress = null;
            port = 0;

            // close app
            frame.dispose();

            // show login
            loginWindow();
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
    private static final int RADIUS = 15;  // Curve radius
    private Color customBackgroundColor;
    private Dimension customSize;

    public roundedButton(String text) {
        this(text, Color.decode("#97b9f9"), new Dimension(200, 40));  // Default color and size
    }

    public roundedButton(String text, Color backgroundColor, Dimension size) {
        super(text);
        this.customBackgroundColor = backgroundColor;
        this.customSize = size;

        setOpaque(false);  // Transparent background
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Padding
        setFont(FontLoader.getSatoshiFont(22f));  // Uses font loader class
        setForeground(Color.DARK_GRAY);  // Default font color
        setContentAreaFilled(false);  // Remove default button background
        setFocusPainted(false);  // Remove hover border
        setPreferredSize(customSize);  // Set custom size
    }

    public void setCustomSize(Dimension size) {
        this.customSize = size;
        setSize(size);  // Update preferred size
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw rounded rectangle shape
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Use custom background color, if provided
        g2.setColor(customBackgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));  // Rounded rectangle

        super.paintComponent(g);  // Paint button text
    }
}
