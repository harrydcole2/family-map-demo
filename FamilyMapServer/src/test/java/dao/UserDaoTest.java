package dao;

import dataAccess.DataAccessException;
import dataAccess.Database;
import dataAccess.UserDao;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDaoTest {
    private Database db;
    private User userA;
    private User userB;
    private UserDao uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        userA = new User("testing123", "password", "email@email.com", "tester",
                "mcgenius", "m", "mcg123");
        userB = new User("newUser!", "yayayay", "excitedTester27@mailinator", "non-playable",
                "character", "f", "user456");

        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        uDao.clear();
    }

    @AfterEach
    public void tearDown() {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        uDao.insert(userA);

        User compareTest = uDao.findByUsername(userA.getUsername());

        assertNotNull(compareTest);
        assertEquals(userA, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        uDao.insert(userA); //failure by double insert

        assertThrows(DataAccessException.class, () -> uDao.insert(userA));
    }

    @Test
    public void findPass() throws DataAccessException {
        uDao.insert(userA);

        User actual = uDao.findByUsername(userA.getUsername());

        assertEquals(userA, actual);
    }

    @Test
    public void findFail() throws DataAccessException {
        uDao.insert(userA);

        User actual = uDao.findByUsername("failure");

        assertEquals(null, actual);
    }

    @Test
    public void clearPass() throws DataAccessException {
        uDao.insert(userA);
        uDao.insert(userB);

        uDao.clear();

        assertEquals(null, uDao.findByUsername(userA.getUsername()));
        assertEquals(null, uDao.findByUsername(userB.getUsername()));
    }

    @Test
    public void validatePass() throws DataAccessException {
        uDao.insert(userA);

        assertTrue(uDao.validate("testing123", "password"));
    }

    public void validateFail() throws DataAccessException {
        uDao.insert(userA);

        assertFalse(uDao.validate("wrong", "wronger"));
    }
}
