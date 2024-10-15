package com.app;

// Imports for making a window and the elements inside said window
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//importing the other class for inserting and connecting
import com.app.config.DatabaseConnector;

public class app {

    public static void main(String[] args) {

         JFrame frame;
         JTextField userIdField;
         JTextField postcodeField;
         JTextField dataField;

            // Creates the JFrame
            frame = new JFrame("[CO2 Data Emissions Input]");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Sets Background White.
            frame.getContentPane().setBackground(new Color(240, 240, 240)); // Sets background colour.


            // Changes the attributes of boxes.

            JLabel userIdLabel = new JLabel("Enter User Id:");
            userIdLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Decreases font size
            userIdField = new JTextField(15); // Decreased field size
            userIdField.setFont(new Font("Arial", Font.PLAIN, 18)); // Decreases font size
            userIdField.setBackground(new Color(255, 255, 255));
            userIdField.setPreferredSize(new Dimension(200, 25)); // Decreases width

            JLabel postcodeLabel = new JLabel("Enter Postcode:");
            postcodeLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Decreases font size
            postcodeField = new JTextField(15); // Decreased field size
            postcodeField.setFont(new Font("Arial", Font.PLAIN, 18)); // Decreases font size
            postcodeField.setBackground(new Color(255, 255, 255));
            postcodeField.setPreferredSize(new Dimension(200, 25)); // Decreases width

            JLabel dataLabel = new JLabel("CO2 Data (kg):");
            dataLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Decreases font size
            dataField = new JTextField(15); // Decreased field size
            dataField.setFont(new Font("Arial", Font.PLAIN, 18)); // Decreases font size
            dataField.setBackground(new Color(255, 255, 255));
            dataField.setPreferredSize(new Dimension(200, 25)); // Decreases width

            // Adds Components To The Frame.

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10)); // Grid Gap
            panel.add(userIdLabel);
            panel.add(userIdField);
            panel.add(postcodeLabel);
            panel.add(postcodeField);
            panel.add(dataLabel);
            panel.add(dataField);
            panel.setBackground(new Color(240, 240, 240)); //
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margins
            frame.add(panel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(240, 240, 240)); // Sets Background Of Bottom Panel.
            JButton submitButton = new JButton("Submit");
            submitButton.setFont(new Font("Arial", Font.BOLD, 24)); // Font Size
            submitButton.setBackground(new Color(10, 25, 86));
            submitButton.setForeground(new Color(255, 255, 255));
            submitButton.setPreferredSize(new Dimension(200, 40));
            submitButton.setHorizontalAlignment(SwingConstants.CENTER);
            buttonPanel.add(submitButton);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            frame.getContentPane().add(Box.createVerticalStrut(20), BorderLayout.PAGE_END); // Strut Size
            frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

            // this gets called when the submit button gets clicked in the window

            submitButton.addActionListener(e -> {
                try {
                    // get input values from feilds
                    DataHandler.USERID = Integer.parseInt(userIdField.getText());
                    DataHandler.POSTCODE = postcodeField.getText();
                    DataHandler.DATA = dataField.getText();

                    //check if fields are empty
                    if (DataHandler.POSTCODE.isEmpty() || DataHandler.DATA.isEmpty()) {
                        //feedback and don't save to db
                        JOptionPane.showMessageDialog(frame, "All fields are required");
                    } else {
                        // insert data as all fields are full
                        boolean success = DatabaseConnector.insertData(DataHandler.USERID, DataHandler.POSTCODE, DataHandler.DATA);

                        // pop up with result
                        if (success) {
                            JOptionPane.showMessageDialog(frame, "Data inserted successfully!");
                        } else {
                            //something went wrong with inserting
                            JOptionPane.showMessageDialog(frame, "Error inserting data. Please try again.");
                        }
                    }

                    // set input boxes to empty
                    userIdField.setText("");
                    postcodeField.setText("");
                    dataField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid integer for user_id.");
                } catch (Exception ex) {
                    // if any errors occur they will go in this pop up
                    JOptionPane.showMessageDialog(frame, "An unexpected error occurred: " + ex.getMessage());
                }
            });

        frame.setSize(600, 400); // Decreased frame size
        frame.setLocationRelativeTo(null); // Centers the frame on the screen
        frame.setVisible(true);
        frame.setResizable(false);

        }
    }

    class DataHandler {
        static int USERID;
        static String POSTCODE;
        static String DATA;
}
