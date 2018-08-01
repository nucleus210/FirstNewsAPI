package com.example.nucle.firstnewsapi.ui.newsapiList;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nucle.firstnewsapi.R;
import com.example.nucle.firstnewsapi.adapters.MynewsListRecyclerViewAdapter;
import com.example.nucle.firstnewsapi.content.NewsContent;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewsListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "NewsListFragment";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    public static NewsListFragment newInstance(FragmentManager fragmentManager) {
        NewsListFragment networkFragment = new NewsListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, networkFragment, "tt")
                .commitAllowingStateLoss();
        return networkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register Broadcast Receiver. Usage on Server Error to clean adapter
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).
                registerReceiver(mClearRecycleReceiver,
                        new IntentFilter("clearRecycle"));

        setNewsContent(); // initialize News content data holder
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new
                    MynewsListRecyclerViewAdapter(getActivity(), NewsContent.ITEMS, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
        // remove listener
        mListener = null;
    }

    /**
     * Initialize {@link NewsContent}  News data content holder
     */
    private void setNewsContent() {
        new NewsContent(getActivity());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String url);
    }
    /**
     * Broadcast receiver. Receive broadcast to clear Recycle Adapter.
     * Message FROM :{@link com.example.nucle.firstnewsapi.NewsApiMainActivity}
     */
    private final BroadcastReceiver mClearRecycleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d("BroadCastReceiver", "Got clear data");
            if(recyclerView!=null) {
                recyclerView.setAdapter(null);
            }
        }
    };
}

