package result;

/**
 * Class for /user/login output
 */
public class LoginResult extends Result{
    /**
     * unique generated authtoken
     */
    private String authtoken;
    /**
     * username of person from request body
     */
    private String username;
    /**
     * personID of person previously generated for this user
     */
    private String personID;

    public LoginResult(String message, boolean success, String authtoken, String username, String personID) {
        super(message, success);
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
