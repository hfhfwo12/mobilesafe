package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.db.domain.ProcessInfo;
import com.example.administrator.engine.ProcessInfoProvider;
import com.example.administrator.util.ConstantValue;
import com.example.administrator.util.SpUtil;
import com.example.administrator.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */

public class ProcessManagerActivity extends Activity implements View.OnClickListener {
    private TextView tv_process_count,tv_memory_info,tv_des;
    private ListView lv_process_list;
    private Button bt_select_all,bt_select_reverse,bt_clear,bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList;

    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;
    private MyAdapter mAdapter;

    private ProcessInfo mProcessInfo;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mAdapter = new MyAdapter();
            lv_process_list.setAdapter(mAdapter);

            if(tv_des!=null && mCustomerList!=null){
                tv_des.setText("用户应用("+mCustomerList.size()+")");
            }
        };
    };
    private long mAvailSpace;
    private String mStrTotalSpace;

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
            if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)){
                return mCustomerList.size()+mSystemList.size()+2;
            }else{
                return mCustomerList.size()+1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
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
                    holder.tv_title.setText("用户进程("+mCustomerList.size()+")");
                }else{
                    holder.tv_title.setText("系统进程("+mSystemList.size()+")");
                }
                return convertView;
            }else{

                ViewHolder holder = null;
                if(convertView == null){
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                holder.tv_memory_info.setText(strSize);


                if(getItem(position).packageName.equals(getPackageName())){
                    holder.cb_box.setVisibility(View.GONE);
                }else{
                    holder.cb_box.setVisibility(View.VISIBLE);
                }

                holder.cb_box.setChecked(getItem(position).isCheck);

                return convertView;
            }
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder{
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initUI();
        initTitleData();
        initListData();
    }

    private void initListData() {
        getData();
    }

    private void getData() {
        new Thread(){
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemList = new ArrayList<ProcessInfo>();
                mCustomerList = new ArrayList<ProcessInfo>();

                for (ProcessInfo info : mProcessInfoList) {
                    if(info.isSystem){

                        mSystemList.add(info);
                    }else{

                        mCustomerList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            };
        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数:"+mProcessCount);


        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);


        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

        tv_memory_info.setText("剩余/总共:"+strAvailSpace+"/"+mStrTotalSpace);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

        tv_des = (TextView) findViewById(R.id.tv_des);

        lv_process_list = (ListView) findViewById(R.id.lv_process_list);

        bt_select_all = (Button) findViewById(R.id.bt_select_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
        bt_clear = (Button)  findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mCustomerList!=null && mSystemList!=null){
                    if(firstVisibleItem>=mCustomerList.size()+1){

                        tv_des.setText("系统进程("+mSystemList.size()+")");
                    }else{

                        tv_des.setText("用户进程("+mCustomerList.size()+")");
                    }
                }

            }
        });

        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if(position == 0 || position == mCustomerList.size()+1){
                    return;
                }else{
                    if(position<mCustomerList.size()+1){
                        mProcessInfo = mCustomerList.get(position-1);
                    }else{

                        mProcessInfo = mSystemList.get(position - mCustomerList.size()-2);
                    }
                    if(mProcessInfo!=null){
                        if(!mProcessInfo.packageName.equals(getPackageName())){

                            mProcessInfo.isCheck = !mProcessInfo.isCheck;

                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_select_reverse:
                selectReverse();
                break;
            case R.id.bt_clear:
                clearAll();
                break;
            case R.id.bt_setting:
                setting();
                break;
        }
    }

    private void setting() {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 清理选中进程
     */
    private void clearAll() {

        List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
        for(ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            if(processInfo.isCheck){

                killProcessList.add(processInfo);
            }
        }
        for(ProcessInfo processInfo:mSystemList){
            if(processInfo.isCheck){

                killProcessList.add(processInfo);
            }
        }

        long totalReleaseSpace = 0;
        for (ProcessInfo processInfo : killProcessList) {

            if(mCustomerList.contains(processInfo)){
                mCustomerList.remove(processInfo);
            }

            if(mSystemList.contains(processInfo)){
                mSystemList.remove(processInfo);
            }

            ProcessInfoProvider.killProcess(this,processInfo);


            totalReleaseSpace += processInfo.memSize;
        }

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }

        mProcessCount -= killProcessList.size();

        mAvailSpace += totalReleaseSpace;

        tv_process_count.setText("进程总数:"+mProcessCount);
        tv_memory_info.setText("剩余/总共"+Formatter.formatFileSize(this, mAvailSpace)+"/"+mStrTotalSpace);

        String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);



        ToastUtil.show(getApplicationContext(),
                String.format("杀死了%d进程,释放了%s空间", killProcessList.size(),totalRelease));
    }

    private void selectReverse() {

        for(ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }
        for(ProcessInfo processInfo:mSystemList){
            processInfo.isCheck = !processInfo.isCheck;
        }

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {

        for(ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            processInfo.isCheck = true;
        }
        for(ProcessInfo processInfo:mSystemList){
            processInfo.isCheck = true;
        }

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }
}

