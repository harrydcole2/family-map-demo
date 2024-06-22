package request;

/**
 * Class for /person/[personID] and /person inputs
 */
public class PersonRequest extends Request {

    /**
     * the ID parsed from URL request (possible null)
     */
    private String personID;
    private String authtoken;

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
}
