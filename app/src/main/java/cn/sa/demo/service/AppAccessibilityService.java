package cn.sa.demo.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import cn.sa.demo.custom.FloatView;
import cn.sa.demo.utils.AccessibilityUtil;
import cn.sa.demo.utils.LuckyMoneyUtil;


/**
 * Created by yzk on 2019/7/16
 *
 * auto click view
 * auto click lucky money
 */

public class AppAccessibilityService extends AccessibilityService {

    private static final String TAG = "XXXX 无障碍";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");
        // 悬浮的 view(小篮球)
        FloatView.createView(this);
        // 注册无障碍状态回调
        AccessibilityUtil.addAccessibilityStateChangeListener(this);
        LuckyMoneyUtil.accessService = this;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                // WeChat view 滚动
                LuckyMoneyUtil.checkWeChatGroup(event);
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                // WeChat 通知
                LuckyMoneyUtil.checkWeChatNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:// 窗口的状态发生改变
                AccessibilityUtil.checkWindow(event, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
