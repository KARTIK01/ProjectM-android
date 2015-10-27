package com.mickledeals.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mickledeals.R;
import com.mickledeals.activities.BusinessPageActivity;
import com.mickledeals.activities.BuyDialogActivity;
import com.mickledeals.activities.ConfirmRedeemDialogActivity;
import com.mickledeals.activities.DetailsActivity;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.activities.MapActivity;
import com.mickledeals.activities.RedeemDialogActivity;
import com.mickledeals.activities.SuccessDialogActivity;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.JSONHelper;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDConnectManager;
import com.mickledeals.utils.MDLocationManager;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.AspectRatioNetworkImageView;
import com.mickledeals.views.NotifyingScrollView;

import org.json.JSONObject;

/**
 * Created by Nicky on 11/28/2014.
 */
public class DetailsFragment extends BaseFragment {

    private static final long INTIIAL_REMAINING_TIME = 2 * 60 * 60 * 1000;
    private static final int REQUEST_CODE_BUY = 1;
    private static final int REQUEST_CODE_REDEEM = 2;
    private static final int REQUEST_CODE_CONFIRM_REDEEM = 3;

    private CouponInfo mHolder;

    //    private int mListType;
    private TextView mBusinessName;
    private TextView mDescription;
    private TextView mPrice;
    private NetworkImageView mImageView;
    private View mBuyBtn;
    private TextView mBuyBtnText;
    private TextView mRedeemBtnText;
    private View mRedeemBtn;
    private View mSaveBtn;
    private View mBusinessInfoBtn;
    private TextView mExpiredDate;
    private TextView mLimited;
    private TextView mFinePrint;
    private TextView mBoughtDate;
    private TextView mPurchaseId;
    private TextView mNotAvailableText;
    private TextView mSaveBtnText;
    private TextView mBusinessInfoBtnText;
    private TextView mAddressBtn;
    private TextView mCallBtn;
    private View mBuyPanel;
    private LinearLayout mMoreCouponRow;
    private View mNavigationHintPanel;
    private TextView mNavLeftText;
    private TextView mNavMidText;
    private TextView mNavRightText;
    private Handler mHandler = new Handler();
    private NotifyingScrollView mDetailsScrollView;
    private NotifyingScrollView.OnScrollChangedListener mScrollListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int id = getArguments().getInt("couponId");
        mHolder = DataListModel.getInstance().getCouponMap().get(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_details, container, false);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBusinessName = (TextView) view.findViewById(R.id.storeName);
        mDescription = (TextView) view.findViewById(R.id.couponDescription);
        mPrice = (TextView) view.findViewById(R.id.couponPrice);
        mLimited = (TextView) view.findViewById(R.id.limited);
        mBoughtDate = (TextView) view.findViewById(R.id.boughtDate);
        mPurchaseId = (TextView) view.findViewById(R.id.purhcaseId);
        mBuyBtn = view.findViewById(R.id.buyBtn);
        mBuyBtnText = (TextView) view.findViewById(R.id.buyBtnText);
        mRedeemBtnText = (TextView) view.findViewById(R.id.redeemBtnText);
        mRedeemBtn = view.findViewById(R.id.redeemBtn);
        mBusinessInfoBtn = view.findViewById(R.id.businessInfoButton);
        mSaveBtn = view.findViewById(R.id.savedButton);
        mBusinessInfoBtnText = (TextView) view.findViewById(R.id.businessInfoButtonText);
        mSaveBtnText = (TextView) view.findViewById(R.id.savedButtonText);
        mExpiredDate = (TextView) view.findViewById(R.id.expiredDate);
        mFinePrint = (TextView) view.findViewById(R.id.finePrint);
        mAddressBtn = (TextView) view.findViewById(R.id.address);
        mCallBtn = (TextView) view.findViewById(R.id.call);
        mBuyPanel = view.findViewById(R.id.buyPanel);
        mNavigationHintPanel = view.findViewById(R.id.navigateHintPanel);
        mNavLeftText = (TextView) view.findViewById(R.id.navLeftText);
        mNavMidText = (TextView) view.findViewById(R.id.navMidText);
        mNavRightText = (TextView) view.findViewById(R.id.navRightText);
        mMoreCouponRow = (LinearLayout) view.findViewById(R.id.moreCouponRow);
        mNotAvailableText = (TextView) view.findViewById(R.id.dealEndedText);

