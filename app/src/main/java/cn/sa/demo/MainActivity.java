package cn.sa.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.Result;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPIProxy;
import com.vondear.rxfeature.activity.ActivityScanerCode;
import com.vondear.rxfeature.module.scaner.OnRxScanerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import cn.sa.demo.activity.ClickActivity;
import cn.sa.demo.activity.FragmentActivity;
import cn.sa.demo.activity.QRCodeActivity;
import cn.sa.demo.activity.ViewActivity;
import cn.sa.demo.activity.WebViewActivity;
import cn.sa.demo.custom.MyWebView;
import cn.sa.demo.custom.SensorsDataUtil;
import cn.sa.demo.data.MySQLiteOpenHelper;
import cn.sa.demo.utils.AccessibilityUtil;
import cn.sa.demo.utils.LuckyMoneyUtil;
import cn.sa.demo.utils.TestHandler;
import cn.sa.demo.utils.ToolBox;
import cn.sa.demo.utils.hook.ClickProxy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnRxScanerListener {

    final static String TAG = "SADemo.MainActivity";
    private TextView tvAssessibility;
    private static ArrayList<String> list = new ArrayList<>(Arrays.asList(
            "android##widget",
            "android##support##v7##widget",
            "android##support##design##widget"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("demo");
        TextView textView = findViewById(R.id.dis);
        textView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){

            @Override
            public void onViewAttachedToWindow(View v) {
                Log.i(TAG,"--- onViewAttachedToWindow ---: "+v.hashCode());
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.i(TAG,"--- onViewDetachedFromWindow ---: "+v.hashCode());
            }
        });

        // 设置一下缓存的地址
        String serverUrl = ToolBox.getServerUrlFromSP(getApplicationContext(),"serverUrl");
        if(!TextUtils.isEmpty(serverUrl)){
            SensorsDataAPI.sharedInstance().setServerUrl(serverUrl);
        }

        // TODO trackInstallation 记录激活事件、并做渠道追踪
        trackInstallation(0);
        initView();
        initInputActionBar();



    }



    EditText editText;
    private void initInputActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            View view = LayoutInflater.from(this).inflate(R.layout.action_bar_input,null);
            ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            actionBar.setCustomView(view,lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            //输入框
            editText = view.findViewById(R.id.edt_action_bar);
            editText.setHint("可更换数据接收地址或扫码填入");
            TextView textView = view.findViewById(R.id.tv_action_bar);
            textView.setText("设置");
            textView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    String input = editText.getText()+"";
                    if(!TextUtils.isEmpty(input)){
                        if(input.contains("https://")||input.contains("http://")){
                            SensorsDataAPI.sharedInstance().setServerUrl(input);
                            // 保存到 SP
                            ToolBox.saveServerUrlToSP(getApplicationContext(),"serverUrl",input);
                            runText.setText(String.format("当前数据接收地址：%s", getServerUrl()));
                            Toast.makeText(MainActivity.this,"数据接收 URL，设置为："+input,Toast.LENGTH_SHORT).show();
                        }
                        // 隐藏软键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                }
            });
            view.findViewById(R.id.scan_action_bar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 扫码的回调
                    ActivityScanerCode.setScanerListener(MainActivity.this);
                    startActivity(new Intent(MainActivity.this, QRCodeActivity.class));
                }
            });

        }
    }

    @Override
    public void onSuccess(String type, Result result) {
        String input = result+"";
        if(!TextUtils.isEmpty(input)){
            Log.i(TAG,result.getText()+"");
            if(input.contains("https://")||input.contains("http://")){
                if(ToolBox.openDebugModeOrHeatMap(input,MainActivity.this)){
                    return;
                }
                // 更新数据接收地址
                SensorsDataAPI.sharedInstance().setServerUrl(input);
                ToolBox.saveServerUrlToSP(getApplicationContext(),"serverUrl",input);
                // to 剪切板
                ToolBox.copy(input,this);
                runText.setText(String.format("当前数据接收地址：%s", getServerUrl()));
                Toast.makeText(this,"已复制到剪切板 & 数据接收 URL 设置为："+input,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFail(String type, String message) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 第一次正常启动，中间出现过 crash ，之后 App 又正常了
        // WebView 页，闪屏页，Main。
//        startActivity(new Intent(MainActivity.this, FragmentActivity.class));
//        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        ClickProxy.hookActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this,"首页","main_xxx");


        String assessibilityStatus = "(已关闭)";
        if(AccessibilityUtil.isAccessibilityServiceRunning(getApplicationContext())){
            assessibilityStatus ="(已开启)";
        }
        tvAssessibility.setText(String.format("无障碍：%s", assessibilityStatus));
    }
    TextView runText;
    private void initView() {
        findViewById(R.id.tv_main_track).setOnClickListener(this);
        findViewById(R.id.tv_main_profileSet).setOnClickListener(this);
        findViewById(R.id.tv_main_login).setOnClickListener(this);
        findViewById(R.id.tv_main_baseView).setOnClickListener(this);
        findViewById(R.id.tv_main_clickType).setOnClickListener(this);
        findViewById(R.id.tv_main_fragment).setOnClickListener(this);
        findViewById(R.id.tv_main_appH5).setOnClickListener(this);
        runText = findViewById(R.id.tv_main_run_text);
        runText.setText(String.format("当前数据接收地址：%s", getServerUrl()));
        tvAssessibility = findViewById(R.id.tv_main_assessibility);
        tvAssessibility.setOnClickListener(this);
        tvAssessibility.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LuckyMoneyUtil.popSeekBar(MainActivity.this);
                return false;
            }
        });
        TestHandler.recycle();
    }

    public String getServerUrl() {
        // 反射调用 getServerUrl
        try {
            Class<?> clazz = Class.forName("com.sensorsdata.analytics.android.sdk.SensorsDataAPI");
            java.lang.reflect.Method sharedInstance = clazz.getMethod("sharedInstance");
            java.lang.reflect.Method getServerUrl = clazz.getDeclaredMethod("getServerUrl");
            getServerUrl.setAccessible(true);
            Object sdkInstance = sharedInstance.invoke(null);
            return (String)getServerUrl.invoke(sdkInstance);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 示例：代码埋点
     * 文档：https://www.sensorsdata.cn/manual/android_sdk.html#3-追踪事件
     */
    private void track() {
        Toast.makeText(MainActivity.this, "代码埋点", Toast.LENGTH_SHORT).show();
        // TODO 代码埋点
        try {
            JSONObject properties = new JSONObject();

            properties.put("ProductID", 18345019902D);                    // 设置商品ID
            properties.put("ProductCatalog", "Laptop Computer");    // 设置商品类别
            properties.put("IsAddedToFav", false);                  // 是否被添加到收藏夹
            // 数据发送到另一个项目
//            properties.put("$project","YangYang")
//                    .put("$token","95c73ae661f85aa0");
            // 埋点触发 "ViewProduct" 事件
            SensorsDataAPI.sharedInstance().track("ViewProduct", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        // 计时开始
//        SensorsDataUtil.onPageStart("首页");
//        // 计时结束
//        SensorsDataUtil.onPageEnd("首页");

    }

    /**
     * 示例：设置用户属性
     * 文档：https://www.sensorsdata.cn/manual/android_sdk.html#5-设置用户属性
     */
    private void profileSet() {
        Toast.makeText(MainActivity.this, "设置用户属性", Toast.LENGTH_SHORT).show();
        // TODO 设置用户属性
        try {
            JSONObject properties = new JSONObject();
            properties.put("Sex", "Male");
            properties.put("Age", 18);
            // 设定用户属性 "Sex" 为 "Male"， "Age" 为 18
            SensorsDataAPI.sharedInstance().profileSet(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        int radom = (int) (Math.random()*1000);
//        SensorsDataAPI.sharedInstance().identify("匿名 @:"+radom);
//        SensorsDataUtil.onPageEnd("首页");

    }

    /**
     * 示例：用户 ID 关联
     * 文档：https://www.sensorsdata.cn/manual/android_sdk.html#41-匿名-id-和登录-id-关联
     */
    private void login() {
        Toast.makeText(MainActivity.this, "注册成功/登录成功", Toast.LENGTH_SHORT).show();
        // TODO 注册成功/登录成功 时，调用 login 传入登录ID
        int radom = (int) (Math.random()*1000);
        SensorsDataAPI.sharedInstance().login("登录 @:"+radom);
    }


    /**
     * @param type 标记，传 0 时会走申请权限的代码，第一次调用 trackInstallation 传 0
     *             记录激活事件、并做渠道追踪。
     *             此方法，在第一个 Activity 的 onCreate 中调用，且要在 onRequestPermissionsResult 中调用。
     *             <p>
     *             文档：https://www.sensorsdata.cn/manual/android_sdk.html#32-记录激活事件
     */
    private void trackInstallation(int type) {
        try {
            // 申请 READ_PHONE_STATE 权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && type == 0) {
                if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, 100);
                    return;
                }
            }
            // 调用 trackInstallation 触发激活事件,做渠道追踪。
            JSONObject properties = new JSONObject();
            properties.put("DownloadChannel", "你们具体渠道包的值");//这里的 DownloadChannel 负责记录下载商店的渠道。这里传入具体应用商店包的标记。
            //这里激活事件取名为 AppInstall（SDK内部已做处理，只会在第一次调用时触发激活事件）
            SensorsDataAPI.sharedInstance().trackInstallation("AppInstall", properties);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // TODO 6.0 以上，申请权限结果回调时，记录激活事件、并做渠道追踪，这里 type 传 100。
            trackInstallation(100);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_track:
                track();//代码埋点
                break;
            case R.id.tv_main_profileSet:
                profileSet();//设置用户属性
                break;
            case R.id.tv_main_login:
                login();//注册成功/登录成功
                break;
            case R.id.tv_main_baseView:
                openViewActivity();//常用控件
                break;
            case R.id.tv_main_clickType:
                openClickActivity();//点击方式
                break;
            case R.id.tv_main_fragment:
                openFragmentActivity();//Fragment
                break;
            case R.id.tv_main_appH5:
                openWebViewActivity();//打通 App 与 H5
                break;
            case R.id.tv_main_assessibility:
                openAccessibilityServiceSettings();//打开无障碍
                break;
            default:
                break;
        }
    }

    /**
     * 打开 ViewActivity
     *
     */
    private void openViewActivity() {
        startActivity(new Intent(MainActivity.this, ViewActivity.class));
    }

    /**
     * 打开 ClickActivity
     *
     */
    private void openClickActivity() {
        startActivity(new Intent(MainActivity.this, ClickActivity.class));
    }

    /**
     * 打开 FragmentActivity
     *
     */
    private void openFragmentActivity() {
        startActivity(new Intent(MainActivity.this, FragmentActivity.class));
    }

    /**
     * 打开 WebViewActivity
     */
    private void openWebViewActivity() {
        startActivity(new Intent(MainActivity.this, WebViewActivity.class));
    }

    /**
     * 打开无障碍
     */
    public  void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            //悬浮窗权限
            if (Build.VERSION.SDK_INT >= 23) {
                if (! Settings.canDrawOverlays(this)) {
                    Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent2,123);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            try {
                int x= (int) event.getRawX();
                int y= (int) event.getRawY();
                JSONObject jsonObject = new JSONObject();

                long now = SystemClock.elapsedRealtime();
                long interval = now - App.ADB_TIME;
                // 此次点击 - 上次点击 > 24小时，则是首次点击
                if(interval> 24*60*60*1000){
                    jsonObject.put("xy",String.format("%s %s|%s",x,y,""));
                    Log.d("adbEvent", String.format("onTouchEvent %s %s|%s", x, y,""));
                } else {
                    jsonObject.put("xy",String.format("%s %s|%s",x,y,interval));
                    Log.d("adbEvent", String.format("onTouchEvent %s %s|%s", x, y,interval));
                }
                // 更新上次点击时间戳
                App.ADB_TIME = now;
//                SensorsDataAPI.sharedInstance().track("adbEvent",jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
