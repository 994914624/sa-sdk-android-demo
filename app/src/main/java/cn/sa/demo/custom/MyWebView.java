package cn.sa.demo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

/**
 * Created by yzk on 2019/6/26
 */

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
        SensorsDataAPI.sharedInstance().showUpWebView(this,false,false);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
