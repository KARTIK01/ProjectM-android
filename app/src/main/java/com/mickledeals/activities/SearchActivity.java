package com.mickledeals.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.fragments.SearchFragment;
import com.mickledeals.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 5/20/2015.
 */
public class SearchActivity extends SwipeDismissActivity {

    //temporary
    private String[] mRecentList = {"sushi", "spa", "Savory Steak House", "seafood", "steak"};

    private List<String> mSuggestionList = new ArrayList<String>();
    private EditText mSearchEdit;
    private View mClear;
    private View mMapToggleView;
    private View mListMapContainer;
    private RecyclerView mSuggestionRecyclerView;
    private View mRecentHotLayout;
    private LinearLayout mRecentLayout;
    private LinearLayout mHotLayout;
    private SearchFragment mSearchFragment;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.searchFragment);


        mRecentHotLayout = findViewById(R.id.recentHotLayout);
        mRecentHotLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //will not receive action down if tap on clickable item
                Utils.toggleKeyboard(false, mSearchEdit, SearchActivity.this);
                return false;
            }
        });

        mRecentLayout = (LinearLayout) findViewById(R.id.recentLayout);
        mHotLayout = (LinearLayout) findViewById(R.id.hotLayout);

        wrapStringsIntoLinearLayout(getResources().getStringArray(R.array.hot_list), mHotLayout, R.layout.hot_searches_card_textview);
        wrapStringsIntoLinearLayout(mRecentList, mRecentLayout, R.layout.recent_searches_card_textview);

        mListMapContainer = findViewById(R.id.listMapContainer);

        mSuggestionRecyclerView = (RecyclerView) findViewById(R.id.searchSuggestionRecyclerView);
        mSuggestionRecyclerView.setAdapter(new SuggestionAdapter());
        mSuggestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSuggestionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSuggestionRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //will not receive action down if tap on clickable item
                Utils.toggleKeyboard(false, mSearchEdit, SearchActivity.this);
                return false;
            }
        });

        mMapToggleView = findViewById(R.id.mapToggleView);
        mClear = findViewById(R.id.searchClear);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toggleKeyboard(true, mSearchEdit, SearchActivity.this);
                mSearchEdit.setText("");
                mClear.setVisibility(View.GONE);

                mMapToggleView.setVisibility(View.INVISIBLE);
                mListMapContainer.setVisibility(View.GONE);
                mSuggestionRecyclerView.setVisibility(View.GONE);
                mRecentHotLayout.setVisibility(View.VISIBLE);
                mSearchEdit.setCursorVisible(true);
            }
        });

        mSearchEdit = (EditText) findViewById(R.id.searchEdit);
        mSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapToggleView.setVisibility(View.INVISIBLE);
                mListMapContainer.setVisibility(View.GONE);
                mSearchEdit.setCursorVisible(true);
            }
        });
        mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sendSearchRequest(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSearchEdit.setTextSize(18f);
                    mClear.setVisibility(View.VISIBLE);

                    mSuggestionRecyclerView.setVisibility(View.VISIBLE);
                    mRecentHotLayout.setVisibility(View.GONE);
                } else {
                    mSearchEdit.setTextSize(15f);
                    mClear.setVisibility(View.GONE);

                    mSuggestionRecyclerView.setVisibility(View.GONE);
                    mRecentHotLayout.setVisibility(View.VISIBLE);
                }
                suggestionSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, final boolean hasFocus) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toggleKeyboard(hasFocus, mSearchEdit, SearchActivity.this);
                    }
                });
            }
        });
    }

    private void suggestionSearch(String query) {

        query = query.toLowerCase().trim();
        mSuggestionList.clear();

        String[] stringArray = getResources().getStringArray(R.array.suggestion_list);
        for (String str : stringArray) {
            if (str.toLowerCase().trim().startsWith(query)) {
                mSuggestionList.add(str);
            }
        }
        mSuggestionRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void sendSearchRequest(String str) {
        Utils.toggleKeyboard(false, mSearchEdit, SearchActivity.this);
        mSearchEdit.setText(str);
        mSearchEdit.setCursorVisible(false);
        mListMapContainer.setVisibility(View.VISIBLE);
        mMapToggleView.setVisibility(View.VISIBLE);
        mSearchFragment.doSearch(str);

    }


    public void wrapStringsIntoLinearLayout(String[] strings, LinearLayout rootLayout, int layoutRes) {

        Context context = rootLayout.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int maxWidth = Utils.getDeviceWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.suggestion_card_left_padding);;
        int widthSoFar = 0;
        boolean isFirstTime = true;
        LinearLayout subLL = null;

        for (final String string : strings) {

            CardView cardView = (CardView) inflater.inflate(layoutRes, null);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (string.equals("Savory Steak House")) {
                        Utils.toggleKeyboard(false, mSearchEdit, SearchActivity.this);
                        Intent i = new Intent(SearchActivity.this, BusinessPageActivity.class);
                        i.putExtra("storeId", 2);
                        startActivity(i);
                    } else {
                        sendSearchRequest(string);
                    }
                }
            });
            TextView tv = (TextView) cardView.findViewById(R.id.tv);
            tv.setText(string);
            int rightMargin = context.getResources().getDimensionPixelSize(R.dimen.card_margin);
            cardView.measure(0, 0);
            int measuredWidth = cardView.getMeasuredWidth();
            if (measuredWidth >= maxWidth - rightMargin) continue;
            widthSoFar += cardView.getMeasuredWidth() + rightMargin;
            if (widthSoFar >= maxWidth || isFirstTime) {
                isFirstTime = false;
                subLL = new LinearLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.suggestion_card_margin_bottom);
                subLL.setLayoutParams(params);
                subLL.setOrientation(LinearLayout.HORIZONTAL);
                rootLayout.addView(subLL);
                widthSoFar = measuredWidth + rightMargin;
            }
            subLL.addView(cardView);

        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView mText;

        public CategoryViewHolder(TextView tv) {
            super(tv);
            mText = tv;
        }
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        public TextView mBusinessName;
        public TextView mBusinessAddr;
        public ImageView mBusniessLogo;

        public BusinessViewHolder(View v) {
            super(v);
            mBusinessName = (TextView) v.findViewById(R.id.businessName);
            mBusinessAddr = (TextView) v.findViewById(R.id.businessAddr);
            mBusniessLogo = (ImageView) v.findViewById(R.id.businessLogo);
        }
    }

    private class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_CATEGORY_SUGGESTION = 0;
        private static final int TYPE_BUSINESS_SUGGESTION = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == TYPE_CATEGORY_SUGGESTION) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_textview, parent, false);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendSearchRequest(((TextView) v).getText().toString());
                    }
                });
                CategoryViewHolder cvh = new CategoryViewHolder(tv);
                return cvh;
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_place_layout, parent, false);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.toggleKeyboard(false, mSearchEdit, SearchActivity.this);
                        Intent i = new Intent(SearchActivity.this, BusinessPageActivity.class);
                        i.putExtra("storeId", 2);
                        startActivity(i);
                    }
                });
                BusinessViewHolder bvh = new BusinessViewHolder(v);
                return bvh;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String string = mSuggestionList.get(position);

            String keyString = mSearchEdit.getText().toString().toLowerCase().trim();
            int index = string.toLowerCase().trim().indexOf(keyString);
            SpannableString spannable = new SpannableString(string);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), index, index + keyString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (holder instanceof CategoryViewHolder) {
                CategoryViewHolder cvh = (CategoryViewHolder) holder;
                cvh.mText.setText(spannable);
            } else {
                BusinessViewHolder bvh = (BusinessViewHolder) holder;
                bvh.mBusinessName.setText(spannable);
                bvh.mBusinessAddr.setText("383 Woodrew St, Daly City");
            }
        }

        @Override
        public int getItemCount() {
            return mSuggestionList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mSuggestionList.get(position).equals("Savory Steak House")) {
                return TYPE_BUSINESS_SUGGESTION;
            } else {
                return TYPE_CATEGORY_SUGGESTION;
            }
        }
    }
}
