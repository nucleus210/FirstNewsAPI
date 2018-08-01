package com.example.nucle.firstnewsapi.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.nucle.firstnewsapi.R;

import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "Settings Activity";
    public static final String KEY_PREF_BACKUP = "backup";
    public static final String KEY_PREF_SEARCH = "search";
    public static final String KEY_PREF_SECTION = "section";
    public static final String KEY_PREF_FROM_DATE = "from-date";
    public static final String KEY_PREF_TO_DATE = "to-date";
    public static final String KEY_PREF_PAGE_SIZE = "page-size";
    public static final String KEY_PREF_PAGE_NUMBER = "page";
    public static final String KEY_PREF_ORDER_BY = "order-by";
    public static final String KEY_PREF_SHOW_IMG = "show-fields";
    public static final String KEY_PREF_SEARCH_DEFAULT = "show-default";
    public static final String KEY_PREF_SEARCH_CONTENT = "show-content";
    public static final String KEY_PREF_SEARCH_TAG = "show-tag";
    public static final String KEY_PREF_SHOW_REFERENCE = "show-references";
    public static final String KEY_PREF_FAV_REFERENCE = "fav";

    public SettingsActivity() {
        // Required empty public constructor
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        // load preference headers and create header groups
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        getDelegate().installViewFactory();
        getDelegate().getMenuInflater();
        return SettingsFragments.class.getName().equals(fragmentName) ||
                GeneralSettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.back_button:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

