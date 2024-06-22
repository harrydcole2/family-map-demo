package service;

import dataAccess.*;
import model.Event;
import request.EventRequest;
import result.EventResult;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Service for /event/[eventID] and /event requests
 */
public class GetEventService {

    /**
     * Gets details about a specific event if passed an event ID, or all events
     *
     * @param req the EventRequest obj which has an ID member (or null)
     * @return EventResult obj, which is array of eventData
     */
    public EventResult getEvent(EventRequest req) {
        Database connManager = new Database();
        try {
            Connection connection = connManager.openConnection();

            AuthtokenDao authtokenDao = new AuthtokenDao(connection);
            String username = authtokenDao.findUsername(req.getAuthtoken());

            if(username == null) {
                throw new DataAccessException("Invalid auth token");
            }
            else {
                EventDao eventDao = new EventDao(connection);

                EventResult positiveResponse;
                if(req.getEventID() != null) {
                    Event event = eventDao.findByEventID(req.getEventID());

                    if(event == null) {
                        throw new DataAccessException("Invalid EventID parameter");
                    }
                    if(!event.getAssociatedUsername().equals(username)) {
                        throw new DataAccessException("Requested event does not belong to this user");
                    }

                    ArrayList<Event> data = new ArrayList<>();
                    data.add(event);

                    positiveResponse = new EventResult(null, true, data);

                }
                else {
                    ArrayList<Event> data = eventDao.findManyByUsername(username);

                    positiveResponse = new EventResult(null, true, data);
                }
                connManager.closeConnection(true);
                return positiveResponse;

            }
        }
        catch(DataAccessException e) {
            connManager.closeConnection(false);

            EventResult negativeResponse = new EventResult("Error: " + e.getMessage(), false, null);
            e.printStackTrace();

            return negativeResponse;
        }
    }
}
