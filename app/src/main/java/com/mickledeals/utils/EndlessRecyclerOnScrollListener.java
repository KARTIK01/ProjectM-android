package com.mickledeals.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Nicky on 7/26/2015.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;
//    private int currentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);


        visibleItemCount = recyclerView.getChildCount() ;
        totalItemCount = mLinearLayoutManager.getItemCount() ;
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();


        if (loading) {
            if (totalItemCount > previousTotal + 1 ) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
//
//        Log.e("XXX", "loading = " + loading + "totalItemCount = " + totalItemCount + "visibleItemCount = " + visibleItemCount +
//                "firstVisibleItem = " + firstVisibleItem + "visibleThreshold = " + visibleThreshold);
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

            if (totalItemCount % MDApiManager.PAGE_SIZE != 0 && !ignoreRemainder()) {
                //just optimization: if there is remainder, that means it is the end already
                return;
            }

            // End has been reached
            // Do something
//            currentPage++;

            onLoadMore();
            loading = true;
        }
    }

    //we need to reset previous total when refresh the whole list, otherwise loading flag never get set to false
    public void resetPreviousTotal() {
        previousTotal = 0;
    }

    public abstract void onLoadMore();

    public abstract boolean ignoreRemainder();
}
