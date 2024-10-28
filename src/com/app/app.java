package com.app;
import java.io.*;
import java.net.*;

public class app {
    private static final int SERVER_PORT = 1234; //server port

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String serverIP = null;

        // enter ip address
        try {
            System.out.print("Enter the server IP address: ");
            serverIP = consoleInput.readLine();
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            return;
        }

        // connect to server
        try (Socket socket = new Socket(serverIP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server at " + serverIP + ".");

            //welcome message
            System.out.println(in.readLine());

            String command;
            while (true) {
                //user commands
                System.out.print("Enter command (LOG or GET LOGS): ");
                try {
                    command = consoleInput.readLine();
                } catch (IOException e) {
                    System.err.println("Error reading command: " + e.getMessage());
                    break;
                }
                out.println(command);

                if (command.equalsIgnoreCase("LOG")) {
                    System.out.print("Enter User ID: ");
                    out.println(consoleInput.readLine());

                    System.out.print("Enter Postcode: ");
                    out.println(consoleInput.readLine());

                    System.out.print("Enter CO2 concentration (ppm): ");
                    out.println(consoleInput.readLine());

                    System.out.println("Server response: " + in.readLine());

                } else if (command.equalsIgnoreCase("GET LOGS")) {
                    String serverResponse;
                    System.out.println("Server logs:");
                    while (!(serverResponse = in.readLine()).equals("END OF LOGS")) {
                        System.out.println(serverResponse);
                    }
                } else {
                    System.out.println("Invalid command. Please enter LOG or GET LOGS.");
                }
            }

        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
        }
    }
}

