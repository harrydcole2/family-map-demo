package com.example.familymapapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.DataCacheCalculator;
import com.example.familymapapp.controller.ServerProxy;
import com.example.familymapapp.tasks.DataSyncTask;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;
import request.EventRequest;
import result.EventResult;
import result.PersonResult;

public class CacheCalculatorTest {

    DataCache cache;
    Event eventA, eventB, eventC, eventD;
    Person personA, personB, personC, personD, personE;

    @Before
    public void setUp() {
        cache = DataCache.getInstance();

        cache.setUserPersonId("gale789");

        eventA = new Event("Biking_123A", "gale", "gale789",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_russo", 2064);
        eventB = new Event("Climbing_456A", "gale", "gale789",
                45.1f, 100.5f, "South Korea", "Seoul",
                "Climbing_Around", 1980);
        eventC = new Event("Swimming_789A", "gale", "steve123",
                35.4f, 140.2f, "Japan", "Hiroshima",
                "Swimming", 2015);
        eventD = new Event("Hanging_Out", "gale", "cindy456",
                120f, 95f, "Hawaii", "Oahu",
                "Surfs_Up", 2015);

        cache.addEvent("Biking_123A", eventA);
        cache.addEvent("Climbing_456A", eventB);
        cache.addEvent("Swimming_789A", eventC);
        cache.addEvent("Hanging_Out", eventD);

        cache.addEventsByPersonId("gale789", Arrays.asList(eventA, eventB));
        cache.addEventsByPersonId("steve123", Arrays.asList(eventC));
        cache.addEventsByPersonId("cindy456", Arrays.asList(eventD));

        cache.addEventsByPersonIdOnMap("gale789", Arrays.asList(eventA, eventB));
        cache.addEventsByPersonIdOnMap("steve123", Arrays.asList(eventC));
        cache.addEventsByPersonIdOnMap("cindy456", Arrays.asList(eventD));

        personA = new Person("steve123", "gale", "steve", "russo", "m",
                null, null, null);
        personB = new Person("cindy456", "gale", "cindy", "russo", "f",
                null, null, null);
        personC = new Person("gale789", "gale", "gale", "russo", "m",
                "steve123", "cindy456", null);
        personD = new Person("sonny142", "gale", "golden", "boy", "m",
                "gale789", "unknown", null);

        personE = new Person("dajaiye", "gale", "rEd", "HeRriNg", "m",
                null, null, null);

        cache.addPerson("steve123", personA);
        cache.addPerson("cindy456", personB);
        cache.addPerson("gale789", personC);
        cache.addPerson("sonny142", personD);
        cache.addPerson("dajaiye", personE);

        cache.addPaternalAncestor(personA);
        cache.addMaternalAncestor(personB);
    }

    @Test
    public void calculateFamilyRelationsPass() {
        ImmutablePair<List<Person>, List<String>> fam = DataCacheCalculator.calculateFamilyRelations(personC);

        String[] expectedRelations = {"Father", "Mother", "Child"};

        Assert.assertArrayEquals(expectedRelations, fam.right.toArray());
        Assert.assertEquals("steve123", fam.left.get(0).getPersonID());
        Assert.assertEquals("cindy456", fam.left.get(1).getPersonID());
        Assert.assertEquals("sonny142", fam.left.get(2).getPersonID());
    }

    @Test
    public void calculateFamilyRelationsFail() {
        ImmutablePair<List<Person>, List<String>> fam = DataCacheCalculator.calculateFamilyRelations(personE);

        Assert.assertEquals(0, fam.left.size());
        Assert.assertEquals(0, fam.right.size());
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> fam.left.get(0));
    }

    @Test
    public void calculateSearchResultsPass() {
        List<Person> personResults = DataCacheCalculator.calculatePersonSearchResults("russo");
        List<Event> eventResults = DataCacheCalculator.calculateEventSearchResults("russo");

        Person[] expectedPersons = { personB, personC, personA };
        Event[] expectedEvents = { eventA };

        Assert.assertArrayEquals(expectedPersons, personResults.toArray());
        Assert.assertArrayEquals(expectedEvents, eventResults.toArray());
    }

    @Test
    public void calculateSearchResultsFail() {
        List<Person> personResults = DataCacheCalculator.calculatePersonSearchResults(" ");
        List<Event> eventResults = DataCacheCalculator.calculateEventSearchResults(" ");

        Assert.assertEquals(0, personResults.size());
        Assert.assertEquals(0, eventResults.size());
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> personResults.get(0));
    }

    @Test
    public void calculateEventsToDisplayPass() {
        List<Event> filteredEvents = DataCacheCalculator.calculateEventsToDisplay(true,
                false, true, true);
        Event[] expectedEvents = { eventA, eventB, eventC };
        Assert.assertArrayEquals(expectedEvents, filteredEvents.toArray());

        List<Event> filteredEvents2 = DataCacheCalculator.calculateEventsToDisplay(false,
                true, true, true);
        Event[] expectedEvents2 = { eventD, eventA, eventB };
        Assert.assertArrayEquals(expectedEvents2, filteredEvents2.toArray());

        List<Event> filteredEvents3 = DataCacheCalculator.calculateEventsToDisplay(true,
                true, false, true);
        Event[] expectedEvents3 = { eventD };
        Assert.assertArrayEquals(expectedEvents3, filteredEvents3.toArray());
    }

    @Test
    public void calculateEventsToDisplayFail() {
        List<Event> filteredEvents = DataCacheCalculator.calculateEventsToDisplay(false,
                false, false, false);
        Event[] expectedEvents = {};
        Assert.assertArrayEquals(expectedEvents, filteredEvents.toArray());
    }


    @Test
    public void sortEventsChronologicallyPass() {
        List<Event> inputList = Arrays.asList(eventA, eventB, eventC);

        List<Event> outputList = DataCacheCalculator.sortEventsChronologically(inputList);
        Assert.assertEquals(eventB, outputList.get(0));
        Assert.assertEquals(eventC, outputList.get(1));
        Assert.assertEquals(eventA, outputList.get(2));
    }

    @Test
    public void sortEventsChronologicallyFail() {
        List<Event> inputList = Arrays.asList(eventA, eventB, eventC);

        List<Event> outputList = DataCacheCalculator.sortEventsChronologically(inputList);
        Assert.assertNotEquals(eventB, outputList.get(1));
        Assert.assertNotEquals(eventC, outputList.get(2));
        Assert.assertNotEquals(eventA, outputList.get(0));
    }
}
