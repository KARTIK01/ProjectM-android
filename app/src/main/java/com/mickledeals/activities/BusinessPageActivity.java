package com.mickledeals.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.adapters.BusinessPhotoSliderAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.NotifyingScrollView;
import com.mickledeals.views.PagerIndicator;
import com.mickledeals.views.RoundedImageView;

/**
 * Created by Nicky on 2/21/2015.
 */
public class BusinessPageActivity extends BaseActivity{

    private RoundedImageView mRoundedImageView;
    private ViewPager mPhotoViewPager;
    private View mShadow;
    private TestDataHolder mHolder;

    private TextView mOpenHours;
    private TextView mAddress;

    private NotifyingScrollView mDetailsScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mDetailsScrollView = (NotifyingScrollView) findViewById(R.id.detailsScrollView);
        mDetailsScrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                setToolBarTransparency(t);
            }
        });
        mRoundedImageView = (RoundedImageView) findViewById(R.id.roundImageView);
        mShadow = findViewById(R.id.toolbarShadow);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRoundedImageView.getLayoutParams();
        params.topMargin = Utils.getDeviceWidth(this) * 336 / 540 - Utils.getPixelsFromDip(50f, getResources());
        mRoundedImageView.setLayoutParams(params);

        mPhotoViewPager = (ViewPager) findViewById(R.id.photoViewPager);
        BusinessPhotoSliderAdapter adapter = new BusinessPhotoSliderAdapter(getSupportFragmentManager(),
                (PagerIndicator) findViewById(R.id.pagerIndicator), mPhotoViewPager);
        mPhotoViewPager.setAdapter(adapter);
        mPhotoViewPager.setOnPageChangeListener(adapter);
        mPhotoViewPager.setPageMargin(Utils.getPixelsFromDip(6f, getResources()));


        mHolder = DataListModel.getInstance().getDataList().get(getIntent().getIntExtra("storeId", 0));

        mOpenHours = (TextView) findViewById(R.id.openHours);
        mOpenHours.setText(getString(R.string.open_hours) + ": 10:00AM to 11:00PM");
        mAddress = (TextView) findViewById(R.id.address);
        mAddress.setText(mHolder.mAddress);

        getSupportActionBar().setTitle(mHolder.getStoreName());
        setToolBarTransparency(0);


        findViewById(R.id.moreCouponRow1).setVisibility(View.VISIBLE);
        findViewById(R.id.moreCouponRow1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(BusinessPageActivity.this, DetailsActivity.class);
                DataListModel.getInstance().getMoreCouponsList().clear();
                DataListModel.getInstance().getMoreCouponsList().add(DataListModel.getInstance().getDataList().get(2));
                i.putExtra("listIndex", 0);
                i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                startActivity(i);
            }
        });
        TestDataHolder otherCoupon = DataListModel.getInstance().getDataList().get(2);
        ((ImageView) findViewById(R.id.moreCouponImage1)).setImageResource(otherCoupon.mSmallImageResId);
        ((TextView) findViewById(R.id.moreCouponTitle1)).setText(otherCoupon.getDescription());
        ((TextView) findViewById(R.id.moreCouponPrice1)).setText("$" + (int) (otherCoupon.mPrice));


        findViewById(R.id.moreCouponRow2).setVisibility(View.VISIBLE);
        findViewById(R.id.moreCouponRow2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(BusinessPageActivity.this, DetailsActivity.class);
                DataListModel.getInstance().getMoreCouponsList().clear();
                DataListModel.getInstance().getMoreCouponsList().add(DataListModel.getInstance().getDataList().get(3));
                i.putExtra("listIndex", 0);
                i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                startActivity(i);
            }
        });
        TestDataHolder otherCoupon2 = DataListModel.getInstance().getDataList().get(3);
        ((ImageView) findViewById(R.id.moreCouponImage2)).setImageResource(otherCoupon2.mSmallImageResId);
        ((TextView) findViewById(R.id.moreCouponTitle2)).setText(otherCoupon2.getDescription());
        ((TextView) findViewById(R.id.moreCouponPrice2)).setText("$" + (int) (otherCoupon2.mPrice));


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_business_page;
    }

    private void setToolBarTransparency(int scrollPos) {
        final int headerHeight = Utils.getDeviceWidth(this) * 336 / 540;
        final float ratio = (float) Math.min(Math.max(scrollPos, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        mToolBar.getBackground().setAlpha(newAlpha);
        mToolBar.setTitleTextColor(Color.argb(newAlpha, 255, 255, 255));
        mShadow.setAlpha(ratio);
    }
}
