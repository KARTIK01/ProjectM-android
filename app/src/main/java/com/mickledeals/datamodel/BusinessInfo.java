package com.mickledeals.datamodel;

import com.mickledeals.R;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
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

    public List<CouponInfo> mCoupons = new ArrayList<CouponInfo>();

    public List<BusinessPhoto> mPhotoIds;

    public BusinessInfo(JSONObject jsonobject) {
        try {
            mId = jsonobject.getInt("id");
            if (!jsonobject.isNull("coverPhotoUrl")) mCoverPhotoUrl = jsonobject.getString("coverPhotoUrl");
            if (!jsonobject.isNull("iconUrl")) mLogoUrl = jsonobject.getString("iconUrl");
            if (!jsonobject.isNull("name")) mName = jsonobject.getString("name");
            if (!jsonobject.isNull("chineseName")) mNameCh = jsonobject.getString("chineseName");
            if (!jsonobject.isNull("description")) mDescription = jsonobject.getString("description");
            if (!jsonobject.isNull("chineseDescription")) mDescriptionCh = jsonobject.getString("chineseDescription");
            if (!jsonobject.isNull("news")) mNews = jsonobject.getString("news");
            if (!jsonobject.isNull("chineseNews")) mNewsCh = jsonobject.getString("chineseNews");
            mLat = jsonobject.getDouble("latitude");
            mLng = jsonobject.getDouble("longitude");
            if (!jsonobject.isNull("address")) mAddress = jsonobject.getString("address");
            if (!jsonobject.isNull("district")) mDistrict = jsonobject.getString("district");
            if (!jsonobject.isNull("zipCode")) mZipCode = jsonobject.getString("zipCode");
            if (!jsonobject.isNull("phone")) mPhone = jsonobject.getString("phone");
            if (!jsonobject.isNull("link")) mWebSiteAddr = jsonobject.getString("link");
            if (!jsonobject.isNull("hours")) mHours = jsonobject.getString("hours");
            JSONObject locationObject = jsonobject.getJSONObject("location");
            if (!locationObject.isNull("city"))  mCity = locationObject.getString("city");
            if (!locationObject.isNull("state")) mState = locationObject.getString("state");

            if (jsonobject.has("coupon")) {
                JSONArray couponList = jsonobject.getJSONArray("coupon");
                for (int i = 0; i < couponList.length(); i++) {
                    try {
                        JSONObject couponObject = couponList.getJSONObject(i);
                        CouponInfo info = new CouponInfo(couponObject);
                        info.mBusinessInfo = this;
                        mCoupons.add(info);
                    } catch (JSONException e) {
//                        DLog.e(MDApiManager.class, e.toString());
                    }
                }
            }
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
