package example.administrator.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.mobilesafe.R;

/**
 * Created by Administrator on 2016/7/12.
 */

public class SettingClickView extends RelativeLayout {
    private TextView tv_des;
    private TextView tv_title;

    public SettingClickView(Context context) {
        this(context,null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View.inflate(context, R.layout.setting_click_view, this);


        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
    }

    /**
     * @param title	设置标题内容
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    /**
     * @param des	设置描述内容
     */
    public void setDes(String des){
        tv_des.setText(des);
    }

}

