package cn.sa.demo.custom;

import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by yzk on 2019-12-24
 * <p>
 * 封装神策的埋点接口，提供和 TD 一致的 onEvent 、onPageStart、onPageEnd 接口。
 */

public class SensorsDataUtil {

    public static void onEvent(String eventId) {
        onEvent(eventId, null, null);
    }

    public static void onEvent(String eventId, String label) {
        onEvent(eventId, label, null);
    }

    public static void onEvent(String eventId, String label, Map<String, Object> keyValues) {
        try {
            JSONObject properties = new JSONObject();
            if (!TextUtils.isEmpty(eventId)) {
                // event_id 作为事件的属性
                properties.put("event_id", eventId);
            }
            if (!TextUtils.isEmpty(label)) {
                // event_label 作为事件的属性
                properties.put("event_label", label);
            }
            if (keyValues != null) {
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    if (entry != null) {
                        properties.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            // 触发 TDEventGroup 事件
            SensorsDataAPI.sharedInstance().track("TDEventGroup", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 对应 TD 的 onPageStart
     */
    public static void onPageStart(String pageName) {
        try {
            if (!TextUtils.isEmpty(pageName)) {
                final String eventNameRegex = String.format("%s_%s_%s", "TDPageView",
                        java.util.UUID.nameUUIDFromBytes(pageName.getBytes()).toString().replace("-", "_"), "SATimer");
                SensorsDataAPI.sharedInstance().trackTimer(eventNameRegex, java.util.concurrent.TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 对应 TD 的 onPageEnd
     */
    public static void onPageEnd(String pageName) {
        try {
            if (!TextUtils.isEmpty(pageName)) {
                JSONObject properties = new JSONObject();
                properties.put("page_name", pageName);
                final String eventNameRegex = String.format("%s_%s_%s", "TDPageView",
                        java.util.UUID.nameUUIDFromBytes(pageName.getBytes()).toString().replace("-", "_"), "SATimer");
                // 计时结束时，触发 TDPageView 事件
                SensorsDataAPI.sharedInstance().trackTimerEnd(eventNameRegex, properties);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
