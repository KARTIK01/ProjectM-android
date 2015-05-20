package com.mickledeals.fragments;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SavedCouponsFragment extends ListResultBaseFragment{


    public List<TestDataHolder> getDataList() {
        List<TestDataHolder> list = DataListModel.getInstance().getNearbyList();
        //tepomorary
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
            if (holder.mSaved) list.add(holder);
        }
        return list;
    }

    public int getFragmentLayoutRes() {
        return R.layout.fragment_saved_deals;
    }

    public String getNoResultToastMessage() {
        return getString(R.string.no_results_found) + " " + getString(R.string.try_different_filter);
    }

    public void setRecyclerView() {
        mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mListResultRecyclerView.setAdapter(new CardAdapter(getActivity(), mDataList, Constants.TYPE_SAVED_LIST, R.layout.card_layout_save));
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
