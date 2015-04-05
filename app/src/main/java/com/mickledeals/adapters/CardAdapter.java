package com.mickledeals.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<TestDataHolder> mDataset;
    protected int mLayoutRes;
    protected FragmentActivity mFragmentActivity;
    protected int mListType;

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView mCardTitle;
        public TextView mCardDescription;
        public ImageView mCardImage;
        public TextView mCardPrice;
        public ImageView mCardSave;

        public MainViewHolder(View v) {
            super(v);
            mCardImage = (ImageView) v.findViewById(R.id.card_image);
            mCardDescription = (TextView) v.findViewById(R.id.card_description);
            mCardTitle = (TextView) v.findViewById(R.id.card_title);
            mCardPrice = (TextView) v.findViewById(R.id.card_price);
            mCardSave = (ImageView) v.findViewById(R.id.card_save);
        }
    }

    public CardAdapter(FragmentActivity fragmentActivity, List<TestDataHolder> myDataset, int listType, int layoutRes) {
        mLayoutRes = layoutRes;
        mDataset = myDataset;
        mFragmentActivity = fragmentActivity;
        mListType = listType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutRes, parent, false);
        final MainViewHolder vh = new MainViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.transitDetailsActivity(mFragmentActivity, vh.getPosition(), mListType, v);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MainViewHolder) {
            MainViewHolder viewholder = (MainViewHolder) holder;
//            if (position % 2 == 0) {
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewholder.mCardView.getLayoutParams();
//                params.rightMargin = viewholder.mCardView.getResources().getDimensionPixelSize(R.dimen.card_margin);
//                viewholder.mCardView.setLayoutParams(params);
//            }

            final TestDataHolder dataHolder = getItem(position);

            viewholder.mCardDescription.setText(dataHolder.getDescription());
            viewholder.mCardTitle.setText(dataHolder.getStoreName());
            viewholder.mCardImage.setImageResource(dataHolder.mSmallImageResId);
            viewholder.mCardPrice.setText(dataHolder.mPrice == 0 ? "Free" : "$" + (int) dataHolder.mPrice);
            if (dataHolder.mPrice == 0) {
                viewholder.mCardPrice.setTextSize(17f);
            } else {
                viewholder.mCardPrice.setTextSize(18f);
            }
            viewholder.mCardSave.setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
            viewholder.mCardSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataHolder.mSaved = !dataHolder.mSaved;
                    ((ImageView) v).setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
                    StringBuilder sb = new StringBuilder();
                    for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
                        if (holder.mSaved) {
                            sb.append(holder.mId);
                            sb.append("|");
                        }
                    }
                    PreferenceHelper.savePreferencesStr(mFragmentActivity, "saveList", sb.toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    protected TestDataHolder getItem(int position) {
        return mDataset.get(position);
    }
}
