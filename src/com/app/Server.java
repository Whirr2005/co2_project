package com.app;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Server {


    private static final int PORT = 1234; // port
    private static final int MAX_CLIENTS = 4; // max clients/users
    private static final List<String> logs = new ArrayList<>(); // tmp logs variable to test server client outputs

    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Server is running on IP Address: " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, logs));
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
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

            out.println("Welcome to the CO2 logging server! Type 'LOG' to log data or 'GET LOGS' to retrieve data.");

            String command;
            while ((command = in.readLine()) != null) {
                if (command.equalsIgnoreCase("LOG")) {
                    handleLogCommand(out, in);
                } else if (command.equalsIgnoreCase("GET LOGS")) {
                    handleGetLogsCommand(out);
                } else {
                    out.println("Unknown command. Type 'LOG' to log data or 'GET LOGS' to retrieve data.");
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connection with client closed.");
            } catch (IOException e) {
                System.err.println("Couldn't close client socket: " + e.getMessage());
            }
        }
    }

    private void handleLogCommand(PrintWriter out, BufferedReader in) throws IOException {
        // get data from client
        String userID = promptClient(out, in, "Enter User ID:");
        String postcode = promptClient(out, in, "Enter postcode:");
        String co2ppm = promptClient(out, in, "Enter CO2 concentration (ppm):");

        // print data from client
        System.out.println("Received data: " + userID + ", " + postcode + ", " + co2ppm);

        String logEntry = String.format("%s, User ID: %s, Postcode: %s, CO2 ppm: %s", new Date(), userID, postcode, co2ppm);
        synchronized (logs) {
            logs.add(logEntry);
        }

        // succeccss
        out.println("Data logged successfully!");
    }

    private void handleGetLogsCommand(PrintWriter out) {
        out.println("CO2 Logs:");
        synchronized (logs) {
            if (logs.isEmpty()) {
                out.println("No logs available.");
            } else {
                for (String log : logs) {
                    out.println(log);
                }
            }
        }
    }

    private String promptClient(PrintWriter out, BufferedReader in, String message) throws IOException {
        out.println(message);
        return in.readLine();
    }
}

