package com.example.nucle.firstnewsapi.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;

import com.example.nucle.firstnewsapi.R;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_BACKUP;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_FROM_DATE;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_ORDER_BY;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_PAGE_NUMBER;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_PAGE_SIZE;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH_CONTENT;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH_DEFAULT;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH_TAG;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SECTION;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SHOW_IMG;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SHOW_REFERENCE;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_TO_DATE;

/**
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class SettingsFragments extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "Settings Activity";
    private SharedPreferences sharedPref;

    public SettingsFragments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get specific preference category fragment from user choice
        String settings = getArguments().getString("settings");
        if ("content search".equals(settings)) {
            addPreferencesFromResource(R.xml.content_preferences);
        } else if ("tag search".equals(settings)) {
            addPreferencesFromResource(R.xml.tag_preferences);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get reference to the SharedPreferences file for this activity
        sharedPref = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Get all preferences values
        Preference searchKey = findPreference(getString(R.string.settings_search_key));
        preferenceSummary(searchKey);

        Preference sectionKey = findPreference(getString(R.string.settings_section_key));
        preferenceSummary(sectionKey);

        Preference orderByTime = findPreference(getString(R.string.settings_order_by_key));
        if (orderByTime != null) {
            preferenceSummary(orderByTime);
        }
        Preference pageSize = findPreference(getString(R.string.settings_page_size_key));
        preferenceSummary(pageSize);

        Preference pageNumber = findPreference(getString(R.string.settings_page_number_key));
        preferenceSummary(pageNumber);

        Preference fromDate = findPreference(getString(R.string.settings_order_by_start_date_key));
        if (fromDate != null) {
            preferenceSummary(fromDate);
        }
        Preference toDate = findPreference(getString(R.string.settings_order_by_end_date_key));
        if (toDate != null) {
            preferenceSummary(toDate);
        }
        Preference backup = findPreference(getString(R.string.settings_local_storage_key));
        backup.setOnPreferenceChangeListener(this);

        Preference showImg = findPreference(getString(R.string.settings_query_images_key));
        showImg.setOnPreferenceChangeListener(this);

        Preference showReferences = findPreference(getString(R.string.settings_show_references_key));
        if (showReferences != null) {
            preferenceSummary(showReferences);
        }

        Preference searchContent = findPreference(getString(R.string.settings_query_content_key));
        if (searchContent != null) {
            searchContent.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Method is used to update preferences values
     *
     * @param preference Get Preference
     */
    private void preferenceSummary(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        String preferenceString = preferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, preferenceString);
    }
    /**
     * Preference change listener
     *
     * @param preference Get Preference
     * @param o preference value
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        String stringValue = o.toString();
        switch (preference.getKey()) {
            case KEY_PREF_FROM_DATE:
                Log.d(TAG, "Preference change" + KEY_PREF_FROM_DATE + "->" + stringValue);
                preference.setSummary(stringValue);
                break;

            case KEY_PREF_TO_DATE:
                Log.d(TAG, "Preference change" + KEY_PREF_TO_DATE + "->" + stringValue);
                preference.setSummary(stringValue);
                break;

            case KEY_PREF_SECTION:
                Log.d(TAG, "Preference change" + KEY_PREF_SECTION + "->" + stringValue);
                ListPreference listSecPreference = (ListPreference) preference;
                int secIndex = listSecPreference.findIndexOfValue(stringValue);
                if (secIndex >= 0) {
                    CharSequence[] labels = listSecPreference.getEntries();
                    preference.setSummary(labels[secIndex]);
                }
                break;

            case KEY_PREF_ORDER_BY:
                Log.d(TAG, "Preference change" + KEY_PREF_ORDER_BY + "->" + stringValue);
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
                break;

            case KEY_PREF_PAGE_NUMBER:
                Log.d(TAG, "Preference change" + KEY_PREF_PAGE_NUMBER + "->" + stringValue);
                preference.setSummary(stringValue);
                break;

            case KEY_PREF_PAGE_SIZE:
                Log.d(TAG, "Preference change" + KEY_PREF_PAGE_SIZE + "->" + stringValue);
                preference.setSummary(stringValue);
                break;

            case KEY_PREF_SEARCH:
                Log.d(TAG, "Preference change" + KEY_PREF_SEARCH + "->" + stringValue);
                preference.setSummary(stringValue);
                break;

            case KEY_PREF_SHOW_REFERENCE:
                Log.d(TAG, "Preference change" + KEY_PREF_SHOW_REFERENCE + "->" + stringValue);
                ListPreference showReference = (ListPreference) preference;
                int showPrefIndex = showReference.findIndexOfValue(stringValue);
                if (showPrefIndex >= 0) {
                    CharSequence[] labels = showReference.getEntries();
                    preference.setSummary(labels[showPrefIndex]);
                }
                break;

            case KEY_PREF_BACKUP:
                Log.d(TAG, "Preference change" + KEY_PREF_BACKUP + "->" + stringValue);
                boolean value = sharedPref.getBoolean(
                        KEY_PREF_BACKUP, Boolean.parseBoolean(""));
                writeSharedPreferences(KEY_PREF_BACKUP, value);
                break;

            case KEY_PREF_SHOW_IMG:
                Log.d(TAG, "Preference change" + KEY_PREF_SHOW_IMG + "->" + stringValue);
                boolean valueImg = sharedPref.getBoolean(
                        KEY_PREF_SHOW_IMG, Boolean.parseBoolean(""));
                writeSharedPreferences(KEY_PREF_SHOW_IMG, valueImg);
                break;

            case KEY_PREF_SEARCH_CONTENT:
                Log.d(TAG, "Preference change" + KEY_PREF_SEARCH_CONTENT + "->" + stringValue);
                writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, Boolean.parseBoolean(stringValue));
                writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, false);
                writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                break;

            case KEY_PREF_SEARCH_TAG:
                Log.d(TAG, "Preference change" + KEY_PREF_SEARCH_TAG + "->" + stringValue);
                writeSharedPreferences(KEY_PREF_SEARCH_TAG, Boolean.parseBoolean(stringValue));
                writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, false);
                writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                break;
        }
        return true;
    }

    /**
     * Method is used to write requested value to Preference Checkboxes
     *
     * @param preference String preference Constant
     * @param value      value true or false
     */
    private void writeSharedPreferences(String preference, boolean value) {
        SharedPreferences.Editor prefs = android.preference.PreferenceManager
                .getDefaultSharedPreferences(getActivity()).edit();
        prefs.putBoolean(preference, value);
        prefs.apply();
    }
}


