package com.mickledeals.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mickledeals.R;
import com.mickledeals.adapters.BusinessPhotoSliderAdapter;
import com.mickledeals.datamodel.BusinessInfo;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.AspectRatioNetworkImageView;
import com.mickledeals.views.NotifyingScrollView;
import com.mickledeals.views.PagerIndicator;
import com.mickledeals.views.RoundedImageView;

/**
 * Created by Nicky on 2/21/2015.
 */
public class BusinessPageActivity extends SwipeDismissActivity {

    private NetworkImageView mCoverImageView;
    private RoundedImageView mRoundedImageView;
    private ViewPager mPhotoViewPager;
    private View mShadow;
    private BusinessInfo mBusinessInfo;

    private View mNewsLabel;
    private View mPhotoLabel;

    private TextView mName;
    private TextView mDescription;
    private TextView mNews;

    private TextView mOpenHours;
    private TextView mAddress;
    private TextView mDirection;
    private TextView mCall;
    private TextView mWebsite;

    private LinearLayout mMoreCouponRow;
    private NotifyingScrollView mDetailsScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mBusinessInfo = (BusinessInfo) getIntent().getSerializableExtra("businessInfo");

        mDetailsScrollView = (NotifyingScrollView) findViewById(R.id.detailsScrollView);
        mDetailsScrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                setToolBarTransparency(t);
            }
        });
        mRoundedImageView = (RoundedImageView) findViewById(R.id.roundImageView);
        mCoverImageView = (NetworkImageView) findViewById(R.id.imageView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCoverImageView.getLayoutParams();
        params.height = MDApplication.sDeviceWidth * 9 / 16;// NEED THIS
        mCoverImageView.setLayoutParams(params);
//        mCoverImageView.setNoAnimation();
        mCoverImageView.setImageUrl(mBusinessInfo.mCoverPhotoUrl, MDApiManager.sImageLoader);
        mShadow = findViewById(R.id.toolbarShadow);
        params = (RelativeLayout.LayoutParams) mRoundedImageView.getLayoutParams();
        params.topMargin = MDApplication.sDeviceWidth * 9 / 16 - Utils.getPixelsFromDip(50f, getResources());
        mRoundedImageView.setLayoutParams(params);
        mRoundedImageView.setImageUrl(mBusinessInfo.mLogoUrl, MDApiManager.sImageLoader);

        mPhotoViewPager = (ViewPager) findViewById(R.id.photoViewPager);

        mNewsLabel = findViewById(R.id.newsLabel);
        mPhotoLabel = findViewById(R.id.photoLabel);
        mName = (TextView) findViewById(R.id.businessName);
        mDescription = (TextView) findViewById(R.id.businessDescription);
        mNews = (TextView) findViewById(R.id.businessNews);


        if (mBusinessInfo.mPhotos.size() > 0) {
            BusinessPhotoSliderAdapter adapter = new BusinessPhotoSliderAdapter(this,
                    (PagerIndicator) findViewById(R.id.pagerIndicator), mBusinessInfo.mPhotos);

            mPhotoViewPager.setAdapter(adapter);
            mPhotoViewPager.addOnPageChangeListener(adapter);
            mPhotoViewPager.setPageMargin(Utils.getPixelsFromDip(6f, getResources()));
        } else {
            mPhotoLabel.setVisibility(View.GONE);
            mPhotoViewPager.setVisibility(View.GONE);
            findViewById(R.id.pagerIndicator).setVisibility(View.GONE);
        }


        mName.setText(mBusinessInfo.getStoreName());
        mDescription.setText(mBusinessInfo.getDescription());
        if (!mBusinessInfo.getNews().isEmpty()) {
            mNews.setText(mBusinessInfo.getNews());
        } else {
            mNewsLabel.setVisibility(View.GONE);
            mNews.setVisibility(View.GONE);
        }

        mOpenHours = (TextView) findViewById(R.id.openHours);
        if (mBusinessInfo.mHours.trim().isEmpty()) {
            mOpenHours.setVisibility(View.GONE);
        } else {
            mOpenHours.setText(mBusinessInfo.mHours);
        }
        mAddress = (TextView) findViewById(R.id.address);
        mAddress.setText(mBusinessInfo.getFullAddress());
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BusinessPageActivity.this, MapActivity.class);
                i.putExtra("dataObject", mBusinessInfo);
                startActivity(i);
            }
        });
        mDirection = (TextView) findViewById(R.id.directions);
        mDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + mBusinessInfo.mLat + ","
                                + mBusinessInfo.mLng));
                startActivity(intent);
            }
        });
        mCall = (TextView) findViewById(R.id.call);
        if (mBusinessInfo.mPhone.trim().isEmpty()) {
            mCall.setVisibility(View.GONE);
        } else {
            mCall.setText(mBusinessInfo.mPhone);
            mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + mBusinessInfo.mPhone.replaceAll("(-|\\(|\\))", "").trim();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
            });
        }
        mWebsite = (TextView) findViewById(R.id.website);
        if (mBusinessInfo.mWebSiteAddr.trim().isEmpty()) {
            mWebsite.setVisibility(View.GONE);
        } else {
            mWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = mBusinessInfo.mWebSiteAddr;
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }


        getSupportActionBar().setTitle(mBusinessInfo.getStoreName());
        setToolBarTransparency(0);


        mMoreCouponRow = (LinearLayout) findViewById(R.id.moreCouponRow);


        if (mBusinessInfo.mCouponsId.size() > 0) {
            for (final Integer couponId : mBusinessInfo.mCouponsId) {

                CouponInfo info = DataListModel.getInstance().getCouponMap().get(couponId);
                if (!info.mActive) continue;
                findViewById(R.id.moreCouponLabel).setVisibility(View.VISIBLE);
                mMoreCouponRow.setVisibility(View.VISIBLE);

                View otherCoupon = getLayoutInflater().inflate(R.layout.card_layout_others, null);
                //load image
                ((TextView) otherCoupon.findViewById(R.id.card_description)).setText(info.getDescription());
                TextView cardPrice = (TextView) otherCoupon.findViewById(R.id.card_price);
                cardPrice.setText(info.getDisplayedPrice());

                final AspectRatioNetworkImageView imageView = ((AspectRatioNetworkImageView) otherCoupon.findViewById(R.id.card_image));
                imageView.setImageUrl(info.mCoverPhotoUrl, MDApiManager.sImageLoader);

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
                        DataListModel.sTransitBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Intent i = new Intent(BusinessPageActivity.this, DetailsActivity.class);
                        DataListModel.getInstance().getMoreCouponsList().clear(); //do not need list, but in case future we need
                        DataListModel.getInstance().getMoreCouponsList().add(couponId);
                        i.putExtra("listIndex", 0);
                        i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                        i.putExtra("fromOtherCoupon", true);
                        i.putExtra("fromBusinessProfile", true);
                        startActivity(i);
                    }
                });
                mMoreCouponRow.addView(otherCoupon);
            }
        }

    }

    @Override
    protected int getLayoutType() {
        return LAYOUT_TYPE_DIALOG_SWIPE; //do not use fullscreen_swipe because of toolbar overlap
    }

    private void setToolBarTransparency(int scrollPos) {
        final int headerHeight = MDApplication.sDeviceWidth * 336 / 540;
        final float ratio = (float) Math.min(Math.max(scrollPos, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        mToolBar.getBackground().mutate().setAlpha(newAlpha);
        mToolBar.setTitleTextColor(Color.argb(newAlpha, 255, 255, 255));
        mShadow.setAlpha(ratio);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_business_page;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {


            String title = mBusinessInfo.getStoreName() + " on MickleDeals!";
            String content = getString(R.string.share_business_page_msg, mBusinessInfo.getStoreName()) + "\n\n" + getString(R.string.share_google_play) + getString(R.string.google_play_link);
            Utils.shareScreenShot(this, mDetailsScrollView, title, content);
        }

        return super.onOptionsItemSelected(item);
    }
}
