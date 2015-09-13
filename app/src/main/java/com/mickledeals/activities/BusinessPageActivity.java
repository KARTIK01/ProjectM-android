package com.mickledeals.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.adapters.BusinessPhotoSliderAdapter;
import com.mickledeals.datamodel.BusinessPhoto;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.NotifyingScrollView;
import com.mickledeals.views.PagerIndicator;
import com.mickledeals.views.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2/21/2015.
 */
public class BusinessPageActivity extends SwipeDismissActivity {

    private RoundedImageView mRoundedImageView;
    private ViewPager mPhotoViewPager;
    private View mShadow;
    private CouponInfo mHolder;

    private TextView mOpenHours;
    private TextView mAddress;
    private TextView mDirection;

    private LinearLayout mMoreCouponRow;
    private NotifyingScrollView mDetailsScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;


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
        params.topMargin = MDApplication.sDeviceWidth * 336 / 540 - Utils.getPixelsFromDip(50f, getResources());
        mRoundedImageView.setLayoutParams(params);

        mPhotoViewPager = (ViewPager) findViewById(R.id.photoViewPager);

        //create adapter after fetching data
        List photoList = new ArrayList<BusinessPhoto>();
        BusinessPhoto photo = new BusinessPhoto();
        photo.mResId = R.drawable.pic_business_1;
        photo.mPhotoDescription = getString(R.string.business_photo1);
        photoList.add(photo);
        photo = new BusinessPhoto();
        photo.mResId = R.drawable.pic_business_2;
        photo.mPhotoDescription = getString(R.string.business_photo2);
        photoList.add(photo);
        photo = new BusinessPhoto();
        photo.mResId = R.drawable.pic_business_3;
        photo.mPhotoDescription = getString(R.string.business_photo3);
        photoList.add(photo);
        photo = new BusinessPhoto();
        photo.mResId = R.drawable.pic_business_4;
        photo.mPhotoDescription = getString(R.string.business_photo4);
        photoList.add(photo);

        BusinessPhotoSliderAdapter adapter = new BusinessPhotoSliderAdapter(getSupportFragmentManager(),
                (PagerIndicator) findViewById(R.id.pagerIndicator), photoList);

        //temp above

        mPhotoViewPager.setAdapter(adapter);
        mPhotoViewPager.setOnPageChangeListener(adapter);
        mPhotoViewPager.setPageMargin(Utils.getPixelsFromDip(6f, getResources()));


        mHolder = DataListModel.getInstance().getDataList().get(getIntent().getIntExtra("storeId", 0));

        mOpenHours = (TextView) findViewById(R.id.openHours);
        mOpenHours.setText(getString(R.string.open_hours) + ": 10:00AM to 11:00PM");
        mAddress = (TextView) findViewById(R.id.address);
        mAddress.setText(mHolder.mAddress);
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BusinessPageActivity.this, MapActivity.class);
                i.putExtra("dataObject", mHolder);
                startActivity(i);
            }
        });
        mDirection = (TextView) findViewById(R.id.directions);
        mDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + mHolder.mLatLng.replace(" ", "")));
                startActivity(intent);
            }
        });

        getSupportActionBar().setTitle(mHolder.getStoreName());
        setToolBarTransparency(0);


        mMoreCouponRow = (LinearLayout) findViewById(R.id.moreCouponRow);
        View otherCoupon = getLayoutInflater().inflate(R.layout.card_layout_others, null);
        mMoreCouponRow.setVisibility(View.VISIBLE);
        mMoreCouponRow.addView(otherCoupon);
        otherCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(BusinessPageActivity.this, DetailsActivity.class);
                DataListModel.getInstance().getMoreCouponsList().clear(); //need to revisit this crappy logic, no need list
                DataListModel.getInstance().getMoreCouponsList().add(DataListModel.getInstance().getDataList().get(mHolder.mId == 2 ? 3 : 2));
                i.putExtra("listIndex", 0);
                i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                startActivity(i);
            }
        });

        CouponInfo otherCouponData = DataListModel.getInstance().getDataList().get(2);
        ((ImageView) otherCoupon.findViewById(R.id.card_image)).setImageResource(otherCouponData.mSmallImageResId);
        ((TextView) otherCoupon.findViewById(R.id.card_description)).setText(otherCouponData.getDescription());
        ((TextView) otherCoupon.findViewById(R.id.card_price)).setText("$" + (int) (otherCouponData.mPrice));

        View otherCoupon2 = getLayoutInflater().inflate(R.layout.card_layout_others, null);
        mMoreCouponRow.setVisibility(View.VISIBLE);
        mMoreCouponRow.addView(otherCoupon2);
        otherCoupon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(BusinessPageActivity.this, DetailsActivity.class);
                DataListModel.getInstance().getMoreCouponsList().clear(); //need to revisit this crappy logic, no need list
                DataListModel.getInstance().getMoreCouponsList().add(DataListModel.getInstance().getDataList().get(mHolder.mId == 2 ? 3 : 2));
                i.putExtra("listIndex", 0);
                i.putExtra("listType", Constants.TYPE_MORE_COUPONS_LIST);
                startActivity(i);
            }
        });
        CouponInfo otherCouponData2 = DataListModel.getInstance().getDataList().get(3);
        ((ImageView) otherCoupon2.findViewById(R.id.card_image)).setImageResource(otherCouponData2.mSmallImageResId);
        ((TextView) otherCoupon2.findViewById(R.id.card_description)).setText(otherCouponData2.getDescription());
        ((TextView) otherCoupon2.findViewById(R.id.card_price)).setText("$" + (int) (otherCouponData2.mPrice));

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
            Utils.shareScreenShot(this, mDetailsScrollView, getString(R.string.share_subject), mHolder.getDescription() + "\n" + mHolder.getStoreName());
        }

        return super.onOptionsItemSelected(item);
    }
}
