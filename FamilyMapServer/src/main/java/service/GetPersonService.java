package service;

import dataAccess.*;
import model.Person;
import request.PersonRequest;
import result.PersonResult;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Service for /person/[personID] and /person requests
 */
public class GetPersonService {
    /**
     * Gets details about a specific person if passed a personID, or all persons
     *
     * @param req the PersonRequest obj which has an ID member (or null)
     * @return PersonResult obj, which is array of personData
     */
    public PersonResult getPerson(PersonRequest req) {

        Database connManager = new Database();
        try {
            Connection connection = connManager.openConnection();

            AuthtokenDao authtokenDao = new AuthtokenDao(connection);
            String username = authtokenDao.findUsername(req.getAuthtoken());

            if(username == null) {
                throw new DataAccessException("Invalid auth token");
            }
            else {
                PersonDao personDao = new PersonDao(connection);

                PersonResult positiveResponse;
                if(req.getPersonID() != null) {
                    Person person = personDao.findByPersonID(req.getPersonID());

                    if(person == null) {
                        throw new DataAccessException("Invalid PersonID parameter");
                    }
                    if(!person.getAssociatedUsername().equals(username)) {
                        throw new DataAccessException("Requested event does not belong to this user");
                    }

                    ArrayList<Person> data = new ArrayList<>();
                    data.add(person);

                    positiveResponse = new PersonResult(null, true, data);

                }
                else {
                    ArrayList<Person> data = personDao.findManyByUsername(username);

                    positiveResponse = new PersonResult(null, true, data);
                }
                connManager.closeConnection(true);
                return positiveResponse;

            }
        }
        catch(DataAccessException e) {
            connManager.closeConnection(false);

            PersonResult negativeResponse = new PersonResult("Error: " + e.getMessage(), false, null);
            e.printStackTrace();

            return negativeResponse;
        }
    }
}
