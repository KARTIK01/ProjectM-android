package com.mickledeals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class BuyDialogActivity extends DialogSwipeDismissActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        TextView cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        TextView confirm = (TextView) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setresult
                Intent i = new Intent();
                i.putExtra("remainingTime", "");
                setResult(RESULT_OK, i);
                finish();

                Intent newIntent = new Intent(BuyDialogActivity.this, SuccessDialogActivity.class);
                startActivity(newIntent);
            }
        });
        final TextView totalPriceTv = (TextView) findViewById(R.id.totalPrice);
        totalPriceTv.setText(getIntent().getStringExtra("price"));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.buy_dialog;
    }
}
