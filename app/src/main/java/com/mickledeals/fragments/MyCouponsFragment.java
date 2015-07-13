package com.mickledeals.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.R;
import com.mickledeals.activities.RedeemDialogActivity;
import com.mickledeals.adapters.MyCouponsAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */


public class MyCouponsFragment extends BaseFragment {

    public static final int REQUEST_CODE_CONFIRM_REDEEM = 1;
    public static final int REQUEST_CODE_REDEEM = 2;
    private MyCouponsAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private List<TestDataHolder> mBoughtList;

    private int mAvailableListSize;
    private int mExpiredListSize;
    private int mUsedListSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoughtList = DataListModel.getInstance().getBoughtList();
        mBoughtList.clear();
        //temporary, get it from server
        getMyCouponLists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_my_coupons, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyCouponsAdapter(this, mBoughtList, Constants.TYPE_BOUGHT_LIST, R.layout.card_layout_my_coupons);
        mAdapter.setSectionListSize(mAvailableListSize, mExpiredListSize, mUsedListSize);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void getMyCouponLists() {
        mAvailableListSize = 0;
        mExpiredListSize = 0;
        mUsedListSize = 0;

        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {

            if (holder.mStatus == Constants.COUPON_STATUS_BOUGHT) {
                mBoughtList.add(holder);
                mAvailableListSize++;
            }
        }
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {

            if (holder.mStatus == Constants.COUPON_STATUS_EXPIRED) {
                mBoughtList.add(holder);
                mExpiredListSize++;
            }
        }
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {

            if (holder.mId == 9 || holder.mId == 16 || holder.mId == 14) {
                mBoughtList.add(holder);
                mUsedListSize++;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_REDEEM) {
                //put it to used
            } else if (requestCode == REQUEST_CODE_CONFIRM_REDEEM) {

                for (TestDataHolder holder : mBoughtList) {
                    if (holder.mId == data.getIntExtra("id", 0)) {
                        holder.mRedeemTime = System.currentTimeMillis();
                    }
                    break;
                }

                Intent i = new Intent(mContext, RedeemDialogActivity.class);
                i.putExtra("storeName", data.getStringExtra("storeName"));
                i.putExtra("couponDesc", data.getStringExtra("couponDesc"));
                i.putExtra("redeemTime", System.currentTimeMillis());
                startActivityForResult(i, REQUEST_CODE_REDEEM);
            }
        }
    }
}
