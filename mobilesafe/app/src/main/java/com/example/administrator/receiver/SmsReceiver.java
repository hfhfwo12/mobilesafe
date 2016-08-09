package com.example.administrator.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.example.administrator.mobilesafe.R;
import com.example.administrator.service.LocationService;
import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;

/**
 * Created by Administrator on 2016/7/11.
 */
public class SmsReceiver extends BroadcastReceiver {
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if(open_security){

            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {

                SmsMessage sms = SmsMessage.createFromPdu((byte[])object);

                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
                mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);

                if(messageBody.contains("#*alarm*#")){

                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

                if(messageBody.contains("#*location*#")){
                    context.startService(new Intent(context,LocationService.class));
                }

                if(messageBody.contains("#*lockscrenn*#")){
                    if(mDPM.isAdminActive(mDeviceAdminSample)){
                        mDPM.lockNow();
                        mDPM.resetPassword("123", 0);
                    }
                }

                if(messageBody.contains("#*wipedate*#")){
                    if(mDPM.isAdminActive(mDeviceAdminSample)){
                        mDPM.wipeData(0);

                    }
                }
            }
        }
    }
}

