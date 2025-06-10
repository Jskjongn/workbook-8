package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {

        // asks for password
        System.out.print("Username: root\nPassword: ");
        String password = userInput.nextLine();

        // 1. open a connection to the database
        // use the database URL to point to the correct database
        Connection connection;

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/northwind",
                    "root",
                    password);

        // create statement
        // the statement is tied to the open connection
        PreparedStatement preparedStatement = connection.prepareStatement(
                """
                    SELECT
                        ProductID
                        , ProductName
                        , UnitPrice
                        , UnitsInStock
                    FROM
                        products
                    """
        );

        // 2. Execute your query
        ResultSet results = preparedStatement.executeQuery();

        // table header
        System.out.print("""
             
             ID      Name                               Price   Stock
             ---     --------------------------         ------  ------
             """);

        // process the results
        while (results.next()) {
            // left aligned by however many spaces to align with table header above
            System.out.printf("%-7d %-34s %-7.2f %-6d\n",
                    results.getInt("ProductID"),
                    results.getString("ProductName"),
                    results.getDouble("UnitPrice"),
                    results.getInt("UnitsInStock"));
        }

        // 3. Close the connection
        results.close();
        preparedStatement.close();
        connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
