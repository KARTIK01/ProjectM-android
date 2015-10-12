package com.mickledeals.datamodel;

import com.mickledeals.R;
import com.mickledeals.activities.MDApplication;
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
    public String mCoverPhotoUrl = "";
    public String mThumbnailPhotoUrl = "";
    public String mDescription = "";
    public String mDescriptionCh = "";
    public float mPrice;
    public String mFinePrint = "";
    public String mFinePrintCh = "";
    public String mExpiredDate = "";
    public String mExpiredDays = "";

    public boolean mSaved;
    public boolean mLimited;
    public boolean mActive;

    public boolean mPurchased;
    public String mPurchaseId = "";

    public BusinessInfo mBusinessInfo;

    public int mStatus = Constants.COUPON_STATUS_DEFAULT;


    public CouponInfo(JSONObject jsonobject) {
        try {
            mId = jsonobject.getInt("id");
            if (!jsonobject.isNull("coverPhotoUrl")) mCoverPhotoUrl = jsonobject.getString("coverPhotoUrl");
            if (!jsonobject.isNull("thumbnailPhotoUrl")) mThumbnailPhotoUrl = jsonobject.getString("thumbnailPhotoUrl");
            if (!jsonobject.isNull("title")) mDescription = jsonobject.getString("title");
            if (!jsonobject.isNull("chineseTitle")) mDescriptionCh = jsonobject.getString("chineseTitle");
            if (!jsonobject.isNull("finePrint")) mFinePrint = jsonobject.getString("finePrint");
            if (!jsonobject.isNull("chineseFinePrint")) mFinePrintCh = jsonobject.getString("chineseFinePrint");
            mPrice = (float) jsonobject.getDouble("price");
            mLimited = jsonobject.getBoolean("limited");
            mActive = jsonobject.getBoolean("active");
            mBusinessInfo = new BusinessInfo(jsonobject.getJSONObject("company"));
            if (!jsonobject.isNull("expireDate")) mExpiredDate = jsonobject.getString("expireDate");
            if (!jsonobject.isNull("expireDays")) mExpiredDays = jsonobject.getString("expireDays");
        } catch (JSONException e) {
            DLog.e(this, e.toString());
        }
    }

    public String getDescription() {
        return (Utils.isChineseLocale() && !(mDescriptionCh == null || mDescriptionCh.trim().isEmpty())) ? mDescriptionCh : mDescription;
    }

    public String getFinePrint() {
        return (Utils.isChineseLocale() && !(mFinePrintCh == null || mFinePrintCh.trim().isEmpty())) ? mFinePrintCh : mFinePrint;
    }

    public String getDisplayedPrice() {
//        MDApplication.sAppContext.getResources().getString(R.string.free)

        String str = null;

        if (mPrice == 0) {
            str = "Free";
        } else if (mPrice < 1) {
            str = (int)(mPrice * 100) + MDApplication.sAppContext.getResources().getString(R.string.cent_symbol);
        } else if (mPrice * 10 % 10 == 0) {
            str = "$" + (int) mPrice;
        } else {
            str = "$" + mPrice;
        }

         return str;
    }


    public String getLocaledDisplayedPrice() {

        String str = null;

        if (mPrice == 0) {
            str = MDApplication.sAppContext.getResources().getString(R.string.free);
        } else if (mPrice < 1) {
            str = (int)(mPrice * 100) + MDApplication.sAppContext.getResources().getString(R.string.cent_symbol);
        } else if (mPrice * 10 % 10 == 0) {
            str = "$" + (int) mPrice;
        } else {
            str = "$" + mPrice;
        }

        return str;
    }


}