        mDetailsScrollView = (NotifyingScrollView) view.findViewById(R.id.detailsScrollView);
        mDetailsScrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                if (mScrollListener != null) mScrollListener.onScrollChanged(who, l, t, oldl, oldt);

                // changing position of ImageView
                mImageView.setTranslationY(t / 2);


//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImageView.getLayoutParams();
//                params.height = Utils.getDeviceWidth(mContext) * 9 / 16 - t;
//                mImageView.setLayoutParams(params);
            }
        });

        String midStr = getArguments().getString("navMidText");
        String leftStr = getArguments().getString("navLeftText");
        String rightStr = getArguments().getString("navRightText");
        if (midStr != null) {
            mNavMidText.setText(midStr);
            mNavMidText.setVisibility(View.VISIBLE);
        }
        if (leftStr != null) {
            mNavLeftText.setText(leftStr);
            mNavLeftText.setVisibility(View.VISIBLE);
        }
        if (rightStr != null) {
            mNavRightText.setText(rightStr);
            mNavRightText.setVisibility(View.VISIBLE);
        }

        mBusinessName.setText(mHolder.mBusinessInfo.getStoreName());
        mDescription.setText(mHolder.getDescription());
        mAddressBtn.setText(mHolder.mBusinessInfo.getShortAddress());
        mCallBtn.setText(mHolder.mBusinessInfo.mPhone);

        String priceText = mHolder.getLocaledDisplayedPrice();
        mBuyBtnText.setText(getString(R.string.unlock_coupon, priceText));
        mPrice.setText(priceText);
        float dist = MDLocationManager.getInstance(mContext).getDistanceFromCurLocation(mHolder);
        String addrShort = mHolder.mBusinessInfo.getDisplayedCity();
        if (dist > 0) {
            String centerDot = getString(R.string.center_dot_symbol);
            addrShort += centerDot + dist + " mi";
        }
        ((TextView) view.findViewById(R.id.addressDist)).setText(addrShort);

        mImageView = (NetworkImageView) view.findViewById(R.id.imageView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        params.height = MDApplication.sDeviceWidth * 9 / 16; //DO NOT NEED THIS if the image is already fitted, this is just for adjusting to 16:9
        mImageView.setLayoutParams(params);
        mImageView.setNoAnimation();
        mImageView.setImageUrl(mHolder.mCoverPhotoUrl, MDApiManager.sImageLoader);



        if (Build.VERSION.SDK_INT >= 21 && getArguments().getBoolean("initialFragment")) {
            mImageView.setTransitionImage(DataListModel.sTransitDrawable);
            mImageView.setTransitionName("cardImage" + mHolder.mId);
            getActivity().startPostponedEnterTransition();
            //seems like no need to use below line???
//            scheduleStartPostponedTransition(mImageView);
        }

        mBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDLoginManager.loginIfNecessary(getActivity(), new MDLoginManager.LoginCallback() {
                    @Override
                    public void onLoginSuccess() {

                        if (mHolder.mPrice == 0) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            MDApiManager.purchaseCoupon(mHolder.mId, 0, null, new MDReponseListenerImpl<JSONObject>() {

                                @Override
                                public void onMDSuccessResponse(JSONObject object) {
                                    super.onMDSuccessResponse(object);

                                    mHolder.setPurhcaseInfo(object);
                                    updateViewForStatus();

                                    Intent newIntent = new Intent(mContext, SuccessDialogActivity.class);
                                    newIntent.putExtra("price", mHolder.mPrice);
                                    newIntent.putExtra("store_name", mHolder.mBusinessInfo.mName);
                                    newIntent.putExtra("coupon_description", mHolder.mDescription);
                                    newIntent.putExtra("pay", false);
                                    startActivity(newIntent);
                                }

                                @Override
                                public void onMDErrorResponse(String errorMessage) {
                                    if (errorMessage != null) {
                                        if (errorMessage.equals("COUPON_NOT_AVAILABLE_FOR_USER")) {
                                            onMDErrorResponse(R.string.purhcase_error_not_available);
                                            return;
                                        }
                                    }
                                    super.onMDErrorResponse(errorMessage);
                                }
                            });
                        } else {
                            Intent i = new Intent(mContext, BuyDialogActivity.class);
                            i.putExtra("couponId", mHolder.mId);
                            startActivityForResult(i, REQUEST_CODE_BUY);

                        }
                    }
                });


            }
        });

        mRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mHolder.mRedeemTime == 0) {
                Intent i = new Intent(mContext, ConfirmRedeemDialogActivity.class);
                startActivityForResult(i, REQUEST_CODE_CONFIRM_REDEEM);
//                } else {
//                    Intent i = new Intent(mContext, RedeemDialogActivity.class);
//                    i.putExtra("storeName", mHolder.mStoreName);
//                    i.putExtra("couponDesc", mHolder.mDescription);
//                    i.putExtra("redeemTime", mHolder.mRedeemTime);
//                    startActivityForResult(i, REQUEST_CODE_REDEEM);
//                }
            }
        });

        mSaveBtnText.setText(mHolder.mSaved ? R.string.saved : R.string.save);
        mSaveBtnText.setCompoundDrawablesWithIntrinsicBounds(mHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off, 0, 0, 0);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHolder.mSaved = !mHolder.mSaved;
                mSaveBtnText.setText(mHolder.mSaved ? R.string.saved : R.string.save);
                mSaveBtnText.setCompoundDrawablesWithIntrinsicBounds(mHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off, 0, 0, 0);
                MDLoginManager.loginIfNecessary(getActivity(), new MDLoginManager.LoginCallback() {
                    @Override
                    public void onLoginSuccess() {
                        MDApiManager.addOrRemoveFavorite(mHolder.mId, mHolder.mSaved);
                    }
                });
