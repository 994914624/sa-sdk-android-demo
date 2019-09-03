package cn.sa.demo.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

import cn.sa.demo.App;


/**
 * Created by yzk on 2019/7/16
 */

public class AccessibilityUtil {

    private static final String TAG = "AccessibilityUtil";
    private static final String SERVER_INFO_ID = "cn.sa.demo/.service.AppAccessibilityService";
    private static final String APP_PACKAGE_NAME = "cn.sa.demo";
    private static final String MY_CUSTOM_CLASS_NAME = "yzk";

    private static int count;

    private static final AccessibilityManager.AccessibilityStateChangeListener listener = new AccessibilityManager.AccessibilityStateChangeListener() {
        @Override
        public void onAccessibilityStateChanged(boolean enabled) {
            String status = "关闭";
            if (enabled) {
                status = "开启";
            }
            Toast.makeText(App.getContext(), "无障碍：" + status, Toast.LENGTH_SHORT).show();
        }
    };

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
    private static AccessibilityManager accessibilityManager;

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
        accessibilityManager.addAccessibilityStateChangeListener(listener);
    }

    /**
     * add 无障碍状态回调
     */
    public static void removeAccessibilityStateChangeListener() {
        if (accessibilityManager == null) return;
        accessibilityManager.removeAccessibilityStateChangeListener(listener);
    }

    public static void checkWindow(AccessibilityEvent event, AccessibilityService accessibilityService) {
        if (AccessibilityUtil.MY_CUSTOM_CLASS_NAME.equals(event.getClassName() + "")) {
            count = 0;
            traverse(accessibilityService.getRootInActiveWindow());
            Log.e(TAG, String.format("-------------------    😈 本次尝试点击了   ------------------->:%d 个控件。", count));
        }
    }

    /**
     * 遍历控件 & 点击
     */
    private static void traverse(AccessibilityNodeInfo info) {
        if (info == null) return;
        if (info.getChildCount() == 0) {
            CharSequence text = info.getText();
            Log.i(TAG, String.format(" 👀 WindowId：%s，Widget：%s，Text：%s\n\n", info.getWindowId(), info.getClassName(), text));
            // 文本不为空/可点击，执行点击
            if (!TextUtils.isEmpty(text) || info.isClickable()) {
                clickP(info);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                traverse(info.getChild(i));
            }
        }
    }

    /**
     * 执行点击
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void clickP(AccessibilityNodeInfo info) {
        try {
            if ("android.widget.ImageButton".equals(info.getClassName() + "")) {
                AccessibilityNodeInfo infoParent = info.getParent();
                if (infoParent.getViewIdResourceName() != null && infoParent.getViewIdResourceName().contains("action_bar")) {
                    return;// 忽略 action_bar 返回箭头的点击
                }
            }
            count++;
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.e(TAG, String.format("-------------------    😈 点击了   ------------------->：%s ：%s：%s", info.getClassName(), info.getText(), info.getViewIdResourceName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 找到文本对应的控件，然后执行点击
     */
    private void findTextThenClick(AccessibilityEvent event, String text) {
        if (event.getSource() == null) return;
        List<AccessibilityNodeInfo> elements = event.getSource().findAccessibilityNodeInfosByText(text);
        if (elements.size() == 0) {
            return;
        }
        for (AccessibilityNodeInfo element : elements) {
            element.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.e(TAG, String.format(" 点击 text --->：%s：%s", element.getClassName(), text));
        }
    }
}

