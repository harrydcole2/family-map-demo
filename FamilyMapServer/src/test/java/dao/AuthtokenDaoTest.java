package dao;

import dataAccess.AuthtokenDao;
import dataAccess.DataAccessException;
import dataAccess.Database;
import model.Authtoken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthtokenDaoTest {
    private Database db;
    private Authtoken authA;
    private Authtoken authB;
    private AuthtokenDao aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        authA = new Authtoken("abcde", "Frank");
        authB = new Authtoken("12345", "Carl");

        Connection conn = db.getConnection();
        aDao = new AuthtokenDao(conn);
        aDao.clear();
    }

    @AfterEach
    public void tearDown() {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        aDao.insert(authA);

        String compareTest = aDao.findUsername("abcde");

        assertNotNull(compareTest);
        assertEquals("Frank", compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        aDao.insert(authA); //failure by double insert

        assertThrows(DataAccessException.class, () -> aDao.insert(authA));
    }

    @Test
    public void findPass() throws DataAccessException {
        aDao.insert(authA);

        String actual = aDao.findUsername(authA.getAuthtoken());

        assertEquals(authA.getUsername(), actual);
    }

    @Test
    public void findFail() throws DataAccessException {
        aDao.insert(authA);

        String actual = aDao.findUsername("Failure");

        assertEquals(null, actual);
    }

    @Test
    public void clearPass() throws DataAccessException {
        aDao.insert(authA);
        aDao.insert(authB);

        aDao.clear();

        assertEquals(null, aDao.findUsername(authA.getUsername()));
        assertEquals(null, aDao.findUsername(authB.getUsername()));
    }
}
