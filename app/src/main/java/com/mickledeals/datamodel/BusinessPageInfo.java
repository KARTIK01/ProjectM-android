package com.mickledeals.datamodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nicky on 9/12/2015.
 */
public class BusinessPageInfo implements Serializable {

    public String mBusinessId;
    public String mLogoImageId;
    public String mCoverPhotoId;

    public String mStoreName;
    public String mStoreDescription;
    public String mNews;

    public List<BusinessPhoto> mPhotoIds;

    public String mAddress;
    public double mLat;
    public double mLng;
    public String mPhone;
    public String mWebSiteAddr;
    public String mHours;

    public List<CouponSimpleInfo> mCoupons;
}
