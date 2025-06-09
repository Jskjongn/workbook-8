package com.pluralsight;

import java.sql.*;

public class App {

    public static void main(String[] args) {

        // 1. open a connection to the database
        // use the database URL to point to the correct database
        Connection connection;

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/northwind",
                    "root",
                    "yearup");

        // create statement
        // the statement is tied to the open connection
        Statement statement = connection.createStatement();
        
        // define your query
        String query = """
                SELECT
                    ProductName
                FROM
                    products
                ORDER BY
                    ProductName
                """;

        // 2. Execute your query
        ResultSet results = statement.executeQuery(query);

        // process the results
        while (results.next()) {
            String productName = results.getString("ProductName");
            System.out.println(productName);
        }
        // 3. Close the connection
        connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
