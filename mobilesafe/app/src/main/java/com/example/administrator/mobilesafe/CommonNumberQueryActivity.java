package com.example.administrator.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.administrator.engine.CommonnumDao;
import com.example.administrator.engine.CommonnumDao.Child;
import com.example.administrator.engine.CommonnumDao.Group;
import java.util.List;

;

/**
 * Created by Administrator on 2016/7/14.
 */

public class CommonNumberQueryActivity extends Activity {
    private ExpandableListView elv_common_number;
    private List<Group> mGroup;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initUI();
        initData();
    }

    /**
     * 给可扩展ListView准备数据,并且填充
     */
    private void initData() {
        CommonnumDao commonnumDao = new CommonnumDao();
        mGroup = commonnumDao.getGroup();

        mAdapter = new MyAdapter();
        elv_common_number.setAdapter(mAdapter);

        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                startCall(mAdapter.getChild(groupPosition, childPosition).number);
                return false;
            }
        });
    }

    protected void startCall(String number) {
        //开启系统的打电话界面
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+number));
        startActivity(intent);
    }

    private void initUI() {
        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
    }

    class MyAdapter extends BaseExpandableListAdapter {
        @Override
        public Child getChild(int groupPosition, int childPosition) {
            return mGroup.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

            tv_name.setText(getChild(groupPosition, childPosition).name);
            tv_number.setText(getChild(groupPosition, childPosition).number);

            return view;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroup.get(groupPosition).childList.size();
        }

        @Override
        public Group getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("			"+getGroup(groupPosition).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return textView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
