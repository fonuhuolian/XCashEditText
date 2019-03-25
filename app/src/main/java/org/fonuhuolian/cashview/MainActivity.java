package org.fonuhuolian.cashview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.fonuhuolian.xcasheditview.XCashEditText;
import org.fonuhuolian.xcasheditview.XCashInputListener;

public class MainActivity extends AppCompatActivity {

    private XCashEditText cashEditText;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cashEditText = (XCashEditText) findViewById(R.id.cEdit);
        tv = (TextView) findViewById(R.id.tv);

        cashEditText.setMaxCashAndInputListener(5, new XCashInputListener() {
            @Override
            public void onInputCorrectCash(double cashValue) {
                Log.e("onInputCorrectCash", cashValue + "");
                tv.setText("格式正确");
            }

            @Override
            public void onInputOverflow(double cashValue) {
                Log.e("onInputOverflow", cashValue + "");

                tv.setText("超出范围：5.00元");
            }
        });
    }
}
