package com.mickledeals.tests;

import com.mickledeals.utils.Utils;

import java.io.Serializable;

/**
 * Created by Nicky on 12/7/2014.
 */
public class TestDataHolder implements Serializable{

    public int mId;
    public int mImageResId;
    public int mSmallImageResId;
    public String mDescription;
    public String mShortDescription;
    public String mDescriptionCh;
    public String mShortDescriptionCh;
    public float mPrice;
    public String mLatLng;
    public String mStoreName;
    public String mStoreNameCh;
    public String mAddress;
    public String mAddressShort;
    public String mOpenHours;
    public String mDetails;
    public int mCategoryId;

    public boolean mSaved;
    public boolean mBought;
    public boolean mExpired;

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
