package com.app;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Server {

    private static final int PORT = 1234; // port
    private static final int MAX_CLIENTS = 4; // max clients/users
    private static final List<String> logs = new ArrayList<>(); // logs list for incoming data
    private static JTextArea logArea; //for console messages

    public static void main(String[] args) {
        //server jframe
        JFrame frame = new JFrame("CO2 Logging Server");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //panels for elements
        JPanel panel = new JPanel(new BorderLayout());
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        //get ip address
        String ipAddress = "Unavailable";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ipAddress = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JLabel infoLabel = new JLabel("Server IP: " + ipAddress + " | Port: " + PORT);
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);

        Server.appendLog(ipAddress);

        //using threads
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                appendLog("New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, logs));
            }
        } catch (IOException e) {
            appendLog("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // add console style messages to text area
    static void appendLog(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final List<String> logs; // logs list

    public ClientHandler(Socket socket, List<String> logs) {
        this.clientSocket = socket;
        this.logs = logs;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Welcome to the CO2 logging server! Type 'LOG' to log data or 'GET DATA' to retrieve data.");
            String command;

            while ((command = in.readLine()) != null) {
                if (command.equalsIgnoreCase("LOG")) {
                    handleLogCommand(out, in);
                } else if (command.equalsIgnoreCase("GET DATA")) {
                    handleGetLogsCommand(out);
                } else {
                    out.println("Unknown command. Type 'LOG' to log data or 'GET DATA' to retrieve data.");
                }
            }
        } catch (IOException e) {
            Server.appendLog("Client handler exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                Server.appendLog("Connection with client closed.");
            } catch (IOException e) {
                Server.appendLog("Couldn't close client socket: " + e.getMessage());
            }
        }
    }

    private void handleLogCommand(PrintWriter out, BufferedReader in) throws IOException {
        //data from client
        String userID = promptClient(out, in, "Enter User ID:");
        String postcode = promptClient(out, in, "Enter postcode:");
        String co2ppm = promptClient(out, in, "Enter CO2 concentration (ppm):");

        //add to log
        String logEntry = String.format("%s, User ID: %s, Postcode: %s, CO2 ppm: %s", new Date(), userID, postcode, co2ppm);
        synchronized (logs) {
            logs.add(logEntry);
        }
        Server.appendLog("Received data: " + logEntry);

        out.println("Data logged successfully!");
    }

    private void handleGetLogsCommand(PrintWriter out) {
        out.println("CO2 Data:");
        synchronized (logs) {
            if (logs.isEmpty()) {
                out.println("No data available.");
            } else {
                for (String log : logs) {
                    out.println(log);
                }
            }
        }
        out.println("END OF DATA");
    }

    private String promptClient(PrintWriter out, BufferedReader in, String message) throws IOException {
        out.println(message);
        return in.readLine();
    }
}
