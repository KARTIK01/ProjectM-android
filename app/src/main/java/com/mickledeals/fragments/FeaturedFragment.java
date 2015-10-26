package com.mickledeals.fragments;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.adapters.FeaturedAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDApiManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class FeaturedFragment extends ListBaseFragment {

    private int mCompletedReponse = 0;


    public List<Integer> getDataList() {
        return DataListModel.getInstance().getBestCouponList();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSliding();
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoSliding();
    }

    public void stopAutoSliding() {
        if (mAdapter != null) ((FeaturedAdapter)mAdapter).stopAutoSliding();
    }

    public void startAutoSliding() {
        if (mAdapter != null) ((FeaturedAdapter)mAdapter).startAutoSliding();
    }

    @Override
    public int getFragmentLayoutRes() {
        return R.layout.fragment_featured;
    }

    @Override
    public void sendRequest() {
        mCompletedReponse = 0;
        MDApiManager.fetchTopFeatureList(new MDApiManager.MDResponseListener<List<Integer>>() {
            @Override
            public void onMDSuccessResponse(List<Integer> object) {
                List<Integer> list = DataListModel.getInstance().getFeatureSliderCouponList();
                list.clear();
                list.addAll(object);
                Collections.shuffle(list);
                mCompletedReponse++;
                notifyDataSetWhenCompleted();
            }

            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
            }

            @Override
            public void onMDErrorResponse(String errorMessage) {
            }
        });
        MDApiManager.fetchNewAddedCouponList(5, new MDApiManager.MDResponseListener<List<Integer>>() {
            @Override
            public void onMDSuccessResponse(List<Integer> object) {
                List<Integer> list = DataListModel.getInstance().getNewAddedCouponList();
                list.clear();
                list.addAll(object);
                mCompletedReponse++;
                notifyDataSetWhenCompleted();
            }

            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
            }

            @Override
            public void onMDErrorResponse(String errorMessage) {
            }
        });
        MDApiManager.fetchFeatureList(new MDApiManager.MDResponseListener<List<Integer>>() {
            @Override
            public void onMDSuccessResponse(List<Integer> object) {
                mDataList.clear();
                mDataList.addAll(object);
                Collections.shuffle(mDataList);
                mCompletedReponse++;
                notifyDataSetWhenCompleted();
            }

            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
                FeaturedFragment.super.onMDNetworkErrorResponse(errorMessage);
            }

            @Override
            public void onMDErrorResponse(String errorMessage) {
                FeaturedFragment.super.onMDErrorResponse(errorMessage);
            }
        });
    }

    private void notifyDataSetWhenCompleted() {
        if (mCompletedReponse >= 3) {
            ((FeaturedAdapter)mAdapter).notifySlideFeatureDataSetChanged();
            ((FeaturedAdapter)mAdapter).notifyNewAddedCouponDataSetChanged();
            mAdapter.notifyDataSetChanged();
            mAdapter.setPendingAnimated();

            if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean needLoadMore() {
        return false;
    }

    public void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager;
        if (mContext.getResources().getInteger(R.integer.dp_width_level) > 0) {
            layoutManager = new GridLayoutManager(mContext, 2);
            ((GridLayoutManager)layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    if (i == 0) return 2;
                    else return 1;
                }
            });
        } else {
            layoutManager = new LinearLayoutManager(mContext);
        }

        mListResultRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FeaturedAdapter(this, DataListModel.getInstance().getBestCouponList(), Constants.TYPE_BEST_LIST, R.layout.card_layout_featured);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
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
}
