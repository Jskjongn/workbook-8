package com.pluralsight.dao;

import com.pluralsight.models.Film;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmDao {

    // property
    private BasicDataSource filmDataSource;

    // constructor
    public FilmDao(BasicDataSource filmDataSource) {
        this.filmDataSource = filmDataSource;
    }

    // gets list of films by actor first and last name
    public List<Film> getFilmByActorName(String firstName, String lastName) {

        // creates list of films
        List<Film> films = new ArrayList<>();

        try (
                // creates connection to datasource which is connected to the database
                Connection connection = filmDataSource.getConnection();

                // connects statement to database and creates query
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            F.film_id
                            , F.title
                            , F.description
                            , F.release_year
                            , F.length
                            , CONCAT(A.first_name, ' ', A.last_name) AS 'Full Name'
                        FROM
                            film F
                            JOIN film_actor FA ON (F.film_id = FA.film_id)
                            JOIN actor A ON (FA.actor_id = A.actor_id)
                        WHERE
                            A.first_name LIKE ? AND A.last_name LIKE ?
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

                    // gets results from columns if while is still true
                    do {
                        int filmID = resultSet.getInt("film_id");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        int releaseYear = resultSet.getInt("release_year");
                        int length = resultSet.getInt("length");
                        String fullName = resultSet.getString("Full Name");

                        // gets results and creates new film object
                        Film film = new Film(filmID, title, description, releaseYear, length, fullName);

                        // adds new film object into films list
                        films.add(film);

                    } while (resultSet.next());
                    // if no match from first and last name then displays no match
                } else {
                    System.out.println("\nNo matches of movies with: " + firstName + " " + lastName);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // returns list of films
        return films;
    }
}
