package com.app;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        createGUI();

    }

    private void createGUI() {
        // Create the frame
        frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Set background
        frame.getContentPane().setBackground(new Color(32, 23, 42)); // Light gray color

        // Create labels and fields
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increased font size
        usernameField = new JTextField(30); // Increased field size for better accessibility
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24)); // Increased font size
        usernameField.setBackground(new Color(255, 255, 255)); // White color
        usernameLabel.setForeground(new Color(255,255,255));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(128, 128, 128)), // Gray border
                BorderFactory.createEmptyBorder(0, 5, 0, 5) // Slight indent
        ));
        usernameField.setPreferredSize(new Dimension(400, 30)); // Increased width and reduced height
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increased font size
        passwordLabel.setForeground(new Color(255,255,255));
        passwordField = new JPasswordField(30); // Increased field size for better accessibility
        passwordField.setFont(new Font("Arial", Font.PLAIN, 24)); // Increased font size
        passwordField.setBackground(new Color(255, 255, 255)); // White color
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(128, 128, 128)), // Gray border
                BorderFactory.createEmptyBorder(0, 5, 0, 5) // Slight indent
        ));
        passwordField.setPreferredSize(new Dimension(400, 30)); // Increased width and reduced height

        // Add components to the frame
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20)); // Increased grid gap for better spacing
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.setBackground(new Color(16, 13, 16)); // Light gray color
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Added margins
        frame.add(panel, BorderLayout.CENTER);

        // Add a login button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(16, 13, 16)); // Light gray color
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 24)); // Increased font size
        loginButton.setHorizontalAlignment(SwingConstants.CENTER);
        loginButton.setBackground(new Color(74, 47, 98)); // Green color
        loginButton.setForeground(new Color(255, 255, 255)); // White color
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(241, 241, 241))); // Black border
        loginButton.setPreferredSize(new Dimension(200, 40)); // Increased width and slightly increased height
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.equals("Admin") && password.equals("Admin")) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose(); // Close the login frame
                    app gui = new app(); // Create a new instance of the CO2EmissionsGUI
                    gui.main(new String[] {"123"});

                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password");
                }
            }
        });
        buttonPanel.add(loginButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Added margins
        frame.getContentPane().add(Box.createVerticalStrut(30), BorderLayout.PAGE_END); // Increased strut size for better spacing
        frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

        // Set up the frame
        frame.setSize(500, 250); // Frame size
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginPage();
            }
        });
    }
}