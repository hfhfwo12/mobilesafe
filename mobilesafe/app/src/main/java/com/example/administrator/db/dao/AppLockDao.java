package com.example.administrator.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.administrator.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */

public class AppLockDao {
    private AppLockOpenHelper appLockOpenHelper;
    private Context context;

    private AppLockDao(Context context){
        this.context = context;

        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    private static AppLockDao appLockDao = null;

    public static AppLockDao getInstance(Context context){
        if(appLockDao == null){
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    //插入方法
    public void insert(String packagename){
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packagename);

        db.insert("applock", null, contentValues);

        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public void delete(String packagename){
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packagename);

        db.delete("applock", "packagename = ?", new String[]{packagename});

        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public List<String> findAll(){
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        List<String> lockPackageList = new ArrayList<String>();
        while(cursor.moveToNext()){
            lockPackageList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lockPackageList;
    }
}
