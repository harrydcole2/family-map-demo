package service;

import dataAccess.DataAccessException;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import result.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends ServiceTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();

        uDao.insert(new User("newUser!", "yayayay", "excitedTester27@mailinator", "non-playable",
                "character", "f", "user456"));

        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }

    @Test
    public void loginPass() {
        LoginService loggingIn = new LoginService();

        LoginRequest req = new LoginRequest();
        req.setUsername("newUser!");
        req.setPassword("yayayay");

        LoginResult result = loggingIn.login(req);

        assertTrue(result.isSuccess());
    }

    @Test
    public void loginFail() {
        LoginService loggingIn = new LoginService();

        LoginRequest req = new LoginRequest();
        req.setUsername("oof!");
        req.setPassword("wrong");

        LoginResult result = loggingIn.login(req);

        assertFalse(result.isSuccess());
    }
}
