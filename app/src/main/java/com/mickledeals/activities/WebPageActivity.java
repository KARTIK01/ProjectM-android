package com.mickledeals.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mickledeals.R;

/**
 * Created by Nicky on 9/11/2015.
 */
public class WebPageActivity extends SwipeDismissActivity {

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        String content = getIntent().getStringExtra("webContent");

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;

            }
        });
        String url = "";
        int titleRes = 0;

        switch (content) {
            case "terms":
                url = "http://www.mickledeals.com/terms";
                titleRes = R.string.terms;
                break;
            case "privacy":
                url = "http://www.mickledeals.com/privacy";
                titleRes = R.string.privacy;
                break;
            default:
        }

        mWebView.loadUrl(url);
        setTitle(titleRes);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_webpage;
    }
}
