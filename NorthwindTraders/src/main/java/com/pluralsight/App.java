package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {

        // establishes the connection for database
        Connection connection = getConnection();

        // displays home screen
        boolean homeScreen = true;
        while (homeScreen) {

            // displays user options
            System.out.print("""
                    
                    What do you want to do?
                        1) Display all products
                        2) Display all customers
                        3) Display all categories
                        0) Exit
                    """);
            System.out.print("Select an option: ");

            // stores user option for switch
            int option = userInput.nextInt();
            switch (option) {
                case 1:
                    displayProducts(connection);
                    continue;
                case 2:
                    displayCustomers(connection);
                    break;
                case 3:
                    displayCategories(connection);
                    break;
                case 0:
                    homeScreen = false;
                    break;
                default:
                    System.out.println("Please choose either 1-3 or 0 to exit!");
            }
        }
    }

    // gets the connection to database
    public static Connection getConnection() {

        // asks for password
        System.out.print("Username: root\nPassword: ");
        String password = userInput.nextLine();

        // creates datasource
        try (BasicDataSource dataSource = new BasicDataSource()) {

            // creates connection to database using url and password
            dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
            dataSource.setUsername("root");
            dataSource.setPassword(password);

            return dataSource.getConnection();

        } catch (SQLException e) {
            System.out.println("Error with opening connection: " + e.getMessage());
            return null;
        }
    }

    // displays all products from products table
    public static void displayProducts(Connection connection) {

        try (
                // creates prepared statement with the connection and writes the query
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

                // takes prepared statement with query to execute
                ResultSet results = preparedStatement.executeQuery();
        ) {
            // table header
            System.out.print("""
                    
                    ID      Name                               Price   Stock
                    ---     --------------------------         ------  ------
                    """);

            // displays the query executed
            while (results.next()) {
                // left aligned by however many spaces to align with table header above
                System.out.printf("%-7d %-34s %-7.2f %-6d\n",
                        results.getInt("ProductID"),
                        results.getString("ProductName"),
                        results.getDouble("UnitPrice"),
                        results.getInt("UnitsInStock"));
            }

        } catch (SQLException e) {
            System.out.println("Error in finding your query: " + e.getMessage());
        }
    }

    // displays all customers
    public static void displayCustomers(Connection connection) {

        try (
                // creates prepared statement with the connection and writes the query
                PreparedStatement preparedStatement = connection.prepareStatement(
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
                ResultSet results = preparedStatement.executeQuery();
        ) {
            // displays query results from column names
            while (results.next()) {
                System.out.printf("""
                                
                                Contact Name: %s
                                Company Name: %s
                                City:         %s
                                Country:      %s
                                Phone:        %s
                                """,
                        results.getString("ContactName"),
                        results.getString("CompanyName"),
                        results.getString("City"),
                        results.getString("Country"),
                        results.getString("Phone"));
            }

        } catch (SQLException e) {
            System.out.println("Error in finding your query: " + e.getMessage());
        }
    }

    // displays all categories
    public static void displayCategories(Connection connection) {

        // eats leftover
        userInput.nextLine();

        try (
                // creates prepared statement and query for category table
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            CategoryID
                            , CategoryName
                        FROM
                            categories
                        ORDER BY
                            CategoryID
                        """);

                // executes query and gets the result set
                ResultSet results = preparedStatement.executeQuery()
        ) {
            // displays the result set
            while (results.next()) {
                System.out.printf("""
                                
                                Category ID: %s
                                Category Name: %s
                                """,
                        results.getInt("CategoryID"),
                        results.getString("CategoryName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // asks user if they want to search by category id
        System.out.print("\nDo you want to display products by category? (Yes/No) ");
        String choice = userInput.nextLine().toLowerCase().trim();

        // if yes then calls method, if no breaks to home screen
        while (true) {
            if (choice.equals("yes") || choice.equals("y")) {
                displayProductByCategory(connection);
                break;
            } else if (choice.equals("no") || choice.equals("n")) {
                break;
            } else {
                System.out.println("Please enter either (Yes/No)!");
                break;
            }
        }
    }

    // displays products depending on category ID number
    public static void displayProductByCategory(Connection connection) {

        // asks user for id
        System.out.print("Enter Category ID to search: ");
        int search = userInput.nextInt();

        try (
                // creates query with a parameter to for matching category id
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            P.ProductID
                            , P.ProductName
                            , P.UnitPrice
                            , P.UnitsInStock
                            , C.CategoryID
                        FROM
                            products P
                            JOIN categories C ON (P.CategoryID = C.CategoryID)
                        WHERE
                            C.CategoryID = ?
                        """);
        ) {
            // replaces first parameter with user input
            preparedStatement.setInt(1, search);

            // executes query and gets result set
            try (ResultSet results = preparedStatement.executeQuery()) {
                // table header
                System.out.print("""
                        
                        ID      Name                               Price   Stock   Category ID
                        ---     --------------------------         ------  ------  -----------
                        """);

                // displays the query executed
                while (results.next()) {
                    // left aligned by however many spaces to align with table header above
                    System.out.printf("%-7d %-34s %-7.2f %-8d %-8d\n",
                            results.getInt("ProductID"),
                            results.getString("ProductName"),
                            results.getDouble("UnitPrice"),
                            results.getInt("UnitsInStock"),
                            results.getInt("CategoryID"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
