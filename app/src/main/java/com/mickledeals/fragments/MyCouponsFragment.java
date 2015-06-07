package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.R;
import com.mickledeals.adapters.MyCouponsAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */


public class MyCouponsFragment extends BaseFragment {

    private RecyclerView mRecyclerView;

    private List<TestDataHolder> mBoughtList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoughtList = DataListModel.getInstance().getBoughtList();
        mBoughtList.clear();
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
            if (holder.mId == 4 || holder.mId == 1 || holder.mId == 5 || holder.mId == 10
            || holder.mId == 15 || holder.mId == 9 || holder.mId == 16) mBoughtList.add(holder);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_my_coupons, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new MyCouponsAdapter(getActivity(), mBoughtList, Constants.TYPE_BOUGHT_LIST, R.layout.card_layout_my_coupons));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
