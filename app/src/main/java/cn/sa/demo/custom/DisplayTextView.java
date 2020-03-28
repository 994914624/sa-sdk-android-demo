package cn.sa.demo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.adapters.ViewBindingAdapter;


/**
 * Created by yzk on 2019-11-27
 */

public class DisplayTextView extends AppCompatTextView implements View.OnAttachStateChangeListener {

    private static final String TAG = "Display....TextView";
    public DisplayTextView(Context context) {
        super(context);
    }

    public DisplayTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DisplayTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addOnAttachStateChangeListener(this);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        Log.i(TAG,"--- onViewAttachedToWindow ---: "+v.hashCode());
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        Log.i(TAG,"--- onViewDetachedFromWindow ---: "+v.hashCode());
    }
}
