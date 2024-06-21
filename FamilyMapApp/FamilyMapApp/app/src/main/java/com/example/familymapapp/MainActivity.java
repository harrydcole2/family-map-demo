package com.example.familymapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;

import com.example.familymapapp.controller.DataCache;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import model.Authtoken;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            LoginFragment fragment = new LoginFragment();
            fragment.registerListener(this);

            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();
    }
}