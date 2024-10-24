package com.app.server;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static JTextArea textArea; // Text area for displaying server logs

    public static void main(String[] args) {
        // Create and show the server window
        JFrame frame = new JFrame("Server");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a text area to display server messages
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Show the window
        frame.setVisible(true);

        // Start the server
        startServer();
    }

    private static void startServer() {
        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            appendText("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                appendText("New client connected");

                // Handle client in a new thread
                new ClientHandler(socket).start();
            }

        } catch (IOException e) {
            appendText("Error in the server: " + e.getMessage());
        }
    }

    // Method to append text to the JTextArea
    private static void appendText(String message) {
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
    }

    // ClientHandler class to handle multiple clients in separate threads
    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

                String message;
                while ((message = reader.readLine()) != null) {
                    appendText("Received from client: " + message);
                }

            } catch (IOException e) {
                appendText("Error with client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    appendText("Error closing client connection: " + e.getMessage());
                }
            }
        }
    }
}
