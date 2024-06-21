package com.example.familymapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;

import com.example.familymapapp.controller.DataCache;

import model.Event;

public class EventActivity extends AppCompatActivity {

    Event event;
    DataCache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        cache = DataCache.getInstance();
        Intent intent = getIntent();
        event = cache.getEventById(intent.getStringExtra("eventId"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.event_fragment_container);
        if (currentFragment == null) {
            MapFragment fragment = new MapFragment();

            Bundle arguments = new Bundle();
            arguments.putString("eventToMark", event.getEventID());
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction().add(R.id.event_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}