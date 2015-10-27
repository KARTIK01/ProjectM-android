package com.mickledeals.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.PaymentHelper;

import org.json.JSONObject;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

/**
 * Created by Nicky on 11/28/2014.
 */
public class EnterCardDetailsActivity extends DialogSwipeDismissActivity {

    private static final int MY_SCAN_REQUEST_CODE = 1;

    private TextView mCardNumber;
    private TextView mMM;
    private TextView mYY;
//    private TextView mCVV;
//    private TextView mZipCode;
    private ImageView mCardImage;
    private CardType mCardType = CardType.INSUFFICIENT_DIGITS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        findViewById(R.id.dialogContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to prevent dismiss dialog when tap on empty space
//                finish();
            }
        });

        mCardNumber = (TextView) findViewById(R.id.cardNumber);
        mMM = (TextView) findViewById(R.id.mm);
        mYY = (TextView) findViewById(R.id.yy);
//        mCVV = (TextView) findViewById(R.id.cvv);
//        mZipCode = (TextView) findViewById(R.id.zipcode);
        mCardImage = (ImageView) findViewById(R.id.cardImage);

        mCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher(mMM, -1));
        mMM.addTextChangedListener(new JumpToNextTextWatcher(mYY, 2));
//        mYY.addTextChangedListener(new JumpToNextTextWatcher(mCVV, 2));
//        mCVV.addTextChangedListener(new JumpToNextTextWatcher(mZipCode, 4));

    }

    public void scanBtnClick(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true); // default: false
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    public void saveBtnClick(View v) {
        boolean success = invalidateInfo();
        if (success) {
            mProgressBar.setVisibility(View.VISIBLE);
            String cardNumber = mCardNumber.getText().toString().replace(" ", "");
            MDApiManager.addPayments(cardNumber, mCardType.name.toLowerCase(), mMM.getText().toString(), "20" + mYY.getText().toString(), new MDReponseListenerImpl<JSONObject>() {
                @Override
                public void onMDSuccessResponse(JSONObject object) {
                    super.onMDSuccessResponse(object);
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onMDErrorResponse(String errorMessage) {
                    super.onMDErrorResponse(R.string.incorrect_card_info_message);
                }
            });
        }
    }

    public void cancelBtnClick(View v) {
        finish();
    }

    private boolean invalidateInfo() {
        if (mCardType == CardType.UNKNOWN || mCardType == CardType.INSUFFICIENT_DIGITS
                || (mCardNumber.getText().toString().replace(" ", "").length() != mCardType.numberLength())) {
            Toast.makeText(this, R.string.invalid_card_number, Toast.LENGTH_LONG).show();
            return false;
        }

        if (mMM.getText().length() < 2 || (mMM.getText().charAt(0) != '0' && mMM.getText().charAt(0) != '1')) {
            Toast.makeText(this, R.string.invalid_card_mm, Toast.LENGTH_LONG).show();
            return false;
        }

        if (mYY.getText().length() < 2) {
            Toast.makeText(this, R.string.invalid_card_yy, Toast.LENGTH_LONG).show();
            return false;
        }

//        if (mCVV.getText().length() < 3) {
//            Toast.makeText(this, R.string.invalid_card_cvv, Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if (mZipCode.getText().length() < 5) {
//            Toast.makeText(this, R.string.invalid_card_zip_code, Toast.LENGTH_LONG).show();
//            return false;
//        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                mCardNumber.setText(scanResult.cardNumber);
                formatCardNumber(mCardNumber.getEditableText());


                if (scanResult.isExpiryValid()) {
                    String mm = String.valueOf(scanResult.expiryMonth);
                    String yy = String.valueOf(scanResult.expiryYear);
                    mMM.setText(mm.length() == 1 ? ("0" + mm) : mm);
                    mYY.setText(yy.substring(yy.length() - 2, yy.length()));
                }

//                if (scanResult.postalCode != null) {
//                    mZipCode.setText(scanResult.postalCode);
//                }
            }
            else {
//                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultStr);
        }
        // else handle other activity results
    }

    private void formatCardNumber(Editable s) {

        final char space = ' ';


        // Remove all spacing char
        int pos = 0;
        while (true) {
            if (pos >= s.length()) break;
            if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                s.delete(pos, pos + 1);
            } else {
                pos++;
            }
        }

        // Insert char where needed.
        pos = 4;
        while (true) {
            if (pos >= s.length()) break;
            final char c = s.charAt(pos);
            // Only if its a digit where there should be a space we insert a space
            if ("0123456789".indexOf(c) >= 0) {
                s.insert(pos, "" + space);
            }
            pos += 5;
        }
        PaymentHelper.setCardIcon(mCardImage, mCardType);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_enter_card_details;
    }

    public class JumpToNextTextWatcher implements TextWatcher {

        private TextView mNextFocusTv;
        private int mMaxLength;

        public JumpToNextTextWatcher(TextView nextFocus, int maxLength) {
            mNextFocusTv = nextFocus;
            mMaxLength = maxLength;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mMaxLength == -1) {//indicate it is card number textview
                int maxLength = mCardType.numberLength();
                //card type could return 4 or -1 if unknown type, make sure its above 12
                if (mCardNumber.getText().toString().replace(" ", "").length() >= maxLength && maxLength >= 12) {
                    mNextFocusTv.requestFocus();
                }
            }
            else if (mMaxLength == 4) {//indicate it is cvv tv
                int maxLength = mCardType.cvvLength();
//                if (mCVV.getText().length() >= maxLength && maxLength >= 3) {
//                    mNextFocusTv.requestFocus();
//                }
            }


            else if (s.length() >= mMaxLength) {
                mNextFocusTv.requestFocus();
            }

        }
    }


    public class FourDigitCardFormatWatcher extends JumpToNextTextWatcher {


        public FourDigitCardFormatWatcher(TextView nextFocus, int maxLength) {
            super(nextFocus, maxLength);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mCardType = CardType.fromCardNumber(mCardNumber.getText().toString().replace(" ", ""));
            super.afterTextChanged(s);
            formatCardNumber(s);

        }
    }
}
