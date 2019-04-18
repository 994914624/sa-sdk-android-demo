package cn.sa.demo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void onCreate() {
        super.onCreate();
        // 在 Application 的 onCreate 初始化 SDK
        initSensorsDataSDK(this);
    }

    /**
     * 初始化 SDK 、设置公共属性、开启自动采集
     */
    private void initSensorsDataSDK(Context context) {
        try {

            isDebugMode = isDebugMode(this);

            // 初始化 SDK
            SensorsDataAPI.sharedInstance(this,new SAConfigOptions(isDebugMode?SA_SERVER_URL_DEBUG:SA_SERVER_URL_RELEASE));

            // 初始化SDK后，获取应用名称设置为公共属性
            JSONObject properties = new JSONObject();
            properties.put("app_name", getAppName(context));
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);

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

            // 开启调试日志
            SensorsDataAPI.sharedInstance().enableLog(isDebugMode);

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
