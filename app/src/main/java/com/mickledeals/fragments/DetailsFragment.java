package com.mickledeals.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.activities.BusinessPageActivity;
import com.mickledeals.activities.BuyDialogActivity;
import com.mickledeals.activities.DetailsActivity;
import com.mickledeals.activities.MapActivity;
import com.mickledeals.activities.RedeemDialogActivity;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.LocationManager;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.NotifyingScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Nicky on 11/28/2014.
 */
public class DetailsFragment extends BaseFragment {

    private static final long INTIIAL_REMAINING_TIME = 2 * 60 * 60 * 1000;
    private static final int REQUEST_CODE_BUY = 1;
    private static final int REQUEST_CODE_REDEEM = 2;

    private TestDataHolder mHolder;

    private TextView mBuyBtn;
    private TextView mJoinVipBtn;
    private TextView mRedeemBtn;
    private TextView mSaveBtn;
    private TextView mBusinessInfoBtn;
    private TextView mShareBtn;
    private View mAddressBtn;
    private View mCallBtn;
    private View mBuyPanel;
    private View mRedeemPanel;
    private LinearLayout mMoreCouponRow;
    private Handler mHandler;
    private TextView mExpiredTime;
    private NotifyingScrollView mDetailsScrollView;
    private NotifyingScrollView.OnScrollChangedListener mScrollListener;

    private int mQuantity = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHolder = DataListModel.getInstance().getDataList().get(getArguments().getInt("storeId"));
        mHandler = new Handler();
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

        mBuyBtn = (TextView) view.findViewById(R.id.buyBtn);
        mJoinVipBtn = (TextView) view.findViewById(R.id.joinVipBtn);
        mRedeemBtn = (TextView) view.findViewById(R.id.redeemBtn);
        mShareBtn = (TextView) view.findViewById(R.id.sharedButton);
        mBusinessInfoBtn = (TextView) view.findViewById(R.id.businessInfoButton);
        mSaveBtn = (TextView) view.findViewById(R.id.savedButton);
        mAddressBtn = view.findViewById(R.id.address);
        mCallBtn = view.findViewById(R.id.call);
        mBuyPanel = view.findViewById(R.id.buyPanel);
        mRedeemPanel = view.findViewById(R.id.redeemPanel);
        mMoreCouponRow = (LinearLayout) view.findViewById(R.id.moreCouponRow);
        mDetailsScrollView = (NotifyingScrollView) view.findViewById(R.id.detailsScrollView);
        mDetailsScrollView.setOnScrollChangedListener(mScrollListener);

        ((TextView) view.findViewById(R.id.storeName)).setText(mHolder.getStoreName());
        ((TextView) view.findViewById(R.id.couponDescription)).setText(mHolder.getDescription());
        ((TextView) view.findViewById(R.id.couponPrice)).setText(mHolder.mPrice == 0 ? "FREE" : "$" + (int) (mHolder.mPrice));
        ((TextView) view.findViewById(R.id.buyPrice)).setText("$" + (int) (mHolder.mPrice));
        ((TextView) view.findViewById(R.id.address)).setText(mHolder.mAddress);
        float dist = LocationManager.getInstance(mContext).getDistanceFromCurLocation(mHolder);
        String addrShort = mHolder.mAddressShort;
        if (dist > 0) {
            addrShort += " • " + dist + " mi";
        }
        ((TextView) view.findViewById(R.id.addressDist)).setText(addrShort);

        ImageView storePhoto = (ImageView) view.findViewById(R.id.imageView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) storePhoto.getLayoutParams();
        params.height = Utils.getDeviceWidth(mContext) * 9 / 16; //DO NOT NEED THIS if the image is already fitted, this is just for adjusting to 16:9
        storePhoto.setLayoutParams(params);
        storePhoto.setImageResource(mHolder.mImageResId);
        if (Build.VERSION.SDK_INT >= 21) {
            storePhoto.setTransitionName("cardImage" + mHolder.mId);
            getActivity().startPostponedEnterTransition();
            //seems like no need to use below line???
            //scheduleStartPostponedTransition(storePhoto);
        }

        mBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showBuyDialog();
                Intent i = new Intent(mContext, BuyDialogActivity.class);
                i.putExtra("price", mHolder.mPrice);
                startActivityForResult(i, REQUEST_CODE_BUY);
            }
        });

        mRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showRedeemDialog();
                Intent i = new Intent(mContext, RedeemDialogActivity.class);
                i.putExtra("storeName", mHolder.mStoreName);
                i.putExtra("couponDesc", mHolder.mDescription);
                startActivityForResult(i, REQUEST_CODE_REDEEM);

            }
        });

        mSaveBtn.setText(mHolder.mSaved ? R.string.saved : R.string.save);
        mSaveBtn.setCompoundDrawablesWithIntrinsicBounds(mHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off, 0, 0, 0);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHolder.mSaved = !mHolder.mSaved;
                mSaveBtn.setText(mHolder.mSaved ? R.string.saved : R.string.save);
                mSaveBtn.setCompoundDrawablesWithIntrinsicBounds(mHolder.mSaved ? R.drawable.ic_star_on : R.drawable.ic_star_off, 0, 0, 0);

                StringBuilder sb = new StringBuilder();
                for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
                    if (holder.mSaved) {
                        sb.append(holder.mId);
                        sb.append("|");
                    }
                }
                PreferenceHelper.savePreferencesStr(mContext, "saveList", sb.toString());
            }
        });

        mAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MapActivity.class);
                i.putExtra("dataObject", mHolder);
                startActivity(i);
            }
        });

        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCoupon();
            }
        });


        if (mHolder.mId == 2 || mHolder.mId == 3) {
            mBusinessInfoBtn.setVisibility(View.VISIBLE);
            mBusinessInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, BusinessPageActivity.class);
                    i.putExtra("storeId", mHolder.mId);
                    startActivity(i);
                }
            });

            view.findViewById(R.id.moreCouponLabel).setVisibility(View.VISIBLE);
            View otherCoupon = getActivity().getLayoutInflater().inflate(R.layout.card_layout_others, null);
            mMoreCouponRow.setVisibility(View.VISIBLE);
            mMoreCouponRow.addView(otherCoupon);
            otherCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, DetailsActivity.class);
                    DataListModel.getInstance().getMoreCouponsList().clear();
                    DataListModel.getInstance().getMoreCouponsList().add(DataListModel.getInstance().getDataList().get(mHolder.mId == 2 ? 3 : 2));
                    i.putExtra("listIndex", 0);
                    i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                    startActivity(i);
                }
            });
            TestDataHolder otherCouponData = DataListModel.getInstance().getDataList().get(mHolder.mId == 2 ? 3 : 2);
            ((ImageView) otherCoupon.findViewById(R.id.card_image)).setImageResource(otherCouponData.mSmallImageResId);
            ((TextView) otherCoupon.findViewById(R.id.card_description)).setText(otherCouponData.getDescription());
            ((TextView) otherCoupon.findViewById(R.id.card_price)).setText("$" + (int) (otherCouponData.mPrice));
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
                mRedeemPanel.setVisibility(View.VISIBLE);
                mBuyPanel.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_CODE_REDEEM) {

            }
        }
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

    private void shareCoupon() {
        View v = getActivity().findViewById(android.R.id.content).getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        int statusBarHeight = Utils.getStatusBarHeight(mContext);
        bitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, bitmap.getWidth(), bitmap.getHeight() - statusBarHeight, null, true);
        File file = Utils.getImageFileLocation();
        if (file == null) {
            Toast.makeText(mContext, R.string.share_failed,
                    Toast.LENGTH_LONG).show();
            return;
        }
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mHolder.getDescription() + "\n" + mHolder.getStoreName());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.fromFile(file));
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
    }

    private void showBuyDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.buy_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .create();

        dialog.show();

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRedeemPanel.setVisibility(View.VISIBLE);
                mBuyPanel.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        final TextView quantityTv = (TextView) view.findViewById(R.id.quantity);
        quantityTv.setText(mQuantity + "");
        final TextView singlePrice = (TextView) view.findViewById(R.id.singlePrice);
        singlePrice.setText("$" + (int) (mHolder.mPrice));
        final TextView totalPriceTv = (TextView) view.findViewById(R.id.totalPrice);
        totalPriceTv.setText("$" + (int) (mQuantity * mHolder.mPrice));
        TextView plus = (TextView) view.findViewById(R.id.plus);
        final TextView minus = (TextView) view.findViewById(R.id.minus);
        if (mQuantity <= 1) minus.setEnabled(false);
        else minus.setEnabled(true);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity--;
                quantityTv.setText(mQuantity + "");
                totalPriceTv.setText("$" + (int) (mQuantity * mHolder.mPrice));
                if (mQuantity <= 1) minus.setEnabled(false);
                else minus.setEnabled(true);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity++;
                quantityTv.setText(mQuantity + "");
                totalPriceTv.setText("$" + (int) (mQuantity * mHolder.mPrice));
                if (mQuantity <= 1) minus.setEnabled(false);
                else minus.setEnabled(true);
            }
        });
    }

    private void showRedeemDialog() {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.redeem_dialog, null);
//        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AlertDialogCustom))
//                .setView(view)
//                .create();
//        dialog.show();
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mHandler.removeCallbacks(mUpdatetimerThread);
//            }
//        });
//        final TextView storeName = (TextView) view.findViewById(R.id.storeName);
//        storeName.setText(mHolder.getStoreName());
//        final TextView discLong = (TextView) view.findViewById(R.id.discLong);
//        discLong.setText(mHolder.getDescription());
//        discLong.setSelected(true);
//        mExpiredTime = (TextView) view.findViewById(R.id.expireTime);
//
//        if (mHolder.mRedeemTime == 0) mHolder.mRedeemTime = System.currentTimeMillis();
//
//        mHandler.removeCallbacks(mUpdatetimerThread);
//        mHandler.postDelayed(mUpdatetimerThread, 0);
    }

    private String getExpiredTimerValue() {

        long timeDiff = System.currentTimeMillis() - mHolder.mRedeemTime;
        long timeRemainingInSecs = (INTIIAL_REMAINING_TIME - timeDiff) / 1000;
        return DateUtils.formatElapsedTime(timeRemainingInSecs);

    }

    private Runnable mUpdatetimerThread = new Runnable() {
        @Override
        public void run() {
            mExpiredTime.setText(getExpiredTimerValue());
            mHandler.postDelayed(this, 0);
        }
    };



}
