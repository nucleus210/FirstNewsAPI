package com.example.nucle.firstnewsapi;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nucle.firstnewsapi.networkloader.NetworkLoader;
import com.example.nucle.firstnewsapi.models.DataHolderModel;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

/**
 * Use the {@link NetworkFragment#getInstance(FragmentManager, String)} factory method to
 * create an instance of this fragment.
 */

public class NetworkFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    private static final String TAG = "NetworkFragment";
    private static final String URL_KEY = "UrlKey";
    private DownloadCallback mCallback;
    private String mUrlString;
    private Context mContext;

    private NetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {
        NetworkFragment networkFragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mContext = getContext();
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        mUrlString = Objects.requireNonNull(getArguments()).getString(URL_KEY);
        DataHolderModel.getInstance(mContext).setUrl(mUrlString);
        startDownload();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_network, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
        Log.e(TAG, "onDetach");
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */

    private void startDownload() {
        if (mUrlString != null) {
            Log.d(TAG, "startDownloadMethod");
            Objects.requireNonNull(getActivity()).getSupportLoaderManager().restartLoader(0,
                    null, this).startLoading();
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onReturnLoaderInstance");
        return new NetworkLoader(mContext, mCallback, mUrlString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.d(TAG, "onLoaderFinished");
        DataHolderModel.getInstance(mContext).setNews(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCallback.finishDownloading();
            Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mCallback.finishDownloading();
            Toast.makeText(getActivity(), "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