//                StringBuilder sb = new StringBuilder();
//                for (CouponInfo holder : DataListModel.getInstance().getDataList().values()) {
//                    if (holder.mSaved) {
//                        sb.append(holder.mId);
//                        sb.append("|");
//                    }
//                }
//                PreferenceHelper.savePreferencesStr(mContext, "saveList", sb.toString());
            }
        });

        if (!MDLoginManager.isLogin()) {
            mSaveBtn.setVisibility(View.GONE);
        }


        mFinePrint.setText(mHolder.getFinePrint() + "\n" + getString(R.string.fine_print_footer));

        mAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MapActivity.class);
                i.putExtra("dataObject", mHolder.mBusinessInfo);
                startActivity(i);
            }
        });

        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mHolder.mBusinessInfo.mPhone.replaceAll("(-|\\(|\\))", "").trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        if (!getArguments().getBoolean("fromBusinessProfile")) {
            mBusinessInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, BusinessPageActivity.class);
                    i.putExtra("businessInfo", mHolder.mBusinessInfo);
                    startActivity(i);
                }
            });
        } else {
            mBusinessInfoBtn.setVisibility(View.INVISIBLE); //invisible instead of gone so that save button remain same size
        }

        updateViewForStatus();

        if (mHolder.mBusinessInfo.mCoupons.size() > 1 && !getArguments().getBoolean("fromOtherCoupon")) {
            view.findViewById(R.id.moreCouponLabel).setVisibility(View.VISIBLE);
            mMoreCouponRow.setVisibility(View.VISIBLE);
            for (final CouponInfo info : mHolder.mBusinessInfo.mCoupons) {
                if (info == mHolder) continue;

                View otherCoupon = getActivity().getLayoutInflater().inflate(R.layout.card_layout_others, null);
                //load image
                ((TextView) otherCoupon.findViewById(R.id.card_description)).setText(info.getDescription());
                TextView cardPrice = (TextView) otherCoupon.findViewById(R.id.card_price);
                cardPrice.setText(info.getDisplayedPrice());
                ((AspectRatioNetworkImageView) otherCoupon.findViewById(R.id.card_image)).setImageUrl(info.mCoverPhotoUrl, MDApiManager.sImageLoader);

                int sp19 = getResources().getDimensionPixelSize(R.dimen.sp_19);
                int sp20 = getResources().getDimensionPixelSize(R.dimen.sp_20);
                if (!info.getDisplayedPrice().contains("$")) { //FREE and cents should be smaller
                    cardPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp20);
                } else {
                    cardPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp19);
                }
                otherCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, DetailsActivity.class);
                        DataListModel.getInstance().getMoreCouponsList().clear(); //do not need list, but in case future we need
                        DataListModel.getInstance().getMoreCouponsList().add(info.mId);
                        i.putExtra("listIndex", 0);
                        i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                        i.putExtra("fromOtherCoupon", true);
                        if (getArguments().getBoolean("fromOtherCoupon")) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        startActivity(i);
                    }
                });
                mMoreCouponRow.addView(otherCoupon);
            }
        }
    }

    private void updateViewForStatus() {
        if (mHolder.mRedeemable) {
            showRedeemableStatus();
        } else if (mHolder.mAvailable || !MDLoginManager.isLogin()) { //show available when user not log in, coz available is missing
            showAvailableStatus();
        } else {
            showNotAvailableStatus();
        }
        showExpiredDate();
    }

    private void showAvailableStatus() {
//        mHandler.removeCallbacks(mUpdatetimerThread);
        mRedeemBtnText.setText(R.string.redeem_coupon);
        mPrice.setVisibility(View.VISIBLE);
        if (mHolder.mLimited) mLimited.setVisibility(View.VISIBLE);
        mBuyBtn.setVisibility(View.VISIBLE);
        mRedeemBtn.setVisibility(View.GONE);
        mBoughtDate.setVisibility(View.GONE);
        mPurchaseId.setVisibility(View.GONE);
    }

    private void showNotAvailableStatus() {
        mNotAvailableText.setVisibility(View.VISIBLE);
        mRedeemBtn.setVisibility(View.GONE);
        mBuyBtn.setVisibility(View.GONE);
        mPrice.setVisibility(View.GONE);
        mLimited.setVisibility(View.GONE);
        mBoughtDate.setVisibility(View.GONE);
        mPurchaseId.setVisibility(View.GONE);
    }

    private void showRedeemableStatus() {
        mPrice.setVisibility(View.GONE);
        mRedeemBtn.setVisibility(View.VISIBLE);
        mBuyBtn.setVisibility(View.GONE);
        mBoughtDate.setText(getString(R.string.purchase_date, Utils.formatDate(mHolder.mPurchaseDate)));
        mBoughtDate.setVisibility(View.VISIBLE);
        mPurchaseId.setText(getString(R.string.purchase_id_message, mHolder.mPurchaseId));
        mPurchaseId.setVisibility(View.VISIBLE);
        mLimited.setVisibility(View.GONE);
    }

    private void showExpiredDate() {
        mExpiredDate.setVisibility(View.VISIBLE);
        String expiredStr = null;
        if (mHolder.mExpiredDate != 0) {
            expiredStr = getString(R.string.expired_date, Utils.formatDate(mHolder.mExpiredDate)); //need format
        } else if (mHolder.mLastRedemptionDate != 0 && mHolder.mRedeemable) {
            expiredStr = getString(R.string.expired_date, Utils.formatDate(mHolder.mLastRedemptionDate)); //need format
        } else if (!mHolder.mExpiredDays.isEmpty()) {
            expiredStr = getString(R.string.expired_in_days, mHolder.mExpiredDays);
        } else {
            mExpiredDate.setVisibility(View.GONE);
        }
        if (expiredStr != null) {
            mExpiredDate.setText(expiredStr);
        }
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        getActivity().startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_BUY) {
                updateViewForStatus();

                Intent newIntent = new Intent(mContext, SuccessDialogActivity.class);
                newIntent.putExtra("price", mHolder.mPrice);
                newIntent.putExtra("store_name", mHolder.mBusinessInfo.mName);
                newIntent.putExtra("coupon_description", mHolder.mDescription);
                newIntent.putExtra("pay", data.getBooleanExtra("pay", false));
                startActivity(newIntent);
            } else if (requestCode == REQUEST_CODE_REDEEM) {
                updateViewForStatus();
            } else if (requestCode == REQUEST_CODE_CONFIRM_REDEEM) {
                if (!MDConnectManager.getInstance(mContext).isNetworkAvailable()) {
                    Utils.showNetworkErrorDialog(mContext);
                    return;
                }
                MDApiManager.redeemCoupon(mHolder.mPurchaseId, new MDApiManager.MDResponseListener<JSONObject>() {
                    @Override
                    public void onMDSuccessResponse(JSONObject object) {

                        boolean available = JSONHelper.getBoolean(object, "available");
                        mHolder.mAvailable = available;
                        mHolder.mRedeemable = false;
                        mHolder.mPurchaseId = "";
                        mHolder.mLastRedemptionDate = 0;
                        mHolder.mPurchaseDate = 0;
                    }

                    @Override
                    public void onMDNetworkErrorResponse(String errorMessage) {
                    }

                    @Override
                    public void onMDErrorResponse(String errorMessage) {
                    }
                });

                Intent i = new Intent(mContext, RedeemDialogActivity.class);
                i.putExtra("couponId", mHolder.mId);
                i.putExtra("storeName", mHolder.mBusinessInfo.mName);
                i.putExtra("couponDesc", mHolder.mDescription);
                i.putExtra("finePrint", mHolder.mFinePrint);
                i.putExtra("purchaseId", mHolder.mPurchaseId);
//                i.putExtra("redeemTime", mHolder.mRedeemTime);
                startActivityForResult(i, REQUEST_CODE_REDEEM);

//                mHandler.removeCallbacks(mUpdatetimerThread);
//                mHandler.postDelayed(mUpdatetimerThread, 0);

            }
        }
    }

    public void showNavPanelHint() {
        mHandler.postDelayed(mHideNavHintPanelRunnable, 1600);
    }

    private Runnable mHideNavHintPanelRunnable = new Runnable() {
        @Override
        public void run() {
            ObjectAnimator anim = ObjectAnimator.ofFloat(mNavigationHintPanel, "alpha", 1f, 0f);
            anim.setDuration(800);
            anim.start();
        }
    };

    public void resetNavPanelHint() {
        mHandler.removeCallbacks(mHideNavHintPanelRunnable);
        mNavigationHintPanel.setAlpha(1f);
    }

    public void setOnScrollChangeListener(NotifyingScrollView.OnScrollChangedListener listener) {
        mScrollListener = listener;
    }

    public int getScrollYPosition() {
        if (mDetailsScrollView == null) {
            return 0;
        }

        return mDetailsScrollView.getScrollY();
    }

//    private String getExpiredTimerValue() {
//
//        long timeDiff = System.currentTimeMillis() - mHolder.mRedeemTime;
//        long timeRemainingInSecs = (INTIIAL_REMAINING_TIME - timeDiff) / 1000;
//        return DateUtils.formatElapsedTime(timeRemainingInSecs);
//
//    }

//    private Runnable mUpdatetimerThread = new Runnable() {
//        @Override
//        public void run() {
//            mRedeemBtnText.setText(getString(R.string.redeem_in) + getExpiredTimerValue());
//            mHandler.postDelayed(this, 0);
//        }
//    };


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            String title = "MickleDeals offer: " + mHolder.getDescription() + " in " + mHolder.mBusinessInfo.getStoreName() + "!";
            String content = mHolder.getDescription() + "\n" + mHolder.mBusinessInfo.getStoreName() + "\n\n"
                    + getString(R.string.share_msg) + "\n\n" + getString(R.string.share_google_play) + getString(R.string.google_play_link);
            Utils.shareScreenShot(getActivity(), mDetailsScrollView, title, content);

        }

        return super.onOptionsItemSelected(item);
    }
}
