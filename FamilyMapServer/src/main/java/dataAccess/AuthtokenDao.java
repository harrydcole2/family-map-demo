package dataAccess;

import model.Authtoken;

import java.sql.*;

public class AuthtokenDao {
    private final Connection conn;

    /**
     * Constructor for AuthtokenDao when we want to do operations on it
     *
     * @param conn a passed connection to perform function
     */
    public AuthtokenDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts an authtoken row into authtoken table
     *
     * @param authtoken an authtoken obj that has data to insert into table
     * @throws DataAccessException when faced SQL exception
     */
    public void insert(Authtoken authtoken) throws DataAccessException {
        String sql = "INSERT INTO authtokens (authtoken, username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtoken.getAuthtoken());
            stmt.setString(2, authtoken.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an event into the database");
        }
    }

    /**
     * Finds authtoken row and returns what user it belongs to
     *
     * @param auth is the authtoken passed over web
     * @return username corresponding to the username for this authtoken
     */
    public String findUsername(String auth) throws DataAccessException {
        String foundUsername;
        ResultSet rs;
        String sql = "SELECT username FROM authtokens WHERE authtoken = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth);
            rs = stmt.executeQuery();

            if(rs.next()) {
                foundUsername = rs.getString("username");
                return foundUsername;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting username by auth in the database");
        }
    }

    /**
     * Clears all data in users table
     *
     * @throws DataAccessException when faced SQL exception
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM authtokens";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the authtoken table");
        }
    }
}
