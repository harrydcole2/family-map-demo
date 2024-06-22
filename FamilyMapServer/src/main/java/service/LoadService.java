package service;

import dataAccess.*;
import model.*;
import request.LoadRequest;
import result.LoadResult;

import java.sql.Connection;

/**
 * Service for /load
 */
public class LoadService {
    /**
     * Clears all the data and loads in given person, user, and event data
     *
     * @param req LoadRequest has users, persons, and events array members
     * @return LoadResult obj with message, success members
     */
    public LoadResult load(LoadRequest req) {
        Database connManager = new Database();
        try {
            Connection connection = connManager.openConnection();

            UserDao userDao = new UserDao(connection);
            userDao.clear();

            PersonDao personDao = new PersonDao(connection);
            personDao.clear();

            EventDao eventDao = new EventDao(connection);
            eventDao.clear();

            AuthtokenDao authtokenDao = new AuthtokenDao(connection);
            authtokenDao.clear();

            User[] users = req.getUsers();
            for(int i = 0; i < users.length; i++) {
                userDao.insert(users[i]);
            }

            Person[] persons = req.getPersons();
            for(int i = 0; i < persons.length; i++) {
                personDao.insert(persons[i]);
            }

            Event[] events = req.getEvents();
            for(int i = 0; i < events.length; i++) {
                eventDao.insert(events[i]);
            }

            connManager.closeConnection(true);

            LoadResult positiveResponse = new LoadResult("Successfully added " + req.getUsers().length + " users, "
                    + req.getPersons().length + " persons, and " + req.getEvents().length + " events to the database.", true);
            return positiveResponse;
        }
        catch(DataAccessException e) {
            connManager.closeConnection(false);

            LoadResult negativeResponse = new LoadResult("Error: " + e.getMessage(), false);
            e.printStackTrace();

            return negativeResponse;
        }
    }
}
