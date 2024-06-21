package com.example.familymapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.DataCacheCalculator;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_RESULT_TYPE = 0;
    private static final int EVENT_RESULT_TYPE = 1;

    private EditText editText;
    private RecyclerView recyclerView;

    private DataCache cache = DataCache.getInstance();
    private List<Person> personResult = new ArrayList<>();
    private List<Event> eventResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        SearchAdapter adapter = new SearchAdapter(personResult, eventResult);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        cache = DataCache.getInstance();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                personResult.clear();
                eventResult.clear();
                String searchText = editText.getText().toString().toLowerCase();

                if(!searchText.trim().isEmpty()) {
                    personResult = DataCacheCalculator.calculatePersonSearchResults(searchText);
                    eventResult = DataCacheCalculator.calculateEventSearchResults(searchText);

                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                    SearchAdapter adapter = new SearchAdapter(personResult, eventResult);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
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

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<Person> personList;
        private List<Event> eventList;

        SearchAdapter(List<Person> personList, List<Event> eventList) {
            this.personList = personList;
            this.eventList = eventList;
        }

        @Override
        public int getItemViewType(int pos) {
            return pos < personList.size() ? PERSON_RESULT_TYPE : EVENT_RESULT_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_layout, parent, false);
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < personList.size()) {
                holder.bind(personList.get(position));
            }
            else {
                holder.bind(eventList.get(position - personList.size()));
            }
        }

        @Override
        public int getItemCount() {
            return personList.size() + eventList.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final int viewType;
        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView itemHeader;
        private TextView itemSubtext;

        private String idForNextActivity;

        public SearchViewHolder(@NonNull View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_horizontal_layout);
            imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            itemHeader = (TextView) itemView.findViewById(R.id.item_header);
            itemSubtext = (TextView) itemView.findViewById(R.id.item_subtext);
        }

        private void bind(Person person) {
            if(person.getGender().equalsIgnoreCase("m")) {
                Drawable maleIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).sizeDp(25)
                        .colorRes(R.color.cobalt_blue);
                imageView.setImageDrawable(maleIcon);
            }
            else {
                Drawable femaleIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).sizeDp(25)
                        .colorRes(R.color.rose);
                imageView.setImageDrawable(femaleIcon);
            }
            String name = person.getFirstName() + " " + person.getLastName();
            itemHeader.setText(name);

            idForNextActivity = person.getPersonID();
        }

        private void bind(Event event) {
            Drawable markerIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).sizeDp(25)
                    .colorRes(R.color.black);
            imageView.setImageDrawable(markerIcon);

            String details = event.getEventType().toUpperCase() + ": " + event.getCity() +
                    ", " + event.getCountry() + " (" + event.getYear() + ")";
            itemHeader.setText(details);

            Person eventPerson = cache.getPersonById(event.getPersonID());
            String name = eventPerson.getFirstName() + " " + eventPerson.getLastName();
            itemSubtext.setText(name);

            idForNextActivity = event.getEventID();
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if(viewType == PERSON_RESULT_TYPE) {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("personId", idForNextActivity);
            }
            else {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("eventId", idForNextActivity);
            }
            startActivity(intent);
        }
    }

}