package cn.sa.demo;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

import cn.sa.demo.utils.AccessibilityUtil;


/**
 * Created by yzk on 2019/7/16
 */

public class AppAccessibilityService extends AccessibilityService {

    private static final String TAG = "无障碍 Accessibility";
    private AccessibilityService accessibilityService;
    private int count;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");
        // 注册无障碍状态回调
        AccessibilityUtil.addAccessibilityStateChangeListener(getApplicationContext());
        accessibilityService = this;

    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!AccessibilityUtil.isAccessibilityServiceRunning(getApplicationContext())) {
            return;
        }
        switch (event.getEventType()) {
            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                checkWindow(event);
            default:
                break;
        }
    }

    private void checkWindow(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        Log.i(TAG, ":::" + event.toString() + "---->: " + className);
        if(AccessibilityUtil.MY_CUSTOM_CLASS_NAME.equals(event.getClassName()+"")){
            count = 0;
            traverse(getRootInActiveWindow());
            Log.e(TAG, String.format("-------------------    😈 本次尝试点击了   ------------------->:%d 个控件。", count));
        }
    }

    /**
     * 遍历控件 & 点击
     */
    private void traverse(AccessibilityNodeInfo info) {
        if (info == null) return;
        if (info.getChildCount() == 0) {
            String text = (String) info.getText();
            Log.i(TAG, String.format(" 👀 WindowId：%s，Widget：%s，Text：%s\n\n", info.getWindowId(),info.getClassName(),text));
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
    private void clickP(AccessibilityNodeInfo info) {
        try {
            if ("android.widget.ImageButton".equals(info.getClassName() + "")) {
                AccessibilityNodeInfo infoParent = info.getParent();
                if (infoParent.getViewIdResourceName() != null && infoParent.getViewIdResourceName().contains("action_bar")) {
                    return;// 忽略 action_bar 返回箭头的点击
                }
            }
            count ++;
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

    /**
     * 返回键
     */
    private void performBack() {
        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {
        accessibilityService = null;
        Log.i(TAG, "onInterrupt");
        Toast.makeText(getApplicationContext(), "无障碍 Interrupt", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessibilityService = null;
        Log.i(TAG, "onDestroy");
    }
}
