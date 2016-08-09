package example.administrator.mobilesafe.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/10.
 */
public class FocusTextView extends TextView{
    public FocusTextView(Context context) {
        super(context);
    }
    public FocusTextView(Context context, AttributeSet attrs){
            super(context,attrs);

    }
    public FocusTextView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }
    @Override
    public  boolean isFocused(){
        return true;
    }
}
