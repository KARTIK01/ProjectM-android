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
import com.mickledeals.utils.JSONHelper;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDConnectManager;
import com.mickledeals.utils.Utils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */


public class MyCouponsFragment extends ListBaseFragment {

    public static final int REQUEST_CODE_CONFIRM_REDEEM = 1;
    public static final int REQUEST_CODE_REDEEM = 2;

    private List<Integer> mAvailableList;
    private List<Integer> mExpiredList;
    private List<Integer> mUsedList;

    private TextView mNoCouponText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoCouponText = (TextView) view.findViewById(R.id.noCouponsText);
    }

    public List<Integer> getDataList() {
        return DataListModel.getInstance().getBoughtList();
    }

    @Override
    public void sendRequest() {
        mAvailableList = null;
        mExpiredList = null;
        mUsedList = null;
        MDApiManager.fetchMyCoupons("purchaseDate", false, false, 0, new MDApiManager.MDResponseListener<List<Integer>>() {
            @Override
            public void onMDSuccessResponse(List<Integer> object) {
                mAvailableList = object;
                checkIfCompleteResponse();
            }
            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
                MyCouponsFragment.super.onMDNetworkErrorResponse(errorMessage);
            }
            @Override
            public void onMDErrorResponse(String errorMessage) {
                MyCouponsFragment.super.onMDErrorResponse(errorMessage);
            }
        });

        MDApiManager.fetchMyCoupons("expireDate", true, false, 0, new MDApiManager.MDResponseListener<List<Integer>>() {
            @Override
            public void onMDSuccessResponse(List<Integer> object) {
                mExpiredList = object;
                checkIfCompleteResponse();
            }
            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
                MyCouponsFragment.super.onMDNetworkErrorResponse(errorMessage);
            }
            @Override
            public void onMDErrorResponse(String errorMessage) {
                MyCouponsFragment.super.onMDErrorResponse(errorMessage);
            }
        });

        MDApiManager.fetchMyCoupons("redemptionDate", false, true, 0, new MDApiManager.MDResponseListener<List<Integer>>() {
            @Override
            public void onMDSuccessResponse(List<Integer> object) {
                mUsedList = object;
                checkIfCompleteResponse();
            }
            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
                MyCouponsFragment.super.onMDNetworkErrorResponse(errorMessage);
            }
            @Override
            public void onMDErrorResponse(String errorMessage) {
                MyCouponsFragment.super.onMDErrorResponse(errorMessage);
            }
        });
    }

    private void checkIfCompleteResponse() {
        if (mAvailableList != null && mExpiredList != null && mUsedList != null) {
            mDataList.clear();
            mDataList.addAll(mAvailableList);
            mDataList.addAll(mExpiredList);
            mDataList.addAll(mUsedList);
            if (mDataList.size() == 0) {
                mNoCouponText.setVisibility(View.VISIBLE);
            }
            setSectionListIndex();
            mAdapter.notifyDataSetChanged();
            mAdapter.setPendingAnimated();
            if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setSectionListIndex() {

        int currentIndex = 0;
        int availableListIndex = mAvailableList.size() == 0 ? -1 : 0;
        if (mAvailableList.size() != 0) currentIndex += mAvailableList.size() + 1;
        int expiredListIndex = mExpiredList.size() == 0 ? -1 : currentIndex;
        if (mExpiredList.size() != 0) currentIndex += mExpiredList.size() + 1;
        int usedListIndex = mUsedList.size() == 0 ? -1 : currentIndex;
        ((MyCouponsAdapter)mAdapter).setSectionListIndex(availableListIndex, expiredListIndex, usedListIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_REDEEM) {
                //put available coupon to used coupon after redeem
                int couponId = data.getIntExtra("couponId", 0);
                mAvailableList.remove(couponId);
                mUsedList.add(0, couponId);
                CouponInfo holder = DataListModel.getInstance().getCouponMap().get(couponId);
                //add used time using current time
                setSectionListIndex();
                mAdapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_CODE_CONFIRM_REDEEM) {
                if (!MDConnectManager.getInstance(mContext).isNetworkAvailable()) {
                    Utils.showNetworkErrorDialog(mContext);
                    return;
                }

                int couponId = data.getIntExtra("couponId", 0);
                final CouponInfo holder = DataListModel.getInstance().getCouponMap().get(couponId);
                MDApiManager.redeemCoupon(holder.mPurchaseId, new MDApiManager.MDResponseListener<JSONObject>() {
                    @Override
                    public void onMDSuccessResponse(JSONObject object) {

                        boolean available = JSONHelper.getBoolean(object, "available");
                        holder.mAvailable = available;
                        holder.mRedeemable = false;
                        holder.mPurchaseId = "";
                        holder.mLastRedemptionDate = 0;
                        holder.mPurchaseDate = 0;
                    }

                    @Override
                    public void onMDNetworkErrorResponse(String errorMessage) {
                    }

                    @Override
                    public void onMDErrorResponse(String errorMessage) {
                    }
                });

//                for (CouponInfo holder : mBoughtList) {
//                    if (holder.mId == data.getIntExtra("id", 0)) {
//                        holder.mRedeemTime = System.currentTimeMillis();
//                        break;
//                    }
//                }

                Intent i = new Intent(mContext, RedeemDialogActivity.class);
                i.putExtra("couponId", holder.mId);
                i.putExtra("storeName", holder.mBusinessInfo.mName);
                i.putExtra("couponDesc", holder.mDescription);
                i.putExtra("finePrint", holder.mFinePrint);
                i.putExtra("purchaseId", holder.mPurchaseId);
//                i.putExtra("redeemTime", System.currentTimeMillis());
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
    public boolean needLoadMore() {
        return true;
    }

    public void setRecyclerView() {
        mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyCouponsAdapter(this, mDataList, Constants.TYPE_BOUGHT_LIST, R.layout.card_layout_my_coupons);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
