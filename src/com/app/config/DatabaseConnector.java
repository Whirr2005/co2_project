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
    private static final String DATABASE_PASSWORD = "";

    public static Connection connect() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            //connect to database
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            if (connection != null) {
                System.out.println("connected to database");
            }
        } catch (SQLException e) {
            System.err.println("connection failed: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("driver class not found: " + e.getMessage());
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
                System.out.println("connection failed");
                return false;
            }

            //add values to sql
            preparedStatement.setInt(1, column1Value);
            preparedStatement.setString(2, column2Value);
            preparedStatement.setString(3, column3Value);
            preparedStatement.setString(4, column4Value);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("databaseConnector successfully inserted");
                isInserted = true;
                //succsess
            } else {
                System.out.println("databaseConnector insertion failed");
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

                //for each line in table add data
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

    
}
