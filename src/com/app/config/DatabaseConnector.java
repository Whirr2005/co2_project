package com.app.config;

import com.app.StyledFrames;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class DatabaseConnector {

    //database login information
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/co2_readings";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = ""; // Leave blank if there is no password

    public static Connection connect() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            //connect to database
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

    // insert data to database
    public static boolean insertData(int column1Value, String column2Value, String column3Value, String column4Value) {
        //set sql query
        String sql = "INSERT INTO data_table (user_id, postcode, data, timeStamp) VALUES (?, ?, ?, ?)";
        boolean isInserted = false;

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // check connection
            if (connection == null) {
                System.out.println("Connection failed. No data was inserted.");
                return false;
            }

            //add values to sql
            preparedStatement.setInt(1, column1Value);
            preparedStatement.setString(2, column2Value);
            preparedStatement.setString(3, column3Value);
            preparedStatement.setString(4, column4Value);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
                isInserted = true;
                //succsess
            } else {
                System.out.println("Data insertion failed.");
                //failed
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }

        return isInserted;
    }

    //pull data from table
    public static List<String[]> readData(String tableName) {
        List<String[]> dataList = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName;

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            //get column count
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            //go down table and add lines to list
            while (resultSet.next()) {
                String[] row = new String[columnCount];

                //for each line in the table add the data
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

    //export table data as csv file
    public static void saveTableToCSV(String tableName) {
        Connection connection = connect();
        if (connection == null) {
            StyledFrames.newPopup("Failed to connect to the database!:" + JOptionPane.ERROR_MESSAGE, "Error");
            return;
        }

        //file chooser destination window set file lcoation
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");

        //default file name
        fileChooser.setSelectedFile(new File("table_data.csv"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();

            // SQL to get all data from the table
            String query = "SELECT * FROM " + tableName;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query);
                 FileWriter csvWriter = new FileWriter(csvFile)) {

                //set column names in the csv file
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(resultSet.getMetaData().getColumnName(i));
                    if (i < columnCount) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");

                // add data to the csv
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(resultSet.getString(i));
                        if (i < columnCount) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }

                StyledFrames.newPopup( "Data saved successfully!", "Success");

            } catch (Exception e) {
                StyledFrames.newPopup("Error saving CSV: " + e.getMessage(), "Error");
            } finally {
                try {
                    connection.close(); //close connection
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
