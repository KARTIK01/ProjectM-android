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
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;

/**
 * Created by Nicky on 7/23/2015.
 */
public abstract class SwipeRefreshBaseFragment extends BaseFragment {

    protected View mNoResultLayout;
    protected View mNoNetworkLayout;
    protected ViewStub mNoNetworkStub;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mListResultRecyclerView;
    protected CardAdapter mAdapter;


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

        mNoResultLayout = view.findViewById(R.id.noResultLayout);
        mNoNetworkStub = (ViewStub) view.findViewById(R.id.noNetworkStub);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.requestDisallowInterceptTouchEvent(false);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary1, R.color.colorPrimary4);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sendRequest();
                }
            });
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        });
    }

    public void sendRequest() {
        DLog.d(this, "sendRequest");
        String request = getRequestURL();
        if (request == null) return;

        if (mNoNetworkLayout != null) mNoNetworkLayout.setVisibility(View.GONE);
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
        MDApiManager.sendStringRequest(request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                onSuccessResponse();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DLog.e(this, error.getMessage());
                if (mNoNetworkLayout == null) mNoNetworkLayout = mNoNetworkStub.inflate();
                mNoNetworkLayout.setVisibility(View.VISIBLE);

                Button retryButton = (Button) mNoNetworkLayout.findViewById(R.id.retryButton);
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendRequest();
                    }
                });
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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

    public void onSuccessResponse() {

        mListResultRecyclerView.getAdapter().notifyDataSetChanged();
        ((CardAdapter) mListResultRecyclerView.getAdapter()).setPendingAnimated();
    }


    public abstract String getRequestURL();

    public abstract int getFragmentLayoutRes();

    public abstract void setRecyclerView();

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
