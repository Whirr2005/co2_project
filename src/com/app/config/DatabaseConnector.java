package com.app.config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class DatabaseConnector {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/co2_readings";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = ""; // Leave blank if there is no password

    public static Connection connect() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            if (connection != null) {
                System.out.println("Connected to the database!");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver class not found: " + e.getMessage());
        }

        return connection;
    }

    // Method to write data into the database
    public static boolean insertData(int column1Value, String column2Value, String column3Value, String column4Value) {
        String sql = "INSERT INTO data_table (user_id, postcode, data, timeStamp) VALUES (?, ?, ?, ?)";
        boolean isInserted = false;

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Check if connection was successful
            if (connection == null) {
                System.out.println("Connection failed. No data was inserted.");
                return false;
            }

            // Set prepared statement values
            preparedStatement.setInt(1, column1Value);
            preparedStatement.setString(2, column2Value);
            preparedStatement.setString(3, column3Value);
            preparedStatement.setString(4, column4Value);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
                isInserted = true;
            } else {
                System.out.println("Data insertion failed.");
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }

        return isInserted;
    }

    // Method to read data from the table
    public static List<String[]> readData(String tableName) {
        List<String[]> dataList = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName;

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Get metadata to dynamically fetch column count
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate through the result set and add each row to the data list
            while (resultSet.next()) {
                String[] row = new String[columnCount];

                // For each column, store the value in the row array
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getString(i);
                }
                dataList.add(row);
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        return dataList;
    }
    // New method to save table data as CSV
    public static void saveTableToCSV(String tableName) {
        Connection connection = connect();
        if (connection == null) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prepare a file chooser dialog to save the file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");

        // Set a default file name
        fileChooser.setSelectedFile(new File("table_data.csv"));

        // Show the dialog and check if the user approves the selection
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();

            // SQL query to retrieve all data from the table
            String query = "SELECT * FROM " + tableName;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query);
                 FileWriter csvWriter = new FileWriter(csvFile)) {

                // Write column headers to the CSV file
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(resultSet.getMetaData().getColumnName(i));
                    if (i < columnCount) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");

                // Write rows to the CSV file
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(resultSet.getString(i));
                        if (i < columnCount) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }

                JOptionPane.showMessageDialog(null, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    connection.close(); // Close the connection
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
