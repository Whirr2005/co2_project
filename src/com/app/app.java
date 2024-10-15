package com.app;

//imports for making a window and the elements inside said window
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//importing the other class for insterting and connetcing
import com.app.config.DatabaseConnector;
//importing font
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class app {

    public static void main(String[] args) {
        // Making a Jframe.
        JFrame frame = new JFrame("[CO2 Data Emissions Input]");

        // Load the custom font
        Font Satoshi = null;
        try {
            // Replace "src/fonts/YourFontFile.ttf" with the actual path inside your project
            Satoshi = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/Satoshi-Variable.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Satoshi);
        } catch (FontFormatException | IOException e) {
            // Catch both FontFormatException and IOException
            e.printStackTrace();
        }

        //use ".setFont(Satoshi)" to set the font for example userIdLabel.setFont(Satoshi);

        // making a Jframe for the UI
        JFrame frame = new JFrame("Data Insertion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400); // Decreased frame size
        frame.setLocationRelativeTo(null); // Centers the frame on the screen
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(4, 2)); // 4 rows, 2 columns
        frame.getContentPane().setBackground(new Color(240, 240, 240)); // Sets background colour.


        // Adding entry boxes in the window.

        JLabel userIdLabel = new JLabel("Enter Employee ID: ");
        userIdLabel.setFont(new Font("Ariel", Font.BOLD, 18));
        JTextField userIdField = new JTextField(); //Defines The Field.
        userIdField.setFont(new Font("Ariel", Font.PLAIN, 18));
        userIdField.setBackground(new Color(255, 255, 255));
        userIdField.setPreferredSize(new Dimension(200,25));

        JLabel postcodeLabel = new JLabel("Enter Postcode: ");
        postcodeLabel.setFont(new Font("Ariel", Font.BOLD, 18));
        JTextField postcodeField = new JTextField(); // Defines the field.
        postcodeField.setFont(new Font("Ariel", Font.PLAIN, 18));
        postcodeField.setBackground(new Color(255,255,255));
        postcodeField.setPreferredSize(new Dimension(200,25));

        JLabel dataLabel = new JLabel("Enter Co2 Data (Kg): ");
        dataLabel.setFont(new Font("Ariel",Font.BOLD,18));
        JTextField dataField = new JTextField(); //Defines the field.
        dataField.setFont(new Font("Ariel", Font.PLAIN, 18));
        dataField.setBackground(new Color(255,255,255));
        dataField.setPreferredSize(new Dimension(200,25));

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
        submitButton.setForeground(new Color(255, 255,255));
        submitButton.setPreferredSize(new Dimension(200, 40));
        submitButton.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(submitButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.getContentPane().add(Box.createVerticalStrut(20), BorderLayout.PAGE_END); // Strut Size
        frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

        // actually putting the components in the window
        frame.add(userIdLabel);
        frame.add(userIdField);
        frame.add(postcodeLabel);
        frame.add(postcodeField);
        frame.add(dataLabel);
        frame.add(dataField);
        frame.add(submitButton);


        // this gets called when the submit button gets clicked in the window
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // get input values from feilds
                    DataHandler.USERID = Integer.parseInt(userIdField.getText());
                    DataHandler.POSTCODE = postcodeField.getText();
                    DataHandler.DATA = dataField.getText();

                    //check if fields are empty
                    if (DataHandler.POSTCODE.isEmpty() || DataHandler.DATA.isEmpty()) {
                        //feedback and don't save to db
                        JOptionPane.showMessageDialog(frame, "All fields are required");
                    }
                    else{
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
                    // if any errors occor they will go in this pop up
                    JOptionPane.showMessageDialog(frame, "An unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}
class DataHandler {
    static int USERID;
    static String POSTCODE;
    static String DATA;
}