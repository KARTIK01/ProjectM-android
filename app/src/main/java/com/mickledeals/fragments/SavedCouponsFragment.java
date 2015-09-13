package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SavedCouponsFragment extends ListMapBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSortSpinner.setSelection(1); //set to date added
    }

    protected boolean isSortByLocation() {
        if (mSortSpinner == null) return false;
        return super.isSortByLocation();
    }

    public List<CouponInfo> getDataList() {
        return DataListModel.getInstance().getSavedList();
    }

    public List<CouponInfo> getTemporaryDataList() {
        //temporary
        List<CouponInfo> list = new ArrayList<CouponInfo>();
        for (int i = 1; i <= DataListModel.getInstance().getDataList().size(); i++) {
            CouponInfo holder = DataListModel.getInstance().getDataList().get(i);
            if (holder.mSaved) list.add(holder);
        }
        return list;
    }

    public int getFragmentLayoutRes() {
        return R.layout.fragment_saved_deals;
    }

    public String getNoResultMessage() {
        return getString(R.string.no_results_found) + " " + getString(R.string.try_different_filter);
    }

    public void setRecyclerView() {
        if (mContext.getResources().getInteger(R.integer.dp_width_level) > 0) {
            mListResultRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        } else {
            mListResultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }
        mAdapter = new CardAdapter(this, mDataList, Constants.TYPE_SAVED_LIST, R.layout.card_layout_save);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
