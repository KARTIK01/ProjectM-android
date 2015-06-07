package com.mickledeals.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.LocationManager;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.AspectRatioImageView;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MainViewHolder> {
    protected List<TestDataHolder> mDataset;
    protected int mLayoutRes;
    protected FragmentActivity mFragmentActivity;
    protected int mListType;

    private AspectRatioImageView mDummpyImageView;

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView mCardTitle;
        public TextView mCardDescription;
        public AspectRatioImageView mCardImage;
        public TextView mCardPrice;
        public ImageView mCardSave;
        public TextView mCardDist;
        public TextView mCardCity;
        public RelativeLayout mCardBaseLayout;

        public MainViewHolder(View v) {
            super(v);
            mCardBaseLayout = (RelativeLayout) v.findViewById(R.id.card_base_layout);
            mCardImage = (AspectRatioImageView) v.findViewById(R.id.card_image);
            mCardDescription = (TextView) v.findViewById(R.id.card_description);
            mCardTitle = (TextView) v.findViewById(R.id.card_title);
            mCardPrice = (TextView) v.findViewById(R.id.card_price);
            mCardSave = (ImageView) v.findViewById(R.id.card_save);
            mCardDist = (TextView) v.findViewById(R.id.card_dist);
            mCardCity = (TextView) v.findViewById(R.id.card_city);
        }
    }

    public CardAdapter(FragmentActivity fragmentActivity, List<TestDataHolder> myDataset, int listType, int layoutRes) {
        mLayoutRes = layoutRes;
        mDataset = myDataset;
        mFragmentActivity = fragmentActivity;
        mListType = listType;
    }

    @Override
    public CardAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutRes, parent, false);
        final MainViewHolder vh = createViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = convertListPosToDataPos(vh.getAdapterPosition());
                mDummpyImageView = new AspectRatioImageView(v.getContext());
                mDummpyImageView.setLayoutParams(vh.mCardImage.getLayoutParams());
                mDummpyImageView.setRatio(vh.mCardImage.getRatio());
                mDummpyImageView.setImageResource(mDataset.get(pos).mSmallImageResId);
                mDummpyImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final int indexToAdd = vh.mCardBaseLayout.indexOfChild(vh.mCardImage);
                vh.mCardBaseLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vh.mCardBaseLayout.removeView(mDummpyImageView);
                        vh.mCardBaseLayout.addView(mDummpyImageView, indexToAdd);
                    }
                }, 1500);
                String transition = "cardImage" + mDataset.get(pos).mId;
                //doesnt seem to need below line
//                if (Build.VERSION.SDK_INT >= 21) v.findViewById(R.id.card_image).setTransitionName(transition);
                Utils.transitDetailsActivity(mFragmentActivity, pos, mListType, v.findViewById(R.id.card_image), transition);
            }
        });
        return vh;
    }

    protected MainViewHolder createViewHolder(View v) {
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardAdapter.MainViewHolder holder, int position) {

//            if (position % 2 == 0) {
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewholder.mCardView.getLayoutParams();
//                params.rightMargin = viewholder.mCardView.getResources().getDimensionPixelSize(R.dimen.card_margin);
//                viewholder.mCardView.setLayoutParams(params);
//            }

        position = convertListPosToDataPos(position);

        final TestDataHolder dataHolder = mDataset.get(position);

        if (holder.mCardBaseLayout != null)
            holder.mCardBaseLayout.removeView(mDummpyImageView);
        if (holder.mCardDescription != null) {
            holder.mCardDescription.setText(dataHolder.getDescription());
            holder.mCardDescription.setSelected(true);
        }
        if (holder.mCardTitle != null) holder.mCardTitle.setText(dataHolder.getStoreName());
        if (holder.mCardImage != null)
            holder.mCardImage.setImageResource(dataHolder.mSmallImageResId);
        if (holder.mCardPrice != null)
            holder.mCardPrice.setText(dataHolder.mPrice == 0 ? mFragmentActivity.getString(R.string.free) : "$" + (int) dataHolder.mPrice);
        if (holder.mCardPrice != null) {
            int delta = 0;
            if (mListType == Constants.TYPE_BEST_LIST) delta = 1;
            if (dataHolder.mPrice == 0) {
                holder.mCardPrice.setTextSize(17f + delta);
            } else {
                holder.mCardPrice.setTextSize(18f + delta);
            }
        }
        if (holder.mCardSave != null) {
            holder.mCardSave.setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
            holder.mCardSave.setOnClickListener(new View.OnClickListener() {
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
        if (holder.mCardCity != null) {
            holder.mCardCity.setText(dataHolder.mAddressShort.replace("San Francisco", "SF"));
        }
        if (holder.mCardDist != null) {
            holder.mCardDist.setVisibility(View.VISIBLE);
            float dist = LocationManager.getInstance(mFragmentActivity).getDistanceFromCurLocation(dataHolder);
            if (dist < 0) {
                holder.mCardDist.setVisibility(View.GONE);
            } else {
                holder.mCardDist.setText(dist + " mi");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    protected int convertListPosToDataPos(int position) {
        return position;
    }
}
