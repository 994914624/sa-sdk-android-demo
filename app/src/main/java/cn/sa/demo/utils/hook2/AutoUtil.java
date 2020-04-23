package cn.sa.demo.utils.hook2;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import org.json.JSONObject;


public class AutoUtil {
    private final static String TAG = "AutoUtil";

    /**
     * 控件点击时触发
     *
     */
    public static void onClick(View view) {
        try {
            // TODO 这里可以触发点击事件
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            Log.d(TAG, String.format("---onClick--- %d , %d", rect.centerX(), rect.centerY()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Activity onResume 时触发
     */
    public static void onResume(Activity activity) {
        // TODO 这里可以触发 Activity 浏览页面事件
        Log.d(TAG, String.format("%s---onResume---", activity.getClass().getSimpleName()));
    }

    /**
     * log
     */
    public static void log() {
        // TODO
        Log.d(TAG, "---yyy log---");
    }

    /**
     * trackEvent
     */
    public static void trackEvent( Object eventType, String eventName,  JSONObject properties, String
            anonymousId) {
        // TODO
        Log.d(TAG, String.format("---yyy trackEvent---%s ---%s ----%s", eventType.toString(),eventName,properties));
    }
}
