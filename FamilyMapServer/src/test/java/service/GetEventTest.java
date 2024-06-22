package service;

import dataAccess.DataAccessException;
import model.Authtoken;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.EventRequest;
import result.EventResult;

import static org.junit.jupiter.api.Assertions.*;

public class GetEventTest extends ServiceTest{

    @BeforeEach
    public void setUp() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();

        aDao.insert(new Authtoken("12345", "Gale"));
        eDao.insert(new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016));
        eDao.insert(new Event("Climbing_456A", "Gale", "Gale123A",
                45.1f, 100.5f, "South Korea", "Seoul",
                "Climbing_Around", 2013));
        eDao.insert(new Event("Swimming_789A", "Jane", "Jane123A",
                35.4f, 140.2f, "Japan", "Hiroshima",
                "Swimming_Around", 2015));

        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }

    @Test
    public void getEventPass() {
        GetEventService getEventService = new GetEventService();

        EventRequest req = new EventRequest();
        req.setAuthtoken("12345");
        req.setEventID("Biking_123A");

        EventResult result = getEventService.getEvent(req);

        assertTrue(result.isSuccess());
        assertEquals("Gale123A", result.getData().get(0).getPersonID());
        assertEquals("Gale", result.getData().get(0).getAssociatedUsername());
        assertEquals("Biking_123A", result.getData().get(0).getEventID());
    }

    @Test
    public void getEventFail() {
        GetEventService getEventService = new GetEventService();

        EventRequest req = new EventRequest();
        req.setAuthtoken("12345");
        req.setEventID("Failure");

        EventResult result = getEventService.getEvent(req);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }

    @Test
    public void getManyEventsPass() {
        GetEventService getEventService = new GetEventService();

        EventRequest req = new EventRequest();
        req.setAuthtoken("12345");

        EventResult result = getEventService.getEvent(req);

        assertTrue(result.isSuccess());
        assertEquals("Biking_123A", result.getData().get(0).getEventID());
        assertEquals("Climbing_456A", result.getData().get(1).getEventID());
    }

    @Test
    public void getManyEventsFail() {
        GetEventService getEventService = new GetEventService();

        EventRequest req = new EventRequest();
        req.setAuthtoken("Failure");

        EventResult result = getEventService.getEvent(req);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }
}
