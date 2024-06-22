package dao;

import dataAccess.DataAccessException;
import dataAccess.Database;
import dataAccess.PersonDao;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonDaoTest {
    private Database db;
    private Person personA;
    private Person personB;
    private Person personC;
    private Person personD;
    private PersonDao pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        personA = new Person("steve123", "gale", "steve", "russo", "m",
                null, null, null);
        personB = new Person("cindy456", "gale", "cindy", "russo", "f",
                null, null, null);
        personC = new Person("gale789", "gale", "gale", "russo", "m",
                "steve123", "cindy456", null);

        personD = new Person("da;jaiye", "redHerring", "rEd", "HeRriNg", "?",
                null, null, null);

        Connection conn = db.getConnection();
        pDao = new PersonDao(conn);
        pDao.clear();
    }

    @AfterEach
    public void tearDown() {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        pDao.insert(personA);

        Person compareTest = pDao.findByPersonID(personA.getPersonID());

        assertNotNull(compareTest);
        assertEquals(personA, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        pDao.insert(personA); //failure by double insert

        assertThrows(DataAccessException.class, () -> pDao.insert(personA));
    }

    @Test
    public void findPass() throws DataAccessException {
        pDao.insert(personA);

        Person actual = pDao.findByPersonID(personA.getPersonID());

        assertEquals(personA, actual);
    }

    @Test
    public void findFail() throws DataAccessException {
        pDao.insert(personA);

        Person actual = pDao.findByPersonID("failure");

        assertEquals(null, actual);
    }

    @Test
    public void findManyPass() throws DataAccessException {
        pDao.insert(personA);
        pDao.insert(personB);
        pDao.insert(personC);
        pDao.insert(personD);

        ArrayList<Person> actual = pDao.findManyByUsername("gale");

        ArrayList<Person> expected = new ArrayList<>();
        expected.add(personA);
        expected.add(personB);
        expected.add(personC);

        assertEquals(personA, expected.get(0));
        assertEquals(personB, expected.get(1));
        assertEquals(personC, expected.get(2));
        assertEquals(expected, actual);
    }

    @Test
    public void findManyFail() throws DataAccessException {
        pDao.insert(personA);
        pDao.insert(personB);
        pDao.insert(personC);
        pDao.insert(personD);

        ArrayList<Person> actual = pDao.findManyByUsername("Gail");

        assertEquals(0, actual.size());
    }

    @Test
    public void clearPass() throws DataAccessException {
        pDao.insert(personA);
        pDao.insert(personB);
        pDao.insert(personC);
        pDao.insert(personD);

        pDao.clear();

        assertEquals(null, pDao.findByPersonID(personA.getPersonID()));
        assertEquals(null, pDao.findByPersonID(personB.getPersonID()));
        assertEquals(null, pDao.findByPersonID(personC.getPersonID()));
        assertEquals(null, pDao.findByPersonID(personD.getPersonID()));
    }

    @Test
    public void clearByUsernamePass() throws DataAccessException {
        pDao.insert(personA);
        pDao.insert(personB);
        pDao.insert(personC);
        pDao.insert(personD);

        pDao.clearByUsername("gale");

        assertEquals(null, pDao.findByPersonID(personA.getPersonID()));
        assertEquals(null, pDao.findByPersonID(personB.getPersonID()));
        assertEquals(null, pDao.findByPersonID(personC.getPersonID()));
        assertEquals(personD, pDao.findByPersonID(personD.getPersonID()));
    }

    @Test
    public void clearByUsernameFail() throws DataAccessException {
        pDao.insert(personA);
        pDao.insert(personB);
        pDao.insert(personC);
        pDao.insert(personD);

        pDao.clearByUsername("gail");

        assertEquals(personA, pDao.findByPersonID(personA.getPersonID()));
        assertEquals(personB, pDao.findByPersonID(personB.getPersonID()));
        assertEquals(personC, pDao.findByPersonID(personC.getPersonID()));
        assertEquals(personD, pDao.findByPersonID(personD.getPersonID()));
    }

    @Test
    public void updateSpouseIDsPass() throws DataAccessException {
        pDao.insert(personA);
        pDao.insert(personB);

        pDao.updateSpouseIDs(personA.getPersonID(), personB.getPersonID());

        assertEquals("cindy456", pDao.findByPersonID(personA.getPersonID()).getSpouseID());
        assertEquals("steve123", pDao.findByPersonID(personB.getPersonID()).getSpouseID());
    }
}
