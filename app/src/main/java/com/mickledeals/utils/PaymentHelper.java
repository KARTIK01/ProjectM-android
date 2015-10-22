package com.mickledeals.utils;

import android.widget.ImageView;

import com.mickledeals.R;

import io.card.payment.CardType;

/**
 * Created by Nicky on 6/26/2015.
 */
public class PaymentHelper {


//
    public static void setCardIcon(ImageView imageView, CardType type) {
        switch (type) {
            case VISA:
                imageView.setImageResource(R.drawable.ic_visa);
                break;
            case MASTERCARD:
                imageView.setImageResource(R.drawable.ic_master_card);
                break;
            case DISCOVER:
                imageView.setImageResource(R.drawable.ic_discover);
                break;
            case AMEX:
                imageView.setImageResource(R.drawable.ic_ae);
                break;
            case UNKNOWN:
            case INSUFFICIENT_DIGITS:
                imageView.setImageResource(R.drawable.ic_empty_card);
                break;
            default:
                imageView.setImageBitmap(type.imageBitmap(imageView.getContext()));
        }
    }
//
//    public static int getIconResFromString(CharSequence cs) {
//        if (cs == null || cs.length() == 0) return R.drawable.ic_empty_card;
//        char c = cs.charAt(0);
//        if (c == '2' || c == '5') return getIconResFromCardType(CardType.MASTERCARD);
//        else if (c == '4') return getIconResFromCardType(CardType.VISA);
//        else if (c == '3') return getIconResFromCardType(CardType.AMERICAN_EXPRESS);
//        else if (c == '6') return getIconResFromCardType(CardType.DISCOVER);
//        return R.drawable.ic_empty_card;
//    }
}
