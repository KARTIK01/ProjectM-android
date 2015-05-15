package com.mickledeals.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SavedCouponsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener{

    private Spinner mCategorySpinner;
    private Spinner mLocationSpinner;
    private RecyclerView mRecyclerView;

    private List<TestDataHolder> mSaveList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSaveList = DataListModel.getInstance().getSavedList();
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
            if (holder.mSaved) mSaveList.add(holder);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_saved_deals, container, false);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DLog.d(this, "onDestroy");
    }

    @Override
    public void onStart() {
        super.onStart();
//        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        mLocationSpinner = (Spinner) view.findViewById(R.id.locationSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.category_name, R.layout.spinner_textview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setOnItemSelectedListener(this);

        adapter = ArrayAdapter.createFromResource(mContext,
                R.array.city_name, R.layout.spinner_textview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(adapter);
        mLocationSpinner.setOnItemSelectedListener(this);

        final int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);
        final int bottomMargin = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new CardAdapter(getActivity(), mSaveList, Constants.TYPE_SAVED_LIST, R.layout.card_layout_save));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mCategorySpinner) {
            mSaveList.clear();
            for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
                if (position == 0 || holder.mCategoryId == position) {
                    if (holder.mSaved) mSaveList.add(holder);
                }
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
