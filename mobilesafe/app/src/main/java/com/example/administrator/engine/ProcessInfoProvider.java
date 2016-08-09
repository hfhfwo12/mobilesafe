package com.example.administrator.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.example.administrator.db.domain.ProcessInfo;
import com.example.administrator.mobilesafe.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */

public class ProcessInfoProvider {

    public static int getProcessCount(Context ctx){

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        return runningAppProcesses.size();
    }


    /**
     * @param ctx
     * @return 返回可用的内存数	bytes
     */
    public static long getAvailSpace(Context ctx){

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        am.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem;
    }


    /**
     * @param ctx
     * @return 返回总的内存数	单位为bytes 返回0说明异常
     */
    public static long getTotalSpace(Context ctx){

        FileReader fileReader  = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader= new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();

            char[] charArray = lineOne.toCharArray();

            StringBuffer stringBuffer = new StringBuffer();
            for (char c : charArray) {
                if(c>='0' && c<='9'){
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(fileReader!=null && bufferedReader!=null){
                    fileReader.close();
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * @param ctx	上下文环境
     * @return		当前手机正在运行的进程的相关信息
     */
    public static List<ProcessInfo> getProcessInfo(Context ctx){

        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();


        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();

            processInfo.packageName = info.processName;

            android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});

            android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];

            processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);

                processInfo.name = applicationInfo.loadLabel(pm).toString();

                processInfo.icon = applicationInfo.loadIcon(pm);

                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem = true;
                }else{
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {

                processInfo.name = info.processName;
                processInfo.icon = ctx.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }


    /**
     * 杀进程方法
     * @param ctx	上下文环境
     * @param processInfo	杀死进程所在的javabean的对象
     */
    public static void killProcess(Context ctx,ProcessInfo processInfo) {

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        am.killBackgroundProcesses(processInfo.packageName);
    }


    /**
     * 杀死所有进程
     * @param ctx	上下文环境
     */
    public static void killAll(Context ctx) {

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {

            if(info.processName.equals(ctx.getPackageName())){

                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }
    }
}