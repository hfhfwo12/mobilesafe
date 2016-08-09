package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.db.domain.AppInfo;
import com.example.administrator.engine.AppInfoProvider;
import com.example.administrator.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/13.
 */


public class AppManagerActivity extends Activity implements View.OnClickListener {
    private List<AppInfo> mAppInfoList;

    private ListView lv_app_list;
    private MyAdapter mAdapter;

    private List<AppInfo> mSystemList;
    private List<AppInfo> mCustomerList;

    private AppInfo mAppInfo;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mAdapter = new MyAdapter();
            lv_app_list.setAdapter(mAdapter);

            if(tv_des!=null && mCustomerList!=null){
                tv_des.setText("用户应用("+mCustomerList.size()+")");
            }
        };
    };

    private TextView tv_des;

    private PopupWindow mPopupWindow;

    class MyAdapter extends BaseAdapter{


        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }


        @Override
        public int getItemViewType(int position) {
            if(position == 0 || position == mCustomerList.size()+1){

                return 0;
            }else{

                return 1;
            }
        }


        @Override
        public int getCount() {
            return mCustomerList.size()+mSystemList.size()+2;
        }

        @Override
        public AppInfo getItem(int position) {
            if(position == 0 || position == mCustomerList.size()+1){
                return null;
            }else{
                if(position<mCustomerList.size()+1){
                    return mCustomerList.get(position-1);
                }else{

                    return mSystemList.get(position - mCustomerList.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);

            if(type == 0){

                ViewTitleHolder holder = null;
                if(convertView == null){
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if(position == 0){
                    holder.tv_title.setText("用户应用("+mCustomerList.size()+")");
                }else{
                    holder.tv_title.setText("系统应用("+mSystemList.size()+")");
                }
                return convertView;
            }else{

                ViewHolder holder = null;
                if(convertView == null){
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
                    holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                if(getItem(position).isSdCard){
                    holder.tv_path.setText("sd卡应用");
                }else{
                    holder.tv_path.setText("手机应用");
                }
                return convertView;
            }
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    static class ViewTitleHolder{
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitle();
        initList();
    }

    private void initList() {
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        tv_des = (TextView) findViewById(R.id.tv_des);
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mCustomerList!=null && mSystemList!=null){
                    if(firstVisibleItem>=mCustomerList.size()+1){

                        tv_des.setText("系统应用("+mSystemList.size()+")");
                    }else{

                        tv_des.setText("用户应用("+mCustomerList.size()+")");
                    }
                }

            }
        });

        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //view点中条目指向的view对象
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if(position == 0 || position == mCustomerList.size()+1){
                    return;
                }else{
                    if(position<mCustomerList.size()+1){
                        mAppInfo = mCustomerList.get(position-1);
                    }else{

                        mAppInfo = mSystemList.get(position - mCustomerList.size()-2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    protected void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow_layout, null);

        TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);


        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        //动画终止时停留在最后一帧
        alphaAnimation.setFillAfter(true);


        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        AnimationSet animationSet = new AnimationSet(true);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);


        mPopupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable());

        mPopupWindow.showAsDropDown(view, 50, -view.getHeight());

        popupView.startAnimation(animationSet);
    }

    private void initTitle() {

        String path = Environment.getDataDirectory().getAbsolutePath();

        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
        String sdMemoryAvailSpace = Formatter.formatFileSize(this,getAvailSpace(sdPath));

        TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);

        tv_memory.setText("磁盘可用:"+memoryAvailSpace);
        tv_sd_memory.setText("sd卡可用:"+sdMemoryAvailSpace);
    }


    /**
     * 返回值结果单位为byte = 8bit,最大结果为2147483647 bytes
     * @param path
     * @return	返回指定路径可用区域的byte类型值
     */
    private long getAvailSpace(String path) {

        StatFs statFs = new StatFs(path);

        long count = statFs.getAvailableBlocks();

        long size = statFs.getBlockSize();

        return count*size;

    }

    @Override
    protected void onResume() {

        getData();
        super.onResume();
    }

    private void getData() {
        new Thread(){
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<AppInfo>();
                mCustomerList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppInfoList) {
                    if(appInfo.isSystem){

                        mSystemList.add(appInfo);
                    }else{

                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            };
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if(mAppInfo.isSystem){
                    ToastUtil.show(getApplicationContext(), "此应用不能卸载");
                }else{
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:

                PackageManager pm = getPackageManager();

                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if(launchIntentForPackage!=null){
                    startActivity(launchIntentForPackage);
                }else{
                    ToastUtil.show(getApplicationContext(), "此应用不能被开启");
                }
                break;

            case R.id.tv_share:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用,应用名称为"+mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }


        if(mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }
}