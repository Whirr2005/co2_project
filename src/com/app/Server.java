package com.app;

import com.app.config.DatabaseConnector;


import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Server {

    private static JTextArea logTextArea; // Text area for logging

    public static void main(String[] args) {
        int port = 1234; // port number

        // server jframe
        JFrame serverFrame = new JFrame("Server Log");
        serverFrame.setSize(500, 400);
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //text area to act as a console /terminal box
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);

        // add text area in scroll box
        serverFrame.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

        //show window
        serverFrame.setLocationRelativeTo(null);
        serverFrame.setVisible(true);

        // getting current ip address
        String ipAddress = "Unavailable";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ipAddress = ip.getHostAddress();
        } catch (UnknownHostException e) {
            logTextArea.append("Error retrieving IP address\n");
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logTextArea.append("ip: " + ipAddress + "\n");
            logTextArea.append("port " + port + "\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logTextArea.append("client connected: " + clientSocket.getInetAddress() + "\n");

                //new thread for client
                new Thread(() -> {
                    try {
                        saveData(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            logTextArea.append("Server error: " + e.getMessage() + "\n");
        }
    }

    private static void saveData(Socket clientSocket) throws IOException {
        try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) { // Add PrintWriter to send response

            String line1 = (String) inputStream.readObject();

            if (Objects.equals(line1, "GET_DATA")){
                List<String[]> data = DatabaseConnector.readData("data_table");
                logTextArea.append("GET_DATA has been called");
                out.println(data.size());
                for (int i = 0; i < data.size(); i++){
                    out.println(Arrays.toString(data.get(i)));
                }


            }
            else{
                // data from client
                int userId = inputStream.readInt(); //line 2

                String line3 = (String) inputStream.readObject();
                String postcode = line1;
                String co2Data = line3;



                logTextArea.append("user id: "+userId+"\n"+"postcode: "+postcode+"\n"+"co2 data: "+co2Data+"\n"); //test
                //make time stamp
                String timeStamp = LocalDateTime.now().toString();

                //inset data in database
                boolean success = DatabaseConnector.insertData(userId, postcode, co2Data, timeStamp);

                // Send response to client and log result
                if (success) {
                    out.println("success");
                    logTextArea.append("data inserted successfully\n");
                } else {
                    out.println("error");
                    logTextArea.append("Error inserting data\n");
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            logTextArea.append("Error processing client data: " + e.getMessage() + "\n");
        } finally {
                clientSocket.close();
        }
    }
}
