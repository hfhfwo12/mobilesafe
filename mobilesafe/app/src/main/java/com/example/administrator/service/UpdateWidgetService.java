package com.example.administrator.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.administrator.engine.ProcessInfoProvider;
import com.example.administrator.mobilesafe.R;
import com.example.administrator.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/7/14.
 */


public class UpdateWidgetService extends Service {
    protected static final String tag = "UpdateWidgetService";
    private Timer mTimer;
    private InnerReceiver mInnerReceiver;
    @Override
    public void onCreate() {

        startTimer();


        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);

        super.onCreate();
    }

    class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){

                startTimer();
            }else{

                cancelTimerTask();
            }
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                updateAppWidget();
                Log.i(tag, "5秒一次的定时任务现在正在运行..........");
            }
        }, 0, 5000);
    }
    public void cancelTimerTask() {

        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }
    protected void updateAppWidget() {

        AppWidgetManager aWM = AppWidgetManager.getInstance(this);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);

        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:"+ProcessInfoProvider.getProcessCount(this));

        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"+strAvailSpace);



        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);


        Intent broadCastintent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastintent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);


        ComponentName componentName = new ComponentName(this,MyAppWidgetProvider.class);

        aWM.updateAppWidget(componentName, remoteViews);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        if(mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }

        cancelTimerTask();
        super.onDestroy();
    }
}


