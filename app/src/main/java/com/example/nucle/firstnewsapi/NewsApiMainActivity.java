package com.example.nucle.firstnewsapi;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nucle.firstnewsapi.connection.ConnectivityApplication;
import com.example.nucle.firstnewsapi.connection.ConnectivityReceiver;
import com.example.nucle.firstnewsapi.models.DataHolderModel;
import com.example.nucle.firstnewsapi.settings.SettingsActivity;
import com.example.nucle.firstnewsapi.ui.newsapiList.FavItemListFragment;
import com.example.nucle.firstnewsapi.ui.newsapiList.NewsListFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

public class NewsApiMainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NewsListFragment.OnListFragmentInteractionListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        DownloadCallback, SearchView.OnQueryTextListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        FavItemListFragment.OnFavFragmentInteractionListener {

    private static final String TAG = "News Activity";
    private static final String DOMAIN_NAME = "content.guardianapis.com";
    private static final String[] FILTER_IMG = {"show-fields", "thumbnail"};
    private static final String[] FILTER_NO_IMG = {"", ""};
    private static final String URL_KEY = "api-key";
    private static final String DEFAULT_PATH = "search";

    private int mConnectionState;                   // Connection STATES
    private HorizontalScrollView mChipLayout;       // Horizontal Scroll View for Chips
    private TextView mSystemLogger;                 // System Logger Text View
    private Context mContext;                       // Application context
    private String mEndPoint;                       // Server Search endpoint holder for Chips

    DownloadCallback mCallback;                     // Download call backs from Network Loader
    NetworkFragment mNetworkFragment;               // Fragment reference
    NewsListFragment newsListFragment;              // Network List Fragment
    FavItemListFragment favItemListFragment;        // Favorite List Fragment
    private SearchView mSearchView;                 // ToolBar search view
    private ConnectivityReceiver receiver;          // Local network Listener

    private Chip mTagChip;                          // Server Search end point chip --> Tag
    private Chip mEditorChip;                       // Server Search end point chip --> Editor
    private Chip mSingleItem;                       // Server Search end point chip --> Single Item
    private Chip mContentChip;                      // Server Search end point chip --> Content
    private Chip mSectionChip;                      // Server Search end point chip --> Section
    private boolean isChipVisible = false;          // Chips visibility boolean
    private boolean isChipSelected = false;         // Chips selection boolean
    private boolean isPathLookupSelected = false;   // Chips selection boolean
    private boolean isAuthorSelected = false;       // Chips selection boolean
    private boolean isMainSearch = false;           // From Tool bar input user search

