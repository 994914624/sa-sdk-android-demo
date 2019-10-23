package cn.sa.demo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.google.zxing.Result;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.vondear.rxfeature.activity.ActivityScanerCode;
import com.vondear.rxfeature.module.scaner.OnRxScanerListener;

import cn.sa.demo.R;
import cn.sa.demo.custom.MyWebView;

/**
 * 打通 App 和 H5
 */
public class WebViewActivity extends BaseActivity implements OnRxScanerListener {

    final static String TAG = "SADemo.WebViewActivity";
    private WebView webView = null;
    private static String WEB_URL = "";
    EditText editText;
    TextView runText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        this.setTitle("打通 App 和 H5 & 注入 JS SDK");
        initInputActionBar();
        initWebView();
    }

    /**
     * 输入框
     */
    private void initInputActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            View view = LayoutInflater.from(this).inflate(R.layout.action_bar_input, null);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            actionBar.setCustomView(view, lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //输入框
            editText = view.findViewById(R.id.edt_action_bar);
            view.findViewById(R.id.tv_action_bar).setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    WEB_URL = editText.getText() + "";
                    if (!TextUtils.isEmpty(WEB_URL)) {
                        webView.loadUrl(WEB_URL);
                        runText.setText(String.format("当前 URL：%s", WEB_URL));
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
                    ActivityScanerCode.setScanerListener(WebViewActivity.this);
                    startActivity(new Intent(WebViewActivity.this, QRCodeActivity.class));
                }
            });
        }
        runText = findViewById(R.id.tv_web_run_text);

    }

    @Override
    public void onSuccess(String type, Result result) {
        WEB_URL = result.getText() + "";
        if (!TextUtils.isEmpty(WEB_URL)) {
            webView.loadUrl(WEB_URL);
            runText.setText(String.format("当前 URL：%s", WEB_URL));
            Toast.makeText(this, "扫码成功", Toast.LENGTH_SHORT).show();
            Log.i(TAG, WEB_URL);
        }
    }

    @Override
    public void onFail(String type, String message) {

    }

    /**
     * 打通 App 和 H5 文档：https://www.sensorsdata.cn/manual/app_h5.html
     */
    private void initWebView() {
        webView = new MyWebView(this);
        ViewGroup viewGroup = findViewById(R.id.webview);
        viewGroup.addView(webView);
        // TODO 打通 App 和 H5
        // 打通 App 和 H5，enableVerify 指定为 true 开启数据接收地址 URL 安全校验
        //SensorsDataAPI.sharedInstance().showUpWebView(webView,false,false);

        // 反射调用 showUpWebView
        try {
            Class<?> clazz = Class.forName("com.sensorsdata.analytics.android.sdk.SensorsDataAPI");
            java.lang.reflect.Method sharedInstance = clazz.getMethod("sharedInstance");
            java.lang.reflect.Method showUpWebView = clazz.getMethod("showUpWebView", android.webkit.WebView.class, boolean.class, boolean.class);
            Object sdkInstance = sharedInstance.invoke(null);
            showUpWebView.invoke(sdkInstance, webView, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 这里使用一个本地 html 来模拟验证 App 和 H5打通
        WEB_URL = "file:///android_asset/h5.html";
        webView.loadUrl(WEB_URL);
        int a = (int) (1000 * Math.random());

        // 我们这边需要调试一下你们 App 内的 H5 页面，麻烦你们 Android 开发同事，在 WebView 的地方调用下面代码，打一个 Debug apk 包发过来，谢谢。
        // 允许 chrome://inspect 调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // TODO 注入 JavaScript SDK
                // 可以在这里，注入 JavaScript SDK
                //webView.loadUrl("javascript:"+INJECT_JS_SDK_CODE);
                Log.e("QQQ",""+url);
                runText.setText(String.format("当前 URL：%s", url));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("QQQQQ",""+url);
                if(url!=null &&(url.contains("https://")||url.contains("http://"))){
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    /**
     * 注入 JavaScript SDK
     */
    private final static String INJECT_JS_SDK_CODE =
            "(function(para) {\n" +
                    "  var p = para.sdk_url, n = para.name, w = window, d = document, s = 'script',x = null,y = null;\n" +
                    "  if(typeof(w['sensorsDataAnalytic201505']) !== 'undefined') {\n" +
                    "      return false;\n" +
                    "  }\n" +
                    "  w['sensorsDataAnalytic201505'] = n;\n" +
                    "  w[n] = w[n] || function(a) {return function() {(w[n]._q = w[n]._q || []).push([a, arguments]);}};\n" +
                    "  var ifs = ['track','quick','register','registerPage','registerOnce','trackSignup', 'trackAbtest', 'setProfile','setOnceProfile','appendProfile', 'incrementProfile', 'deleteProfile', 'unsetProfile', 'identify','login','logout','trackLink','clearAllRegister','getAppStatus'];\n" +
                    "  for (var i = 0; i < ifs.length; i++) {\n" +
                    "    w[n][ifs[i]] = w[n].call(null, ifs[i]);\n" +
                    "  }\n" +
                    "  if (!w[n]._t) {\n" +
                    "    x = d.createElement(s), y = d.getElementsByTagName(s)[0];\n" +
                    "    x.async = 1;\n" +
                    "    x.src = p;\n" +
                    "    x.setAttribute('charset','UTF-8');\n" +
                    "    y.parentNode.insertBefore(x, y);\n" +
                    "    w[n].para = para;\n" +
                    "  }\n" +
                    "})({\n" +
                    "  sdk_url: 'http://static.sensorsdata.cn/sdk/1.13.2/sensorsdata.min.js',\n" +
                    "  heatmap_url: 'http://static.sensorsdata.cn/sdk/1.13.2/heatmap.min.js',\n" +
                    "  name: 'sensors',\n" +
                    "  use_app_track:true,\n" +
                    "  server_url: 'http://sdk-test.datasink.sensorsdata.cn/sa?token=95c73ae661f85aa0&project=yangzhankun',\n" +
                    "  heatmap:{}\n" +
                    "});\n" +
                    "sensors.quick('autoTrack');";

}
