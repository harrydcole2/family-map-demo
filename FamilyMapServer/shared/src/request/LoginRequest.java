package request;

/**
 * Class for /user/login input
 */
public class LoginRequest extends Request{
    /**
     * username passed in request body
     */
    private String username;
    /**
     * password passed in request body
     */
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
