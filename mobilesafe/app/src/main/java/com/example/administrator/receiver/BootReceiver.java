package com.example.administrator.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;

/**
 * Created by Administrator on 2016/7/11.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "重启手机成功了,并且监听到了相应的广播......");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();

        String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");

        if(!simSerialNumber.equals(sim_number)){

            SmsManager sms = SmsManager.getDefault();
            String phone = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
            sms.sendTextMessage(phone, null, "sim change!!!", null, null);
        }
    }
}

