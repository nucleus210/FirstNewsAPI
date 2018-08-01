package com.example.nucle.firstnewsapi.content;

import android.content.Context;
import android.util.Log;

import com.example.nucle.firstnewsapi.models.DataHolderModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class NewsContent {

    /**
     * An array of sample (newsContent) items.
     */
    public static final List<NewsContentItem> ITEMS = new ArrayList<>();
    private static final String TAG = "NewsContent";
    private int mCount;
    /**
     * A map of sample (news content) items, by ID.
     */
    // tmp hash map for a single pokemon
    private ArrayList<HashMap<String, String>> resultNewsObject;

    public NewsContent(Context context) {
        // clear data
        clearItem();
        // initialize array list
        resultNewsObject = new ArrayList<>();
        // get data from model
        resultNewsObject = DataHolderModel.getInstance(context).getNewsObjects();

        try {
            // check size before update data
            mCount = resultNewsObject.size();
        } catch (NullPointerException w) {
            Log.w(TAG, "NuLL", w);
        }

        // Add some sample items.
        for (int i = 0; i <= mCount - 1; i++) {
            addItem(createNewsContentItem(i));
        }

        try {
            Log.d(TAG, "New Data ->" + resultNewsObject.size());
        } catch (NullPointerException w) {
            Log.d(TAG, "NuLL", w);
        }
    }

    private static void clearItem() {
        ITEMS.clear();
    }

    private static void addItem(NewsContentItem item) {
        ITEMS.add(item);
    }

    private NewsContentItem createNewsContentItem(int position) {
        return new NewsContentItem(
                makeSection(position),
                makeDate(position),
                makeDetail(position),
                makeUrl(position),
                makeFirstName(position),
                makeLastName(position),
                makeImage(position));

    }

    private String makeSection(int position) {
        return resultNewsObject.get(position).get("sectionName");
    }

    private String makeDate(int position) {
        return resultNewsObject.get(position).get("webPublicationDate");
    }

    private String makeDetail(int position) {
        return resultNewsObject.get(position).get("webTitle");
    }

    private String makeUrl(int position) {
        return resultNewsObject.get(position).get("webUrl");
    }

    private String makeFirstName(int position) {
        return resultNewsObject.get(position).get("makeFirstName");
    }

    private String makeLastName(int position) {
        return resultNewsObject.get(position).get("makeLastName");
    }

    private String makeImage(int position) {
        return resultNewsObject.get(position).get("thumbnail");
    }

    /**
     * A newsContent item representing a piece of content.
     */
    public static class NewsContentItem {
        public final String section;
        public final String date;
        public final String details;
        public final String webUrl;
        public final String firstName;
        public final String lastName;
        public final String bitmap;

        NewsContentItem(String section,
                        String date,
                        String details,
                        String webUrl,
                        String firstName,
                        String lastName,
                        String bitmap) {

            this.section = section;
            this.date = date;
            this.details = details;
            this.webUrl = webUrl;
            this.firstName = firstName;
            this.lastName = lastName;
            this.bitmap = bitmap;
        }
    }
}
