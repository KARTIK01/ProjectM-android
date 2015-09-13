package com.mickledeals.datamodel;

import java.io.Serializable;

/**
 * Created by Nicky on 12/7/2014.
 */
public class CouponSimpleInfo implements Serializable{

    public int mId;
    public int mImageId;
    public String mDescription;
    public float mPrice;
    public boolean mSaved;
    public String mStoreName;
    public String mDistance;
    public String mAddressShort;

}
