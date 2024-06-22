package dataAccess;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private final Connection conn;

    /**
     * Constructor for UserDao when we want to do operations on it
     *
     * @param conn a passed connection to perform function
     */
    public UserDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a user row into users table
     *
     * @param user a user obj that has data to insert into table
     * @throws DataAccessException when faced SQL exception
     */
    public void insert(User user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email, firstName, lastName, " +
                "gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an event into the database");
        }
    }

    /**
     * Finds user row and converts it to object to return
     *
     * @param username is username
     * @return User obj with all user information
     */
    public User findByUsername(String username) throws DataAccessException {
        User user;
        ResultSet rs;
        String sql = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"),
                       rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                       rs.getString("personID"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an event in the database");
        }
    }

    /**
     * Clears all data in users table
     *
     * @throws DataAccessException when faced SQL exception
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table");
        }
    }

    /**
     * Checks login information to see if correct
     *
     * @param username passed username to check
     * @param password to check against
     * @return whether login proceeded correctly
     */
    public boolean validate(String username, String password) throws DataAccessException {
        ResultSet rs;
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if(rs.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting username by auth in the database");
        }
    }
}
