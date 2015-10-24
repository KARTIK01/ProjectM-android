package com.mickledeals.datamodel;

import com.mickledeals.utils.JSONHelper;

import org.json.JSONObject;

import io.card.payment.CardType;

/**
 * Created by Nicky on 9/12/2015.
 */
public class PaymentInfo {

    public int mPaymentId;
    public CardType mCardType;
    public String mPaypalAccount = "";
    public String mLastFourDigits = "";
    public boolean mPrimary;

    public PaymentInfo(JSONObject jsonObject) {
        mPaymentId = JSONHelper.getInteger(jsonObject, "id");
        mPrimary = JSONHelper.getBoolean(jsonObject, "primaryPayment");
        mPaypalAccount = JSONHelper.getString(jsonObject, "payPalAccount");
        mLastFourDigits = JSONHelper.getString(jsonObject, "creditCardNumber");
        mCardType = CardType.fromString(JSONHelper.getString(jsonObject, "creditCardType"));
    }

}
