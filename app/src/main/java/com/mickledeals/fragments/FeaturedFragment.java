package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.R;
import com.mickledeals.adapters.FeaturedAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;

/**
 * Created by Nicky on 11/28/2014.
 */
public class FeaturedFragment extends BaseFragment {

    private ViewPager mPager;
    private RecyclerView mFeatureRecyclerView;
    private RecyclerView mPopularCouponRecyclerView;
    private RecyclerView mBestCouponRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FeaturedAdapter mFeatureAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_featured, container, false);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFeatureRecyclerView = (RecyclerView) view.findViewById(R.id.featureRecyclerView);
        mLayoutManager = new LinearLayoutManager(mContext);
//        mLayoutManager = new GridLayoutManager(mContext, 2);
//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int i) {
//                if (i == 0) return 2;
//                else return 1;
//            }
//        });

        mFeatureRecyclerView.setLayoutManager(mLayoutManager);
        mFeatureAdapter = new FeaturedAdapter(getActivity(), DataListModel.getInstance().getBestCouponList(), Constants.TYPE_BEST_LIST, R.layout.card_layout_featured);
        mFeatureRecyclerView.setAdapter(mFeatureAdapter);
        mFeatureRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mFeatureRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int pos = parent.getChildPosition(view);
//                if (pos != 0) {
//                    boolean leftside = pos % 2 == 1;
//                    outRect.left = leftside ? margin : margin / 2;
//                    outRect.right = leftside ? margin / 2 : margin;
//                    outRect.bottom = bottomMargin;
//                }
//            }
//        });

    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSliding();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFeatureAdapter.notifyDataSetChanged();
        startAutoSliding();
    }

    public void stopAutoSliding() {
        if (mFeatureAdapter != null) mFeatureAdapter.stopAutoSliding();
    }

    public void startAutoSliding() {
        if (mFeatureAdapter != null) mFeatureAdapter.startAutoSliding();
    }
}
