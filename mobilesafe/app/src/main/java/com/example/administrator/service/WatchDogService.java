package com.example.administrator.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.example.administrator.db.dao.AppLockDao;
import com.example.administrator.mobilesafe.EnterPsdActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/7/15.
 */

public class WatchDogService extends Service {
    private boolean isWatch;
    private AppLockDao mDao;
    private List<String> mPacknameList;
    private InnerReceiver mInnerReceiver;
    private String mSkipPackagename;
    private MyContentObserver mContentObserver;
    @Override
    public void onCreate() {

        mDao = AppLockDao.getInstance(this);
        isWatch = true;
        watch();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");

        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);



        mContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(
                Uri.parse("content://applock/change"), true, mContentObserver);
        super.onCreate();
    }

    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            new Thread(){
                public void run() {
                    mPacknameList = mDao.findAll();
                };
            }.start();
            super.onChange(selfChange);
        }
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            mSkipPackagename = intent.getStringExtra("packagename");
        }
    }

    private void watch() {

        new Thread(){
            public void run() {
                mPacknameList = mDao.findAll();
                while(isWatch){

                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);

                    String packagename = runningTaskInfo.topActivity.getPackageName();




                    if(mPacknameList.contains(packagename)){

                        if(!packagename.equals(mSkipPackagename)){

                            Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packagename", packagename);
                            startActivity(intent);
                        }
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();

    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onDestroy() {

        isWatch = false;

        if(mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }

        if(mContentObserver!=null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }
}