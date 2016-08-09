package com.example.administrator.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
public class ServiceUtil {
    private static final String tag = "ServiceUtil";

    /**
     * @param ctx	上下文环境
     * @param serviceName 判断是否正在运行的服务
     * @return true 运行	false 没有运行
     */
    public static boolean isRunning(Context ctx, String serviceName){

        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            Log.i(tag, "runningServiceInfo.service.getClassName() = "+runningServiceInfo.service.getClassName());

            if(serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}

