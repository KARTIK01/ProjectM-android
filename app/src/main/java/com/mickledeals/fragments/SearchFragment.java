package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SearchFragment extends ListResultBaseFragment{

    private TextView mNoResultMsg;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoResultMsg = (TextView) view.findViewById(R.id.noResultMsg);
    }

    //temporary
    public void doSearch(String str) {
        if (mLocationSpinner.getSelectedItemPosition() == 0) mNoResultMsg.setText(getString(R.string.no_results_found_in_search, str));
        else mNoResultMsg.setText(getString(R.string.no_results_found_in_search_in_location, str, mLocationSpinner.getSelectedItem().toString()));
        str = str.toLowerCase().trim();
        mDataList.clear();
        if (str.equals("sushi")) {
            mDataList.add(DataListModel.getInstance().getDataList().get(14));
            mDataList.add(DataListModel.getInstance().getDataList().get(16));
            mDataList.add(DataListModel.getInstance().getDataList().get(17));
        } else if (str.equals("seafood")) {
            mDataList.add(DataListModel.getInstance().getDataList().get(2));
            mDataList.add(DataListModel.getInstance().getDataList().get(3));
            mDataList.add(DataListModel.getInstance().getDataList().get(12));
            mDataList.add(DataListModel.getInstance().getDataList().get(13));
            mDataList.add(DataListModel.getInstance().getDataList().get(14));
            mDataList.add(DataListModel.getInstance().getDataList().get(16));
        } else if (str.equals("spa")) {
            mDataList.add(DataListModel.getInstance().getDataList().get(7));
            mDataList.add(DataListModel.getInstance().getDataList().get(18));
        } else if (str.equals("steak")) {
            mDataList.add(DataListModel.getInstance().getDataList().get(2));
            mDataList.add(DataListModel.getInstance().getDataList().get(11));
        }
        sendUpdateRequest();
    }

    public List<TestDataHolder> getDataList() {
        List<TestDataHolder> list = DataListModel.getInstance().getSearchResultList();
        return list;
    }

    public int getFragmentLayoutRes() {
        return R.layout.fragment_search;
    }

    public String getNoResultToastMessage() {
        return mNoResultMsg.getText().toString();
    }

    public void setRecyclerView() {
        mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mListResultRecyclerView.setAdapter(new CardAdapter(getActivity(), mDataList, Constants.TYPE_SEARCH_RESULT_LIST, R.layout.card_layout_search));
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
