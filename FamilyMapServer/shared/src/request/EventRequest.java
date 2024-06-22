package request;

/**
 * Class for /event/[eventID] and /event inputs
 */
public class EventRequest extends Request{
    /**
     * the ID parsed from URL request (possible null)
     */
    private String eventID;

    private String authtoken;
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
}
