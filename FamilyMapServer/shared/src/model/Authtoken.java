package model;

import java.util.Objects;

/**
 * Authtoken class modelling one row of authtoken table
 */
public class Authtoken {
    /**
     * Unique authtoken
     */
    private String authtoken;
    /**
     * Username that is associated with the authtoken
     */
    private String username;

    /**
     * Constructor for Authtoken row
     *
     * @param authtoken is unique string for authorized user
     * @param username is name of current user to which tree belongs
     */
    public Authtoken(String authtoken, String username) {
        this.authtoken = authtoken;
        this.username = username;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authtoken authToken = (Authtoken) o;
        return Objects.equals(authtoken, authToken.authtoken) && Objects.equals(username, authToken.username);
    }
}
