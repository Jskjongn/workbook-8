package com.pluralsight;

import com.pluralsight.dao.ShippersDao;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);
    static BasicDataSource dataSource = null;
    static ShippersDao shippersDataManager = null;

    public static void main(String[] args) {

        // creates datasource
        openDataSource();

        // creates data manager
        shippersDataManager = new ShippersDao(dataSource);

        // ----------------------------------------------------

        // displays all shippers
        shippersDataManager.displayShippers();

        // adds new shipper
        addNewShipper();

        // displays all shippers
        shippersDataManager.displayShippers();

        // updates shipper phone
        updateShipperPhone();

        // displays all shippers
        shippersDataManager.displayShippers();

        // deletes shipper
        deleteShipper();

        // displays all shippers
        shippersDataManager.displayShippers();

        // ----------------------------------------------------

        // closes the datasource
        closeDataSource();
    }

    // adds a new shipper
    public static void addNewShipper() {

        // user enters shipper details and stores it
        System.out.println("\nEnter new shipper details");
        System.out.print("Company Name: ");
        String companyName = userInput.nextLine();
        System.out.print("Phone: ");
        String phone = userInput.nextLine();

        // takes user input and inserts into query to add
        shippersDataManager.addShipper(companyName, phone);
    }

    // updates shippers phone
    public static void updateShipperPhone() {

        // user enter shipper id
        System.out.print("\nEnter shipper ID of which you want to update: ");
        int shipperID = userInput.nextInt();
        // eats leftover
        userInput.nextLine();
        // user enters new phone number
        System.out.print("Enter new phone number: ");
        String phone = userInput.nextLine();

        // takes user input and uses it in a query to update
        shippersDataManager.updateShipperPhone(shipperID, phone);
    }

    // deletes a shipper
    public static void deleteShipper() {

        // user enters shipper id
        System.out.print("\nEnter shipper ID of which you want to delete: ");
        int shipperID = userInput.nextInt();
        // eats leftover
        userInput.nextLine();

        // takes user input and deletes shipper by id
        shippersDataManager.deleteShipper(shipperID);
    }

    // creates datasource and connects to database
    public static void openDataSource() {
        // prompts user for password to database
        System.out.print("Username: root\nPassword: ");
        String password = userInput.nextLine().trim();

        // creates the datasource
        dataSource = new BasicDataSource();

        // sets url with username and password
        dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
        dataSource.setUsername("root");
        dataSource.setPassword(password);
    }

    // closes datasource
    public static void closeDataSource() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
