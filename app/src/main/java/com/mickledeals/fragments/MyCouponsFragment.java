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
import com.mickledeals.datamodel.MyCouponInfo;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.EventBus;
import com.mickledeals.utils.JSONHelper;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDConnectManager;
import com.mickledeals.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */


public class MyCouponsFragment extends ListBaseFragment {

    public static final int REQUEST_CODE_CONFIRM_REDEEM = 1;
    public static final int REQUEST_CODE_REDEEM = 2;

    private List<MyCouponInfo> mAvailableList;
    private List<MyCouponInfo> mExpiredList;
    private List<MyCouponInfo> mUsedList;

    private List<MyCouponInfo> mMyCouponList = new ArrayList<MyCouponInfo>();

    private TextView mNoCouponText;
    private boolean mPendingUpdate;

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
        //load all list except when page to load > 1
        if (mPageToLoad <= 1) {
            mAvailableList = null;
            mExpiredList = null;
            mUsedList = null;
            MDApiManager.fetchMyCoupons("purchaseDate", false, false, 0, new MDApiManager.MDResponseListener<List<MyCouponInfo>>() {
                @Override
                public void onMDSuccessResponse(List<MyCouponInfo> object) {
                    mAvailableList = object;
                    for (MyCouponInfo info : object) {
                        info.mStatus = Constants.MYCOUPON_AVAILABLE;
                    }
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

            MDApiManager.fetchMyCoupons("expireDate", true, false, 0, new MDApiManager.MDResponseListener<List<MyCouponInfo>>() {
                @Override
                public void onMDSuccessResponse(List<MyCouponInfo> object) {
                    mExpiredList = object;
                    for (MyCouponInfo info : object) {
                        info.mStatus = Constants.MYCOUPON_EXPIRED;
                    }
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

        MDApiManager.fetchMyCoupons("redemptionDate", false, true, mPageToLoad, new MDApiManager.MDResponseListener<List<MyCouponInfo>>() {
            @Override
            public void onMDSuccessResponse(List<MyCouponInfo> object) {
                for (MyCouponInfo info : object) {
                    info.mStatus = Constants.MYCOUPON_USED;
                }
                if (mPageToLoad > 1) {
                    //remove progress bar
                    mDataList.remove(mDataList.size() - 1);
                    mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                    mUsedList.addAll(object);
                    mMyCouponList.addAll(object);
                    for (MyCouponInfo info : object) {
                        mDataList.add(info.mId);
                    }
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount() - object.size(), object.size());
                    mAdapter.setPendingAnimated();
                } else {
                    mUsedList = object;
                    checkIfCompleteResponse();
                }
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
            mMyCouponList.clear();
            mMyCouponList.addAll(mAvailableList);
            mMyCouponList.addAll(mExpiredList);
            mMyCouponList.addAll(mUsedList);
            for (MyCouponInfo info : mMyCouponList) {
                mDataList.add(info.mId);
            }
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
        ((MyCouponsAdapter) mAdapter).setSectionListIndex(availableListIndex, expiredListIndex, usedListIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_REDEEM) {
//                //put available coupon to used coupon after redeem
//                int couponId = data.getIntExtra("couponId", 0);
//                mAvailableList.remove((Integer)couponId);
//                mUsedList.add(0, couponId);
//                CouponInfo holder = DataListModel.getInstance().getCouponMap().get(couponId);
//                //add used time using current time
//                setSectionListIndex();
//                mAdapter.notifyDataSetChanged();
                //too much to do above, just simply send new request
//                prepareSendRequest();
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

                        Bundle bundle = new Bundle();
                        bundle.putInt("couponId", holder.mId);
                        EventBus.getInstance().sendEvent(EventBus.EVENT_REDEEM, bundle);
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
        mAdapter = new MyCouponsAdapter(this, mDataList, Constants.TYPE_BOUGHT_LIST, R.layout.card_layout_my_coupons, mMyCouponList);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onEventUpdate(int event, Bundle data) {
        if (event == EventBus.EVENT_REDEEM) {
            int couponId = data.getInt("couponId");
            for (MyCouponInfo info : mAvailableList) {
                if (info.mId == couponId) {
                    mAvailableList.remove(info);
                    info.mRedemptionDate = System.currentTimeMillis();
                    info.mStatus = Constants.MYCOUPON_USED;
                    mUsedList.add(0, info);
                    break;
                }
            }
            mPendingUpdate = true;
        } else if (event == EventBus.EVENT_PURCHASE) {
            int couponId = data.getInt("couponId");
            MyCouponInfo info = new MyCouponInfo();
            info.mStatus = Constants.MYCOUPON_AVAILABLE;
            info.mId = couponId;
            mAvailableList.add(0, info);
            mPendingUpdate = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPendingUpdate) {
            checkIfCompleteResponse();
            mPendingUpdate = false;
        }
    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mHasPaused = true;
//    }
}
