package service;

import dataAccess.DataAccessException;
import model.Authtoken;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoadRequest;
import result.LoadResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoadTest extends ServiceTest{

    @BeforeEach
    public void setUp() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();

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

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }

    @Test
    public void loadPass() throws DataAccessException {
        LoadService loading = new LoadService();

        User[] users = {new User("hello", "a", "b", "c", "d", "f", "f"),
                new User("hello2", "a", "b", "c", "d", "e", "f") };

        Person[] persons = {new Person("1", "hello", "a","b", "m", "c", "d", "e")};

        Event[] events = {new Event("1","hello","1", (float) 4,(float) 5,"USA","Vancouver", "normal", 1900) };

        LoadRequest loadReq = new LoadRequest(users, persons, events);
        LoadResult result = loading.load(loadReq);


        assertTrue(result.isSuccess());
        connectToDatabase();

        assertNull(eDao.findByEventID("Biking_123A"));
        assertNull(pDao.findByPersonID("cindy456"));
        assertNull(uDao.findByUsername("newUser!"));
        assertNull(aDao.findUsername("token"));

        assertEquals(events[0], eDao.findByEventID("1"));
        assertEquals(persons[0], pDao.findByPersonID("1"));
        assertEquals(users[0], uDao.findByUsername("hello"));

        db.closeConnection(true);
    }

    @Test
    public void loadNothingPass() throws DataAccessException {
        LoadService loading = new LoadService();

        User[] users = {};
        Person[] persons = {};
        Event[] events = {};

        LoadRequest loadReq = new LoadRequest(users, persons, events);
        LoadResult result = loading.load(loadReq);

        assertTrue(result.isSuccess());
        connectToDatabase();

        assertNull(eDao.findByEventID("Biking_123A"));
        assertNull(pDao.findByPersonID("cindy456"));
        assertNull(uDao.findByUsername("newUser!"));
        assertNull(aDao.findUsername("token"));

        db.closeConnection(true);
    }
}
