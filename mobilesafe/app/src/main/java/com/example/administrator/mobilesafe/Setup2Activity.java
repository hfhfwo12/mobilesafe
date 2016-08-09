package com.example.administrator.mobilesafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;
import com.example.administrator.util.ToastUtil;

import example.administrator.mobilesafe.view.SettingItemView;

/**
 * Created by Administrator on 2016/7/10.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUI();
    }

    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if(TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setCheck(false);
        }else{
            siv_sim_bound.setCheck(true);
        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isCheck = siv_sim_bound.isCheck();

                siv_sim_bound.setCheck(!isCheck);
                if(!isCheck){

                    TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = manager.getSimSerialNumber();
                    SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                }else{

                    SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    @Override
    protected void showNextPage() {

        String serialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");

        boolean isCheck = siv_sim_bound.isCheck();
        if(isCheck){
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);

            finish();

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请绑定sim卡");
        }

    }

    @Override
    protected void showPrePage() {

        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_main, R.anim.pre_out_main);

    }
}
