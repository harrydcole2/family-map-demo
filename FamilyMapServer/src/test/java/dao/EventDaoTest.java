package dao;

import dataAccess.DataAccessException;
import dataAccess.Database;
import dataAccess.EventDao;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class EventDaoTest {
    private Database db;
    private Event eventA;
    private Event eventB;
    private Event eventC;
    private EventDao eDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        eventA = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        eventB = new Event("Climbing_456A", "Gale", "Gale123A",
                45.1f, 100.5f, "South Korea", "Seoul",
                "Climbing_Around", 2013);
        eventC = new Event("Swimming_789A", "Jane", "Jane123A",
                35.4f, 140.2f, "Japan", "Hiroshima",
                "Swimming_Around", 2015);

        Connection conn = db.getConnection();
        eDao = new EventDao(conn);
        eDao.clear();
    }

    @AfterEach
    public void tearDown() {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        eDao.insert(eventA);

        Event compareTest = eDao.findByEventID(eventA.getEventID());

        assertNotNull(compareTest);
        assertEquals(eventA, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        eDao.insert(eventA); //failure by double insert

        assertThrows(DataAccessException.class, () -> eDao.insert(eventA));
    }

    @Test
    public void findPass() throws DataAccessException {
        eDao.insert(eventA);

        Event actual = eDao.findByEventID(eventA.getEventID());

        assertEquals(eventA, actual);
    }

    @Test
    public void findFail() throws DataAccessException {
        eDao.insert(eventA);

        Event actual = eDao.findByEventID("failure");

        assertEquals(null, actual);
    }

    @Test
    public void findManyPass() throws DataAccessException {
        eDao.insert(eventA);
        eDao.insert(eventB);
        eDao.insert(eventC);

        ArrayList<Event> actual = eDao.findManyByUsername("Gale");

        ArrayList<Event> expected = new ArrayList<>();
        expected.add(eventA);
        expected.add(eventB);

        assertEquals(eventA, expected.get(0));
        assertEquals(eventB, expected.get(1));
        assertEquals(expected, actual);
    }

    @Test
    public void findManyFail() throws DataAccessException {
        eDao.insert(eventA);
        eDao.insert(eventB);
        eDao.insert(eventC);

        ArrayList<Event> actual = eDao.findManyByUsername("Gail");

        assertEquals(0, actual.size());
    }

    @Test
    public void clearPass() throws DataAccessException {
        eDao.insert(eventA);
        eDao.insert(eventB);
        eDao.insert(eventC);

        eDao.clear();

        assertEquals(null, eDao.findByEventID(eventA.getEventID()));
        assertEquals(null, eDao.findByEventID(eventB.getEventID()));
        assertEquals(null, eDao.findByEventID(eventC.getEventID()));

    }

    @Test
    public void clearByUsernamePass() throws DataAccessException {
        eDao.insert(eventA);
        eDao.insert(eventB);
        eDao.insert(eventC);

        eDao.clearByUsername("Gale");

        assertEquals(null, eDao.findByEventID(eventA.getEventID()));
        assertEquals(null, eDao.findByEventID(eventB.getEventID()));
        assertEquals(eventC, eDao.findByEventID(eventC.getEventID()));
    }

    @Test
    public void clearByUsernameFail() throws DataAccessException {
        eDao.insert(eventA);
        eDao.insert(eventB);
        eDao.insert(eventC);

        eDao.clearByUsername("Gail");

        assertEquals(eventA, eDao.findByEventID(eventA.getEventID()));
        assertEquals(eventB, eDao.findByEventID(eventB.getEventID()));
        assertEquals(eventC, eDao.findByEventID(eventC.getEventID()));
    }
}
