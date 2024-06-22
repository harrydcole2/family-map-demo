package service;

import dataAccess.*;
import result.ClearResult;

import java.sql.Connection;

/**
 * Service for /clear request
 */
public class ClearService {
    /**
     * Deletes all data in user, authtoken, person, and event tables
     *
     * @return a ClearResult object that contains message and success members
     */
    public ClearResult clear() {
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

            connManager.closeConnection(true);

            ClearResult positiveResponse = new ClearResult("Clear succeeded.", true);
            return positiveResponse;
        }
        catch(DataAccessException e) {
            connManager.closeConnection(false);

            ClearResult negativeResponse = new ClearResult("Error: Internal server error", false);

            return negativeResponse;
        }
    }
}
