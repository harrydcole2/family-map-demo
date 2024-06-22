package service;

import dataAccess.*;
import model.Authtoken;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.ClearResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearTest extends ServiceTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        connectToDatabase();

        eDao.insert(new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016));
        pDao.insert(new Person("cindy456", "gale", "cindy",
                "russo", "f", null, null, "steve123"));
        uDao.insert(new User("newUser!", "yayayay", "excitedTester27@mailinator", "non-playable",
                "character", "f", "user456"));
        aDao.insert(new Authtoken("token", "gale"));

        db.closeConnection(true);
    }

    @Test
    public void clearPass() throws DataAccessException {
        ClearService clearing = new ClearService();
        ClearResult result = clearing.clear();

        assertEquals(true, result.isSuccess());
        assertEquals("Clear succeeded.", result.getMessage());

        connectToDatabase();

        assertNull(eDao.findByEventID("Biking_123A"));
        assertNull(pDao.findByPersonID("cindy456"));
        assertNull(uDao.findByUsername("newUser!"));
        assertNull(aDao.findUsername("token"));

        db.closeConnection(false);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }
}
