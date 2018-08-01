package com.example.nucle.firstnewsapi.networkloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.example.nucle.firstnewsapi.DownloadCallback;
import com.example.nucle.firstnewsapi.models.DataHolderModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import static com.example.nucle.firstnewsapi.DownloadCallback.Codes;

public class NetworkLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "NetworkLoader";
    private DownloadCallback mCallback;
    private final Context mContext;
    private final String urls;
    private String mData;

    public NetworkLoader(Context context, DownloadCallback downloadCallback, String url) {
        super(context);
        this.mContext = context;
        setCallback(downloadCallback);
        Log.d(TAG, "Constructor");
        this.urls = url;
    }

    private void setCallback(DownloadCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        if (urls == null) {
            // Clearing Data
            mCallback.updateControlMessageFromDownload(
                    (composeMessage(Codes.SECOND_ARGUMENT_STATE,
                                    Codes.STATE_IGNORE,
                                    Codes.STATE_CLEARING)));}

        if (takeContentChanged() || mData == null) {
            // Something has changed or we have no data,
            // so kick off loading it
            if (urls != null) {
                forceLoad();
                Log.d(TAG, "forceLoading");}
        }
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Log.d(TAG, "loadInBackground");
        if (!isReset() && urls != null && urls.length() > 0) {
            try {
                URL url = new URL(urls);
                // fetch data from server
                mData = downloadUrl(url);
                if (mData != null) {
                    // Read incoming data
                    renderNews(mData);
                    // inform data success
                    deliverResult(mData);
                } else {
                    throw new IOException("No response received.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
                // send control message to change downloading state to None. Download fail
                mCallback.updateControlMessageFromDownload(
                        (composeMessage(Codes.SECOND_ARGUMENT_STATE,
                                        Codes.STATE_IGNORE,
                                        Codes.STATE_NONE)));
            }
        } return mData;
    }

    protected void onReset() {
        Log.d(TAG, "onReset");
        mData = null;
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     *
     * @param url URL address from user input
     * @return String result
     */

    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);

            connection.connect();
            // Open communications link (network traffic occurs here).
            int responseCode = connection.getResponseCode();
            // inform UI and user about server respond
            mCallback.updateControlMessageFromDownload
                    (composeMessage(Codes.FIRST_ARGUMENT_STATE, responseCode, Codes.STATE_IGNORE));
            // check server respond or go trow IOException
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                connection.disconnect();
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // Converts Stream to String.
                result = readStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }


    /**
     * Converts the json of an InputStream to a String.
     *
     * @param stream Input Stream from server respond
     */

    private String readStream(InputStream stream) throws IOException {

        // define new JSON object
        JSONObject data = null;
        // Create new Buffer reader for incoming Input Stream
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        // Initialize new string object
        StringBuilder json = new StringBuilder(1024);
        String tmp;
        // Read data in temp String
        while ((tmp = reader.readLine()) != null)
            json.append(tmp).append("\n");
        reader.close();
        try {
            data = new JSONObject(json.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error on remote fetch");
        }
        return Objects.requireNonNull(data).toString();
    }

    /**
     * Compose the current state of the message connection
     *
     * @param arg1 An integer defining the current connection state
     * @param arg2 An integer defining the current connection state
     */

    private Message composeMessage(int what, int arg1, int arg2) {
        Message msg = new Message();
        msg.what = what;

        if (arg1 != Codes.STATE_IGNORE) {
            msg.arg1 = arg1;
        } else {
            msg.arg1 = Codes.STATE_IGNORE;
        }
        if (arg2 != Codes.STATE_IGNORE) {
            msg.arg2 = arg2;
        } else {
            msg.arg2 = Codes.STATE_IGNORE;
        }
        return msg;
    }

    /**
     * Method is used to fetch data from Json server respond
     *
     * @param news Fetched Json in String format
     */

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void renderNews(String news) {
        JSONObject reader;
        JSONArray list;
        Log.d(TAG, "Send Data to model");
        try {
            reader = new JSONObject(news);
            JSONObject respond = reader.getJSONObject("response");
            list = respond.getJSONArray("results");
            if (list.length() < 1) {
                mCallback.updateControlMessageFromDownload(
                        (composeMessage(Codes.FIRST_ARGUMENT_STATE,
                                Codes.HTTP_SERVER_UNKNOWN,
                                Codes.STATE_IGNORE)));
            }

            for (int i = 0; i < list.length(); i++) {
                // tmp hash map for a single news data
                HashMap<String, String> newsTmp = new HashMap<>();

                JSONObject newsData = list.getJSONObject(i);

                if (newsData.has("sectionName")) {
                    String sectionName = newsData.get("sectionName").toString();
                    newsTmp.put("sectionName", sectionName);
                }
                if (newsData.has("fields")) {
                    String bitmapUrl = newsData.getJSONObject("fields").get("thumbnail").toString();
                    newsTmp.put("thumbnail", bitmapUrl);
                }

                if (newsData.has("webTitle")) {
                    String webTitle = newsData.get("webTitle").toString();
                    newsTmp.put("webTitle", webTitle);
                }

                if (newsData.has("webPublicationDate")) {
                    String webPublicationDate = newsData.get("webPublicationDate").toString();
                    newsTmp.put("webPublicationDate", webPublicationDate);
                }

                if (newsData.has("webUrl")) {
                    String newsUrs = newsData.get("webUrl").toString();
                    newsTmp.put("webUrl", newsUrs);
                }

                if (newsData.has("firstName") && newsData.has("lastName")) {
                    String contributorFirstName = newsData.get("firstName").toString();
                    String contributorLastName = newsData.get("lastName").toString();
                    newsTmp.put("firstName", contributorFirstName);
                    newsTmp.put("lastName", contributorLastName);
                }

                // Update new data to model
                DataHolderModel.getInstance(mContext).setNewsObjects(newsTmp);
            }
        } catch (JSONException e) {
            Log.e(TAG, "One or more fields not found in the JSON data");
        } finally {
            Log.d(TAG, "DOWNLOAD DATA READY");
            // set data to null
            mData = null;
            // send control message to change downloading state to None. Download complete
            mCallback.updateControlMessageFromDownload(
                    (composeMessage(Codes.SECOND_ARGUMENT_STATE,
                                    Codes.STATE_IGNORE,
                                    Codes.STATE_NONE)));
            // send control message to create new List Fragment instance
            mCallback.finishDownloading();
        }
    }
}


