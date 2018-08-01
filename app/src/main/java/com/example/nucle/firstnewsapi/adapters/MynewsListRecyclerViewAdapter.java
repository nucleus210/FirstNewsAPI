package com.example.nucle.firstnewsapi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nucle.firstnewsapi.R;
import com.example.nucle.firstnewsapi.content.NewsContent;
import com.example.nucle.firstnewsapi.content.NewsContent.NewsContentItem;
import com.example.nucle.firstnewsapi.models.DataHolderModel;
import com.example.nucle.firstnewsapi.ui.newsapiList.NewsListFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NewsContent} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MynewsListRecyclerViewAdapter extends
        RecyclerView.Adapter<MynewsListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "Adapter";
    private final List<NewsContentItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public MynewsListRecyclerViewAdapter(Context context, List<NewsContentItem> items,
                                         OnListFragmentInteractionListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
        //  abs = new Abs(mContext);
        Log.e(TAG, "Data ->" + mValues.size());
        // mValues.clear();
        Log.e(TAG, "New Data ->" + mValues.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_newslist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mSectionName.setText(mValues.get(position).section);
        holder.mTitleContent.setText(mValues.get(position).details);
        holder.mTitleDate.setText(mValues.get(position).date);

        String mAuthorFullName = mValues.get(position).firstName
                + " " + mValues.get(position).lastName;
        if (mValues.get(position).firstName != null || mValues.get(position).lastName != null) {
            holder.mAuthorName.setText(mAuthorFullName);
        } else {
            holder.mAuthorName.setText(null);
        }
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
                    mListener.onListFragmentInteraction(holder.mWebUrl.getText().toString());
                }
            }
        });

        holder.mFavoriteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Button Fav click ->" + mValues.size());
                //initialize new Hash map object
                HashMap<String, String> newsTmp = new HashMap<>();
                //add data to array
                newsTmp.put("sectionName",mValues.get(position).section);
                newsTmp.put("thumbnail",mValues.get(position).bitmap);
                newsTmp.put("webTitle",mValues.get(position).details);
                newsTmp.put("webPublicationDate",mValues.get(position).date);
                newsTmp.put("webUrl",mValues.get(position).webUrl);
                newsTmp.put("firstName",mValues.get(position).firstName);
                newsTmp.put("lastName",mValues.get(position).lastName);
                // Store data in model
                DataHolderModel.getInstance(mContext).setFavItems(newsTmp);
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

        ViewHolder(View view) {
            super(view);
            mView = view;
            mSectionName = view.findViewById(R.id.item_section_name);
            mTitleContent = view.findViewById(R.id.item_news_content);
            mTitleDate = view.findViewById(R.id.item_content_date);
            mAuthorName = view.findViewById(R.id.item_author_name);
            mFavoriteBt = view.findViewById(R.id.favorite_button);
            mBitmapUrl = view.findViewById(R.id.item_image);
            mShareBt = view.findViewById(R.id.share_button);
            mWebUrl = view.findViewById(R.id.item_web_url);
        }
    }
}
