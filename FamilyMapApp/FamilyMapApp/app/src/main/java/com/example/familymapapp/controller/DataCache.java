package com.example.familymapapp.controller;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.*;

public class DataCache {
    private static DataCache instance = new DataCache();
    public static DataCache getInstance() {
        return instance;
    }
    private DataCache() {}
    private String userPersonId;

    private Map<String, Person> persons = new HashMap<>();
    private Map<String, Event> events = new HashMap<>();
    private Map<String, List<Event>> eventsByPersonId = new HashMap<>();
    private Map<String, List<Event>> eventsByPersonIdOnMap = new HashMap<>();

    private Set<Person> paternalAncestors = new HashSet<>();
    private Set<Person> maternalAncestors = new HashSet<>();


    public void setUserPersonId(String id) {
        this.userPersonId = id;
    }
    public String getUserPersonId() {
        return userPersonId;
    }

    public void addPerson(String id, Person person) {
        persons.put(id, person);
    }
    public Person getPersonById(String id) {
        return persons.get(id);
    }
    public Map<String, Person> getPersons() {
        return persons;
    }
    public void addEvent(String id, Event event) {
        events.put(id, event);
    }
    public Event getEventById(String id) {
        return events.get(id);
    }

    public void addEventsByPersonId(String id, List<Event> events) {
        eventsByPersonId.put(id, events);
    }
    public List<Event> getEventsByPersonId(String id) {
        return eventsByPersonId.get(id);
    }

    public void addEventsByPersonIdOnMap(String id, List<Event> events) {
        eventsByPersonIdOnMap.put(id, events);
    }
    public Map<String, List<Event>> getEventsByPersonIdOnMap() {
        return eventsByPersonIdOnMap;
    }

    public void addMaternalAncestor(Person person) {
        maternalAncestors.add(person);
    }
    public Set<Person> getMaternalAncestors() {
        return maternalAncestors;
    }
    public void addPaternalAncestor(Person person) {
        paternalAncestors.add(person);
    }
    public Set<Person> getPaternalAncestors() {
        return paternalAncestors;
    }

    public void clearCache() {
        persons.clear();
        events.clear();
        eventsByPersonIdOnMap.clear();
        eventsByPersonId.clear();
        paternalAncestors.clear();
        maternalAncestors.clear();
        userPersonId = null;
    }
}
