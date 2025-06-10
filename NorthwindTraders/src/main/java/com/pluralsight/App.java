package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);
    //static Connection connection = null;

    public static void main(String[] args) {

        // establishes the connection for database
        try ( Connection connection = getConnection()) {

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
                        System.out.println("Please choose either 1-2 or 0 to exit!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            System.out.println("Error with opening connection: " + e.getMessage());
            return null;
        }
    }

    // displays all products from products table
    public static void displayProducts(Connection connection) {

        try (
                // creates prepared statement with the connection and writes the query
                PreparedStatement preparedStatement1 = connection.prepareStatement(
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
                ResultSet resultSet1 = preparedStatement1.executeQuery();

        ) {
            // table header
            System.out.print("""
                    
                    ID      Name                               Price   Stock
                    ---     --------------------------         ------  ------
                    """);

            // displays the query executed
            while (resultSet1.next()) {
                // left aligned by however many spaces to align with table header above
                System.out.printf("%-7d %-34s %-7.2f %-6d\n",
                        resultSet1.getInt("ProductID"),
                        resultSet1.getString("ProductName"),
                        resultSet1.getDouble("UnitPrice"),
                        resultSet1.getInt("UnitsInStock"));
            }

        } catch (SQLException e) {
            System.out.println("Error in finding your query: " + e.getMessage());
        }
    }

    // displays all customers
    public static void displayCustomers(Connection connection) {

        try (
                // creates prepared statement with the connection and writes the query
                PreparedStatement preparedStatement1 = connection.prepareStatement(
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
                ResultSet resultSet1 = preparedStatement1.executeQuery();
        ) {
            // displays query results from column names
            while (resultSet1.next()) {
                System.out.printf("""
                                
                                Contact Name: %s
                                Company Name: %s
                                City:         %s
                                Country:      %s
                                Phone:        %s
                                """,
                        resultSet1.getString("ContactName"),
                        resultSet1.getString("CompanyName"),
                        resultSet1.getString("City"),
                        resultSet1.getString("Country"),
                        resultSet1.getString("Phone"));
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
                PreparedStatement preparedStatement1 = connection.prepareStatement("""
                        SELECT
                            CategoryID
                            , CategoryName
                        FROM
                            categories
                        ORDER BY
                            CategoryID
                        """);

                // executes query and gets the result set
                ResultSet results = preparedStatement1.executeQuery()
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
                PreparedStatement preparedStatement2 = connection.prepareStatement("""
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
            preparedStatement2.setInt(1, search);

            // executes query and gets result set
            try (ResultSet results2 = preparedStatement2.executeQuery()) {
                // table header
                System.out.print("""
                        
                        ID      Name                               Price   Stock   Category ID
                        ---     --------------------------         ------  ------  -----------
                        """);

                // displays the query executed
                while (results2.next()) {
                    // left aligned by however many spaces to align with table header above
                    System.out.printf("%-7d %-34s %-7.2f %-6d %-8d\n",
                            results2.getInt("ProductID"),
                            results2.getString("ProductName"),
                            results2.getDouble("UnitPrice"),
                            results2.getInt("UnitsInStock"),
                            results2.getInt("CategoryID"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
