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

        JFrame frame = new JFrame("Server");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);


        frame.setVisible(true);

        final int PORT = 12345; //localhost:12345
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
                        }
                        else appendText("Data not inserted", textArea);
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
            e.printStackTrace();
        }
    }

    private static void appendText(String message, JTextArea textArea) {
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
    }


}
