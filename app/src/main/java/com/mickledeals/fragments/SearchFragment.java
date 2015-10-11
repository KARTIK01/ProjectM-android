package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SearchFragment extends ListMapBaseFragment {

    private String mSearchStr;

    private TextView mNoResultMsg;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoResultMsg = (TextView) view.findViewById(R.id.noResultMsg);
    }

    public void doSearch(String str) {
        if (mLocationSpinner.getSelectedItemPosition() == 0) mNoResultMsg.setText(getString(R.string.no_results_found_in_search, str));
        else mNoResultMsg.setText(getString(R.string.no_results_found_in_search_in_location, str, mLocationSpinner.getSelectedItem().toString()));
        str = str.toLowerCase().trim();
        mSearchStr = str;
        prepareSendRequest();
    }

    @Override
    public void prepareSendRequest() {
        if (mSearchStr == null) return;

        super.prepareSendRequest();
    }

    protected String getSearchText() {
        return mSearchStr;
    }

    public List<CouponInfo> getDataList() {
        List<CouponInfo> list = DataListModel.getInstance().getSearchResultList();
        return list;
    }

    public int getFragmentLayoutRes() {
        return R.layout.fragment_search;
    }

    public String getNoResultMessage() {
        return mNoResultMsg.getText().toString();
    }

    public void setRecyclerView() {
        if (mContext.getResources().getInteger(R.integer.dp_width_level) > 0) {
            mListResultRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        } else {
            mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }
        mAdapter = new CardAdapter(this, mDataList, Constants.TYPE_SEARCH_RESULT_LIST, R.layout.card_layout_search);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
