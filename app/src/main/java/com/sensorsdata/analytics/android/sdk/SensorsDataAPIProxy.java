package com.sensorsdata.analytics.android.sdk;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by yzk on 2020/4/14
 */

public class SensorsDataAPIProxy extends SensorsDataAPI  {

   public SensorsDataAPI sensorsDataAPI;

   public SensorsDataAPIProxy(){
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.sensorsdata.analytics.android.sdk.SensorsDataAPI");
            java.lang.reflect.Method sharedInstance = clazz.getMethod("sharedInstance");
            Object sdkInstance = sharedInstance.invoke(null);
            sensorsDataAPI = (SensorsDataAPI) sdkInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void trackEvent(String eventName,  JSONObject properties, String
            anonymousId) {
        Log.d("SA_Proxy", String.format("-----%s, --- %s", eventName, properties));

        try {
            Class<?> clazz = Class.forName("com.sensorsdata.analytics.android.sdk.SensorsDataAPI");
            java.lang.reflect.Method sharedInstance = clazz.getMethod("sharedInstance");
            Object sdkInstance = sharedInstance.invoke(null);
            java.lang.reflect.Method trackEvent = clazz.getDeclaredMethod("trackEvent", EventType.class, String.class, JSONObject.class,String.class);
            trackEvent.setAccessible(true);

            // 反射调用 trackEvent
            trackEvent.invoke(sdkInstance, EventType.TRACK, eventName, properties,anonymousId);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        super.track("test2");
//        sensorsDataAPI.trackEvent(eventName, properties);
    }
}
