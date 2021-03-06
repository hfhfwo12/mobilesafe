package com.example.administrator.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;
import com.example.administrator.util.ToastUtil;

/**
 * Created by Administrator on 2016/7/10.
 */
public class Setup4Activity extends BaseSetupActivity {
    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initUI();
    }

    private void initUI() {
        cb_box = (CheckBox) findViewById(R.id.cb_box);
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cb_box.setChecked(open_security);
        if(open_security){
            cb_box.setText("安全设置已开启");
        }else{
            cb_box.setText("安全设置已关闭");
        }

        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                if(isChecked){
                    cb_box.setText("安全设置已开启");
                }else{
                    cb_box.setText("安全设置已关闭");
                }
            }
        });

    }

    @Override
    protected void showNextPage() {
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if(open_security){
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);

            finish();
            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }else{
            ToastUtil.show(getApplicationContext(), "请开启防盗保护");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.pre_in_main, R.anim.pre_out_main);
    }
}

