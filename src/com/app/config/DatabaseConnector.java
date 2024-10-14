package com.app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/co2_readings";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = ""; // Leave blank if there is no password

    public static Connection connect() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // connect to db
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

    // write to table in db
    public static boolean insertData(int column1Value, String column2Value, String column3Value) {
        String sql = "INSERT INTO data_table (user_id, postcode, data) VALUES (?, ?, ?)";
        boolean isInserted = false; // used to check if it worked

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // check connection is successful
            if (connection == null) {
                System.out.println("Connection failed. No data was inserted.");
                return false;
            }

            // prepared statement values
            preparedStatement.setInt(1, column1Value);
            preparedStatement.setString(2, column2Value);
            preparedStatement.setString(3, column3Value);
            int rowsAffected = preparedStatement.executeUpdate();

            // print inserted successfully
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
}
