package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);
    static Connection connection = null;
    static PreparedStatement preparedStatement = null;
    static ResultSet resultSet = null;

    public static void main(String[] args) {

        // establishes the connection for database
        connection = getConnection();

        // displays home screen
        boolean homeScreen = true;
        while (homeScreen) {

            // displays user options
            System.out.print("""
                
                What do you want to do?
                    1) Display all products
                    2) Display all customers
                    0) Exit
                """);
            System.out.print("Select an option: ");

            // stores user option for switch
            int option = userInput.nextInt();
            switch (option) {
                case 1:
                    displayProducts();
                    continue;
                case 2:
                    displayCustomers();
                    break;
                case 0:
                    closeResources();
                    homeScreen = false;
                    break;
                default:
                    System.out.println("Please choose either 1-2 or 0 to exit!");
            }
        }
    }

    // gets the connection to database
    public static Connection getConnection() {

        // asks for password
        System.out.print("Username: root\nPassword: ");
        String password = userInput.nextLine();

        try {
            // creates connection to database using url and password
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/northwind",
                    "root",
                    password);
        } catch (SQLException e) {
            System.out.println("Error with connection: " + e.getMessage());
            return null;
        }
    }

    // displays all products from products table
    public static void displayProducts() {

        try {
            // creates prepared statement with the connection and writes the query
            preparedStatement = connection.prepareStatement(
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

            // takes prepared statement with query to execute
            resultSet = preparedStatement.executeQuery();

            // table header
            System.out.print("""
             
             ID      Name                               Price   Stock
             ---     --------------------------         ------  ------
             """);

            // displays the query executed
            while (resultSet.next()) {
                // left aligned by however many spaces to align with table header above
                System.out.printf("%-7d %-34s %-7.2f %-6d\n",
                        resultSet.getInt("ProductID"),
                        resultSet.getString("ProductName"),
                        resultSet.getDouble("UnitPrice"),
                        resultSet.getInt("UnitsInStock"));
            }

        } catch (SQLException e) {
            System.out.println("Error in finding your query: " + e.getMessage());
        }
    }

    // displays all customers
    public static void displayCustomers() {

        try {
            // creates prepared statement with the connection and writes the query
            preparedStatement = connection.prepareStatement(
                    """
                        SELECT
                            ContactName
                            , CompanyName
                            , City
                            , Country
                            , Phone
                        FROM
                            customers
                        WHERE
                            Country IS NOT NULL
                        ORDER BY
                            Country
                        """
            );
            // executes the query from the prepared statement
            resultSet = preparedStatement.executeQuery();

            // displays query results from column names
            while(resultSet.next()) {
                System.out.printf("""
                        
                        Contact Name: %s
                        Company Name: %s
                        City:         %s
                        Country:      %s
                        Phone:        %s
                        """,
                        resultSet.getString("ContactName"),
                        resultSet.getString("CompanyName"),
                        resultSet.getString("City"),
                        resultSet.getString("Country"),
                        resultSet.getString("Phone"));
            }

        } catch (SQLException e) {
            System.out.println("Error in finding your query: " + e.getMessage());
        }
    }

    // closes all resources
    public static void closeResources() {
        try {
            // close the resources if not null
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
