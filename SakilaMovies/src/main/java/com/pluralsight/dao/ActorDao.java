package com.pluralsight.dao;

import com.pluralsight.models.Actor;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorDao {

    // property
    private BasicDataSource actorDataSource;

    // constructor
    public ActorDao(BasicDataSource actorDataSource) {
        this.actorDataSource = actorDataSource;
    }

    // gets a list of actors by their last name
    public List<Actor> getActorByLastName(String lastName) {

        // creates a list of actors with a certain last name
        List<Actor> actors = new ArrayList<>();

        try (
                // creates connection to datasource which is connected to the database
                Connection connection = actorDataSource.getConnection();
                // connects statement to database and creates query
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            actor_id
                            , first_name
                            , last_name
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

                // displays values from column names
                while (resultSet.next()) {
                    // gets results and stores them
                    int actorID = resultSet.getInt("actor_id");
                    String actorFirstName = resultSet.getString("first_name");
                    String actorLastName = resultSet.getString("last_name");

                    // add the results from each column into a new actor object
                    Actor actor = new Actor(actorID, actorFirstName, actorLastName);

                    // adds new actor object to actors list
                    actors.add(actor);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // returns list of actors
        return actors;
    }
}
