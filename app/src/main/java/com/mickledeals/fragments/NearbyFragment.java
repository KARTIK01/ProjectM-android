package com.mickledeals.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class NearbyFragment extends ListMapBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager.connect();
    }

    public List<TestDataHolder> getDataList() {
        return DataListModel.getInstance().getNearbyList();
    }

    public List<TestDataHolder> getTemporaryDataList() {
        //temporary
        List<TestDataHolder> list = new ArrayList<TestDataHolder>();
        for (int i = 1; i < DataListModel.getInstance().getDataList().size(); i++) {
            TestDataHolder holder = DataListModel.getInstance().getDataList().get(i);
            list.add(holder);
        }
        return list;
    }

    public int getFragmentLayoutRes() {
        return R.layout.fragment_nearby;
    }

    public String getNoResultMessage() {
        return getString(R.string.no_results_found) + " " + getString(R.string.try_different_filter);
    }

    public void setRecyclerView() {
        final int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);
        final int bottomMargin = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        final int column = mContext.getResources().getInteger(R.integer.dp_width_level) > 0 ? 3 : 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, column);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mAdapter.getItemViewType(position)){
                    case CardAdapter.VIEW_PROGRESS:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        mListResultRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new CardAdapter(this, mDataList, Constants.TYPE_NEARBY_LIST, R.layout.card_layout);
        mListResultRecyclerView.setAdapter(mAdapter);
        mListResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mListResultRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildPosition(view);
                boolean leftside = pos % column == 0;
                boolean rightside = pos % column == (column - 1);
                outRect.left = leftside ? margin : margin / 2;
                outRect.right = rightside ? margin : margin / 2;
                outRect.bottom = bottomMargin;
            }
        });
        mListResultRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                    DLog.d(NearbyFragment.this, "onloadmore page = " + currentPage);
                mDataList.add(null);
                mAdapter.notifyItemInserted(mDataList.size());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress bar
                        mDataList.remove(mDataList.size() - 1);
                        mAdapter.notifyItemRemoved(mDataList.size());
                        for (int i = 1; i < DataListModel.getInstance().getDataList().size(); i++) {
                            TestDataHolder holder = DataListModel.getInstance().getDataList().get(i);
                            mDataList.add(holder);
                            mListResultRecyclerView.getAdapter().notifyItemInserted(mDataList.size());
                        }
                    }
                }, 3000);

            }
        });
    }
}
