package com.example.familymapapp.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.DataCacheCalculator;
import com.example.familymapapp.controller.ServerProxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.*;
import request.EventRequest;
import request.PersonRequest;
import result.EventResult;
import result.PersonResult;

public class DataSyncTask implements Runnable{

    private Handler messageHandler;
    private EventRequest eventRequest = new EventRequest();
    private PersonRequest personRequest = new PersonRequest();

    DataCache cache;
    private String host;
    private String port;

    public DataSyncTask(Handler messageHandler, String auth, String host, String port) {
        this.messageHandler = messageHandler;
        this.host = host;
        this.port = port;
        eventRequest.setAuthtoken(auth);
        personRequest.setAuthtoken(auth);
        cache = DataCache.getInstance();
    }

    @Override
    public void run() {
        EventResult eventRes = ServerProxy.getEvents(eventRequest, host, port);
        PersonResult personRes = ServerProxy.getPeople(personRequest, host, port);

        for(int i = 0; i < eventRes.getData().size(); i++) {
            cache.addEvent(eventRes.getData().get(i).getEventID(), eventRes.getData().get(i));
        }

        for(int i = 0; i < personRes.getData().size(); i++) {
            List<Event> eventsOfPerson = new ArrayList<>();
            for(int j = 0; j < eventRes.getData().size(); j++) {
                if(personRes.getData().get(i).getPersonID().equals(eventRes.getData().get(j).getPersonID())) {
                    eventsOfPerson.add(eventRes.getData().get(j));
                }
            }
            eventsOfPerson = DataCacheCalculator.sortEventsChronologically(eventsOfPerson);

            cache.addPerson(personRes.getData().get(i).getPersonID(), personRes.getData().get(i));
            cache.addEventsByPersonId(personRes.getData().get(i).getPersonID(), eventsOfPerson);
        }

        Person userPerson = cache.getPersonById(cache.getUserPersonId());
        Person mother = cache.getPersonById(userPerson.getMotherID());
        Person father = cache.getPersonById(userPerson.getFatherID());
        addToMotherSide(mother);
        addToFatherSide(father);

        sendMessage(eventRes.isSuccess() && personRes.isSuccess());
    }

    private void sendMessage(boolean isSuccess) {
        Message message = Message.obtain();

        DataCache cache = DataCache.getInstance();
        Bundle messageBundle = new Bundle();
        if(isSuccess == true) {
            messageBundle.putBoolean("isSuccess", true);
            Person user = cache.getPersonById(cache.getUserPersonId());

            messageBundle.putString("name", user.getFirstName() + " " + user.getLastName());
        }
        else {
            messageBundle.putBoolean("isSuccess", false);
        }
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }

    private void addToMotherSide(Person person) {
        cache.addMaternalAncestor(person);
        Person father = cache.getPersonById(person.getFatherID());
        Person mother = cache.getPersonById(person.getMotherID());

        if(father != null && mother != null) {
            addToMotherSide(father);
            addToMotherSide(mother);
        }
    }

    private void addToFatherSide(Person person) {
        cache.addPaternalAncestor(person);
        Person father = cache.getPersonById(person.getFatherID());
        Person mother = cache.getPersonById(person.getMotherID());

        if(father != null && mother != null) {
            addToFatherSide(father);
            addToFatherSide(mother);
        }
    }
}
