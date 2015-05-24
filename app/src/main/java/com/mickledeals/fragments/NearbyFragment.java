package com.mickledeals.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;

import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class NearbyFragment extends ListResultBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager.connect();
    }

    public List<TestDataHolder> getDataList() {
        List<TestDataHolder> list = DataListModel.getInstance().getNearbyList();

        //temporary
        for (int i = 1; i <= DataListModel.getInstance().getDataList().size(); i++) {
            TestDataHolder holder = DataListModel.getInstance().getDataList().get(i);
            list.add(holder);
        }
        return list;
    }

    public int getFragmentLayoutRes() {
        return R.layout.fragment_nearby;
    }

    public String getNoResultToastMessage() {
        return getString(R.string.no_results_found) + " " + getString(R.string.try_different_filter);
    }

    public void setRecyclerView() {
        final int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);
        final int bottomMargin = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        mListResultRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mListResultRecyclerView.setAdapter(new CardAdapter(getActivity(), mDataList, Constants.TYPE_NEARBY_LIST, R.layout.card_layout));
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mListResultRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
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
}
