package com.example.familymapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.DataCacheCalculator;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    Person person;
    DataCache cache;

    TextView firstName;
    TextView lastName;
    TextView gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        cache = DataCache.getInstance();

        Intent intent = getIntent();
        person = cache.getPersonById(intent.getStringExtra("personId"));

        firstName = findViewById(R.id.first_name_placeholder);
        lastName = findViewById(R.id.last_name_placeholder);
        gender = findViewById(R.id.gender_placeholder);

        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
        if(person.getGender().equalsIgnoreCase("m")) {
            gender.setText("Male");
        }
        else {
            gender.setText("Female");
        }

        List<Event> lifeStory = new ArrayList<>();
        if(cache.getEventsByPersonIdOnMap().get(person.getPersonID()) != null) {
            lifeStory = cache.getEventsByPersonIdOnMap().get(person.getPersonID());
        }

        ImmutablePair<List<Person>, List<String>> familyRelationships = DataCacheCalculator.calculateFamilyRelations(person);
        List<Person> familyMembers = familyRelationships.left;
        List<String> relationsToPerson = familyRelationships.right;

        ExpandableListView expandableListView = findViewById(R.id.expandable_list_view);
        expandableListView.setAdapter(new ExpandableListAdapter(lifeStory, familyMembers, relationsToPerson));
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

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        public static final int EVENT_GROUP_POSITION = 0;
        public static final int PERSON_GROUP_POSITION = 1;
        private List<Event> lifeStory;
        private List<Person> familyMembers;
        private List<String> familyRelations;

        ExpandableListAdapter(List<Event> lifeStory, List<Person> familyMembers, List<String> familyRelations) {
            this.lifeStory = lifeStory;
            this.familyMembers = familyMembers;
            this.familyRelations = familyRelations;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int i) {
            switch (i) {
                case EVENT_GROUP_POSITION:
                    return lifeStory.size();
                case PERSON_GROUP_POSITION:
                    return familyMembers.size();
                default:
                    throw new IllegalStateException("unrecognized group pos");
            }
        }

        @Override
        public Object getGroup(int i) {
            switch (i) {
                case EVENT_GROUP_POSITION:
                    return "Life Events";
                case PERSON_GROUP_POSITION:
                    return "Family";
                default:
                    throw new IllegalStateException("unrecognized group pos");
            }
        }

        @Override
        public Object getChild(int groupPos, int childPos) {
            switch (groupPos) {
                case EVENT_GROUP_POSITION:
                    return lifeStory.get(childPos);
                case PERSON_GROUP_POSITION:
                    return familyMembers.get(childPos);
                default:
                    throw new IllegalStateException("unrecognized group pos");
            }
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_group_layout, parent, false);
            }

            TextView title = convertView.findViewById(R.id.list_title);

            switch (i) {
                case EVENT_GROUP_POSITION:
                    title.setText("Life Events");
                    break;
                case PERSON_GROUP_POSITION:
                    title.setText("Family");
                    break;
                default:
                    throw new IllegalStateException("unrecognized group pos");
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = getLayoutInflater().inflate(R.layout.item_layout, parent, false);

            switch (groupPos) {
                case EVENT_GROUP_POSITION:
                    initLifeEvent(itemView, childPos);
                    break;
                case PERSON_GROUP_POSITION:
                    initFamMember(itemView,childPos);
                    break;
                default:
                    throw new IllegalStateException("unrecognized group pos");
            }

            return itemView;
        }

        private void initLifeEvent(View convertView, final int childPos) {

            Event event = lifeStory.get(childPos);

            TextView mainText = convertView.findViewById(R.id.item_header);
            TextView subtext = convertView.findViewById(R.id.item_subtext);
            LinearLayout itemLayout = convertView.findViewById(R.id.item_horizontal_layout);
            ImageView imageView = convertView.findViewById(R.id.item_icon);

            String details = event.getEventType().toUpperCase() + ": " + event.getCity() +
                    ", " + event.getCountry() + " (" + event.getYear() + ")";
            mainText.setText(details);
            String name = person.getFirstName() + " " + person.getLastName();
            subtext.setText(name);

            Drawable markerIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).sizeDp(25)
                    .colorRes(R.color.black);
            imageView.setImageDrawable(markerIcon);

            itemLayout.setOnClickListener(view -> {
                Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                intent.putExtra("eventId", event.getEventID());
                startActivity(intent);
            });
        }

        private void initFamMember(View convertView, final int childPos) {
            Person relative = familyMembers.get(childPos);
            if(relative == null) {
                return;
            }

            TextView itemHeader = convertView.findViewById(R.id.item_header);
            TextView itemSubtext = convertView.findViewById(R.id.item_subtext);
            LinearLayout itemLayout = convertView.findViewById(R.id.item_horizontal_layout);
            ImageView imageView = convertView.findViewById(R.id.item_icon);

            String relativeName = relative.getFirstName() + " " + relative.getLastName();
            itemHeader.setText(relativeName);
            itemSubtext.setText(familyRelations.get(childPos));

            if(relative.getGender().equalsIgnoreCase("m")) {
                Drawable maleIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).sizeDp(25)
                        .colorRes(R.color.cobalt_blue);
                imageView.setImageDrawable(maleIcon);
            }
            else {
                Drawable femaleIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).sizeDp(25)
                        .colorRes(R.color.rose);
                imageView.setImageDrawable(femaleIcon);
            }

            itemLayout.setOnClickListener(view -> {
                Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                intent.putExtra("personId", relative.getPersonID());
                startActivity(intent);
            });
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}