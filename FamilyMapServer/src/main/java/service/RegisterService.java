package service;

import dataAccess.*;
import model.Authtoken;
import model.User;
import request.RegisterRequest;
import result.RegisterResult;

import java.sql.Connection;
import java.util.UUID;

/**
 * Service for /user/register request
 */
public class RegisterService extends FakeDataService {
    /**
     * Creates new user row, generates 4 generations of ancestor data, logs user in, and returns auth token
     *
     * @param req RegisterRequest which has username, password, email, firstName, lastName, gender members
     * @return RegisterResult which has authtoken, username, personID, success, and message members
     */
    public RegisterResult register(RegisterRequest req) {

        Database connManager = new Database();
        try {
            Connection connection = connManager.openConnection();

            UserDao userDao = new UserDao(connection);
            String newUserPersonID = UUID.randomUUID().toString().substring(0,5);
            User newUser = new User(req.getUsername(), req.getPassword(), req.getEmail(), req.getFirstName(), req.getLastName(),
                    req.getGender(), newUserPersonID);
            userDao.insert(newUser);

            generateRootPerson(connection, newUser, 4);

            AuthtokenDao authtokenDao = new AuthtokenDao(connection);
            String authtoken = UUID.randomUUID().toString();
            authtokenDao.insert(new Authtoken(authtoken, req.getUsername()));

            connManager.closeConnection(true);

            RegisterResult positiveResponse = new RegisterResult(null, true, authtoken, req.getUsername(), newUserPersonID);
            return positiveResponse;
        } catch (DataAccessException e) {
            connManager.closeConnection(false);

            RegisterResult negativeResponse = new RegisterResult("Error: " + e.getMessage(), false, null, null, null);
            e.printStackTrace();

            return negativeResponse;
        }
    }
}