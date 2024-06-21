package com.example.familymapapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.familymapapp.controller.ColorManagerCache;
import com.example.familymapapp.controller.DataCache;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_heirarchy, rootKey);

        Preference logoutButton = getPreferenceManager().findPreference("logout");
        if (logoutButton != null) {
            logoutButton.setOnPreferenceClickListener(arg0 -> {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction("" + Math.random());

                DataCache cache = DataCache.getInstance();
                cache.clearCache();
                ColorManagerCache colorCache = ColorManagerCache.getInstance();
                colorCache.clearCache();

                startActivity(intent);
                return true;
            });
        }
    }
}
