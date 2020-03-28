package cn.sa.demo.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;


public class AutoUtil {
    private final static String TAG = "AutoUtil";

    /**
     * 控件点击时触发
     *
     */
    public static void onClick(View view) {
        // TODO 这里可以触发点击事件
        Log.d(TAG, "---onClick---" );
    }


    /**
     * Activity onResume 时触发
     */
    public static void onResume(Activity activity) {
        // TODO 这里可以触发 Activity 浏览页面事件
        Log.d(TAG, activity.getClass().getSimpleName() + "---onResume---");
    }


    /**
     * log
     */
    public static void log() {
        // TODO
        Log.d(TAG, "---yyy log---");
    }
}
