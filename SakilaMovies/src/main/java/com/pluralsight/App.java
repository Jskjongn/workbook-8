package com.pluralsight;

import com.pluralsight.dao.ActorDao;
import com.pluralsight.dao.FilmDao;
import com.pluralsight.models.Actor;
import com.pluralsight.models.Film;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);
    static BasicDataSource dataSource = null;
    static ActorDao actorDataManager = null;
    static FilmDao filmDataManager = null;

    public static void main(String[] args) {

        // creates datasource
        openDataSource();

        // creates data managers
        actorDataManager = new ActorDao(dataSource);
        filmDataManager = new FilmDao(dataSource);

        System.out.println("\nWelcome to Sakila Movies!");

        // home screen
        boolean homeScreen = true;
        while (homeScreen) {

            System.out.print("""
                
                What would you like to do?
                1) Search actor by last name
                2) Search film by actor first and last name
                0) Exit
                """);
            System.out.print("Select an option: ");
            // stores user input for switch statement
            int option = userInput.nextInt();
            userInput.nextLine();

            switch (option) {
                case 1:
                    displayActorName();
                    break;
                case 2:
                    displayFilm();
                    break;
                case 0:
                    closeDataSource();
                    homeScreen = false;
                    break;
                default:
                    System.out.println("Please enter either 1-2 or 0 to exit!");
            }
        }

    }

    // displays names of actors with a certain last name
    private static void displayActorName() {

        // prompts for last name
        System.out.print("\nEnter last name of actor: ");
        String lastName = userInput.nextLine().trim();

        // takes the list from data manager and stores in new list
        List<Actor> actors = actorDataManager.getActorByLastName(lastName);

        // loops through each actor in list to display actor details
        for (Actor actor : actors) {
            System.out.printf("""
                    
                    Actor ID: %d
                    First Name: %s
                    Last Name: %s
                    """, actor.getActorID(), actor.getFirstName(), actor.getLastName());
        }
    }

    // searches movie title by first and last name of actor
    private static void displayFilm() {

        // prompts for first and last name
        System.out.print("\nEnter first name of actor: ");
        String firstName = userInput.nextLine().trim();
        System.out.print("Enter last name of actor: ");
        String lastName = userInput.nextLine().trim();

        // takes the list from data manager and stores in new list
        List<Film> films = filmDataManager.getFilmByActorName(firstName, lastName);

        // loops through each film in list to display film details
        for (Film film : films) {
            System.out.printf("""
                    
                    Film ID: %d
                    Title: %s
                    Description: %s
                    Release Year: %d
                    Length: %d
                    Actor: %s
                    """, film.getFilmID(), film.getTitle(), film.getDescription(), film.getReleaseYear(), film.getLength(), film.getName());
        }
    }

    // creates datasource and connects to database
    public static void openDataSource() {
        // prompts user for password to database
        System.out.print("Username: root\nPassword: ");
        String password = userInput.nextLine().trim();

        // creates the datasource
        dataSource = new BasicDataSource();

        // sets url with username and password
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
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
