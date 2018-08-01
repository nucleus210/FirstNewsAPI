package com.example.nucle.firstnewsapi.ui.newsapiList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nucle.firstnewsapi.R;
import com.example.nucle.firstnewsapi.adapters.MyFavItemListRecyclerViewAdapter;
import com.example.nucle.firstnewsapi.content.FavoriteContent;
import com.example.nucle.firstnewsapi.content.NewsContent;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link OnFavFragmentInteractionListener}
 * interface.
 */
public class FavItemListFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "FAV FRAGMENT";
    private int mColumnCount = 1;
    private OnFavFragmentInteractionListener mListener;
    private NewsListFragment newsListFragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavItemListFragment() { }

    @SuppressWarnings("unused")
    public static FavItemListFragment newInstance(FragmentManager fragmentManager) {
        FavItemListFragment fragment = new FavItemListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, "fav")
                .commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        setNewsContent(); // initialize News content data holder
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyFavItemListRecyclerViewAdapter(FavoriteContent.ITEMS, mListener));
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "keyCode: " + keyCode);

                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i(TAG, "onKey Back listener is working!!!");
                    Objects.requireNonNull(getFragmentManager()).popBackStack(null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    if (Objects.requireNonNull(getActivity()).findViewById(R.id.container) != null) {
                        if (getFragmentManager().findFragmentByTag("LIST_FRAGMENT") == null) {
                            Log.e(TAG, "Fragment inflated");
                             newsListFragment = NewsListFragment.newInstance(getFragmentManager());
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    /**
     * Initialize {@link NewsContent}  News data content holder
     */
    private void setNewsContent() {
        new FavoriteContent(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavFragmentInteractionListener) {
            mListener = (OnFavFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFavFragmentInteractionListener {
        void onFavFragmentInteraction(String item);
    }
}
