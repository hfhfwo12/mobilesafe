package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.engine.VirusDao;
import com.example.administrator.util.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/7/15.
 */

public class AnitVirusActivity extends Activity {
    protected static final int SCANING = 100;

    protected static final int SCAN_FINISH = 101;

    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pb_bar;
    private LinearLayout ll_add_text;
    private int index = 0;
    private List<ScanInfo> mVirusScanInfoList;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SCANING:
                    //1,显示正在扫描应用的名称
                    ScanInfo info = (ScanInfo)msg.obj;
                    tv_name.setText(info.name);
                    //2,在线性布局中添加一个正在扫描应用的TextView
                    TextView textView = new TextView(getApplicationContext());
                    if(info.isVirus){
                        //是病毒
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒:"+info.name);
                    }else{
                        //不是病毒
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全:"+info.name);
                    }
                    ll_add_text.addView(textView, 0);
                    break;
                case SCAN_FINISH:
                    tv_name.setText("扫描完成");
                    //停止真正执行的旋转动画
                    iv_scanning.clearAnimation();
                    //告知用户卸载包含了病毒的应用
                    unInstallVirus();
                    break;
            }
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anit_virus);

        initUI();
        initAnimation();
        checkVirus();
    }

    protected void unInstallVirus() {
        for(ScanInfo scanInfo:mVirusScanInfoList){
            String packageName = scanInfo.packageName;
            //源码
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:"+packageName));
            startActivity(intent);
        }
    }

    private void checkVirus() {
        new Thread(){
            public void run() {

                List<String> virusList = VirusDao.getVirusList();


                PackageManager pm = getPackageManager();

                List<PackageInfo> packageInfoList = pm.getInstalledPackages(
                        PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);


                mVirusScanInfoList = new ArrayList<ScanInfo>();


                List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();


                pb_bar.setMax(packageInfoList.size());


                for (PackageInfo packageInfo : packageInfoList) {
                    ScanInfo scanInfo = new ScanInfo();

                    Signature[] signatures = packageInfo.signatures;

                    Signature signature = signatures[0];
                    String string = signature.toCharsString();

                    String encoder = Md5Util.encoder(string);

                    if(virusList.contains(encoder)){

                        scanInfo.isVirus = true;
                        mVirusScanInfoList.add(scanInfo);
                    }else{
                        scanInfo.isVirus = false;
                    }

                    scanInfo.packageName = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    scanInfoList.add(scanInfo);

                    index++;
                    pb_bar.setProgress(index);

                    try {
                        Thread.sleep(50+new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    Message msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            };
        }.start();
    }

    class ScanInfo{
        public boolean isVirus;
        public String packageName;
        public String name;
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);

        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);

        rotateAnimation.setFillAfter(true);

        iv_scanning.startAnimation(rotateAnimation);
    }

    private void initUI() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_name = (TextView) findViewById(R.id.tv_name);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
    }
}

