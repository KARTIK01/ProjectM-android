package com.mickledeals.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;

import java.util.List;

/**
 * Created by Nicky on 7/23/2015.
 */
public abstract class SwipeRefreshBaseFragment extends BaseFragment implements MDApiManager.MDResponseListener<List<CouponInfo>>{

    protected View mNoResultLayout;
    protected View mNoNetworkLayout;
    protected ViewStub mNoNetworkStub;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mListResultRecyclerView;
    protected CardAdapter mAdapter;

    protected List<CouponInfo> mDataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataList = getDataList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                getFragmentLayoutRes(), container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListResultRecyclerView = (RecyclerView) view.findViewById(R.id.listResultRecyclerView);
        setRecyclerView();
//        mListResultRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mListResultRecyclerView.getLayoutManager()) {
//            @Override
//            public void onLoadMore(int currentPage) {
//                DLog.d(SwipeRefreshBaseFragment.this, "onloadmore page = " + currentPage);
//                mDataList.add(null);
//                mAdapter.notifyItemInserted(mDataList.size());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //remove progress bar
//                        mDataList.remove(mDataList.size() - 1);
//                        mAdapter.notifyItemRemoved(mDataList.size());
//                        for (int i = 1; i < DataListModel.getInstance().getDataList().size(); i++) {
//                            TestDataHolder holder = DataListModel.getInstance().getDataList().get(i);
//                            mDataList.add(holder);
//                            mListResultRecyclerView.getAdapter().notifyItemInserted(mDataList.size());
//                        }
//                    }
//                }, 3000);
//
//            }
//        });


        mNoResultLayout = view.findViewById(R.id.noResultLayout);
        mNoNetworkStub = (ViewStub) view.findViewById(R.id.noNetworkStub);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.requestDisallowInterceptTouchEvent(false);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary1, R.color.colorPrimary4);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    prepareSendRequest();
                }
            });
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                prepareSendRequest();
            }
        });
    }

    public void prepareSendRequest() {
        DLog.d(this, "prepareSendRequest");

        if (mNoNetworkLayout != null) mNoNetworkLayout.setVisibility(View.GONE);
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);

        sendRequest(false);

//        MDApiManager.sendJSONArrayRequest(request, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//
//
//                Log.d("ZZZ", "reponse= " + response);
//
//                onSuccessResponse();
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                //UNCOMMENT IT
//
//                DLog.e(this, "message = " + error.getMessage());
//
//
////                if (mNoNetworkLayout == null) mNoNetworkLayout = mNoNetworkStub.inflate();
////                mNoNetworkLayout.setVisibility(View.VISIBLE);
////
////                Button retryButton = (Button) mNoNetworkLayout.findViewById(R.id.retryButton);
////                retryButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        prepareSendRequest();
////                    }
////                });
////                mSwipeRefreshLayout.setRefreshing(false);
//
//
//                //remove this please
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        onSuccessResponse();
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 1000);
//            }
//        });
    }

    @Override
    public void onMDSuccessResponse(List<CouponInfo> resultList) {
        mDataList.clear();
        mDataList.addAll(resultList);
        onSuccessResponse();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onMDNetworkErrorResponse(String errorMessage) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onMDErrorResponse(String errorMessage) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onSuccessResponse() {
        mListResultRecyclerView.getAdapter().notifyDataSetChanged();
        ((CardAdapter) mListResultRecyclerView.getAdapter()).setPendingAnimated();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();

        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);
    }

    public abstract void sendRequest(boolean loadMore);

    public abstract int getFragmentLayoutRes();

    public abstract void setRecyclerView();

    public abstract List<CouponInfo> getDataList();

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
