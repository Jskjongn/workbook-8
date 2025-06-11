package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {

        // creates the connection to pass into methods
        Connection connection = getConnection();

        System.out.println("\nWelcome to Sakila Movies!");

        // home screen
        boolean homeScreen = true;
        while (homeScreen) {

            System.out.print("""
                
                What would you like to do?
                1) Search by actor last name
                2) Search by actor first and last name
                0) Exit
                """);
            System.out.print("Select an option: ");
            // stores user input for switch statement
            int option = userInput.nextInt();
            userInput.nextLine();

            switch (option) {
                case 1:
                    displayLastName(connection);
                    break;
                case 2:
                    displayFirstAndLastName(connection);
                    break;
                case 0:
                    homeScreen = false;
                    break;
                default:
                    System.out.println("Please enter either 1-2 or 0 to exit!");
            }
        }
    }

    // creates connection to database
    public static Connection getConnection() {

        // prompts user for password to database
        System.out.print("Username: root\nPassword: ");
        String password = userInput.nextLine().trim();

        // creates the datasource and sets the url with user and password
        try (BasicDataSource dataSource = new BasicDataSource()) {

            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
            dataSource.setUsername("root");
            dataSource.setPassword(password);

            // returns the connection
            return dataSource.getConnection();

        } catch (SQLException e) {
            System.out.println("Error with opening connection: " + e.getMessage());
            return null;
        }
    }

    // displays names of actors with a certain last name
    private static void displayLastName(Connection connection) {

        // prompts for last name
        System.out.print("\nEnter last name of actor: ");
        String lastName = userInput.nextLine().trim();

        try (
                // connects statement to database and creates query
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            actor_id
                            , CONCAT(first_name, ' ', last_name) AS 'Full Name'
                        FROM
                            actor
                        WHERE
                            last_name LIKE ?
                        """)
        ) {
            // sets the parameter in the query to use user input
            preparedStatement.setString(1, "%" + lastName + "%");

            // executes the query and puts results into result set
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // table header
                System.out.print("""
                        
                        ID      Actor Name
                        ---     --------------------------
                        """);
                // displays values from column names
                while (resultSet.next()) {
                    System.out.printf("%-7d %s\n",
                            resultSet.getInt("actor_id"),
                            resultSet.getString("Full Name"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // displays movie titles by first and last name of actor
    private static void displayFirstAndLastName(Connection connection) {

        // prompts for first and last name
        System.out.print("\nEnter first name of actor: ");
        String firstName = userInput.nextLine().trim();
        System.out.print("Enter last name of actor: ");
        String lastName = userInput.nextLine().trim();

        try (
                // connects statement to database and creates query
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            CONCAT(A.first_name, ' ', A.last_name) AS 'Full Name'
                            , F.title
                        FROM
                            actor A
                            JOIN film_actor FM ON (A.actor_id = FM.actor_id)
                            JOIN film F ON (FM.film_id = F.film_id)
                        WHERE
                            first_name LIKE ? AND last_name LIKE ?
                        """)
        ) {
            // sets the two parameters of first and last name
            preparedStatement.setString(1, "%" + firstName + "%");
            preparedStatement.setString(2, "%" + lastName + "%");

            // puts the executed query into the result sets
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // if there is a match of first and last name then displays movie titles
                if (resultSet.next()) {
                    System.out.println("\nMatches of movies with: " + firstName + " " + lastName);

                    // table header
                    System.out.print("""
                        
                        Actor Name                      Movie Title
                        --------------------------      ----------------------------
                        """);

                    // displays values from columns if while is still true
                    do {
                        System.out.printf("%-31s %s\n",
                                resultSet.getString("Full Name"),
                                resultSet.getString("title"));
                    } while (resultSet.next());
                    // if no match from first and last name then displays no match
                } else {
                    System.out.println("No matches of movies with: " + firstName + " " + lastName);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
