package com.example.familymapapp;

import static org.junit.Assert.assertFalse;

import com.example.familymapapp.controller.ServerProxy;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import model.Event;
import model.Person;
import model.User;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;


public class ServerProxyTest {

    @Test
    public void loginPass() {
        LoginRequest req = new LoginRequest();
        req.setUsername("sheila");
        req.setPassword("parker");

        LoginResult result = ServerProxy.login(req, "localhost", "8080");

        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void loginFail() {
        LoginRequest req = new LoginRequest();
        req.setUsername("oof!");
        req.setPassword("wrong");

        LoginResult result = ServerProxy.login(req, "localhost", "8080");

        assertFalse(result.isSuccess());
        Assert.assertNotNull(result.getMessage());
    }

    @Test
    public void registerPass() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("Testing");
        req.setPassword("123");
        req.setEmail("email");
        req.setFirstName("McBob");
        req.setLastName("Tester");
        req.setGender("m");

        RegisterResult result = ServerProxy.register(req, "localhost", "8080");

        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void RegisterFail() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("Test");
        req.setPassword("password");
        req.setEmail("email");
        req.setFirstName("Tester");
        req.setLastName("McGenius");
        req.setGender("m");

        RegisterResult result = ServerProxy.register(req, "localhost", "8080");

        assertFalse(result.isSuccess());
        Assert.assertNotNull(result.getMessage());
    }

    @Test
    public void getEventsPass() {
        EventRequest req = new EventRequest();
        req.setAuthtoken("09939d76-f587-4881-964f-0513865fbbb9");

        EventResult result = ServerProxy.getEvents(req, "localhost", "8080");

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("Sheila_Birth", result.getData().get(0).getEventID());
        Assert.assertEquals("Sheila_Marriage", result.getData().get(1).getEventID());
    }

    @Test
    public void getEventsFail() {
        EventRequest req = new EventRequest();
        req.setAuthtoken("Failure");

        EventResult result = ServerProxy.getEvents(req, "localhost", "8080");

        Assert.assertFalse(result.isSuccess());
        Assert.assertNull(result.getData());
    }

    @Test
    public void getPersonsPass() {
        PersonRequest req = new PersonRequest();
        req.setAuthtoken("09939d76-f587-4881-964f-0513865fbbb9");

        PersonResult result = ServerProxy.getPeople(req, "localhost", "8080");

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("Sheila_Parker", result.getData().get(0).getPersonID());
        Assert.assertEquals("Davis_Hyer", result.getData().get(1).getPersonID());
    }

    @Test
    public void getPersonsFail() {
        PersonRequest req = new PersonRequest();
        req.setAuthtoken("Failure");

        PersonResult result = ServerProxy.getPeople(req, "localhost", "8080");

        Assert.assertFalse(result.isSuccess());
        Assert.assertNull(result.getData());
    }
}
