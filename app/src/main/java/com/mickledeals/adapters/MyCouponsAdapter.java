package com.mickledeals.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.activities.ConfirmRedeemDialogActivity;
import com.mickledeals.activities.RedeemDialogActivity;
import com.mickledeals.fragments.MyCouponsFragment;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class MyCouponsAdapter extends CardAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COUPONS = 1;
    private int mAvailableListSize;
    private int mExpiredListSize;
    private int mUsedListSize;

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

    public MyCouponsAdapter(Fragment fragment, List<TestDataHolder> myDataset, int listType, int layoutRes) {
        super(fragment, myDataset, listType, layoutRes);
    }

    public void setSectionListSize(int availableListSize, int expiredListSize, int usedListSize) {
        mAvailableListSize = availableListSize;
        mExpiredListSize = expiredListSize;
        mUsedListSize = usedListSize;
        //add header count
        if (mAvailableListSize > 0) mAvailableListSize++;
        if (mExpiredListSize > 0) mExpiredListSize++;
        if (mUsedListSize > 0) mUsedListSize++;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if (viewType == TYPE_COUPONS) {
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

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coupon_section_row_dropdown, parent, false);

            HeaderViewHolder hvh = new HeaderViewHolder(v);
            return hvh;
        }
        return null;
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
            if (headerStr.equals("")) {
                hvh.mHeaderRow.setVisibility(View.GONE);
                hvh.mHeaderText.setVisibility(View.GONE);
            } else {
                hvh.mHeaderRow.setVisibility(View.VISIBLE);
                hvh.mHeaderText.setVisibility(View.VISIBLE);
                hvh.mHeaderText.setText(headerStr);
            }
        } else {
            super.onBindViewHolder(holder, position);

            position = convertListPosToDataPos(position);
            final TestDataHolder dataHolder = mDataset.get(position);

            MyCouponViewHolder vh = (MyCouponViewHolder) holder;
            vh.mCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataHolder.mRedeemTime == 0) {
                        Intent i = new Intent(mFragmentActivity, ConfirmRedeemDialogActivity.class);
                        i.putExtra("storeName", dataHolder.mStoreName);
                        i.putExtra("couponDesc", dataHolder.mDescription);
                        i.putExtra("id", dataHolder.mId);
                        mFragment.startActivityForResult(i, MyCouponsFragment.REQUEST_CODE_CONFIRM_REDEEM);
                    } else {
                        Intent i = new Intent(mFragmentActivity, RedeemDialogActivity.class);
                        i.putExtra("storeName", dataHolder.mStoreName);
                        i.putExtra("couponDesc", dataHolder.mDescription);
                        i.putExtra("redeemTime", dataHolder.mRedeemTime);
                        mFragment.startActivityForResult(i, MyCouponsFragment.REQUEST_CODE_REDEEM);
                    }
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
        return mAvailableListSize + mUsedListSize + mExpiredListSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (getPositionHeader(position) != null)
            return TYPE_HEADER;

        return TYPE_COUPONS;
    }

    @Override
    protected int convertListPosToDataPos(int position) {
        if (position < (mAvailableListSize + 1)) {
            return position - 1;
        } else if (position < (mAvailableListSize + mExpiredListSize + 2)) {
            return position - 2;
        } else {
            return position - 3;
        }
    }

    private String getPositionHeader(int position) {
        String availableStr = mFragmentActivity.getResources().getString(R.string.available);
        String expiredStr = mFragmentActivity.getResources().getString(R.string.expired);
        String usedStr = mFragmentActivity.getResources().getString(R.string.used);
        if (position == 0) {
            if (mAvailableListSize != 0) return availableStr;
            else if (mExpiredListSize != 0) return expiredStr;
            else return usedStr;
        } else if (position == mAvailableListSize) {
            if (mExpiredListSize != 0) return expiredStr;
            else return usedStr;
        } else if (position == mAvailableListSize + mExpiredListSize) {
            if (mUsedListSize != 0) return usedStr;
            else return usedStr;
        }
        return null;
    }


}
