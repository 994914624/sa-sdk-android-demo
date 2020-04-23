package cn.sa.demo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.WebView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
//import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.growingio.android.sdk.deeplink.DeeplinkCallback;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
//import com.sensorsdata.analytics.android.sdk.SensorsDataEncrypt;
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackEventCallBack;
import com.vondear.rxtool.RxTool;

//import com.sensorsdata.analytics.android.sdk.data.DbAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sa.demo.custom.FloatView;
import cn.sa.demo.custom.MyWebView;
import cn.sa.demo.custom.SensorsDataUtil;
import cn.sa.demo.observer.AppActivityLifecycleCallbacks;

public class App extends Application {

    final static String TAG = "SADemo.App";

    /**
     * Android SDK 文档：https://www.sensorsdata.cn/manual/android_sdk.html
     */

    // debug 模式的数据接收地址 （测试，测试项目）
    final static String SA_SERVER_URL_DEBUG = "https://newsdktest.datasink.sensorsdata.cn/sa?project=yangzhankun&token=5a394d2405c147ca";

    // release 模式的数据接收地址（发版，正式项目）
    final static String SA_SERVER_URL_RELEASE = "https://newsdktest.datasink.sensorsdata.cn/sa?project=yangzhankun&token=5a394d2405c147ca";

    private boolean isDebugMode;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static long ADB_TIME;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        this.registerActivityLifecycleCallbacks(new AppActivityLifecycleCallbacks());
        // RxTool 扫码等功能库
        RxTool.init(this);
        // chrome://inspect 查看数据库
        Stetho.initializeWithDefaults(this);
        // 在 Application 的 onCreate 初始化 SDK
        initSensorsDataSDK();
        // 风控 SDK
        //initSensorsRiskControlAPI();
        // GIO
//        initGIO();
        // Firebase
//        fireBaseEvent();
        // AppsFlyer
//        initAppsFlyer();
        setFullScreenView();

    }

    private void setFullScreenView() {

    }

    private void initAppsFlyer() {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {


            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {

                for (String attrName : conversionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("LOG_TAG", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {

                for (String attrName : conversionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("LOG_TAG", "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init("qrdZGj123456789", conversionListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);
        AppsFlyerLib.getInstance().setDebugLog(true);
//        AppsFlyerLib.getInstance().setMinTimeBetweenSessions();
    }

    /**
     * FireBase 触发事件
     */
    private void fireBaseEvent() {
        // 设置用户属性
        FirebaseAnalytics.getInstance(this).setUserProperty("name","小白");
        FirebaseAnalytics.getInstance(this).setUserId("登录 ID-456");
        Bundle bundle = new Bundle();
        bundle.putString("key1","xxx1");
        bundle.putBundle("key2",new Bundle());
        FirebaseAnalytics.getInstance(this).logEvent("testEvent",bundle);
        FirebaseAnalytics.getInstance(this).setSessionTimeoutDuration(180000L);

        String e = FirebaseAnalytics.Param.ITEM_ID;

//        FirebaseAnalytics.getInstance(this).logEvent("testEvent2",new Bundle());

//        Log.e("abccc", String.format("getFirebaseInstanceId: %s,  getAppInstanceId:%s", FirebaseAnalytics.getInstance(getApplicationContext()).getFirebaseInstanceId(), FirebaseAnalytics.getInstance(getApplicationContext()).getAppInstanceId().getResult()));

//        FirebaseAnalytics.getInstance(this).resetAnalyticsData();

        
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
        GrowingIO.getInstance().track("test");
//        SQLiteDatabaseLockedException
//        Pair
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
           SensorsDataAPI.sharedInstance(this, saConfigOptions);
//            SensorsDataAPI.startWithConfigOptions(this, saConfigOptions);

            // 初始化 SDK 后，可以获取应用名称设置为公共属性
            JSONObject properties = new JSONObject();
            properties.put("app_name", getAppName(context));
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);

            SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
            SensorsDataAPI.sharedInstance().enableHeatMap();

            // 初始化 SDK 后，开启 RN 页面控件点击事件的自动采集
            SensorsDataAPI.sharedInstance().enableReactNativeAutoTrack();
            SensorsDataAPI.sharedInstance().trackAppCrash();


//            WebView webView = new MyWebView(this);
//            webView.loadUrl("http://192.168.1.64:6789");

            // 开启加密
//            SensorsDataAPI.sharedInstance().enableEncrypt(true);
//
//            // 设置加密 SecretKey
//            SensorsDataAPI.sharedInstance().persistentSecretKey(new SensorsDataEncrypt.PersistentSecretKey() {
//
//                @Override
//                public void saveSecretKey(SensorsDataEncrypt.SecreteKey secreteKey) {
//                    // 这里用于存储密钥，如果采用默认密钥不动态更新，可以不实现。
//                }
//
//                @Override
//                public SensorsDataEncrypt.SecreteKey loadSecretKey() {
//                    // 这里是 SDK 读取密钥，必须实现。SDK 通过该接口读取密钥用于加密
//                    return new SensorsDataEncrypt.SecreteKey("XXX",1);
//                }
//            });

            SensorsDataAPI.sharedInstance().setTrackEventCallBack(new SensorsDataTrackEventCallBack() {
                @Override
                public boolean onTrackEvent(String eventName, JSONObject eventProperties) {

                    Log.e("qqqqqqq","事件名:"+eventName +"  属性:"+eventProperties);
                    return true;
                }
            });
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
