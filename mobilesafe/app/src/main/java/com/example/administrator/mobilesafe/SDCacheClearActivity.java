package com.example.administrator.mobilesafe;

/**
 * Created by Administrator on 2016/7/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SDCacheClearActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(getApplicationContext());
        textView.setText("SDCacheClearActivity");
        setContentView(textView);
    }
}
