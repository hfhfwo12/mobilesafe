package com.example.administrator.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/12.
 */
public class AddressDao {
    private static final String tag = "AddressDao";

    public static String path = "data/data/com.example.administrator.mobilesafe/files/address.db";
    private static String mAddress = "未知号码";
    /**传递一个电话号码,开启数据库连接,进行访问,返回一个归属地
     * @param phone	查询电话号码
     */
    public static String getAddress(String phone){
        mAddress = "未知号码";
        String regularExpression = "^1[3-8]\\d{9}";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if(phone.matches(regularExpression)){
            phone = phone.substring(0,7);

            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);

            if(cursor.moveToNext()){
                String outkey = cursor.getString(0);
                Log.i(tag, "outkey = "+outkey);

                Cursor indexCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if(indexCursor.moveToNext()){

                    mAddress = indexCursor.getString(0);
                    Log.i(tag, "address = "+mAddress);
                }
            }else{
                mAddress = "未知号码";
            }
        }else{
            int length = phone.length();
            switch (length) {
                case 3://119 110 120 114
                    mAddress = "报警电话";
                    break;
                case 4://119 110 120 114
                    mAddress = ".....";
                    break;
                case 5://10086 99555
                    mAddress = "服务电话....";
                    break;
                case 7:
                    mAddress = "本地电话";
                    break;
                case 8:
                    mAddress = "本地电话";
                    break;
                case 11:

                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
                    if(cursor.moveToNext()){
                        mAddress = cursor.getString(0);
                    }else{
                        mAddress = "未知号码";
                    }
                    break;
                case 12:

                    String area1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
                    if(cursor1.moveToNext()){
                        mAddress = cursor1.getString(0);
                    }else{
                        mAddress = "未知号码";
                    }
                    break;
            }
        }
        return mAddress;
    }
}

