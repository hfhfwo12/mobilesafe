package com.example.administrator.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2016/7/14.
 */

public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ProcessInfoProvider.killAll(context);
    }
}
