package com.pluralsight.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

public class ShippersDao {

    // property
    private BasicDataSource shippersDataSource;

    // constructor
    public ShippersDao(BasicDataSource shippersDataSource) {
        this.shippersDataSource = shippersDataSource;
    }

    // displays all shippers
    public void displayShippers() {

        try (
                // creates connection using datasource
                Connection connection = shippersDataSource.getConnection();
                // connects and creates a query
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            ShipperID
                            , CompanyName
                            , Phone
                        FROM
                            shippers;
                        """);
                // executes query and gets its results
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            // table header
            System.out.print("""
                    
                    ID      Company Name         Phone
                    ---     -----------------    -----------------
                    """);
            // gets rows from columns and stores them
            while (resultSet.next()) {
                int shipperID = resultSet.getInt("ShipperID");
                String companyName = resultSet.getString("CompanyName");
                String phone = resultSet.getString("Phone");

                // displays results
                System.out.printf("%-7d %-20s %s\n", shipperID, companyName, phone);
            }

        } catch (SQLException e) {
            System.out.println("Error with displaying shippers: " + e.getMessage());
        }
    }

    // adds a new shipper
    public void addShipper(String companyName, String phone) {

        try (
                // creates connection
                Connection connection = shippersDataSource.getConnection();
                // connects and creates insert into query to write a new row
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        INSERT INTO `shippers` (`CompanyName`, `Phone`)
                        VALUES
                        (?, ?);
                        """, Statement.RETURN_GENERATED_KEYS);
        ) {
            // sets parameters in query
            preparedStatement.setString(1, companyName);
            preparedStatement.setString(2, phone);

            // updates and stores number of rows
            int rows = preparedStatement.executeUpdate();
            // displays number of rows updated
            System.out.println("\nNumber of rows updated: " + rows);

            // gets new primary key of new row
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                // displays new primary key
                while (keys.next()) {
                    System.out.printf("New shipper with ID: %d added", keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.print("Error with adding shipper: " + e.getMessage());
        }
    }

    // updates a shippers phone number
    public void updateShipperPhone(int shipperID, String phone) {

        try (
                // creates connection
                Connection connection = shippersDataSource.getConnection();
                // connects and creates query to update phone number using shipper id
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        UPDATE shippers
                        SET Phone = ?
                        WHERE ShipperID = ?;
                        """);
        ) {
            // sets parameters with user input
            preparedStatement.setString(1, phone);
            preparedStatement.setInt(2, shipperID);
            // gets number of rows updated
            int rows = preparedStatement.executeUpdate();
            // displays rows and new phone number
            System.out.println("\nNumber of rows updated: " + rows);
            System.out.println("Phone number updated to: " + phone);

        } catch (SQLException e) {
            System.out.print("Error with updating shipper phone: " + e.getMessage());
        }
    }

    // deletes a shipper
    public void deleteShipper(int shipperID) {

        try (
                // creates connection
                Connection connection = shippersDataSource.getConnection();
                // connects and creates query to delete a row
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        DELETE FROM shippers
                        WHERE ShipperID = ?;
                        """);
        ) {
            // sets parameter
            preparedStatement.setInt(1, shipperID);
            // gets number of rows
            int rows = preparedStatement.executeUpdate();
            // displays rows and shipper id that was deleted
            System.out.println("\nNumber of rows deleted: " + rows);
            System.out.printf("Shipper ID %d deleted\n", shipperID);

        } catch (SQLException e) {
            System.out.print("Error with deleting shipper: " + e.getMessage());
        }
    }
}