    SharedPreferences sharedPref;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_api_main_activity);
        Log.d(TAG, "onCreate");
        setState(Codes.STATE_NONE);
        mContext = getBaseContext();

        // Get reference to the SharedPreferences file for this activity
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // Register OnSharedPreference Change listener
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
        // get tool bar view
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        // register Network connection Broadcast Receiver
        receiver = new ConnectivityReceiver();
        this.registerReceiver(receiver, filter);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get Views
        mSystemLogger = findViewById(R.id.system_logger);
        mChipLayout = findViewById(R.id.chip_layout);
        mChipLayout.setVisibility(View.GONE);
        mTagChip = findViewById(R.id.tag_search);
        mEditorChip = findViewById(R.id.by_author);
        mContentChip = findViewById(R.id.content_search);
        mSectionChip = findViewById(R.id.section_search);
        mSingleItem = findViewById(R.id.path_lookup);

        // Get Views
        // Chips Group
        ChipGroup mChipGroup = findViewById(R.id.search_groups);
        mChipGroup.setChipSpacing(8);
        mChipGroup.invalidate();
        // get shared preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        /*
          OnClick Listeners Chips
         */
        mChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                switch (i) {
                    case R.id.content_search:
                        mEndPoint = getString(R.string.endpoint_search);
                        isChipSelected = true;
                        writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, true);
                        writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                        writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                        break;

                    case R.id.section_search:
                        mEndPoint = getString(R.string.endpoint_sections);
                        isChipSelected = true;
                        writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, true);
                        writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                        writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                        break;

                    case R.id.tag_search:
                        mEndPoint = getString(R.string.endpoint_tags);
                        isChipSelected = true;
                        writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, true);
                        writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                        writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                        break;

                    case R.id.by_author:
                        isAuthorSelected = true;
                        isChipSelected = false;
                        writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, true);
                        writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                        writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                        break;

                    case R.id.path_lookup:
                        isPathLookupSelected = true;
                        isChipSelected = false;
                        writeSharedPreferences(KEY_PREF_SEARCH_DEFAULT, true);
                        writeSharedPreferences(KEY_PREF_SEARCH_CONTENT, false);
                        writeSharedPreferences(KEY_PREF_SEARCH_TAG, false);
                        break;
                }
            }
        });
        //  startDownload(DEFAULT_PATH);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkConnection();
        // Host Activity will handle callbacks from task.
        mCallback = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register OnSharedPreference listener
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        // register connection status listener
        ConnectivityApplication.getInstance().setConnectivityListener(this);
        startDownload(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister OnSharedPreference listener
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);

        mCallback = null;
        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Create Options Menu
     *
     * @param menu Menu
     * @return boolean
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(
                Objects.requireNonNull(searchManager).getSearchableInfo(getComponentName()));
        mSearchView.setIconified(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChipVisible) {
                    mChipLayout.setVisibility(View.VISIBLE);
                    isChipVisible = true;
                }
            }
        });
        return true;
    }

    /**
     * Handle selected option from Action Bar
     *
     * @param item Menu items
     * @return selected option
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.fav_fragment:
                if (findViewById(R.id.container) != null) {
                    if (getFragmentManager().findFragmentByTag("FAV_FRAGMENT") == null) {
                        Log.e(TAG, "Fragment inflated");
                        favItemListFragment =
                                FavItemListFragment.newInstance(getSupportFragmentManager());
                    }
                }
                return true;

            case R.id.settings:
                Log.d(TAG, "Setting Fragment");

                // Start Setting Activity
                Intent intent = new Intent(
                        NewsApiMainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Handle selected option from Navigation Drawer
     *
     * @param item Menu items
     * @return boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.favorites:
                if (findViewById(R.id.container) != null) {
                    if (getFragmentManager().findFragmentByTag("FAV_FRAGMENT") == null) {
                        Log.e(TAG, "Fragment inflated");
                        favItemListFragment =
                                FavItemListFragment.newInstance(getSupportFragmentManager());
                    }
                }
                break;

            case R.id.nav_connect:
                checkConnection();
                return true;

            case R.id.nav_settings:
                // Start Setting Activity
                Intent intent = new Intent(
                        NewsApiMainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_share:
                Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                return true;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onListFragmentInteraction(String url) {
        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(implicit);
    }


    /**
     * Method to handle intend from user search input
     * This method work. After Using Intent Give me twice result. Later found is emulator BUG
     * Switch to this approach
     *
     * @param msg User search intent submit text
     */

    @Override
    public boolean onQueryTextSubmit(String msg) {
        Log.d(TAG, "Search result: " + msg);
        if (isChipVisible) {
            mSearchView.clearDisappearingChildren();
            mSearchView.onActionViewCollapsed();
            mChipLayout.setVisibility(View.GONE);
            isChipVisible = false;
            isMainSearch = true;
        }
        startDownload(msg);
        return false;
    }

    /**
     * Method to handle intend from user search input
     *
     * @param msg User search text listener
     */

    @Override
    public boolean onQueryTextChange(String msg) {
        Log.d(TAG, "Search result: " + msg);
        return false;
    }

    /**
     * Method to manually check connection status
     */
    private void startDownload(String userQuery) {
        Log.d(TAG, "Start Download!");
        boolean isCon = checkConnection();
        if (isCon && mConnectionState == Codes.STATE_NONE) {
            DataHolderModel.getInstance(mContext).clearData();
            clearRecycle();
            String apiKey = BuildConfig.ApiKey;
            String[] apiKeyBuild = {URL_KEY, apiKey};
            String[] selectedQuery = {"q", userQuery};
            String url;

            boolean isContentSearch = sharedPref.getBoolean(
                    KEY_PREF_SEARCH_CONTENT, Boolean.parseBoolean(""));
            boolean isTagSearch = sharedPref.getBoolean(
                    KEY_PREF_SEARCH_TAG, Boolean.parseBoolean(""));
            boolean isDefaultSearch = sharedPref.getBoolean(
                    KEY_PREF_SEARCH_DEFAULT, Boolean.parseBoolean(""));

            if (isContentSearch) {
                url = ContentSettingsRender(userQuery);

            } else if (isTagSearch) {
                url = tagSettingRender(userQuery);

            } else if (isDefaultSearch && isPathLookupSelected) {
                url = urlPathBuilder(userQuery, apiKeyBuild);
                isPathLookupSelected = false;

            } else if (isDefaultSearch && isAuthorSelected) {
                String endpoint = getString(R.string.endpoint_tags);
                String filter = userQuery + getString(R.string.contributor);
                String[] authorQuery = {"q", filter};
                url = urlBuilder(endpoint, authorQuery, FILTER_IMG, apiKeyBuild);
                isAuthorSelected = false;

            } else if (isDefaultSearch && isChipSelected) {
                url = urlBuilder(mEndPoint, selectedQuery, FILTER_IMG, apiKeyBuild);
                isChipSelected = false;

            } else {
                url = urlBuilder(DEFAULT_PATH, selectedQuery, FILTER_IMG, apiKeyBuild);
            }

            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), url);
            uncheckChips();
            setState(Codes.STATE_CONNECTING);
            Log.d(TAG, "NewStateDown: " + mConnectionState);
        }
    }

    /**
     * Method to manually check connection status
     *
     * @return connection state true of false
     */
    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        return isConnected;
    }

    /**
     * Showing the status in Snack bar if device has no wifi and mobile data connection
     *
     * @param isConnected Result from network check true or false
     */

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = getString(R.string.connected_to_internet);
        } else {
            message = getString(R.string.disconnected_from_internet);
        }
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.container), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Method is used to compose URL query and filter main searches endpoints
     *
     * @param endpoint    End point searches like Content, Section, Tag and etc.
     * @param queryParamA user input search from ToolBar
     * @return myUrl Composed Url
     */

    private String urlBuilder(@NonNull String endpoint,
                              @NonNull String[] queryParamA,
                              @NonNull String[] queryParamB,
                              @NonNull String[] apiKey) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.protocol))
                .authority(DOMAIN_NAME)
                .appendPath(endpoint)
                .appendQueryParameter(queryParamA[0], queryParamA[1])
                .appendQueryParameter(queryParamB[0], queryParamB[1])
                .appendQueryParameter(apiKey[0], apiKey[1]);
        String myUrl = builder.build().toString();

        Log.e(TAG, "myUrls" + myUrl);
        return myUrl;
    }

    /**
     * Method is used to compose URL query and filter main searches endpoints
     *
     * @param endpoint End point searches like Content, Section, Tag and etc.
     * @return myUrl Composed Url
     */
    private String urlPathBuilder(@NonNull String endpoint, @NonNull String[] apiKey) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.protocol))
                .authority(DOMAIN_NAME)
                .appendPath(endpoint)
                .appendQueryParameter(apiKey[0], apiKey[1]);

        String myUrl = builder.build().toString();

        Log.e(TAG, "myUrls" + myUrl);
        return myUrl;
    }

    /**
     * Method is used to compose URL query and filter advanced settings searches by content
     *
     * @param endpoint    End point searches like Content, Section, Tag and etc.
     * @param section     user input search from ToolBar
     * @param fromDate    starting Date
     * @param toDate      end Date
     * @param order       Order By Oldest, Newest, Relevant
     * @param pageN       current page
     * @param pageSize    How many pages to be displayed
     * @param filterQuery Search phrase
     * @param filterImg   result that contains images
     * @return myUrl Composed Url
     */

    private String urlSettingsContainBuilder(@NonNull String   endpoint,
                                             @NonNull String[] section,
                                             @NonNull String[] fromDate,
                                             @NonNull String[] toDate,
                                             @NonNull String[] order,
                                             @NonNull String[] pageN,
                                             @NonNull String[] pageSize,
                                             @NonNull String[] filterQuery,
                                             @NonNull String[] filterImg,
                                             @NonNull String[] apiKey) {

        Uri.Builder builder = new Uri.Builder();

        builder.scheme(getString(R.string.protocol))
                .authority(DOMAIN_NAME)
                .appendPath(endpoint)
                .appendQueryParameter(section[0], section[1])
                .appendQueryParameter(fromDate[0], fromDate[1])
                .appendQueryParameter(toDate[0], toDate[1])
                .appendQueryParameter(order[0], order[1])
                .appendQueryParameter(pageN[0], pageN[1])
                .appendQueryParameter(pageSize[0], pageSize[1])
                .appendQueryParameter(filterQuery[0], filterQuery[1])
                .appendQueryParameter(filterImg[0], filterImg[1])
                .appendQueryParameter(apiKey[0], apiKey[1]);
        String myUrl = builder.build().toString();
        Log.e(TAG, "myUrls" + myUrl);
        return myUrl;
    }

    /**
     * Method is used to compose URL query and filter advanced settings searches by tag
     *
     * @param endpoint    End point searches like Content, Section, Tag and etc.
     * @param section     user input search from ToolBar
     * @param pageN       current page
     * @param pageSize    How many pages to be displayed
     * @param filterQuery Search phrase
     * @param filterImg   result that contains images
     * @return myUrl Composed Url
     */

    private String urlSettingsTagBuilder(@NonNull String   endpoint,
                                         @NonNull String[] section,
                                         @NonNull String[] showRef,
                                         @NonNull String[] pageN,
                                         @NonNull String[] pageSize,
                                         @NonNull String[] filterQuery,
                                         @NonNull String[] filterImg,
                                         @NonNull String[] apiKey) {

        Uri.Builder builder = new Uri.Builder();

        builder.scheme(getString(R.string.protocol))
                .authority(DOMAIN_NAME)
                .appendPath(endpoint)
                .appendQueryParameter(section[0], section[1])
                .appendQueryParameter(showRef[0], showRef[1])
                .appendQueryParameter(pageN[0], pageN[1])
                .appendQueryParameter(pageSize[0], pageSize[1])
                .appendQueryParameter(filterQuery[0], filterQuery[1])
                .appendQueryParameter(filterImg[0], filterImg[1])
                .appendQueryParameter(apiKey[0], apiKey[1]);
        String myUrl = builder.build().toString();
        Log.e(TAG, "myUrls" + myUrl);
        return myUrl;
    }

    /**
     * Method is used to compose URL query and filter main searches endpoints
     *
     * @return myUrl Composed Url
     */
    private String ContentSettingsRender(String search) {

        String url;

        String[] selection = {SettingsActivity.KEY_PREF_SECTION,
                sharedPref.getString(SettingsActivity.KEY_PREF_SECTION, "")};

        // if user type in search input write new value to setting
        if (isMainSearch) {
            writeSearchPreferences(KEY_PREF_SEARCH, search);
            isMainSearch = false;
        }

        String[] selectedQuery = {"q",
                sharedPref.getString(SettingsActivity.KEY_PREF_SEARCH, "")};

        String[] fromDate = {SettingsActivity.KEY_PREF_FROM_DATE,
                sharedPref.getString(SettingsActivity.KEY_PREF_FROM_DATE, "")};

        String[] toDate = {SettingsActivity.KEY_PREF_TO_DATE,
                sharedPref.getString(SettingsActivity.KEY_PREF_TO_DATE, "")};

        String[] orderBy = {SettingsActivity.KEY_PREF_ORDER_BY,
                sharedPref.getString(SettingsActivity.KEY_PREF_ORDER_BY, "")};

        String[] pageN = {SettingsActivity.KEY_PREF_PAGE_NUMBER,
                sharedPref.getString(SettingsActivity.KEY_PREF_PAGE_NUMBER, "")};

        String[] pageSize = {SettingsActivity.KEY_PREF_PAGE_SIZE,
                sharedPref.getString(SettingsActivity.KEY_PREF_PAGE_SIZE, "")};

        boolean isImg = sharedPref.getBoolean(SettingsActivity
                .KEY_PREF_SHOW_IMG, Boolean.parseBoolean(""));

        String apiKey = BuildConfig.ApiKey;
        String[] apiKeyBuild = {URL_KEY, apiKey};

        if (isImg) {
            url = urlSettingsContainBuilder(DEFAULT_PATH,
                    selection,
                    fromDate,
                    toDate,
                    orderBy,
                    pageN,
                    pageSize,
                    selectedQuery,
                    FILTER_IMG,
                    apiKeyBuild);
        } else {
            url = urlSettingsContainBuilder(DEFAULT_PATH,
                    selection,
                    fromDate,
                    toDate,
                    orderBy,
                    pageN,
                    pageSize,
                    selectedQuery,
                    FILTER_NO_IMG,
                    apiKeyBuild);
        }
        return url;
    }

    /**
     * Method is used to compose URL query and filter main searches endpoints
     *
     * @return myUrl Composed Url
     */
    private String tagSettingRender(String search) {

        String url;

        String[] selection = {SettingsActivity.KEY_PREF_SECTION,
                sharedPref.getString(SettingsActivity.KEY_PREF_SECTION, "")};

        // if user type in search input write new value to setting
        if (isMainSearch) {
            writeSearchPreferences(KEY_PREF_SEARCH, search);
            isMainSearch = false;
        }

        String[] selectedQuery = {"q",
                sharedPref.getString(SettingsActivity.KEY_PREF_SEARCH, "")};

        String[] showRef = {SettingsActivity.KEY_PREF_SHOW_REFERENCE,
                sharedPref.getString(SettingsActivity.KEY_PREF_SHOW_REFERENCE, "")};

        String[] pageN = {SettingsActivity.KEY_PREF_PAGE_NUMBER,
                sharedPref.getString(SettingsActivity.KEY_PREF_PAGE_NUMBER, "")};

        String[] pageSize = {SettingsActivity.KEY_PREF_PAGE_SIZE,
                sharedPref.getString(SettingsActivity.KEY_PREF_PAGE_SIZE, "")};

        String apiKey = BuildConfig.ApiKey;
        String[] apiKeyBuild = {URL_KEY, apiKey};

        url = urlSettingsTagBuilder(DEFAULT_PATH,
                selection,
                showRef,
                pageN,
                pageSize,
                selectedQuery,
                FILTER_IMG,
                apiKeyBuild);
        return url;
    }

    /**
     * Method is used to un check Chips Views
     */
    private void uncheckChips() {
        try {
            mContentChip.setChecked(false);
            mSectionChip.setChecked(false);
            mEditorChip.setChecked(false);
            mSingleItem.setChecked(false);
            mTagChip.setChecked(false);
            isChipSelected = false;
        } catch (NullPointerException w) {
            Log.e(TAG, "NuLL", w);
        }
    }

    /**
     * Asynchronous call to Broadcast given intent to all interested BroadcastReceivers
     * Broadcast selected Song position to FullScreenPlayer
     * Message TO:{@link NewsListFragment}
     */
    private void clearRecycle() {
        Log.d(TAG, "Broadcasting message");
        Intent intent = new Intent("clearRecycle");
        LocalBroadcastManager.getInstance(Objects
                .requireNonNull(mContext)).sendBroadcast(intent);
    }

    /**
     * Set the current state of the message connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, getString(R.string.state_msg) + mConnectionState + " -> " + state);
        mConnectionState = state;
    }


    /**
     * Callback Handler to display Server status messages from
     * {@link com.example.nucle.firstnewsapi.networkloader.NetworkLoader}
     */
    @Override
    public void updateControlMessageFromDownload(Message msg) {
        switch (msg.what) {
            case Codes.FIRST_ARGUMENT_STATE:
                switch (msg.arg1) {
                    case Codes.HTTP_OK_CODE:
                        Log.d(TAG, "Server connection: " +
                                getString(R.string.connection_success));
                        mSystemLogger.setText(null);
                        setState(Codes.STATE_CONNECTING);
                        break;

                    case Codes.HTTP_ERROR_MALFORMED_REQUEST:
                        Log.d(TAG, "Server error: " + getString(R.string.malformed_request));
                        mSystemLogger.setText(getString(R.string.malformed_request));
                        setState(Codes.STATE_NONE);
                        clearRecycle();
                        break;

                    case Codes.HTTP_ERROR_UNAUTHORISED:
                        Log.d(TAG, "Server error: " + getString(R.string.unauthorized_request));
                        mSystemLogger.setText(getString(R.string.unauthorized_request));
                        setState(Codes.STATE_NONE);
                        clearRecycle();
                        break;

                    case Codes.HTTP_ERROR_NOT_FOUND:
                        Log.d(TAG, "Server error: " + getString(R.string.not_found_request));
                        mSystemLogger.setText(getString(R.string.not_found_request));
                        setState(Codes.STATE_NONE);
                        clearRecycle();
                        break;

                    case Codes.HTTP_SERVER_ERROR:
                        setState(Codes.STATE_NONE);
                        Log.d(TAG, "Server error: " + getString(R.string.server_error));
                        mSystemLogger.setText(getString(R.string.server_error));
                        clearRecycle();
                        break;

                    case Codes.HTTP_SERVER_UNKNOWN:
                        Log.d(TAG, "Server error: " + getString(R.string.server_unknown));
                        mSystemLogger.setText(getString(R.string.server_unknown));
                        clearRecycle();
                        break;
                }

            case Codes.SECOND_ARGUMENT_STATE:
                switch (msg.arg2) {
                    case Codes.STATE_NONE:
                        setState(Codes.STATE_NONE);
                        Log.d(TAG, "NewState: " + mConnectionState);
                        break;

                    case Codes.STATE_IGNORE:
                        Log.d(TAG, "NewState: " + " DuMMy");
                        break;

                    case Codes.STATE_CONNECTING:
                        setState(Codes.STATE_CONNECTING);
                        Log.d(TAG, "NewState: " + mConnectionState);
                        break;

                    case Codes.STATE_CLEARING:
                        setState(Codes.STATE_CLEARING);
                        Log.d(TAG, "NewState: " + mConnectionState);
                        break;

                    case Codes.STATE_DOWNLOAD_COMPLETE:
                        setState(Codes.STATE_NONE);
                        Log.d(TAG, "NewState: " + mConnectionState);
                        break;
                }
        }
    }

    /**
     * Callback to get new instance of {@link NewsListFragment}
     * From {@link com.example.nucle.firstnewsapi.networkloader.NetworkLoader}
     */
    @Override
    public void finishDownloading() {
        renderUI();
    }

    /**
     * Method is used to inflate new fragment instance of {@link NewsListFragment}
     */
    private void renderUI() {
        Log.d(TAG, "RenderUI");
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.container) != null) {
            if (getFragmentManager().findFragmentByTag("LIST_FRAGMENT") == null) {
                Log.e(TAG, "Fragment inflated");
                newsListFragment = NewsListFragment.newInstance(getSupportFragmentManager());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_PREF_SECTION:
                Log.d(TAG, "Preference changes " + KEY_PREF_SECTION + "-> " + key);
                break;
            case KEY_PREF_ORDER_BY:
                Log.d(TAG, "Preference change " + KEY_PREF_ORDER_BY + "-> " + key);
                break;
            case KEY_PREF_SEARCH:
                Log.d(TAG, "Preference change " + KEY_PREF_SEARCH + "-> " + key);
                break;
            case KEY_PREF_FROM_DATE:
                Log.d(TAG, "Preference change " + KEY_PREF_FROM_DATE + "-> " + key);
                break;
            case KEY_PREF_TO_DATE:
                Log.d(TAG, "Preference change " + KEY_PREF_TO_DATE + "-> " + key);
                break;
            case KEY_PREF_PAGE_NUMBER:
                Log.d(TAG, "Preference change " + KEY_PREF_PAGE_NUMBER + "-> " + key);
                break;
            case KEY_PREF_PAGE_SIZE:
                Log.d(TAG, "Preference change " + KEY_PREF_PAGE_SIZE + "-> " + key);
                break;
            case KEY_PREF_SHOW_IMG:
                Log.d(TAG, "Preference change " + KEY_PREF_SHOW_IMG + "-> " + key);
                break;
            case KEY_PREF_SHOW_REFERENCE:
                Log.d(TAG, "Preference change " + KEY_PREF_SHOW_REFERENCE + "-> " + key);
                break;
            case KEY_PREF_SEARCH_DEFAULT:
                Log.d(TAG, "Preference change " + KEY_PREF_SEARCH_DEFAULT + "-> " + key);
                break;
            case KEY_PREF_SEARCH_CONTENT:
                Log.d(TAG, "Preference change " + KEY_PREF_SEARCH_CONTENT + "-> " + key);
                break;
            case KEY_PREF_SEARCH_TAG:
                Log.d(TAG, "Preference change " + KEY_PREF_SEARCH_TAG + "-> " + key);
                break;
            case KEY_PREF_BACKUP:
                Log.d(TAG, "Preference change " + KEY_PREF_BACKUP + "-> " + key);
                break;
        }
    }

    /**
     * Method is used to write requested value to Preference Checkboxes
     *
     * @param preference String preference Constant
     * @param value      value true or false
     */
    private void writeSharedPreferences(String preference, boolean value) {
        SharedPreferences.Editor prefs = android.preference.PreferenceManager
                .getDefaultSharedPreferences(this).edit();
        prefs.putBoolean(preference, value);
        prefs.apply();
    }

    /**
     * Method is used to write requested value to Preference Checkboxes
     *
     * @param preference String preference Constant
     * @param search     user input search write to preference
     */
    private void writeSearchPreferences(String preference, String search) {
        SharedPreferences.Editor prefs = android.preference.PreferenceManager
                .getDefaultSharedPreferences(this).edit();
        prefs.putString(preference, search);
        prefs.apply();
    }

    /**
     * Interface from {@link FavItemListFragment}
     *
     * @param item String preference item
     */
    @Override
    public void onFavFragmentInteraction(String item) {
    }
}

