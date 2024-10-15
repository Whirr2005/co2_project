package com.app;

//imports for making a window and the elements inside said window
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//importing the other class for insterting and connetcing
import com.app.config.DatabaseConnector;

public class app {

    public static void main(String[] args) {
        // Making a Jframe.
        JFrame frame = new JFrame("[CO2 Data Emissions Input]");
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

        // this gets called when the submit button gets clicked in the window
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // get input values from feilds
                    int column1Value = Integer.parseInt(userIdField.getText());
                    String column2Value = postcodeField.getText();
                    String column3Value = dataField.getText();

                    //check if feilds are empty
                    if (column2Value.isEmpty() || column3Value.isEmpty()) {
                        //feedback and dont save to db
                        JOptionPane.showMessageDialog(frame, "All fields are required");
                    }
                    else{
                        // insert data as all feilds are full
                        boolean success = DatabaseConnector.insertData(column1Value, column2Value, column3Value);

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
