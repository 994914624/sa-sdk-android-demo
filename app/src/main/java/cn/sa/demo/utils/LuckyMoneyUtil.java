package cn.sa.demo.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import static android.os.Looper.getMainLooper;

/**
 * Created by yzk on 2019-08-30
 * WeChat 7.0.6 版本，auto click lucky money
 */

public class LuckyMoneyUtil {

    private static final String TAG = "XXXX LuckyMoneyUtil";
    private static final int CLICKED_STEP_ONE = 1;
    private static final int CLICKED_STEP_TWO = 2;
    private static final String PACKAGE = "com.tencent.mm";
    private static final String BUTTON_OPEN_ID_7_0_6 = "com.tencent.mm:id/d5a";// 每个版本 Button "開" id 会变
    public static AccessibilityService accessService;
    private static Runnable checkOpenButtonTask = new Runnable() {
        @Override
        public void run() {
            if (accessService != null) {
                traverseWeChatMessage(accessService.getRootInActiveWindow());
            }
        }
    };
    private static Handler handler = new Handler(getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLICKED_STEP_ONE:
                    Log.e(TAG, "-----------> STEP_ONE");
                    // 0.5秒后寻找 "開" 按钮 / 或点击通知后的遍历
                    handler.postDelayed(checkOpenButtonTask, 500);
                    // 1.5秒后 再寻找一次 "開" 按钮（用于查漏，防止由于网络等因素0.5秒时没找到"開"）
                    handler.postDelayed(checkOpenButtonTask, 1500);
                    break;
                case CLICKED_STEP_TWO:
                    Log.e(TAG, "-----------> STEP_TWO");
                    // 如果走到 STEP_TWO 说明已打开 lucky money 或没抢到，取消 1.5 秒的 task
                    handler.removeCallbacks(checkOpenButtonTask);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 检测当前窗口聊天内容
     */
    public static void checkWeChatGroup(AccessibilityEvent event) {
        if (PACKAGE.equals(event.getPackageName() + "")) {
            traverseWeChatMessage(accessService.getRootInActiveWindow());
        }
    }

    /**
     * 检测 Notification
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void checkWeChatNotification(AccessibilityEvent event) {
        if (PACKAGE.equals(event.getPackageName() + "")) {
            try {
                if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                    Notification notification = (Notification) event.getParcelableData();
                    String text = notification.tickerText + "";
                    Log.i(TAG, "-------Notification------->: " + text);
                    if (text.contains("[微信红包]")||text.equals("null")) {
                        PendingIntent pendingIntent = notification.contentIntent;
                        // 打开消息
                        pendingIntent.send();
                        // 1秒后，检测聊天窗口中的内容
                        handler.sendEmptyMessageDelayed(CLICKED_STEP_ONE, 1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 遍历 node & 点击
     */
    @TargetApi(18)
    private static void traverseWeChatMessage(AccessibilityNodeInfo info) {
        if (info == null) return;
        if (info.getChildCount() == 0) {
            String text = info.getText() + "";
            //Log.i(TAG, String.format("PackageName：%s，Widget：%s，Text：%s，ID：%s\n\n", info.getPackageName(), info.getClassName(), text, info.getViewIdResourceName()));
            // WeChat 聊天中的「微信红包」
            if (text.contains("微信红包")) {
                try {
                    AccessibilityNodeInfo infoParent = info.getParent().getParent();
                    if (infoParent.getChild(0).getChild(0).getChild(1).getChildCount() < 2) {//childCount=2 为已领取/已领取完
                        // 点击父节点
                        infoParent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        // 点击了第一步
                        handler.sendEmptyMessage(CLICKED_STEP_ONE);
                    } else {
                        Log.e(TAG, String.format("------ %s 不再点击-----> ", infoParent.getChild(0).getChild(0).getChild(1).getChild(1).getText()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // WeChat 点击 "開" 按钮
            if (BUTTON_OPEN_ID_7_0_6.equals(info.getViewIdResourceName())) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.e(TAG, "----------->  開");
                performBack();
                // 点击了第二步
                handler.sendEmptyMessage(CLICKED_STEP_TWO);
            }
            // 已被抢完，返回
            if (text.contains("手慢了，红包派完了")) {
                performBack();
                handler.sendEmptyMessage(CLICKED_STEP_TWO);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                traverseWeChatMessage(info.getChild(i));
            }
        }
    }

    /**
     * 2秒后，执行返回键
     */
    private static void performBack() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessService != null) {
                    accessService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    Log.e(TAG, "-----------> perform back");
                }
            }
        }, 2000);
    }
}
