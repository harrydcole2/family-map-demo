package service;

import dataAccess.*;
import model.Authtoken;
import model.User;
import request.LoginRequest;
import result.LoginResult;

import java.sql.Connection;
import java.util.UUID;

/**
 * Service for /user/login request
 */
public class LoginService {
    /**
     * Logs the user in and returns authtoken
     *
     * @param req LoginRequest that has username and password members
     * @return LoginResult that has authtoken, username, personID, message, success members
     */
    public LoginResult login(LoginRequest req) {
        Database connManager = new Database();
        try {
            Connection connection = connManager.openConnection();

            UserDao userDao = new UserDao(connection);
            if(!userDao.validate(req.getUsername(), req.getPassword())) {
                throw new DataAccessException("Request property missing or has invalid value");
            }

            AuthtokenDao authtokenDao = new AuthtokenDao(connection);
            String authtoken = UUID.randomUUID().toString();
            authtokenDao.insert(new Authtoken(authtoken, req.getUsername()));

            User user = userDao.findByUsername(req.getUsername());
            String userPersonID = user.getPersonID();

            connManager.closeConnection(true);

            LoginResult positiveResponse = new LoginResult(null, true, authtoken, req.getUsername(), userPersonID);
            return positiveResponse;
        }
        catch(DataAccessException e) {
            connManager.closeConnection(false);

            LoginResult negativeResponse = new LoginResult("Error: Internal server error", false, null, null, null);
            e.printStackTrace();

            return negativeResponse;
        }
    }
}
