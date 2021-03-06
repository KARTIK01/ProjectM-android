package com.mickledeals.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mickledeals.R;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.views.MyLinearLayoutManager;
import com.mickledeals.views.PagerIndicator;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class FeaturedAdapter extends CardAdapter {
    private static final int TYPE_TOP_SECTION = 0;
    private static final int TYPE_BEST_COUPONS = 1;
    private FeatureSliderAdapter mFeatureSliderAdapter;
    private RecyclerView mAddedCouponRecyclerView;
    private ViewGroup mTopSectionLayout;

    public static class HeaderViewHolder extends CardAdapter.MainViewHolder {
        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    public FeaturedAdapter(Fragment fragment, List<Integer> myDataset, int listType, int layoutRes) {
        super(fragment, myDataset, listType, layoutRes);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if (viewType != TYPE_TOP_SECTION) {
            return super.onCreateViewHolder(parent, viewType);

        } else  {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.featured_top_section, parent, false);
            mTopSectionLayout = (ViewGroup)v;
            ViewPager pager = (ViewPager) v.findViewById(R.id.featurePager);
            View pagerLayout = v.findViewById(R.id.pagerLayout);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pagerLayout.getLayoutParams();
            params.height = MDApplication.sDeviceWidth * 9 / 16;
            pagerLayout.setLayoutParams(params);
            if (mFeatureSliderAdapter == null) {
                mFeatureSliderAdapter = new FeatureSliderAdapter(mFragmentActivity,
                        (PagerIndicator) v.findViewById(R.id.pagerIndicator), pager);
                pager.setAdapter(mFeatureSliderAdapter);
                pager.setOnPageChangeListener(mFeatureSliderAdapter);
            }

            HeaderViewHolder hvh = new HeaderViewHolder(v);

            mAddedCouponRecyclerView = (RecyclerView) v.findViewById(R.id.addedCouponRecyclerView);
            mAddedCouponRecyclerView.setLayoutManager(new MyLinearLayoutManager(mFragmentActivity, LinearLayoutManager.HORIZONTAL, false));
            mAddedCouponRecyclerView.setAdapter(new CardAdapter(mFragment, DataListModel.getInstance().getNewAddedCouponList(), Constants.TYPE_NEW_ADDED_LIST, R.layout.card_layout_new));
            mAddedCouponRecyclerView.setItemAnimator(new DefaultItemAnimator());
//            RecyclerView mPopularCouponRecyclerView = (RecyclerView) v.findViewById(R.id.popularCouponRecyclerView);
//            mPopularCouponRecyclerView.setLayoutManager(new MyLinearLayoutManager(mFragmentActivity, LinearLayoutManager.HORIZONTAL, false));
//            mPopularCouponRecyclerView.setAdapter(new SmallCardAdapter(mFragmentActivity, DataListModel.getInstance().getPopularCouponList(), Constants.TYPE_POPULAR_LIST, R.layout.card_layout_new));
//            mPopularCouponRecyclerView.setItemAnimator(new DefaultItemAnimator());
            return hvh;
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (isPositionHeader(position)) {
            for (int i = 0; i < mTopSectionLayout.getChildCount(); i++) {
                setAnimation(mTopSectionLayout.getChildAt(i), i - 3);
            }
            return;
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return TYPE_TOP_SECTION;

        return super.getItemViewType(position);
    }


    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void stopAutoSliding() {
        if (mFeatureSliderAdapter != null) {
            mFeatureSliderAdapter.stopAutoSliding();
        }
    }

    public void startAutoSliding() {
        if (mFeatureSliderAdapter != null) {
            mFeatureSliderAdapter.startAutoSliding();
        }
    }

    @Override
    protected int convertListPosToDataPos(int position) {

        return position - 1;
    }

    public void notifyNewAddedCouponDataSetChanged() {
        mAddedCouponRecyclerView.getAdapter().notifyDataSetChanged();
        notifyDataSetChanged();

    }
    public void notifySlideFeatureDataSetChanged() {
        mFeatureSliderAdapter.notifyDataSetChanged();
    }

}
