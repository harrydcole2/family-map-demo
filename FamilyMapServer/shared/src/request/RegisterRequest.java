package request;

/**
 * Class for /user/register input
 */
public class RegisterRequest extends Request {
    /**
     * username from request body
     */
    private String username;
    /**
     * password from request body
     */
    private String password;
    /**
     * email from request body
     */
    private String email;
    /**
     * firstName from request body
     */
    private String firstName;
    /**
     * lastName from request body
     */
    private String lastName;
    /**
     * gender from request body, either "m" or "f"
     */
    private String gender; // "m" or "f"

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
