package cn.sa.demo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
//import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.growingio.android.sdk.deeplink.DeeplinkCallback;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
//import com.sensorsdata.analytics.android.sdk.data.DbAdapter;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sa.demo.observer.AppActivityLifecycleCallbacks;

public class App extends Application {

    final static String TAG = "SADemo.App";

    /**
     * Android SDK 文档：https://www.sensorsdata.cn/manual/android_sdk.html
     */

    // debug 模式的数据接收地址 （测试，测试项目）
    final static String SA_SERVER_URL_DEBUG = "http://sdk-test.datasink.sensorsdata.cn/sa?project=yangzhankun&token=95c73ae661f85aa0";

    // release 模式的数据接收地址（发版，正式项目）
    final static String SA_SERVER_URL_RELEASE = "【正式项目】数据接收地址";

    private boolean isDebugMode;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        // 在 Application 的 onCreate 初始化 SDK
        initSensorsDataSDK(this);
        initGIO();
        this.registerActivityLifecycleCallbacks(new AppActivityLifecycleCallbacks());
    }

    private void initGIO() {
        GrowingIO.startWithConfiguration(this, new Configuration()
                .trackAllFragments()
                .setChannel("XXX应用商店")
                .setMutiprocess(true)
                .setDebugMode(true)
                .setTestMode(true)
                .setTrackWebView(true)// 采集所有 WebView
                .setHashTagEnable(true) // 采集 WebView 页面锚点（单页面）
                .setDeeplinkCallback(new DeeplinkCallback() {
                    @Override
                    public void onReceive(Map<String, String> map, int i) {
                        //if (i == DeeplinkCallback.SUCCESS) {
                            Log.e(TAG, "-----> " + map.toString());
                        //}
                    }
                })
        );
    }

    /**
     * 初始化 SDK 、设置公共属性、开启自动采集
     */
    private void initSensorsDataSDK(Context context) {
        try {

            isDebugMode = isDebugMode(this);

            // 初始化神策 SDK
            SensorsDataAPI.sharedInstance(this,new SAConfigOptions(SA_SERVER_URL_DEBUG).enableMultiProcess(true));
          //  SensorsDataAPI.sharedInstance(this,SA_SERVER_URL_DEBUG, SensorsDataAPI.DebugMode.DEBUG_OFF);
            // 初始化SDK后，获取应用名称设置为公共属性

            JSONObject properties = new JSONObject();
            properties.put("app_name", getAppName(context));
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
            SensorsDataAPI.sharedInstance().getDistinctId();


            // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
            List<SensorsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
            // $AppStart
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_START);
            // $AppEnd
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_END);
            // $AppViewScreen
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
            // $AppClick
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
            SensorsDataAPI.sharedInstance().enableAutoTrack(eventTypeList);

            // 开启 Fragment $AppViewScreen
            SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();

            //初始化 SDK 之后，开启可视化全埋点, 在采集 $AppClick 事件时会记录 View 的 ViewPath
            //SensorsDataAPI.sharedInstance().enableVisualizedAutoTrack();
            // 开启调试日志
            SensorsDataAPI.sharedInstance().enableLog(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context App 的 Context
     *                获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param context App 的 Context
     * @return debug return true,release return false
     * 用于判断是 debug 包，还是 relase 包
     */
    public static boolean isDebugMode(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
