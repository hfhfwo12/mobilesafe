package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class SplashActivity extends Activity {

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initDB();
        initShortCut();
        button=(Button) findViewById(R.id.bt_home);
        button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
});
    }

    /**
     * 生成快捷方式
     */
    private void initShortCut() {

        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");

        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);

        sendBroadcast(intent);

        SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
    }
    private void initDB() {

        initAddressDB("address.db");

        initAddressDB("commonnum.db");
        initAddressDB("antivirus.db");
    }
    private void initAddressDB(String dbName) {

        File files = getFilesDir();
        File file = new File(files, dbName);
        if(file.exists()){
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;

        try {
            stream = getAssets().open(dbName);

            fos = new FileOutputStream(file);

            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){
                fos.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

