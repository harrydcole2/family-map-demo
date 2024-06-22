package service;

import dataAccess.*;
import model.User;
import request.FillRequest;
import result.FillResult;

import java.sql.Connection;

/**
 * Service for /fill/[username]/{generations}
 */
public class FillService extends FakeDataService {

    /**
     * populate database with data up to generations given
     *
     * @param req FillRequest that has username and generations members
     * @return FillResult with message and success members to communicate how it went
     */
    public FillResult fill(FillRequest req) {
        Database connManager = new Database();
        try {
            Connection connection = connManager.openConnection();

            UserDao userDao = new UserDao(connection);
            User currUser = userDao.findByUsername(req.getUsername());

            if(currUser == null) {
                throw new DataAccessException("Invalid username or generations parameter");
            }

            PersonDao personDao = new PersonDao(connection);
            personDao.clearByUsername(req.getUsername());

            EventDao eventDao = new EventDao(connection);
            eventDao.clearByUsername(req.getUsername());

            int generations = req.getGenerations();
            if(req.getGenerations() < 0) {
                generations = 4;
            }
            generateRootPerson(connection, currUser, generations);

            connManager.closeConnection(true);

            int X = ((int)Math.pow(2,generations+1)) - 1;
            int Y = X*4 - 3;
            FillResult positiveResponse = new FillResult("Successfully added " + X + " persons and " + Y + " events to the database", true);
            return positiveResponse;
        }
        catch(DataAccessException e) {
            connManager.closeConnection(false);

            FillResult negativeResponse = new FillResult("Error: " + e.getMessage(), false);
            e.printStackTrace();

            return negativeResponse;
        }
    }
}
