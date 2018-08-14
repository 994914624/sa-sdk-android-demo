package cn.sa.demo;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "SADemo.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("启动页");
        // TODO trackInstallation 记录激活事件、并做渠道追踪，这里 type 传 0
        trackInstallation(0);
        track();// 代码埋点示例
        login();// 用户 ID 关联示例
        profileSet();// 设置用户属性示例
        openWebViewActivity();
    }



    /**
     * 示例：代码埋点
     * 文档：https://www.sensorsdata.cn/manual/android_sdk.html#3-追踪事件
     */
    private void track() {
        findViewById(R.id.btn_main_track).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "代码埋点", Toast.LENGTH_SHORT).show();
                // TODO 代码埋点
                try {
                    JSONObject properties = new JSONObject();
                    properties.put("ProductID", 123456);                    // 设置商品ID
                    properties.put("ProductCatalog", "Laptop Computer");    // 设置商品类别
                    properties.put("IsAddedToFav", false);                  // 是否被添加到收藏夹
                    // 埋点触发 "ViewProduct" 事件
                    SensorsDataAPI.sharedInstance().track("ViewProduct", properties);
                    //SensorsDataAPI.sharedInstance().setServerUrl("http://sdk-test.cloud.sensorsdata.cn:8006/sa?project=yangzhankun&token=95c73ae661f85aa0");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 示例：用户 ID 关联
     * 文档：https://www.sensorsdata.cn/manual/android_sdk.html#41-用户注册
     */
    private void login() {
        findViewById(R.id.btn_main_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "注册成功/登录成功", Toast.LENGTH_SHORT).show();
                // TODO 注册成功/登录成功 时，调用 login 传入登录ID
                SensorsDataAPI.sharedInstance().login("你们服务端分配给用户的具体的登录ID");
            }
        });
    }

    /**
     * 示例：设置用户属性
     * 文档：https://www.sensorsdata.cn/manual/android_sdk.html#5-设置用户属性
     */
    private void profileSet() {
        findViewById(R.id.btn_main_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // TODO 6.0 以上，申请权限结果回调时，记录激活事件、并做渠道追踪，这里 type 传 100。
            trackInstallation(100);
        }
    }

    /**
     * @param type 标记，传 0 时会走申请权限的代码，第一次调用 trackInstallation 传 0
     *             记录激活事件、并做渠道追踪。
     *             此方法，在第一个 Activity 的 onCreate 中调用，且要在 onRequestPermissionsResult 中调用。
     *             <p>
     *             App 渠道追踪文档：https://www.sensorsdata.cn/manual/app_channel_tracking.html
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

    /**
     * 打开 WebViewActivity
     */
    private void openWebViewActivity() {
        findViewById(R.id.btn_main_app_h5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });
    }
}
