package com.example.nucle.firstnewsapi.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nucle.firstnewsapi.R;
import com.example.nucle.firstnewsapi.content.FavoriteContent.FavoriteItem;
import com.example.nucle.firstnewsapi.ui.newsapiList.FavItemListFragment.OnFavFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FavoriteItem} and makes a call to the
 * specified {@link OnFavFragmentInteractionListener}.
 */
public class MyFavItemListRecyclerViewAdapter extends RecyclerView.Adapter<MyFavItemListRecyclerViewAdapter.ViewHolder> {

    private final List<FavoriteItem> mValues;
    private final OnFavFragmentInteractionListener mListener;
    private static final String TAG = "FAV ADAPTER" ;

    public MyFavItemListRecyclerViewAdapter(List<FavoriteItem> items, OnFavFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favorite_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mSectionName.setText(mValues.get(position).section);
        holder.mTitleContent.setText(mValues.get(position).details);
        holder.mTitleDate.setText(mValues.get(position).date);

        String mAuthorFullName = mValues.get(position).firstName
                + " " + mValues.get(position).lastName;
        if (mValues.get(position).firstName != null || mValues.get(position).firstName != null) {
            holder.mAuthorName.setText(mAuthorFullName);
        } else { holder.mAuthorName.setText(null);}
        holder.mWebUrl.setText(mValues.get(position).webUrl);
        if (mValues.get(position).bitmap != null) {
            Picasso.get().load(mValues.get(position).bitmap).into(holder.mBitmapUrl);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFavFragmentInteraction(holder.mWebUrl.getText().toString());
                }
            }
        });

        holder.mFavoriteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Button Fav click ->" + mValues.size());
             //   DataHolderModel.getInstance(mContext).setNewsItems(mValues.get(position));
            }
        });

        holder.mShareBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.e(TAG, "Button Share click ->" + mValues.size());
                holder.mWebUrl.getText().toString();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mSectionName;
        final TextView mTitleDate;
        final TextView mTitleContent;
        final TextView mAuthorName;
        final TextView mWebUrl;
        final ImageView mBitmapUrl;
        final ImageButton mShareBt;
        final ImageButton mFavoriteBt;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mSectionName = view.findViewById(R.id.fav_item_section_name);
            mTitleContent = view.findViewById(R.id.fav_item_news_content);
            mTitleDate = view.findViewById(R.id.fav_item_content_date);
            mAuthorName = view.findViewById(R.id.fav_item_author_name);
            mFavoriteBt = view.findViewById(R.id.fav_favorite_button);
            mBitmapUrl = view.findViewById(R.id.fav_item_image);
            mShareBt = view.findViewById(R.id.fav_share_button);
            mWebUrl = view.findViewById(R.id.fav_item_web_url);
        }
    }
}
