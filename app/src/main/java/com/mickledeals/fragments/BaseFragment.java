package com.mickledeals.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.mickledeals.R;
import com.mickledeals.activities.BaseActivity;
import com.mickledeals.utils.EventBus;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.Utils;

/**
 * Created by Nicky on 11/28/2014.
 */
public abstract class BaseFragment extends Fragment implements EventBus.EventListener{

    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        EventBus.getInstance().registerListener(this);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarLoading);
        if (mProgressBar != null) {
            mProgressBar.getIndeterminateDrawable().setColorFilter(
                    getResources().getColor(R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            mProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //block touch event to disable button click from behind
                }
            });
        }
    }

    public Toolbar getToolBar() {
        return ((BaseActivity) getActivity()).getToolBar();
    }

    public boolean handleBackPressed() {
        return false;
    }

    public void onFragmentPause() {

    }

    public void onFragmentResume() {

    }

    @Override
    public void onEventUpdate(int event, Bundle data) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregisterListener(this);
    }

    protected class MDReponseListenerImpl<T> implements MDApiManager.MDResponseListener<T> {
        @Override
        public void onMDSuccessResponse(T object) {
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onMDNetworkErrorResponse(String errorMessage) {
            if (errorMessage.equals("timeout")) {
                Utils.showNetworkTimeOutDialog(mContext);
            } else {
                Utils.showNetworkErrorDialog(mContext);
            }
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onMDErrorResponse(String errorMessage) {
            int errorMessageRes = R.string.response_error_message_unknown;
            Utils.showAlertDialog(mContext, R.string.response_error_title, errorMessageRes);
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }

        public void onMDErrorResponse(int errorMessageRes) {
            Utils.showAlertDialog(mContext, R.string.response_error_title, errorMessageRes);
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }
    }


}
