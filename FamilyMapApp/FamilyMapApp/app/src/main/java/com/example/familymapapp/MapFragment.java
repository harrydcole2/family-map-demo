package com.example.familymapapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.familymapapp.controller.ColorManagerCache;
import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.DataCacheCalculator;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap = null;

    SharedPreferences sharedPreferences;
    DataCache cache;
    TextView personName;
    TextView eventDetails;
    ImageView imageView;
    LinearLayout eventDetailsLayout;

    String personIdForSelectedMarker = null;
    String eventIdToCenterOn = null;
    List<Polyline> AllPolylines = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        eventDetails = (TextView) view.findViewById(R.id.event_details);
        personName = (TextView) view.findViewById(R.id.person_name);
        imageView = (ImageView) view.findViewById(R.id.event_image);
        eventDetailsLayout = (LinearLayout) view.findViewById(R.id.event_details_layout);

        FragmentManager manager = getChildFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) manager.findFragmentById(R.id.map_support_fragment);
        fragment.getMapAsync(this);

        if(getArguments() != null) {
            eventIdToCenterOn = getArguments().getString("eventToMark");
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        eventDetailsLayout.setOnClickListener(view -> {
            if(personIdForSelectedMarker != null) {
                Intent intent = new Intent(getContext(), PersonActivity.class);
                intent.putExtra("personId", personIdForSelectedMarker);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(eventIdToCenterOn == null) {
            super.onCreateOptionsMenu(menu, inflater);

            inflater.inflate(R.menu.map_menu, menu);

            MenuItem searchMenuItem = menu.findItem(R.id.menuSearch);
            searchMenuItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_search).actionBarSize()
                    .colorRes(R.color.white));

            MenuItem settingsMenuItem = menu.findItem(R.id.menuSettings);
            settingsMenuItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_gear).actionBarSize()
                    .colorRes(R.color.white));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {

        int id = menu.getItemId();
        if(id == R.id.menuSearch) {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.menuSettings) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else {
            return super.onOptionsItemSelected(menu);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if(mMap != null) {
            mMap.clear();
            cache.getEventsByPersonIdOnMap().clear();
            markupMap();
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);

        Drawable search = new IconDrawable(getContext(), FontAwesomeIcons.fa_search).sizeDp(25);
        imageView.setImageDrawable(search);

        markupMap();
        if(eventIdToCenterOn != null) {
            populateMarkerDetails(cache.getEventById(eventIdToCenterOn));
            removeAllLines();
            drawLines(cache.getEventById(eventIdToCenterOn));
        }
    }

    private void markupMap() {
        cache = DataCache.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean fatherFilter = sharedPreferences.getBoolean("fatherFilter",true);
        boolean motherFilter = sharedPreferences.getBoolean("motherFilter", true);
        boolean maleFilter = sharedPreferences.getBoolean("maleFilter", true);
        boolean femaleFilter = sharedPreferences.getBoolean("femaleFilter", true);

        List<Event> eventsToDisplay = DataCacheCalculator.calculateEventsToDisplay(fatherFilter,
                motherFilter,maleFilter,femaleFilter);

        for(int i = 0; i < eventsToDisplay.size(); i++) {
            addEventToMap(eventsToDisplay.get(i));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event event = (Event) marker.getTag();

                populateMarkerDetails(event);
                removeAllLines();
                drawLines(event);

                return true;
            }
        });
    }

    private void addEventToMap(Event event) {
        ColorManagerCache colorCache = ColorManagerCache.getInstance();

        float googleColor;
        if(colorCache.getMarkerColors().containsKey(event.getEventType().toLowerCase())) {
            googleColor = colorCache.getMarkerColors().get(event.getEventType().toLowerCase());
        }
        else {
            googleColor = colorCache.nextColor();
            colorCache.addMarkerColor(event.getEventType().toLowerCase(), googleColor);
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLatitude(), event.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(googleColor)));

        marker.setTag(event);
    }

    private void populateMarkerDetails(Event event) {
        String details = event.getEventType().toUpperCase() + ": " + event.getCity() +
                ", " + event.getCountry() + " (" + event.getYear() + ")";
        eventDetails.setText(details);
        String name = cache.getPersonById(event.getPersonID()).getFirstName() + " " +
                cache.getPersonById(event.getPersonID()).getLastName();
        personName.setText(name);

        Person person = cache.getPersonById(event.getPersonID());
        personIdForSelectedMarker = person.getPersonID();

        if(person.getGender().equalsIgnoreCase("m")) {
            Drawable maleIcon = new IconDrawable(getContext(), FontAwesomeIcons.fa_male).sizeDp(25)
                    .colorRes(R.color.cobalt_blue);
            imageView.setImageDrawable(maleIcon);
        }
        else {
            Drawable femaleIcon = new IconDrawable(getContext(), FontAwesomeIcons.fa_female).sizeDp(25)
                    .colorRes(R.color.rose);
            imageView.setImageDrawable(femaleIcon);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.getLatitude(), event.getLongitude())));
    }

    private void drawLines(Event event) {
        Person person = cache.getPersonById(event.getPersonID());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(sharedPreferences.getBoolean("spouseLineToggle", true)) {
            Person spouse = cache.getPersonById(person.getSpouseID());

            if(spouse != null) {
                List<Event> spouseEvents = cache.getEventsByPersonIdOnMap().get(spouse.getPersonID());
                if(spouseEvents != null) {
                    Event spouseBirth = spouseEvents.get(0);
                    LatLng start = new LatLng(event.getLatitude(), event.getLongitude());
                    LatLng end = new LatLng(spouseBirth.getLatitude(), spouseBirth.getLongitude());
                    PolylineOptions options = new PolylineOptions().add(start).add(end).color(Color.RED);
                    Polyline line = mMap.addPolyline(options);

                    AllPolylines.add(line);
                }
            }
        }

        if(sharedPreferences.getBoolean("lifeStoryToggle", true)) {
            List<Event> lifeStory = cache.getEventsByPersonId(person.getPersonID());
            for(int i = 0; i < lifeStory.size() - 1; i++) {
                LatLng start = new LatLng(lifeStory.get(i).getLatitude(), lifeStory.get(i).getLongitude());
                LatLng end = new LatLng(lifeStory.get(i+1).getLatitude(), lifeStory.get(i+1).getLongitude());

                PolylineOptions options = new PolylineOptions().add(start).add(end).color(Color.GREEN);
                Polyline line = mMap.addPolyline(options);

                AllPolylines.add(line);
            }
        }

        if(sharedPreferences.getBoolean("familyLineToggle", true)) {
            float width = 20;
            generateTreeLines(event, width);
        }
    }

    private void generateTreeLines(Event event, Float width) {
        Person person = cache.getPersonById(event.getPersonID());
        Person father = cache.getPersonById(person.getFatherID());
        Person mother = cache.getPersonById(person.getMotherID());

        if(father != null && mother != null) {
            LatLng start = new LatLng(event.getLatitude(), event.getLongitude());

            if(cache.getEventsByPersonIdOnMap().get(father.getPersonID()) != null) {
                LatLng end = new LatLng(cache.getEventsByPersonId(father.getPersonID()).get(0).getLatitude(),
                        cache.getEventsByPersonId(father.getPersonID()).get(0).getLongitude());

                PolylineOptions options = new PolylineOptions().add(start).add(end).color(Color.BLUE)
                        .width(width);
                Polyline line = mMap.addPolyline(options);
                AllPolylines.add(line);
                generateTreeLines(cache.getEventsByPersonId(father.getPersonID()).get(0), width / 2);
            }

            if(cache.getEventsByPersonIdOnMap().get(mother.getPersonID()) != null) {
                LatLng end2 = new LatLng(cache.getEventsByPersonId(mother.getPersonID()).get(0).getLatitude(),
                        cache.getEventsByPersonId(mother.getPersonID()).get(0).getLongitude());

                PolylineOptions options2 = new PolylineOptions().add(start).add(end2).color(Color.BLUE)
                        .width(width);
                Polyline line2 = mMap.addPolyline(options2);
                AllPolylines.add(line2);
                generateTreeLines(cache.getEventsByPersonId(mother.getPersonID()).get(0), width / 2);
            }
        }
    }

    private void removeAllLines() {
        for(Polyline line : AllPolylines) {
            line.remove();
        }
    }

}