package request;

import model.Event;
import model.Person;
import model.User;

/**
 * Class for /load HTTP input
 */
public class LoadRequest extends Request{
    /**
     * parsed from json array of user obj
     */
    private User[] users;
    /**
     * parsed from json array of person obj
     */
    private Person[] persons;
    /**
     * parsed from json array of event obj
     */
    private Event[] events;

    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
