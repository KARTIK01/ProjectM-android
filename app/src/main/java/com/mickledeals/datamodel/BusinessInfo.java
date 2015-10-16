package com.mickledeals.datamodel;

import com.mickledeals.R;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.utils.JSONHelper;
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
        mId = JSONHelper.getInteger(jsonobject, "id");
        mCoverPhotoUrl = JSONHelper.getString(jsonobject, "coverPhotoUrl");
        mLogoUrl = JSONHelper.getString(jsonobject, "iconUrl");
        mName = JSONHelper.getString(jsonobject, "name");
        mNameCh = JSONHelper.getString(jsonobject, "chineseName");
        mDescription = JSONHelper.getString(jsonobject, "description");
        mDescriptionCh = JSONHelper.getString(jsonobject, "chineseDescription");
        mNews = JSONHelper.getString(jsonobject, "news");
        mNewsCh = JSONHelper.getString(jsonobject, "chineseNews");
        mLat = JSONHelper.getDouble(jsonobject, "latitude");
        mLng = JSONHelper.getDouble(jsonobject, "longitude");

        mAddress = JSONHelper.getString(jsonobject, "address");
        mDistrict = JSONHelper.getString(jsonobject, "district");
        mZipCode = JSONHelper.getString(jsonobject, "zipCode");
        mPhone = JSONHelper.getString(jsonobject, "phone");
        mWebSiteAddr = JSONHelper.getString(jsonobject, "link");
        mHours = JSONHelper.getString(jsonobject, "hours");


        JSONObject locationObject = JSONHelper.getJSONObject(jsonobject, "location");
        if (locationObject != null) {
            mCity = JSONHelper.getString(locationObject, "city");
            mState = JSONHelper.getString(locationObject, "state");
        }

        JSONArray couponList = JSONHelper.getJSONArray(jsonobject, "coupon");
        if (couponList != null) {
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
