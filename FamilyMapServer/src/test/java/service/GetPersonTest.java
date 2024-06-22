package service;

import dataAccess.DataAccessException;
import model.Authtoken;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.PersonRequest;
import result.PersonResult;

import static org.junit.jupiter.api.Assertions.*;

public class GetPersonTest extends ServiceTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();

        aDao.insert(new Authtoken("12345", "gale"));
        pDao.insert(new Person("steve123", "gale", "steve", "russo", "m",
                null, null, null));
        pDao.insert(new Person("cindy456", "gale", "cindy", "russo", "f",
                null, null, null));
        pDao.insert(new Person("da;jaiye", "redHerring", "rEd", "HeRriNg", "?",
                null, null, null));

        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }

    @Test
    public void getPersonPass() {
        GetPersonService getPersonService = new GetPersonService();

        PersonRequest req = new PersonRequest();
        req.setAuthtoken("12345");
        req.setPersonID("steve123");

        PersonResult result = getPersonService.getPerson(req);

        assertTrue(result.isSuccess());
        assertEquals("steve123", result.getData().get(0).getPersonID());
        assertEquals("gale", result.getData().get(0).getAssociatedUsername());
    }

    @Test
    public void getPersonFail() {
        GetPersonService getPersonService = new GetPersonService();

        PersonRequest req = new PersonRequest();
        req.setAuthtoken("12345");
        req.setPersonID("Failure");

        PersonResult result = getPersonService.getPerson(req);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }

    @Test
    public void getManyPersonsPass() {
        GetPersonService getPersonService = new GetPersonService();

        PersonRequest req = new PersonRequest();
        req.setAuthtoken("12345");

        PersonResult result = getPersonService.getPerson(req);

        assertTrue(result.isSuccess());
        assertEquals("steve123", result.getData().get(0).getPersonID());
        assertEquals("cindy456", result.getData().get(1).getPersonID());
    }

    @Test
    public void getManyPersonsFail() {
        GetPersonService getPersonService = new GetPersonService();

        PersonRequest req = new PersonRequest();
        req.setAuthtoken("Failure");

        PersonResult result = getPersonService.getPerson(req);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }
}
