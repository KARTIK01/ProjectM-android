package com.mickledeals.adapters;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDLocationManager;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.AspectRatioImageView;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MainViewHolder> {

    public final static int VIEW_ITEM = 11;
    public final static int VIEW_PROGRESS = 12;

    protected List<CouponInfo> mDataset;
    protected int mLayoutRes;
    protected FragmentActivity mFragmentActivity;
    protected Fragment mFragment;
    protected int mListType;
    protected boolean mAnimate;

    private boolean mClickable = true;//to prevent multilpe click

    private AspectRatioImageView mDummpyImageView;

//    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
//        public View mProgressLayout;
//        public ProgressViewHolder(View v) {
//            super(v);
//            mProgressLayout = v.findViewById(R.id.progressBarLayout);
//        }
//    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        public View mCardView;
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
            mCardView = v.findViewById(R.id.card_view);
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

    public CardAdapter(Fragment fragment, List<CouponInfo> myDataset, int listType, int layoutRes) {
        mFragment = fragment;
        mLayoutRes = layoutRes;
        mDataset = myDataset;
        mFragmentActivity = fragment.getActivity();
        mListType = listType;
    }

    @Override
    public CardAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

        if (viewType == VIEW_PROGRESS) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_footer, parent, false);
            // no need another viewholder coz we are not setting anything to progressbar
            final MainViewHolder vh = createViewHolder(v);
            return vh;
        } else {


            View v = LayoutInflater.from(parent.getContext())
                    .inflate(mLayoutRes, parent, false);
            final MainViewHolder vh = createViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mClickable) return;
                    mClickable = false;
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
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClickable = true;
                        }
                    }, 2000);
                    String transition = "cardImage" + mDataset.get(pos).mId;
                    //doesnt seem to need below line
//                if (Build.VERSION.SDK_INT >= 21) v.findViewById(R.id.card_image).setTransitionName(transition);
                    Utils.transitDetailsActivity(mFragmentActivity, pos, mListType, v.findViewById(R.id.card_image), transition);
                }
            });
            return vh;
        }
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

        if (holder == null) return; //if holder is null, it is progressbar layout

        final int newPos  = convertListPosToDataPos(position);

        final CouponInfo dataHolder = mDataset.get(newPos);

        if (holder.mCardBaseLayout != null)
            holder.mCardBaseLayout.removeView(mDummpyImageView);
        if (holder.mCardDescription != null) {
            holder.mCardDescription.setText(dataHolder.getDescription());
            holder.mCardDescription.setSelected(true);
        }
        if (holder.mCardTitle != null) holder.mCardTitle.setText(dataHolder.mBusinessInfo.getStoreName());
        if (holder.mCardImage != null)
            holder.mCardImage.setImageResource(dataHolder.mSmallImageResId);

        holder.mCardPrice.setText(dataHolder.getDisplayedPrice());

        int sp17 = mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.sp_17);
        int sp18 = mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.sp_18);
        int sp19 = mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.sp_19);

        boolean extraSize = false;
        if (mListType == Constants.TYPE_BEST_LIST) extraSize = true;
        if (!dataHolder.getDisplayedPrice().contains("$")) { //FREE and cents should be smaller
            holder.mCardPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, extraSize ? sp18 : sp17);
        } else {
            holder.mCardPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, extraSize ? sp19 : sp18);
        }
        if (holder.mCardSave != null) {
            holder.mCardSave.setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
            holder.mCardSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataHolder.mSaved = !dataHolder.mSaved;
                    ((ImageView) v).setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
                    StringBuilder sb = new StringBuilder();
                    for (CouponInfo holder : DataListModel.getInstance().getDataList().values()) {
                        if (holder.mSaved) {
                            sb.append(holder.mId);
                            sb.append("|");
                        }
                    }
                    PreferenceHelper.savePreferencesStr(mFragmentActivity, "saveList", sb.toString());

                    if (mListType == Constants.TYPE_SAVED_LIST && !dataHolder.mSaved) {
                        //confirm dialog
                        //remove from recycler view
                        mDataset.remove(newPos);
                        notifyItemRemoved(newPos);
                    }
                }
            });
        }
        if (holder.mCardCity != null) {
//            holder.mCardCity.setText(dataHolder.mAddressShort.replace("San Francisco", "SF"));
        }
        if (holder.mCardDist != null) {
            holder.mCardDist.setVisibility(View.VISIBLE);
            float dist = MDLocationManager.getInstance(mFragmentActivity).getDistanceFromCurLocation(dataHolder);
            if (dist < 0) {
                holder.mCardDist.setVisibility(View.GONE);
            } else {
                holder.mCardDist.setText(dist + " mi");
            }
        }
        setAnimation(holder.mCardView, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mDataset.size()) return VIEW_ITEM; //prevent index oob
        return mDataset.get(position)!=null? VIEW_ITEM: VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    protected int convertListPosToDataPos(int position) {
        return position;
    }


    public void setPendingAnimated() {
        mAnimate = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnimate = false;
            }
        }, 200);
    }

    protected void setAnimation(View viewToAnimate, int pos)
    {
        if (mAnimate) {
            Animation animation = AnimationUtils.loadAnimation(mFragmentActivity, R.anim.card_layout_enter_anim);
            if (mListType == Constants.TYPE_NEARBY_LIST) {
                animation.setStartOffset(pos / 2 * 2 * 70);
            } else if (mListType == Constants.TYPE_BEST_LIST) {
                animation.setStartOffset((pos + 3)* 50 ); //add 3 for header offset
            } else {
                animation.setStartOffset(pos * 50);
            }
            viewToAnimate.startAnimation(animation);
        }
    }
}
