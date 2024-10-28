package com.app.server;

import com.app.config.DatabaseConnector;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {

    public static void main(String[] args) {

        // Create and show the server window
        JFrame frame = new JFrame("Server");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a text area to display server messages
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        // Prompt user to enter a port number
        String portInput = JOptionPane.showInputDialog(frame, "Enter the port number:", "Port Selection", JOptionPane.QUESTION_MESSAGE);

        // Validate the port input (must be an integer within the valid port range)
        int PORT;
        try {
            PORT = Integer.parseInt(portInput);
            if (PORT < 1 || PORT > 65535) {
                throw new NumberFormatException("Invalid port number. Port must be between 1 and 65535.");
            }
        } catch (NumberFormatException e) {
            appendText("Invalid port number. Using default port 12345.", textArea);
            PORT = 12345;  // Default port number
        }

        // Start the server on the selected port
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            appendText("Server is running and waiting for clients on port " + PORT, textArea);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    appendText("Connected to client.", textArea);

                    // Read the client's request (e.g., "insert" or "read")
                    String operation = (String) in.readObject();

                    if ("insert".equals(operation)) {
                        // Receive data for insertion
                        int userId = in.readInt();
                        String postcode = (String) in.readObject();
                        String data = (String) in.readObject();
                        String timestamp = (String) in.readObject();

                        // Insert data into the database
                        boolean success = DatabaseConnector.insertData(userId, postcode, data, timestamp);
                        out.writeBoolean(success);
                        out.flush();
                        if (success) {
                            appendText("Data inserted", textArea);
                        } else {
                            appendText("Data not inserted", textArea);
                        }
                    } else if ("read".equals(operation)) {
                        // Fetch data from the database
                        List<String[]> dataList = DatabaseConnector.readData("data_table");
                        out.writeObject(dataList);  // Send data to client
                        out.flush();
                    }
                } catch (Exception e) {
                    appendText("Error with client connection: " + e.getMessage(), textArea);
                }
            }
        } catch (IOException e) {
            appendText("Server error: " + e.getMessage(), textArea);
        }
    }

    // Method to append text to the JTextArea
    private static void appendText(String message, JTextArea textArea) {
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
    }
}
