package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.util.ToastUtil;

/**
 * Created by Administrator on 2016/7/15.
 */

public class EnterPsdActivity extends Activity {
    private String packagename;
    private TextView tv_app_name;
    private ImageView iv_app_icon;
    private EditText et_psd;
    private Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        packagename = getIntent().getStringExtra("packagename");
        setContentView(R.layout.activity_enter_psd);
        initUI();
        initData();
    }

    private void initData() {

        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename,0);
            Drawable icon = applicationInfo.loadIcon(pm);
            iv_app_icon.setBackgroundDrawable(icon);
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_psd.getText().toString();
                if(!TextUtils.isEmpty(psd)){
                    if(psd.equals("123")){

                        Intent intent = new Intent("android.intent.action.SKIP");
                        intent.putExtra("packagename",packagename);
                        sendBroadcast(intent);

                        finish();
                    }else{
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                }else{
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });
    }

    private void initUI() {
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);

        et_psd = (EditText) findViewById(R.id.et_psd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
