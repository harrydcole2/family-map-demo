package service;

import dataAccess.DataAccessException;
import dataAccess.EventDao;
import dataAccess.PersonDao;
import jsonModel.JsonDataCache;
import jsonModel.Location;
import model.Event;
import model.Person;
import model.User;

import java.sql.Connection;
import java.util.Random;
import java.util.UUID;

public class FakeDataService {
    private EventDao eventDao;
    private PersonDao personDao;
    private Random rand;
    private JsonDataCache randomData;
    private String associatedUsername;

    public void generateRootPerson(Connection c, User user, int generations) throws DataAccessException {
        eventDao = new EventDao(c);
        personDao = new PersonDao(c);
        rand = new Random();
        randomData = JsonDataCache.getInstance();
        associatedUsername = user.getUsername();

        int year = rand.nextInt(10) + 1995;

        Person mother = null;
        Person father = null;

        if (generations > 0) {
            mother = generatePerson(c, year, "f", generations - 1);
            father = generatePerson(c, year,"m", generations - 1);

            establishMarriageEvents(father, mother, year);
        }

        Person rootPerson = new Person(user.getPersonID(), associatedUsername, user.getFirstName(),
                user.getLastName(), user.getGender());

        if (mother != null) {
            rootPerson.setFatherID(father.getPersonID());
            rootPerson.setMotherID(mother.getPersonID());
        }
        personDao.insert(rootPerson);

        Location birthPlace = randomData.randomLocation();
        Event birth = new Event(UUID.randomUUID().toString().substring(0,5), associatedUsername, user.getPersonID(),
                Float.parseFloat(birthPlace.getLatitude()), Float.parseFloat(birthPlace.getLongitude()), birthPlace.getCountry(),
                birthPlace.getCity(),  "birth", year);

        eventDao.insert(birth);
    }
    private Person generatePerson(Connection c, int year, String gender, int generations) throws DataAccessException {

        year = year - (rand.nextInt(13) + 15);

        Person mother = null;
        Person father = null;

        if(generations > 0) {
            mother = generatePerson(c, year, "f", generations - 1);
            father = generatePerson(c, year,"m", generations - 1);

            establishMarriageEvents(father, mother, year);
        }

        String personID = UUID.randomUUID().toString().substring(0,5);
        Person person = null;
        if(gender == "m") {
            person = new Person(personID, associatedUsername, randomData.randomMaleName(),
                    randomData.randomLastName(), gender);
        }
        else if (gender == "f") {
            person = new Person(personID, associatedUsername, randomData.randomFemaleName(),
                    randomData.randomLastName(), gender);
        }


        if(mother != null) {
            person.setFatherID(father.getPersonID());
            person.setMotherID(mother.getPersonID());
        }
        personDao.insert(person);

        generateEvents(person, year);

        return person;
    }

    private void establishMarriageEvents(Person father, Person mother, int year) throws DataAccessException {
        mother.setSpouseID(father.getPersonID());
        father.setSpouseID(mother.getPersonID());
        personDao.updateSpouseIDs(father.getPersonID(), mother.getPersonID());

        Location loc = randomData.randomLocation();
        Event marriageMother = new Event(UUID.randomUUID().toString().substring(0,5), associatedUsername, mother.getPersonID(),
                Float.parseFloat(loc.getLatitude()), Float.parseFloat(loc.getLongitude()), loc.getCountry(), loc.getCity(),
                "marriage to " + mother.getFirstName(), year);
        Event  marriageFather = new Event(UUID.randomUUID().toString().substring(0,5), associatedUsername, father.getPersonID(),
                Float.parseFloat(loc.getLatitude()), Float.parseFloat(loc.getLongitude()), loc.getCountry(), loc.getCity(),
                "marriage to " + father.getFirstName(), year);

        eventDao.insert(marriageMother);
        eventDao.insert(marriageFather);
    }

    private void generateEvents(Person person, int year) throws DataAccessException {
        Location birthPlace = randomData.randomLocation();
        Event birth = new Event(UUID.randomUUID().toString().substring(0,5), associatedUsername, person.getPersonID(),
                Float.parseFloat(birthPlace.getLatitude()), Float.parseFloat(birthPlace.getLongitude()), birthPlace.getCountry(),
                birthPlace.getCity(),  "birth", year);

        Location deathPlace = randomData.randomLocation();
        Event death = new Event(UUID.randomUUID().toString().substring(0,5), associatedUsername, person.getPersonID(),
                Float.parseFloat(deathPlace.getLatitude()), Float.parseFloat(deathPlace.getLongitude()), deathPlace.getCountry(),
                deathPlace.getCity(), "death", year + 60 + rand.nextInt(50));

        Location extraPlace = randomData.randomLocation();
        Event other = new Event(UUID.randomUUID().toString().substring(0,5), associatedUsername, person.getPersonID(),
                Float.parseFloat(extraPlace.getLatitude()), Float.parseFloat(extraPlace.getLongitude()), extraPlace.getCountry(),
                extraPlace.getCity(), "other", year + rand.nextInt(50));

        eventDao.insert(birth);
        eventDao.insert(death);
        eventDao.insert(other);
    }
}
