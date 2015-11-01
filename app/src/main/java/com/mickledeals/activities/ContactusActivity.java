package com.mickledeals.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDLoginManager;

import org.json.JSONObject;

/**
 * Created by Nicky on 11/28/2014.
 */
public class ContactusActivity extends SwipeDismissActivity {

    private TextView mEmail;
    private TextView mSubject;
    private TextView mMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mEmail = (TextView) findViewById(R.id.emailText);
        mSubject = (TextView) findViewById(R.id.subjectText);
        mMessage = (TextView) findViewById(R.id.messageText);

        if (MDLoginManager.mEmailAddr != null) {
            mEmail.setText(MDLoginManager.mEmailAddr);
        }
        mSubject.requestFocus();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_contact_us;
    }


    public void submitClick(View v) {
        if (mMessage.getText().toString().length() < 8) {
            Toast.makeText(this, R.string.contact_us_message_too_short, Toast.LENGTH_SHORT).show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            MDApiManager.contactUs(MDLoginManager.mUserName, mEmail.getText().toString(), mSubject.getText().toString(), mMessage.getText().toString(),
                    Build.VERSION.RELEASE + " " + Build.MANUFACTURER + " " + Build.MODEL, getString(R.string.versionNo), new MDReponseListenerImpl<JSONObject>() {
                        @Override
                        public void onMDSuccessResponse(JSONObject object) {
                            super.onMDSuccessResponse(object);
                            Toast.makeText(ContactusActivity.this, R.string.contact_us_succeed, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
    }
}
