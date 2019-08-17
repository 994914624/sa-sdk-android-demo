package cn.sa.demo.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import java.util.List;


/**
 * Created by yzk on 2019/7/16
 */

public class AccessibilityUtil {
    private static final String SERVER_INFO_ID = "cn.sa.demo/.AppAccessibilityService";
    private static final String APP_PACKAGE_NAME = "cn.sa.demo";
    public static final String MY_CUSTOM_CLASS_NAME = "yzk";

    /**
     * 发出 AccessibilityEvent 事件 TYPE_WINDOW_STATE_CHANGED
     */
    public static void sendAccessibilityEvent(Context context) {
        trySendAccessibilityEvent(context);
    }

    private static void trySendAccessibilityEvent(Context context) {
        try {
            if (context == null) return;
            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (!accessibilityManager.isEnabled()) {
                return;
            }
            AccessibilityEvent event = AccessibilityEvent.obtain(
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            //一个自己定义类名，用于识别出哪些是自己发的 AccessibilityEvent
            event.setClassName(MY_CUSTOM_CLASS_NAME);
            event.setPackageName(APP_PACKAGE_NAME);
            accessibilityManager.sendAccessibilityEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断无障碍是否正在运行
     */
    public static AccessibilityManager accessibilityManager;

    public static boolean isAccessibilityServiceRunning(Context context) {
        try {
            if (context == null) return false;
            if (accessibilityManager == null) {
                accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            }
            List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
            for (AccessibilityServiceInfo i : list) {
                if (i.getId().equals(SERVER_INFO_ID)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * add 无障碍状态回调
     */
    public static void addAccessibilityStateChangeListener(Context context) {
        if (context == null) return;
        if (accessibilityManager == null) {
            accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        }
        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
            @Override
            public void onAccessibilityStateChanged(boolean enabled) {
                String status = "关闭";
                if(enabled){
                    status = "开启";
                }
                Toast.makeText(context, "无障碍："+status, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
