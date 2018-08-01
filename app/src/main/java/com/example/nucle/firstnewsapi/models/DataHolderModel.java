package com.example.nucle.firstnewsapi.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.provider.Contacts.SettingsColumns.KEY;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_BACKUP;
import static com.example.nucle.firstnewsapi.settings.SettingsActivity.KEY_PREF_FAV_REFERENCE;

public class DataHolderModel {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static DataHolderModel mInstance;
    private ArrayList<HashMap<String, String>> newsObjects;
    private ArrayList<HashMap<String, String>> favNews;
    private String firstNewsJson;
    private String webUrl;

    /**
     * Constructor
     */
    private DataHolderModel(Context context) {
        mContext = context;
        getFavPreference();
    }
    /**
     * Set favorite news object into collection
     * @param news Favorite News collection Array
     */
    public void setFavItems(HashMap<String, String> news) {
        if (favNews == null) {
            favNews = new ArrayList<>();
        }
        this.favNews.add(news);
        saveFavNews(favNews);
    }
    /**
     * Get fetched news object into collection
     * @return Favorite news collection
     */
    public ArrayList<HashMap<String, String>> getFavItems() {
        return favNews;
    }
    /**
     * Set fetched news object into collection
     * @param news News collection Array
     */
    public void setNewsObjects(HashMap<String, String> news) {
        if (newsObjects == null) {
            newsObjects = new ArrayList<>();
        }
        this.newsObjects.add(news);
    }
    /**
     * Get fetched news object into collection
     * @return news collection
     */
    public ArrayList<HashMap<String, String>> getNewsObjects() {
        return newsObjects;
    }
    /**
     * get Url String query
     */
    @SuppressWarnings("unused")
    public String getUrl() {
        return webUrl;
    }
    /**
     * Set Url String query
     */
    public void setUrl(String url) {
        this.webUrl = url;
    }
    /**
     * Get news String data from String
     */
    @SuppressWarnings("unused")
    public String getNews() {
        return firstNewsJson;
    }
    /**
     * Set news String data from Json
     */
    public void setNews(String news) {
        this.firstNewsJson = news;
    }

    /**
     * Clear data before fetch new json. This will empty only news collection
     */
    public void clearData() {
        if (newsObjects != null) {
            newsObjects.clear();
        }
    }

    /**
     * Save Favorite data from collection to Shared Preference
     *
     * @param favNews Favorite Data Collection
     */
    private void saveFavNews(ArrayList<HashMap<String, String>> favNews) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isBackup = sharedPref.getBoolean(
                KEY_PREF_BACKUP, Boolean.parseBoolean(""));
        if (isBackup) {
            //converting the collection into a JSON
            JSONArray result = new JSONArray(favNews);
            SharedPreferences pref = mContext.getSharedPreferences(KEY_PREF_FAV_REFERENCE, 0);

            //Storing the string in pref file
            SharedPreferences.Editor prefEditor = pref.edit();
            prefEditor.putString(KEY, result.toString());
            prefEditor.apply();
        }
    }

    /**
     * Get Favorite data from stored Shared Preference
     */
    private void getFavPreference() {
        //Getting preference
        SharedPreferences pref = mContext.getSharedPreferences(KEY_PREF_FAV_REFERENCE, 0);
        //Getting the JSON from preference
        String storedCollection = pref.getString(KEY, null);
        if (storedCollection != null) {
            //Parse the string to populate favorite collection.
            try {
                JSONArray array = new JSONArray(storedCollection);
                HashMap<String, String> item;
                for (int i = 0; i < array.length(); i++) {
                    String obj = String.valueOf(array.get(i));
                    JSONObject ary = new JSONObject(obj);
                    Iterator<String> it = ary.keys();
                    item = new HashMap<>();
                    while (it.hasNext()) {
                        String key = it.next();
                        item.put(key, String.valueOf(ary.get(key)));
                    }
                    if (favNews == null) {
                        favNews = new ArrayList<>();
                    }
                    // add favorite data to collection
                    favNews.add(item);
                }
            } catch (JSONException e) {
                Log.e(TAG, "while parsing", e);
            }
        }
    }

    /**
     * Get model instance when is used from activities or fragments
     *
     * @param context Context from activities
     * @return DataModel instance
     */
    public static synchronized DataHolderModel getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataHolderModel(context);
        }
        return mInstance;
    }
}
