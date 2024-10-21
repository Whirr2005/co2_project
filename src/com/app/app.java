package com.app;

import java.time.LocalDateTime;
import javax.swing.*;
import java.awt.*;
import com.app.config.DatabaseConnector;
import java.awt.geom.RoundRectangle2D;

public class app {

    public static void main(String[] args) {

        JFrame frame;
        //entry fields
        final RoundedTextField userIdField = new RoundedTextField();
        final RoundedTextField postcodeField = new RoundedTextField();
        final RoundedTextField dataField = new RoundedTextField();

            // creates JFrame
            frame = new JFrame("Co2 Program Input data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            //style fields and labels
            //user id
            JLabel userIdLabel = new JLabel("Enter User Id:");
            userIdField.setBackground(Color.decode("#f4f5fc"));
            userIdField.setPreferredSize(new Dimension(200, 25)); // sets width
            userIdLabel.setFont(FontLoader.getSatoshiFont(28f));
            userIdLabel.setForeground(Color.decode("#f4f5fc"));
            //postcode
            JLabel postcodeLabel = new JLabel("Enter Postcode:");
            postcodeField.setBackground(Color.decode("#f4f5fc"));
            postcodeField.setPreferredSize(new Dimension(200, 25)); // sets width
            postcodeLabel.setFont(FontLoader.getSatoshiFont(18f));
            postcodeLabel.setForeground(Color.decode("#f4f5fc"));
            //co2 data readings
            JLabel dataLabel = new JLabel("CO2 Data (kg):");
            dataLabel.setFont(FontLoader.getSatoshiFont(18f));
            dataLabel.setForeground(Color.decode("#f4f5fc"));
            dataField.setBackground(Color.decode("#f4f5fc"));
            dataField.setPreferredSize(new Dimension(200, 25)); // sets width

            //creates panel
            JPanel Inputpanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Grid Gap
            Inputpanel.add(userIdLabel);
            Inputpanel.add(userIdField);
            Inputpanel.add(postcodeLabel);
            Inputpanel.add(postcodeField);
            Inputpanel.add(dataLabel);
            Inputpanel.add(dataField);
            Inputpanel.setBackground(Color.decode("#24293e"));
            //panel margins
            Inputpanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            frame.add(Inputpanel, BorderLayout.CENTER);

            //second panel for the buttons
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
            //margins
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            frame.getContentPane().add(Box.createVerticalStrut(20), BorderLayout.PAGE_END);
            frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

            //sets frame size and rules
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);

            //run on submit
            submitButton.addActionListener(_ -> {
                try {
                    // get input values from feilds
                    DataHandler.USERID = Integer.parseInt(userIdField.getText());
                    DataHandler.POSTCODE = postcodeField.getText();
                    DataHandler.DATA = dataField.getText();

                    //get current time
                    LocalDateTime LDTime = java.time.LocalDateTime.now();//get current time
                    String timeStamp = LDTime.toString();

                    //check if fields are empty
                    if (DataHandler.POSTCODE.isEmpty() || DataHandler.DATA.isEmpty()) {
                        //feedback and don't save to db
                        StyledFrames.newPopup("All fields are required", "Error");
                    }
                    else if(postcodeCoords.getCoords(DataHandler.POSTCODE) == null) {
                        StyledFrames.newPopup("postcode not valid", "Error");
                    }
                    else {
                        // insert data as all fields are full
                        boolean success = DatabaseConnector.insertData(DataHandler.USERID, DataHandler.POSTCODE, DataHandler.DATA, timeStamp);

                        // pop up with result
                        if (success) {
                            StyledFrames.newPopup("Data inserted successfully!", "Success");
                        } else {
                            //something went wrong with inserting
                            StyledFrames.newPopup( "Error inserting data. Please try again.", "Error");
                        }
                    }

                    // set input boxes to empty
                    userIdField.setText("");
                    postcodeField.setText("");
                    dataField.setText("");
                } catch (NumberFormatException ex) {
                    StyledFrames.newPopup("Please enter a valid integer for user_id.", "Error");
                } catch (Exception ex) {
                    // if any errors occur they will go in this pop up
                    StyledFrames.newPopup("An unexpected error occurred: " + ex.getMessage(), "Error");
                }
            });


            //mpa button action
             mapButton.addActionListener(_ -> {
                 //calls create() finction from MapPanel class
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