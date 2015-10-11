package com.mickledeals.datamodel;

import com.mickledeals.R;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nicky on 9/12/2015.
 */
public class BusinessInfo implements Serializable {

    public int mId;
    public String mLogoUrl = "";
    public String mCoverPhotoUrl = "";

    public String mName = "";
    public String mNameCh = "";
    public String mDescription = "";
    public String mDescriptionCh = "";
    public String mNews = "";
    public String mNewsCh = "";

    public double mLat;
    public double mLng;
    private String mAddress = "";
    public String mCity = "";
    public String mState = "";
    public String mZipCode = "";
    public String mDistrict = "";
    public String mPhone = "";
    public String mWebSiteAddr = "";
    public String mHours = "";

    public List<CouponSimpleInfo> mCoupons;

    public List<BusinessPhoto> mPhotoIds;

    public BusinessInfo(JSONObject jsonobject) {
        try {
            mId = jsonobject.getInt("id");
            mCoverPhotoUrl = jsonobject.getString("coverPhotoUrl");
            mLogoUrl = jsonobject.getString("iconUrl");
            mName = jsonobject.getString("name");
            mNameCh = jsonobject.getString("chineseName");
            mDescription = jsonobject.getString("description");
            mDescriptionCh = jsonobject.getString("chineseDescription");
            mNews = jsonobject.getString("news");
            mNewsCh = jsonobject.getString("chineseNews");
            mLat = jsonobject.getDouble("latitude");
            mLng = jsonobject.getDouble("longitude");
            mAddress = jsonobject.getString("address");
            mDistrict = jsonobject.getString("district");
            mZipCode = jsonobject.getString("zipCode");
            mPhone = jsonobject.getString("phone");
            mWebSiteAddr = jsonobject.getString("link");
            mHours = jsonobject.getString("hours");
            JSONObject locationObject = jsonobject.getJSONObject("location");
            mCity = locationObject.getString("city");
            mState = locationObject.getString("state");
        } catch (JSONException e) {
            DLog.e(this, e.toString());
        }
    }

    public String getStoreName() {
        return (Utils.isChineseLocale() && !(mNameCh == null || mNameCh.trim().isEmpty())) ? mNameCh : mName;
    }

    public String getNews() {
        return (Utils.isChineseLocale() && !(mNewsCh == null || mNewsCh.trim().isEmpty())) ? mNewsCh : mNews;
    }

    public String getDescription() {
        return (Utils.isChineseLocale() && !(mDescriptionCh == null || mDescriptionCh.trim().isEmpty())) ? mDescriptionCh : mDescription;
    }

    public String getDisplayedCity() {
        String centerDot = MDApplication.sAppContext.getResources().getString(R.string.center_dot_symbol);
        return ((mDistrict == null || mDistrict.equals("")) ? "" : (mDistrict + centerDot)) + mCity;
    }

    public String getShortDisplayedCity() {
        return getDisplayedCity().replace("San Francisco", "SF");
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (!mAddress.isEmpty()) sb.append(mAddress + "\n");
        if (!mDistrict.isEmpty()) sb.append(mDistrict + ", ");
        if (!mCity.isEmpty()) sb.append(mCity + ", ");
        if (!mState.isEmpty()) sb.append(mState + " ");
        if (!mZipCode.isEmpty()) sb.append(mZipCode + " ");
        return sb.toString();
    }

    public String getShortAddress() {
        StringBuilder sb = new StringBuilder();
        if (!mAddress.isEmpty()) sb.append(mAddress + ", ");
        if (!mCity.isEmpty()) sb.append(mCity);
        return sb.toString();
    }
}
