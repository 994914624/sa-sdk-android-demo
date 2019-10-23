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
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.vondear.rxtool.RxTool;

//import com.sensorsdata.analytics.android.sdk.data.DbAdapter;

import org.json.JSONException;
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
    final static String SA_SERVER_URL_DEBUG = "https://sdktest.datasink.sensorsdata.cn/sa?project=yangzhankun&token=21f2e56df73988c7";

    // release 模式的数据接收地址（发版，正式项目）
    final static String SA_SERVER_URL_RELEASE = "https://sdktest.datasink.sensorsdata.cn/sa?project=yangzhankun&token=21f2e56df73988c7";

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
        initSensorsDataSDK();
        // 风控 SDK
        //initSensorsRiskControlAPI();
        initGIO();
        RxTool.init(this);
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
    private void initSensorsDataSDK() {
        try {
            // 设置 SAConfigOptions，传入数据接收地址 SA_SERVER_URL
            SAConfigOptions saConfigOptions = new SAConfigOptions(isDebugMode(this) ? SA_SERVER_URL_DEBUG : SA_SERVER_URL_RELEASE);

            // 通过 SAConfigOptions 设置神策 SDK options
            saConfigOptions.setAutoTrackEventType(
                            SensorsAnalyticsAutoTrackEventType.APP_START | // 自动采集 App 启动事件
                            SensorsAnalyticsAutoTrackEventType.APP_END | // 自动采集 App 退出事件
                            SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN | // 自动采集 App 浏览页面事件
                            SensorsAnalyticsAutoTrackEventType.APP_CLICK)   // 自动采集控件点击事件
                    .enableLog(isDebugMode(this))        // 开启神策调试日志，默认关闭(调试时，可开启日志)。

                    .enableTrackAppCrash(); // 开启 crash 采集

            // 需要在主线程初始化神策 SDK
            SensorsDataAPI.startWithConfiguration(this, saConfigOptions);

            // 初始化 SDK 后，可以获取应用名称设置为公共属性
            JSONObject properties = new JSONObject();
            properties.put("app_name", getAppName(context));
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);

            SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
            SensorsDataAPI.sharedInstance().enableHeatMap();

            // 初始化 SDK 后，开启 RN 页面控件点击事件的自动采集
            SensorsDataAPI.sharedInstance().enableReactNativeAutoTrack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化风控 SDK，注意由于风控会使用神策分析 SDK 中的 API 接口，需要先初始化神策分析 SDK。
     */
//    private void initSensorsRiskControlAPI () {
//        SARiskControlAPI.RCConfigOptions rcConfigOptions = new SARiskControlAPI.RCConfigOptions();
//        rcConfigOptions.enableTrackAppList(true, true);
//        // 开启传感器
//        rcConfigOptions.enableSensorDetector(true);
//        SARiskControlAPI.startWithConfigOptions(this, rcConfigOptions);
//    }

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
