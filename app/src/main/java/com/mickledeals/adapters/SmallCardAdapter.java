package com.mickledeals.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Utils;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class SmallCardAdapter extends RecyclerView.Adapter<SmallCardAdapter.ViewHolder> {
    private List<TestDataHolder> mDataset;
    private FragmentActivity mFragmentActivity;
    private int mLayoutRes;
    protected int mListType;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mCardTitle;
        public TextView mCardDescription;
        public ImageView mCardImage;

        public ViewHolder(View v) {
            super(v);
            mCardImage = (ImageView) v.findViewById(R.id.card_image);
            mCardDescription = (TextView) v.findViewById(R.id.card_description);
            mCardTitle = (TextView) v.findViewById(R.id.card_title);
        }
    }

    public SmallCardAdapter(FragmentActivity fragmentActivity, List<TestDataHolder> myDataset, int listType, int layoutRes) {
        mLayoutRes = layoutRes;
        mDataset = myDataset;
        mFragmentActivity = fragmentActivity;
        mListType = listType;
    }

    @Override
    public SmallCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutRes, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.transitDetailsActivity(mFragmentActivity, vh.getAdapterPosition(), mListType, v, null);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCardDescription.setText(mDataset.get(position).getShortDescription());
        holder.mCardTitle.setText(mDataset.get(position).getStoreName());
        holder.mCardImage.setImageResource(mDataset.get(position).mSmallImageResId);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
