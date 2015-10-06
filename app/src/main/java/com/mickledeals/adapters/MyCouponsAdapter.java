package com.mickledeals.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.activities.ConfirmRedeemDialogActivity;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.fragments.MyCouponsFragment;
import com.mickledeals.utils.Constants;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class MyCouponsAdapter extends CardAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COUPONS = 1;

    private int mAvailableListIndex = -1;
    private int mExpiredListIndex = -1;
    private int mUsedListIndex = -1;

    //extends because onCreateViewHolder returns MainViewHolder, performance impact is very little
    public static class HeaderViewHolder extends MainViewHolder {
        public TextView mHeaderText;
        public View mHeaderRow;
//        public TextView mClearText;
        public HeaderViewHolder(View v) {
            super(v);
            mHeaderText = (TextView) v.findViewById(R.id.header);
            mHeaderRow =  v.findViewById(R.id.header_row);
//            mClearText = (TextView) v.findViewById(R.id.clear);
        }
    }

    public static class MyCouponViewHolder extends MainViewHolder {
        public TextView mCardButton;
        public TextView mCardExpiredDate;
        public TextView mCardDealEnded;
        public MyCouponViewHolder(View v) {
            super(v);
            mCardButton = (TextView) v.findViewById(R.id.card_button);
            mCardExpiredDate = (TextView) v.findViewById(R.id.card_expired_date);
            mCardDealEnded = (TextView) v.findViewById(R.id.card_deal_ended);
        }
    }

    public MyCouponsAdapter(Fragment fragment, List<CouponInfo> myDataset, int listType, int layoutRes) {
        super(fragment, myDataset, listType, layoutRes);
    }

    public void setSectionListIndex(int availableListIndex, int expiredListIndex, int usedListIndex) {
        mAvailableListIndex = availableListIndex;
        mExpiredListIndex = expiredListIndex;
        mUsedListIndex = usedListIndex;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if (viewType != TYPE_HEADER) { //could be progress type
            MyCouponViewHolder holder = (MyCouponViewHolder)super.onCreateViewHolder(parent, viewType);
//            holder.mCardButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //redeem or buy again
//
//
//                }
//            });
            return holder;

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coupon_section_row_dropdown, parent, false);

            HeaderViewHolder hvh = new HeaderViewHolder(v);
            return hvh;
        }
    }

    @Override
    protected MyCouponViewHolder createViewHolder(View v) {
        return new MyCouponViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        String headerStr = getPositionHeader(position);
        if (headerStr != null) {
            HeaderViewHolder hvh = (HeaderViewHolder)holder;
            hvh.mHeaderText.setText(headerStr);
            setAnimation(hvh.mHeaderText, position);
        } else {
            super.onBindViewHolder(holder, position);

            position = convertListPosToDataPos(position);
            final CouponInfo dataHolder = mDataset.get(position);

            MyCouponViewHolder vh = (MyCouponViewHolder) holder;
            vh.mCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (dataHolder.mRedeemTime == 0) {
                        Intent i = new Intent(mFragmentActivity, ConfirmRedeemDialogActivity.class);
                        i.putExtra("storeName", dataHolder.mBusinessInfo.mName);
                        i.putExtra("couponDesc", dataHolder.mDescription);
                        i.putExtra("id", dataHolder.mId);
                        mFragment.startActivityForResult(i, MyCouponsFragment.REQUEST_CODE_CONFIRM_REDEEM);
//                    } else {
//                        Intent i = new Intent(mFragmentActivity, RedeemDialogActivity.class);
//                        i.putExtra("storeName", dataHolder.mStoreName);
//                        i.putExtra("couponDesc", dataHolder.mDescription);
//                        i.putExtra("redeemTime", dataHolder.mRedeemTime);
//                        i.putExtra("id", dataHolder.mId);
//                        mFragment.startActivityForResult(i, MyCouponsFragment.REQUEST_CODE_REDEEM);
//                    }
                }
            });

            if (dataHolder.mStatus == Constants.COUPON_STATUS_BOUGHT) {
                vh.mCardExpiredDate.setText(vh.mCardExpiredDate.getResources().getString(R.string.expire_date));
                vh.mCardButton.setText(vh.mCardButton.getResources().getString(R.string.redeem));
                vh.mCardDealEnded.setVisibility(View.GONE);
                vh.mCardButton.setVisibility(View.VISIBLE);
            } else if (dataHolder.mStatus == Constants.COUPON_STATUS_EXPIRED) {
                vh.mCardExpiredDate.setText(vh.mCardExpiredDate.getResources().getString(R.string.expire_date));
                vh.mCardDealEnded.setVisibility(View.VISIBLE);
                vh.mCardButton.setVisibility(View.GONE);
            } else {
                vh.mCardExpiredDate.setText(vh.mCardExpiredDate.getResources().getString(R.string.used_date));
                vh.mCardDealEnded.setVisibility(View.GONE);
                vh.mCardButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        int headerCount = 0;
        if (mAvailableListIndex != -1) headerCount++;
        if (mExpiredListIndex != -1) headerCount++;
        if (mUsedListIndex != -1) headerCount++;
        return mDataset.size() + headerCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (getPositionHeader(position) != null)
            return TYPE_HEADER;

        return super.getItemViewType(position);
    }

    @Override
    protected int convertListPosToDataPos(int position) {

        int headerCount = 0;

        if (position > mAvailableListIndex && mAvailableListIndex != -1) headerCount++;
        if (position > mExpiredListIndex && mExpiredListIndex != -1) headerCount++;
        if (position > mUsedListIndex && mUsedListIndex != -1) headerCount++;

        return position - headerCount;
    }

    private String getPositionHeader(int position) {
        if (position == mAvailableListIndex) return mFragmentActivity.getResources().getString(R.string.available);
        else if (position == mExpiredListIndex) return mFragmentActivity.getResources().getString(R.string.expired);
        else if (position == mUsedListIndex) return mFragmentActivity.getResources().getString(R.string.used);
        else return null;
    }


}
