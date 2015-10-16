package com.mickledeals.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.activities.RedeemDialogActivity;
import com.mickledeals.adapters.MyCouponsAdapter;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDApiManager;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */


public class MyCouponsFragment extends ListBaseFragment {

    public static final int REQUEST_CODE_CONFIRM_REDEEM = 1;
    public static final int REQUEST_CODE_REDEEM = 2;

    private TextView mNoCouponText;

    private List<CouponInfo> mBoughtList;

    private int mCurrentIndex = 0;
    private int mAvailableListIndex = -1;
    private int mExpiredListIndex = -1;
    private int mUsedListIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoughtList = DataListModel.getInstance().getBoughtList();
        //temporary, get it from server
        getMyCouponLists();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoCouponText = (TextView) view.findViewById(R.id.noCouponsText);
    }

    public List<CouponInfo> getDataList() {
        return DataListModel.getInstance().getBoughtList();
    }

    public void getMyCouponLists() {

        mCurrentIndex = 0;
        mAvailableListIndex = -1;
        mExpiredListIndex = -1;
        mUsedListIndex = -1;
        mBoughtList.clear();
//        for (CouponInfo holder : DataListModel.getInstance().getDataList().values()) {
//
//            if (holder.mStatus == Constants.COUPON_STATUS_BOUGHT) {
//                mBoughtList.add(holder);
//                if (mAvailableListIndex == -1) {
//                    mAvailableListIndex = mCurrentIndex;
//                    mCurrentIndex++;
//                }
//                mCurrentIndex++;
//            }
//        }
//        for (CouponInfo holder : DataListModel.getInstance().getDataList().values()) {
//
//            if (holder.mStatus == Constants.COUPON_STATUS_EXPIRED) {
//                mBoughtList.add(holder);
//                if (mExpiredListIndex == -1) {
//                    mExpiredListIndex = mCurrentIndex;
//                    mCurrentIndex++;
//                }
//                mCurrentIndex++;
//            }
//        }
//        for (CouponInfo holder : DataListModel.getInstance().getDataList().values()) {
//
//            if (holder.mId == 9 || holder.mId == 16 || holder.mId == 14) {
//                mBoughtList.add(holder);
//                if (mUsedListIndex == -1) {
//                    mUsedListIndex = mCurrentIndex;
//                }
//            }
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_REDEEM) {
                //put available coupon to used coupon after redeem
                for (CouponInfo holder : mBoughtList) {
                    if (holder.mId == data.getIntExtra("id", 0)) {
//                        holder.mRedeemTime = 0;
                        holder.mStatus = Constants.COUPON_STATUS_DEFAULT;
                        getMyCouponLists();
                        ((MyCouponsAdapter)mAdapter).setSectionListIndex(mAvailableListIndex, mExpiredListIndex, mUsedListIndex);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if (requestCode == REQUEST_CODE_CONFIRM_REDEEM) {

//                for (CouponInfo holder : mBoughtList) {
//                    if (holder.mId == data.getIntExtra("id", 0)) {
//                        holder.mRedeemTime = System.currentTimeMillis();
//                        break;
//                    }
//                }

                Intent i = new Intent(mContext, RedeemDialogActivity.class);
                i.putExtra("storeName", data.getStringExtra("storeName"));
                i.putExtra("couponDesc", data.getStringExtra("couponDesc"));
                i.putExtra("redeemTime", System.currentTimeMillis());
//                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(i, REQUEST_CODE_REDEEM);
            }
        }
    }

    @Override
    public void prepareSendRequest() {
        super.prepareSendRequest();
        mNoCouponText.setVisibility(View.GONE);
    }

    @Override
    public int getFragmentLayoutRes() {
        return R.layout.fragment_my_coupons;
    }

    @Override
    public void sendRequest(boolean loadMore) {
        MDApiManager.fetchFeatureList(this);
    }

    @Override
    public void onSuccessResponse() {
        super.onSuccessResponse();

        if (mBoughtList.size() == 0) {
            mNoCouponText.setVisibility(View.VISIBLE);
        }
    }

    public void setRecyclerView() {
        mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyCouponsAdapter(this, mBoughtList, Constants.TYPE_BOUGHT_LIST, R.layout.card_layout_my_coupons);
        ((MyCouponsAdapter)mAdapter).setSectionListIndex(mAvailableListIndex, mExpiredListIndex, mUsedListIndex);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
