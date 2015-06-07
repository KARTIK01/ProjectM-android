package com.mickledeals.adapters;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.tests.TestDataHolder;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class MyCouponsAdapter extends CardAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COUPONS = 1;

    //extends because onCreateViewHolder returns MainViewHolder, performance impact is very little
    public static class HeaderViewHolder extends MainViewHolder {
        public TextView mHeaderText;
        public TextView mClearText;
        public HeaderViewHolder(View v) {
            super(v);
            mHeaderText = (TextView) v.findViewById(R.id.header);
            mClearText = (TextView) v.findViewById(R.id.clear);
        }
    }

    public static class MyCouponViewHolder extends MainViewHolder {
        public TextView mCardButton;
        public TextView mCardExpiredDate;
        public MyCouponViewHolder(View v) {
            super(v);
            mCardButton = (TextView) v.findViewById(R.id.card_button);
            mCardExpiredDate = (TextView) v.findViewById(R.id.card_expired_date);
        }
    }

    public MyCouponsAdapter(FragmentActivity fragmentActivity, List<TestDataHolder> myDataset, int listType, int layoutRes) {
        super(fragmentActivity, myDataset, listType, layoutRes);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if (viewType == TYPE_COUPONS) {
            MyCouponViewHolder holder = (MyCouponViewHolder)super.onCreateViewHolder(parent, viewType);
            holder.mCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //redeem or buy again


                }
            });
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
        if (isPositionHeader(position)) {
            HeaderViewHolder hvh = (HeaderViewHolder)holder;
            if (position == 0) {
                hvh.mHeaderText.setText(hvh.mHeaderText.getResources().getString(R.string.expiring));
                hvh.mClearText.setVisibility(View.GONE);
            } else if (position == 2) {
                hvh.mHeaderText.setText(hvh.mHeaderText.getResources().getString(R.string.available));
                hvh.mClearText.setVisibility(View.GONE);
            } else if (position == 5) {
                hvh.mHeaderText.setText(hvh.mHeaderText.getResources().getString(R.string.expired));
                hvh.mClearText.setVisibility(View.VISIBLE);
            } else if (position == 8) {
                hvh.mHeaderText.setText(hvh.mHeaderText.getResources().getString(R.string.used));
                hvh.mClearText.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBindViewHolder(holder, position);
            MyCouponViewHolder vh = (MyCouponViewHolder) holder;
            vh.mCardExpiredDate.setText(vh.mCardExpiredDate.getResources().getString(R.string.expire_date));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_COUPONS;
    }

    @Override
    protected int convertListPosToDataPos(int position) {

        if (position == 1) position = 0;
        else if (position == 3) position = 1;
        else if (position == 4) position = 2;
        else if (position == 6) position = 3;
        else if (position == 7) position = 4;
        else if (position == 9) position = 5;
        else if (position == 10) position = 6;

        return position;
    }

    private boolean isPositionHeader(int position) {
        return position == 0 || position == 2 || position == 5 || position == 8;
    }


}
