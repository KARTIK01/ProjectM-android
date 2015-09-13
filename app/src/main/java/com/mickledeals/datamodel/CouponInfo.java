package com.mickledeals.datamodel;

import com.mickledeals.utils.Constants;
import com.mickledeals.utils.Utils;

import java.io.Serializable;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CouponInfo implements Serializable{

    public int mId;
    public int mImageResId;
    public int mSmallImageResId; //temp
    public String mDescription;
    public String mShortDescription;
    public String mDescriptionCh;
    public String mShortDescriptionCh;
    public float mPrice;
    public String mLatLng;
    public String mStoreId;
    public String mStoreName;
    public String mStoreNameCh;
    public String mAddress;
    public String mAddressShort;
    public String mDistance;
    public String mDetails;
    public String mFinePrint;
    public String mExpiredDate;
    public String mPhoneNumber;
    public int mCategoryId; //temp
    public int mStatus = Constants.COUPON_STATUS_DEFAULT;

    public boolean mSaved;

    public long mRedeemTime;

    public String getStoreName() {
        return (Utils.isChineseLocale() && mStoreNameCh != null) ? mStoreNameCh : mStoreName;
    }

    public String getDescription() {
        return (Utils.isChineseLocale() && mDescriptionCh != null) ? mDescriptionCh : mDescription;
    }

    public String getShortDescription() {
        return (Utils.isChineseLocale() && mShortDescriptionCh != null) ? mShortDescriptionCh : mShortDescription;
    }

}
