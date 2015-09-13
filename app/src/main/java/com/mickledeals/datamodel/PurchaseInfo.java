package com.mickledeals.datamodel;

import java.io.Serializable;

/**
 * Created by Nicky on 9/12/2015.
 */
public class PurchaseInfo implements Serializable {

    public String mCurrentCredit;
    public String mCreditToUse;
    public String mPurchaseNo; //to display in coupon details after purchase
    public String mTotalPrice;
    public PaymentInfo mPaymentInfo;

}
