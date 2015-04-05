package com.mickledeals.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
public class NearbyFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private Spinner mCategorySpinner;
    private Spinner mLocationSpinner;
    private RecyclerView mNearbyRecyclerView;

    private List<TestDataHolder> mNearbyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNearbyList = DataListModel.getInstance().getNearbyList();
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
                mNearbyList.add(holder);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_nearby, container, false);

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
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

        mNearbyRecyclerView = (RecyclerView) view.findViewById(R.id.nearbyRecyclerView);
        mNearbyRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mNearbyRecyclerView.setAdapter(new CardAdapter(getActivity(), mNearbyList, Constants.TYPE_NEARBY_LIST, R.layout.card_layout));
        mNearbyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNearbyRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildPosition(view);
                boolean leftside = pos % 2 == 0;
                outRect.left = leftside ? margin : margin / 2;
                outRect.right = leftside ? margin / 2 : margin;
                outRect.bottom = bottomMargin;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mCategorySpinner) {
            mNearbyList.clear();
            for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
                if (position == 0 || holder.mCategoryId == position) {
                    mNearbyList.add(holder);
                }
            }
            mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
