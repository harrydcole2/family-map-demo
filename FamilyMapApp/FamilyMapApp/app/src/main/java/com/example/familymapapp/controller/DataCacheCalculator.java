package com.example.familymapapp.controller;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

import model.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
public class DataCacheCalculator {
    private static DataCache cache = DataCache.getInstance();
    public static ImmutablePair<List<Person>, List<String>> calculateFamilyRelations(Person person) {
        List<Person> family = new ArrayList<>();
        List<String> relationsToPerson = new ArrayList<>();
        if(person.getFatherID() != null) {
            family.add(cache.getPersonById(person.getFatherID()));
            relationsToPerson.add("Father");
        }
        if(person.getMotherID() != null) {
            family.add(cache.getPersonById(person.getMotherID()));
            relationsToPerson.add("Mother");
        }
        if(person.getSpouseID() != null) {
            family.add(cache.getPersonById(person.getSpouseID()));
            relationsToPerson.add("Spouse");
        }
        String childId = getChildId(person);
        if(childId != null) {
            family.add(cache.getPersonById(childId));
            relationsToPerson.add("Child");
        }

        ImmutablePair<List<Person>, List<String>> familyRelationships = ImmutablePair.of(family, relationsToPerson);
        return familyRelationships;
    }

    private static String getChildId(Person person) {
        for (Map.Entry<String, Person> entry : cache.getPersons().entrySet()) {
            if (entry.getValue().getMotherID() == null || entry.getValue().getFatherID() == null) {
                continue;
            }
            if (entry.getValue().getMotherID().equals(person.getPersonID()) ||
                    entry.getValue().getFatherID().equals(person.getPersonID())) {
                return entry.getValue().getPersonID();
            }
        }
        return null;
    }

    public static List<Person> calculatePersonSearchResults(String searchText) {
        List<Person> personList = new ArrayList<>();
        if(!searchText.trim().isEmpty()) {
            Map<String, Person> personMap = cache.getPersons();
            for (Map.Entry<String, Person> entry : personMap.entrySet()) {
                String name = entry.getValue().getFirstName() + " " + entry.getValue().getLastName();
                name = name.toLowerCase();
                if (name.contains(searchText)) {
                    personList.add(entry.getValue());
                }
            }
        }
        return personList;
    }

    public static List<Event> calculateEventSearchResults(String searchText) {
        List<Event> eventList = new ArrayList<>();
        if(!searchText.trim().isEmpty()) {
            Map<String, List<Event>> filteredEventsMap = cache.getEventsByPersonIdOnMap();
            for (Map.Entry<String, List<Event>> entry : filteredEventsMap.entrySet()) {
                List<Event> lifeEvents = entry.getValue();
                for (int i = 0; i < lifeEvents.size(); i++) {
                    Event event = lifeEvents.get(i);
                    String details = event.getEventType().toUpperCase() + ": " + event.getCity() +
                            ", " + event.getCountry() + " (" + event.getYear() + ")";
                    details = details.toLowerCase();
                    if (details.contains(searchText)) {
                        eventList.add(event);
                    }
                }
            }
        }
        return eventList;
    }

    public static List<Event> calculateEventsToDisplay(boolean fatherFilter, boolean motherFilter, boolean maleFilter, boolean femaleFilter) {
        List<Event> eventsToDisplay = new ArrayList<>();

        Set<Person> fatherSide = cache.getPaternalAncestors();
        Set<Person> motherSide = cache.getMaternalAncestors();
        Map<String, Person> personMap = cache.getPersons();
        for (Map.Entry<String,Person> entry : personMap.entrySet()) {
            Person relative = entry.getValue();
            if(cache.getEventsByPersonId(relative.getPersonID()) == null) {
                continue;
            }
            if(fatherSide.contains(relative) && fatherFilter) {
                if((maleFilter && relative.getGender().equalsIgnoreCase("m"))
                        || femaleFilter && relative.getGender().equalsIgnoreCase("f")) {
                    eventsToDisplay.addAll(cache.getEventsByPersonId(relative.getPersonID()));
                    cache.addEventsByPersonIdOnMap(relative.getPersonID(),cache.getEventsByPersonId(relative.getPersonID()));
                }
            }
            else if(motherSide.contains(relative) && motherFilter) {
                if((maleFilter && relative.getGender().equalsIgnoreCase("m"))
                        || femaleFilter && relative.getGender().equalsIgnoreCase("f")) {
                    eventsToDisplay.addAll(cache.getEventsByPersonId(relative.getPersonID()));
                    cache.addEventsByPersonIdOnMap(relative.getPersonID(),cache.getEventsByPersonId(relative.getPersonID()));
                }
            }
            else if(!fatherSide.contains(relative) && !motherSide.contains(relative)){
                if((maleFilter && relative.getGender().equalsIgnoreCase("m"))
                        || femaleFilter && relative.getGender().equalsIgnoreCase("f")) {
                    eventsToDisplay.addAll(cache.getEventsByPersonId(relative.getPersonID()));
                    cache.addEventsByPersonIdOnMap(relative.getPersonID(),cache.getEventsByPersonId(relative.getPersonID()));
                }
            }
        }
        return eventsToDisplay;
    }

    public static List<Event> sortEventsChronologically(List<Event> events) {
        events.sort(new SortByYear());
        return events;
    }

    static class SortByYear implements Comparator<Event> {
        @Override
        public int compare(Event a, Event b) {
            return a.getYear() - b.getYear();
        }
    }
}
