package service;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import jsonModel.*;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import request.FillRequest;
import result.FillResult;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FillTest extends ServiceTest {

    @BeforeEach
    public void setUp() throws DataAccessException{
        Gson gson = new Gson();
        try {
            Reader fReader = new FileReader("json" + File.separator + "fnames.json");
            FNamesData femaleNames = gson.fromJson(fReader, FNamesData.class);

            Reader mReader = new FileReader("json" + File.separator + "mnames.json");
            MNamesData maleNames = gson.fromJson(mReader, MNamesData.class);

            Reader sReader = new FileReader("json" + File.separator + "snames.json");
            SNamesData lastNames = gson.fromJson(sReader, SNamesData.class);

            Reader locReader = new FileReader("json" + File.separator + "locations.json");
            LocationData locationData = gson.fromJson(locReader, LocationData.class);

            JsonDataCache cache = JsonDataCache.getInstance();
            cache.loadData(femaleNames, maleNames, lastNames, locationData);

        } catch (IOException e) {
            e.printStackTrace();

            System.out.println("Problem loading into cache");
        }

        connectToDatabase();
        new ClearService().clear();
        uDao.insert(new User("Test","password", "email@email", "tester",
                "mcGenius", "m", "12345"));
        db.closeConnection(true);
    }

    @Test
    public void fillPass() throws DataAccessException{
        FillRequest req = new FillRequest();
        req.setUsername("Test");
        req.setGenerations(2);

        FillService filling = new FillService();
        FillResult result = filling.fill(req);

        connectToDatabase();

        ArrayList<Event> events = eDao.findManyByUsername("Test");
        ArrayList<Person> persons = pDao.findManyByUsername("Test");

        db.closeConnection(true);

        assertTrue(result.isSuccess());
        assertEquals(7, persons.size());
        assertEquals(25, events.size());
    }

    @Test
    public void fillFail() throws DataAccessException{
        FillRequest req = new FillRequest();
        req.setUsername("Failure");
        req.setGenerations(2);

        FillService filling = new FillService();
        FillResult result = filling.fill(req);

        connectToDatabase();

        ArrayList<Event> events = eDao.findManyByUsername("Test");
        ArrayList<Person> persons = pDao.findManyByUsername("Test");

        db.closeConnection(true);

        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertEquals(0, persons.size());
        assertEquals(0, events.size());
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }
}
