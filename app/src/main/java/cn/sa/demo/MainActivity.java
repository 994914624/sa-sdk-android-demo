package cn.sa.demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import org.json.JSONException;
import org.json.JSONObject;
import cn.sa.demo.activity.ClickActivity;
import cn.sa.demo.activity.FragmentActivity;
import cn.sa.demo.activity.ViewActivity;
import cn.sa.demo.activity.WebViewActivity;
import cn.sa.demo.utils.AccessibilityUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String TAG = "SADemo.MainActivity";
    private TextView tvAssessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("demo");
        // TODO trackInstallation 记录激活事件、并做渠道追踪
        trackInstallation(0);
        initView();
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
    protected void onResume() {
        super.onResume();
        String assessibilityStatus = "(已关闭)";
        if(AccessibilityUtil.isAccessibilityServiceRunning(getApplicationContext())){
            assessibilityStatus ="(已开启)";
        }
        tvAssessibility.setText(String.format("无障碍：%s", assessibilityStatus));
    }

    private void initView() {
        findViewById(R.id.tv_main_track).setOnClickListener(this);
        findViewById(R.id.tv_main_profileSet).setOnClickListener(this);
        findViewById(R.id.tv_main_login).setOnClickListener(this);
        findViewById(R.id.tv_main_baseView).setOnClickListener(this);
        findViewById(R.id.tv_main_clickType).setOnClickListener(this);
        findViewById(R.id.tv_main_fragment).setOnClickListener(this);
        findViewById(R.id.tv_main_appH5).setOnClickListener(this);
        tvAssessibility = findViewById(R.id.tv_main_assessibility);
        tvAssessibility.setOnClickListener(this);
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
            properties.put("ProductID", 123456);                    // 设置商品ID
            properties.put("ProductCatalog", "Laptop Computer");    // 设置商品类别
            properties.put("IsAddedToFav", false);                  // 是否被添加到收藏夹
            // 数据发送到另一个项目
            properties.put("$project","YangYang")
                    .put("$token","95c73ae661f85aa0");
            // 埋点触发 "ViewProduct" 事件
          //  SensorsDataAPI.sharedInstance().trackWithProject("ViewProduct", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
}
