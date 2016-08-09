package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;

/**
 * Created by Administrator on 2016/7/10.
 */
public class SetupOverActivity extends Activity {
    private TextView tv_phone;
    private TextView tv_reset_setup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if(setup_over){
            setContentView(R.layout.activity_setup_over);
        }else{
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
        initUI();
    }
    private void initUI() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        String phone = SpUtil.getString(this,ConstantValue.CONTACT_PHONE, "");
        tv_phone.setText(phone);


        tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}