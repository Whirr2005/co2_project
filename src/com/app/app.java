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

        // importing font
        File fontFile = new File("C:/Users/mrfoo/IdeaProjects/co2_project/src/com/app/fonts/Satoshi-Variable.ttf");
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);

        // making a jframe
        JFrame frame = new JFrame("Data Insertion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400); // Decreased frame size
        frame.setLocationRelativeTo(null); // Centers the frame on the screen
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(4, 2)); // 4 rows, 2 columns

        // adding entry boxes in the window
        JLabel userIdLabel = new JLabel("Enter user_id (integer): ");
        JTextField userIdField = new JTextField();

        JLabel postcodeLabel = new JLabel("Enter postcode: ");
        JTextField postcodeField = new JTextField();

        JLabel dataLabel = new JLabel("Enter data: ");
        JTextField dataField = new JTextField();

        JButton submitButton = new JButton("Submit");

        // actually putting the componets in the window
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

                    //check if feilds are empty
                    if (DataHandler.POSTCODE.isEmpty() || DataHandler.DATA.isEmpty()) {
                        //feedback and dont save to db
                        JOptionPane.showMessageDialog(frame, "All fields are required");
                    }
                    else{
                        // insert data as all feilds are full
                        boolean success = DatabaseConnector.insertData(DataHandler.USERID, DataHandler.POSTCODE, DataHandler.DATA);

                        // pop up with resukt
                        if (success) {
                            JOptionPane.showMessageDialog(frame, "Data inserted successfully!");
                        } else {
                            //somthing went wrong with inserting
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