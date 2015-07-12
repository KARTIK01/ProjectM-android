package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SavedCouponsFragment extends ListResultBaseFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //send request here, no need to wait for view created, use default spinner settings
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendUpdateRequest();
    }

    public List<TestDataHolder> getDataList() {
        return DataListModel.getInstance().getSavedList();
    }

    public List<TestDataHolder> getTemporaryDataList() {
        //temporary
        List<TestDataHolder> list = new ArrayList<TestDataHolder>();
        for (int i = 1; i <= DataListModel.getInstance().getDataList().size(); i++) {
            TestDataHolder holder = DataListModel.getInstance().getDataList().get(i);
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
        if (mContext.getResources().getInteger(R.integer.dp_width_level) > 0) {
            mListResultRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        } else {
            mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }
        mListResultRecyclerView.setAdapter(new CardAdapter(this, mDataList, Constants.TYPE_SAVED_LIST, R.layout.card_layout_save));
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
