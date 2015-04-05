package com.mickledeals.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mickledeals.R;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.MyLinearLayoutManager;
import com.mickledeals.views.PagerIndicator;

import java.util.List;

/**
 * Created by Nicky on 12/7/2014.
 */
public class FeaturedAdapter extends CardAdapter {
    private static final int TYPE_TOP_SECTION = 0;
    private static final int TYPE_BEST_COUPONS = 1;

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    public FeaturedAdapter(FragmentActivity fragmentActivity, List<TestDataHolder> myDataset, int listType, int layoutRes) {
        super(fragmentActivity, myDataset, listType, layoutRes);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if (viewType == TYPE_BEST_COUPONS) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(mLayoutRes, parent, false);
            final MainViewHolder vh = new MainViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.transitDetailsActivity(mFragmentActivity, vh.getPosition() - 1, mListType, v);
                }
            });
            return vh;

        } else if (viewType == TYPE_TOP_SECTION) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.featured_top_section, parent, false);
            ViewPager pager = (ViewPager) v.findViewById(R.id.featurePager);
            View pagerLayout = v.findViewById(R.id.pagerLayout);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pagerLayout.getLayoutParams();
            params.height = Utils.getDeviceWidth(mFragmentActivity) * 9 / 16;
            pagerLayout.setLayoutParams(params);
            FeatureSliderAdapter adapter = new FeatureSliderAdapter(mFragmentActivity.getSupportFragmentManager(),
                    (PagerIndicator) v.findViewById(R.id.pagerIndicator), pager);
            pager.setAdapter(adapter);
            pager.setOnPageChangeListener(adapter);
            pager.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.e("ZZZ", "hasFocus = " + hasFocus);
                }
            });

            HeaderViewHolder hvh = new HeaderViewHolder(v);

            RecyclerView mAddedCouponRecyclerView = (RecyclerView) v.findViewById(R.id.addedCouponRecyclerView);
            RecyclerView mPopularCouponRecyclerView = (RecyclerView) v.findViewById(R.id.popularCouponRecyclerView);
            mAddedCouponRecyclerView.setLayoutManager(new MyLinearLayoutManager(mFragmentActivity, LinearLayoutManager.HORIZONTAL, false));
            mPopularCouponRecyclerView.setLayoutManager(new MyLinearLayoutManager(mFragmentActivity, LinearLayoutManager.HORIZONTAL, false));
            mAddedCouponRecyclerView.setAdapter(new SmallCardAdapter(mFragmentActivity, DataListModel.getInstance().getNewAddedCouponList(), Constants.TYPE_NEW_ADDED_LIST, R.layout.small_card_layout));
            mPopularCouponRecyclerView.setAdapter(new SmallCardAdapter(mFragmentActivity, DataListModel.getInstance().getPopularCouponList(), Constants.TYPE_POPULAR_LIST, R.layout.small_card_layout));
            mAddedCouponRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mPopularCouponRecyclerView.setItemAnimator(new DefaultItemAnimator());
            return hvh;
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_TOP_SECTION;

        return TYPE_BEST_COUPONS;
    }


    protected TestDataHolder getItem(int position) {
        return mDataset.get(position - 1);
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


}
