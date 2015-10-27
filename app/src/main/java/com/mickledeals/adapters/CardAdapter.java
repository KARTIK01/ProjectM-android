package com.mickledeals.adapters;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDLocationManager;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.AspectRatioImageView;
import com.mickledeals.views.AspectRatioNetworkImageView;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MainViewHolder> {

    public final static int VIEW_ITEM = 11;
    public final static int VIEW_PROGRESS = 12;

    protected List<Integer> mDataset;
    protected int mLayoutRes;
    protected FragmentActivity mFragmentActivity;
    protected Fragment mFragment;
    protected int mListType;
    protected boolean mAnimate;
    private Animation mAnimation;

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
        public AspectRatioNetworkImageView mCardImage;
        public TextView mCardPrice;
        public ImageView mCardSave;
        public TextView mCardDist;
        public TextView mCardCity;
        public RelativeLayout mCardBaseLayout;


        public MainViewHolder(View v) {
            super(v);
            mCardView = v.findViewById(R.id.card_view);
            mCardBaseLayout = (RelativeLayout) v.findViewById(R.id.card_base_layout);
            mCardImage = (AspectRatioNetworkImageView) v.findViewById(R.id.card_image);
            mCardDescription = (TextView) v.findViewById(R.id.card_description);
            mCardTitle = (TextView) v.findViewById(R.id.card_title);
            mCardPrice = (TextView) v.findViewById(R.id.card_price);
            mCardSave = (ImageView) v.findViewById(R.id.card_save);
            mCardDist = (TextView) v.findViewById(R.id.card_dist);
            mCardCity = (TextView) v.findViewById(R.id.card_city);
        }
    }

    public CardAdapter(Fragment fragment, List<Integer> myDataset, int listType, int layoutRes) {
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
                    int pos = convertListPosToDataPos(vh.getAdapterPosition());
                    String transition = null;
                    if (Build.VERSION.SDK_INT >= 21) {
                        //for optimizing share element transition
                        if (!mClickable) return;
                        mClickable = false;
                        mDummpyImageView = new AspectRatioImageView(v.getContext());
                        mDummpyImageView.setLayoutParams(vh.mCardImage.getLayoutParams());
                        mDummpyImageView.setRatio(vh.mCardImage.getRatio());
                        mDummpyImageView.setImageDrawable(vh.mCardImage.getDrawable());
                        DataListModel.sTransitDrawable = vh.mCardImage.getDrawable();
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
                        transition = "cardImage" + mDataset.get(pos);
                        //doesnt seem to need below line
//                if (Build.VERSION.SDK_INT >= 21) v.findViewById(R.id.card_image).setTransitionName(transition);
                    }
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

//        Log.e("ZZZ", "card adapter onBindViewHolder" + position);
//            if (position % 2 == 0) {
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewholder.mCardView.getLayoutParams();
//                params.rightMargin = viewholder.mCardView.getResources().getDimensionPixelSize(R.dimen.card_margin);
//                viewholder.mCardView.setLayoutParams(params);
//            }

        if (holder == null) return; //if holder is null, it is progressbar layout

        final int newPos  = convertListPosToDataPos(position);

        if (mDataset.get(newPos) == null) return; //if id is null, it is progressbar layout

        final CouponInfo dataHolder = DataListModel.getInstance().getCouponInfoFromList(mDataset, newPos);


        if (holder.mCardBaseLayout != null)
            holder.mCardBaseLayout.removeView(mDummpyImageView);
        if (holder.mCardDescription != null) {
            holder.mCardDescription.setText(dataHolder.getDescription());
        }
        DLog.d(this, "coupon id = " + dataHolder.mId + " coupon description = " + dataHolder.mDescription);
        if (holder.mCardTitle != null) holder.mCardTitle.setText(dataHolder.mBusinessInfo.getStoreName());
        if (holder.mCardImage != null) {
            holder.mCardImage.setImageUrl(dataHolder.mCoverPhotoUrl, MDApiManager.sImageLoader);
        }


        int sp17 = mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.sp_17);
        int sp18 = mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.sp_18);
        int sp19 = mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.sp_19);

        boolean extraSize = false;
        if (mListType == Constants.TYPE_BEST_LIST) extraSize = true;

        if (holder.mCardPrice != null) {
            holder.mCardPrice.setText(dataHolder.getDisplayedPrice());
            if (!dataHolder.getDisplayedPrice().contains("$")) { //FREE and cents should be smaller
                holder.mCardPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, extraSize ? sp18 : sp17);
            } else {
                holder.mCardPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, extraSize ? sp19 : sp18);
            }
        }

        if (holder.mCardSave != null) {
            if (!MDLoginManager.isLogin()) {
                holder.mCardSave.setVisibility(View.GONE);
            } else {
                holder.mCardSave.setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
                holder.mCardSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    StringBuilder sb = new StringBuilder();
//                    for (CouponInfo holder : DataListModel.getInstance().getDataList().values()) {
//                        if (holder.mSaved) {
//                            sb.append(holder.mId);
//                            sb.append("|");
//                        }
//                    }
//                    PreferenceHelper.savePreferencesStr(mFragmentActivity, "saveList", sb.toString());
                    if (mListType == Constants.TYPE_SAVED_LIST) {

                        AlertDialog dialog = new AlertDialog.Builder(v.getContext(), R.style.AppCompatAlertDialogStyle)
                                .setMessage(R.string.remove_save_msg)
                                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataHolder.mSaved = !dataHolder.mSaved;
                                        MDApiManager.addOrRemoveFavorite(dataHolder.mId, dataHolder.mSaved);
                                        mDataset.remove(newPos);
                                        notifyItemRemoved(newPos);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    } else {
                        dataHolder.mSaved = !dataHolder.mSaved;
                        ((ImageView) v).setImageResource(dataHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off);
                        MDApiManager.addOrRemoveFavorite(dataHolder.mId, dataHolder.mSaved);
                    }
                    }
                });
            }
        }
        if (holder.mCardCity != null) {
            holder.mCardCity.setText(dataHolder.mBusinessInfo.getShortDisplayedCity());
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
        }, 100);
    }

    public boolean isAnimating() {
        return mAnimation != null && mAnimation.hasStarted() && !mAnimation.hasEnded();
    }

    protected void setAnimation(View viewToAnimate, int pos)
    {
        pos = pos % MDApiManager.PAGE_SIZE;
        if (mAnimate) {
            mAnimation = AnimationUtils.loadAnimation(mFragmentActivity, R.anim.card_layout_enter_anim);
            if (mListType == Constants.TYPE_NEARBY_LIST) {
                mAnimation.setStartOffset(pos / 2 * 2 * 70);
            } else if (mListType == Constants.TYPE_BEST_LIST) {
                mAnimation.setStartOffset((pos + 3)* 50 ); //add 3 for header offset
            } else {
                mAnimation.setStartOffset(pos * 50);
            }
            viewToAnimate.startAnimation(mAnimation);
        }
    }

    public int getListType() {
        return mListType;
    }
}
