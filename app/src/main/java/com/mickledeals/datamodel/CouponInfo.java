package com.mickledeals.datamodel;

import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CouponInfo implements Serializable{


    public int mImageResId; //temp
    public int mSmallImageResId; //temp

    public int mId;
    public String mCoverPhotoUrl;
    public String mThumbnailPhotoUrl;
    public String mDescription;
    public String mDescriptionCh;
    public float mPrice;
    public String mFinePrint;
    public String mFinePrintCh;
    public long mExpiredDate;
    public int mExpiredDays;

    public boolean mSaved;
    public boolean mLimited;
    public boolean mActive;

    public boolean mPurchased;
    public String mPurchaseId;

    public BusinessInfo mBusinessInfo;

    public int mStatus = Constants.COUPON_STATUS_DEFAULT;


    public CouponInfo(JSONObject jsonobject) {
        try {
            mId = jsonobject.getInt("id");
            mCoverPhotoUrl = jsonobject.getString("coverPhotoUrl");
            mThumbnailPhotoUrl = jsonobject.getString("thumbnailPhotoUrl");
            mDescription = jsonobject.getString("title");
            mDescriptionCh = jsonobject.getString("chineseTitle");
            mFinePrint = jsonobject.getString("finePrint");
            mFinePrintCh = jsonobject.getString("chineseFinePrint");
            mPrice = (float) jsonobject.getDouble("price");
            mLimited = jsonobject.getBoolean("limited");
            mActive = jsonobject.getBoolean("active");
            mBusinessInfo = new BusinessInfo(jsonobject.getJSONObject("company"));
            mExpiredDate = jsonobject.getLong("expireDate");
            mExpiredDays = jsonobject.getInt("expireDays");
        } catch (JSONException e) {
            DLog.e(this, e.toString());
        }
    }

    public String getDescription() {
        return (Utils.isChineseLocale() && mDescriptionCh != null) ? mDescriptionCh : mDescription;
    }

    public String getFinePrint() {
        return (Utils.isChineseLocale() && mFinePrintCh != null) ? mFinePrintCh : mFinePrint;
    }

    public String getDisplayedPrice() {
//        MDApplication.sAppContext.getResources().getString(R.string.free)
         return mPrice == 0 ? "Free" : "$" + mPrice;
    }


}
