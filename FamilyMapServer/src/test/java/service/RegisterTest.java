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
import request.RegisterRequest;
import result.RegisterResult;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest extends ServiceTest{

    @BeforeEach
    public void setUp() throws DataAccessException {
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
        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        connectToDatabase();
        new ClearService().clear();
        db.closeConnection(true);
    }

    @Test
    public void registerPass() throws DataAccessException{
        RegisterService registering = new RegisterService();
        RegisterRequest req = new RegisterRequest();
        req.setUsername("Test");
        req.setPassword("password");
        req.setEmail("email");
        req.setFirstName("Tester");
        req.setLastName("McGenius");
        req.setGender("m");

        RegisterResult result = registering.register(req);

        connectToDatabase();

        ArrayList<Event> events = eDao.findManyByUsername("Test");
        ArrayList<Person> persons = pDao.findManyByUsername("Test");

        db.closeConnection(true);

        assertTrue(result.isSuccess());
        assertEquals(31, persons.size());
        assertEquals(121, events.size());
    }

    @Test
    public void RegisterFail() throws DataAccessException{
        connectToDatabase();
        uDao.insert(new User("Test","Test","Test","Test","Test","Test","Test"));
        db.closeConnection(true);

        RegisterService registering = new RegisterService();
        RegisterRequest req = new RegisterRequest();
        req.setUsername("Test");
        req.setPassword("password");
        req.setEmail("email");
        req.setFirstName("Tester");
        req.setLastName("McGenius");
        req.setGender("m");

        RegisterResult result = registering.register(req);

        connectToDatabase();

        ArrayList<Event> events = eDao.findManyByUsername("Test");
        ArrayList<Person> persons = pDao.findManyByUsername("Test");

        db.closeConnection(true);

        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertEquals(0, persons.size());
        assertEquals(0, events.size());
    }
}
