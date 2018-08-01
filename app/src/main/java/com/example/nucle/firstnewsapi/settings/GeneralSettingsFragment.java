package com.example.nucle.firstnewsapi.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;

import com.example.nucle.firstnewsapi.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_BACKUP;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH_CONTENT;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH_DEFAULT;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SEARCH_TAG;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_SHOW_IMG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralSettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "General Settings";
    private SharedPreferences sharedPref;

    public GeneralSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GeneralSettingsFragment.
     */
    public static GeneralSettingsFragment newInstance() {
        return new GeneralSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String settings = getArguments().getString("settings");

        if ("general settings".equals(settings)) {
            addPreferencesFromResource(R.xml.general_preferences);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get reference to the SharedPreferences file for this activity
        sharedPref = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Get preferences values
        Preference showImg = findPreference(getString(R.string.settings_query_images_key));
        showImg.setOnPreferenceChangeListener(this);

        Preference searchDefault = findPreference(getString(R.string.settings_query_default_key));
        searchDefault.setOnPreferenceChangeListener(this);

        Preference searchContent = findPreference(getString(R.string.settings_query_content_key));
        searchContent.setOnPreferenceChangeListener(this);

        Preference searchTag = findPreference(getString(R.string.settings_query_tag_key));
        searchTag.setOnPreferenceChangeListener(this);

        Preference backupSwish = findPreference(getString(R.string.settings_local_storage_key));
        backupSwish.setOnPreferenceChangeListener(this);

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
            case KEY_PREF_SEARCH_DEFAULT:
                Log.d(TAG, "Preference change" + KEY_PREF_SEARCH_DEFAULT + "->" + stringValue);
                writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, Boolean.parseBoolean(stringValue));
                uncheckDependOptions(KEY_PREF_SEARCH_CONTENT, KEY_PREF_SEARCH_TAG);
                writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                break;

            case KEY_PREF_SEARCH_CONTENT:
                Log.d(TAG, "Preference change" + KEY_PREF_SEARCH_CONTENT + "->" + stringValue);
                writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, Boolean.parseBoolean(stringValue));
                uncheckDependOptions(KEY_PREF_SEARCH_DEFAULT, KEY_PREF_SEARCH_TAG);
                writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, false);
                writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                break;

            case KEY_PREF_SEARCH_TAG:
                Log.d(TAG, "Preference change" + KEY_PREF_SEARCH_TAG + "->" + stringValue);
                writeSharedPreferences(KEY_PREF_SEARCH_TAG, Boolean.parseBoolean(stringValue));
                uncheckDependOptions(KEY_PREF_SEARCH_DEFAULT, KEY_PREF_SEARCH_CONTENT);
                writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, false);
                writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                break;

            case KEY_PREF_BACKUP:
                Log.d(TAG, "Preference change" + KEY_PREF_BACKUP + "->" + stringValue);
                writeSharedPreferences(KEY_PREF_BACKUP, Boolean.parseBoolean(stringValue));
                break;

            case KEY_PREF_SHOW_IMG:
                Log.d(TAG, "Preference change" + KEY_PREF_SHOW_IMG + "->" + stringValue);
                boolean valueImg = sharedPref
                        .getBoolean(KEY_PREF_SHOW_IMG, Boolean.parseBoolean(""));
                writeSharedPreferences(KEY_PREF_SHOW_IMG, valueImg);
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

    /**
     * Method is used to un check Preference Checkboxes
     *
     * @param optA Check box A
     * @param optB Check Box B
     */
    private void uncheckDependOptions(String optA, String optB) {
        CheckBoxPreference checkBoxA = (CheckBoxPreference) findPreference(optA);
        checkBoxA.setChecked(false);
        CheckBoxPreference checkBoxB = (CheckBoxPreference) findPreference(optB);
        checkBoxB.setChecked(false);
    }
}
