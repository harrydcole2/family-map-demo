package dataAccess;

import model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PersonDao {
    private final Connection conn;

    /**
     * Constructor for PersonDao when we want to do operations on it
     *
     * @param conn a passed connection to perform function
     */
    public PersonDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a person row into persons table
     *
     * @param person a person obj that has data to insert into table
     * @throws DataAccessException when faced SQL exception
     */
    public void insert(Person person) throws DataAccessException {
        String sql = "INSERT INTO persons (personID, associatedUsername, firstName, lastName, " +
                "gender, fatherID, motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a person into the database");
        }
    }

    /**
     * Finds person row and converts it to object to return
     *
     * @param personID is unique
     * @return User obj with all user information
     */
    public Person findByPersonID(String personID) throws DataAccessException {
        Person person;
        ResultSet rs;
        String sql = "SELECT * FROM persons WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a person in the database");
        }
    }

    public ArrayList<Person> findManyByUsername(String username) throws DataAccessException{
        ArrayList<Person> persons = new ArrayList<>();
        ResultSet rs;
        String sql = "SELECT * FROM persons WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while(rs.next()) {
                persons.add(new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID")));
            }
            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an event in the database");
        }
    }

    /**
     * Clears all data in persons table
     *
     * @throws DataAccessException when faced SQL exception
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM persons";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the person table");
        }
    }

    public void clearByUsername(String username) throws DataAccessException {
        String sql = "DELETE FROM persons WHERE associatedUsername = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing persons by associatedUsername");
        }
    }

    public void updateSpouseIDs(String father, String mother) throws DataAccessException {
        String sql = "UPDATE persons SET spouseID = ? WHERE personID = ?;";
        String sql2 = "UPDATE persons SET spouseID = ? WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt.setString(1, father);
            stmt.setString(2, mother);

            stmt2.setString(1,mother);
            stmt2.setString(2,father);

            stmt.executeUpdate();
            stmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while adding spouseIDs into the database");
        }
    }
}
