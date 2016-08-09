package com.example.administrator.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.administrator.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/13.
 */

public class AppInfoProvider {
    /**
     * 返回当前手机所有的应用的相关信息(名称,包名,图标,(手机内存,sd卡),(系统,用户));
     * @param ctx	获取包管理者的上下文环境
     * @return	包含手机安装应用相关信息的集合
     */
    public static List<AppInfo> getAppInfoList(Context ctx){

        PackageManager pm = ctx.getPackageManager();

        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();

        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();

            appInfo.packageName = packageInfo.packageName;

            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.name = applicationInfo.loadLabel(pm).toString();

            appInfo.icon = applicationInfo.loadIcon(pm);
            //7,判断是否为系统应用(每一个手机上的应用对应的flag都不一致)
            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){

                appInfo.isSystem = true;
            }else{

                appInfo.isSystem = false;
            }

            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){

                appInfo.isSdCard = true;
            }else{

                appInfo.isSdCard = false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}

