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
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.AspectRatioImageView;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
                int pos = mListType == Constants.TYPE_BEST_LIST ? vh.getAdapterPosition() -1 : vh.getAdapterPosition();
                mDummpyImageView = new AspectRatioImageView(v.getContext());
                mDummpyImageView.setLayoutParams(vh.mCardImage.getLayoutParams());
                mDummpyImageView.setRatio(vh.mCardImage.getRatio());
                mDummpyImageView.setImageResource(mDataset.get(pos).mSmallImageResId);
                mDummpyImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int indexToAdd = vh.mCardBaseLayout.indexOfChild(vh.mCardImage);
                vh.mCardBaseLayout.addView(mDummpyImageView, indexToAdd);
                String transition = "cardImage" + mDataset.get(pos).mId;
                //doesnt seem to need below line
//                if (Build.VERSION.SDK_INT >= 21) v.findViewById(R.id.card_image).setTransitionName(transition);
                Utils.transitDetailsActivity(mFragmentActivity, pos, mListType, v.findViewById(R.id.card_image), transition);
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

            if (viewholder.mCardBaseLayout != null) viewholder.mCardBaseLayout.removeView(mDummpyImageView);
            if (viewholder.mCardDescription != null) viewholder.mCardDescription.setText(dataHolder.getDescription());
            if (viewholder.mCardTitle != null) viewholder.mCardTitle.setText(dataHolder.getStoreName());
            if (viewholder.mCardImage != null) viewholder.mCardImage.setImageResource(dataHolder.mSmallImageResId);
            if (viewholder.mCardPrice != null) viewholder.mCardPrice.setText(dataHolder.mPrice == 0 ? mFragmentActivity.getString(R.string.free) : "$" + (int) dataHolder.mPrice);
            if (viewholder.mCardPrice != null) {
                if (dataHolder.mPrice == 0) {
                    viewholder.mCardPrice.setTextSize(17f);
                } else {
                    viewholder.mCardPrice.setTextSize(18f);
                }
            }
            if (viewholder.mCardSave != null) {
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
            if (viewholder.mCardDist != null) {
                viewholder.mCardDist.setVisibility(View.VISIBLE);
                float dist = Utils.getDistanceFromCurLocation(dataHolder);
                if (dist == 0) {
                    viewholder.mCardDist.setVisibility(View.GONE);
                } else {
                    viewholder.mCardDist.setText(dist + " mi");
                }
            }
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
