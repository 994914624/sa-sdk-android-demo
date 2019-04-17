package cn.sa.demo.activity;


import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import cn.sa.demo.R;

/**
 * 打通 App 和 H5
 */
public class WebViewActivity extends BaseActivity {

    final static String TAG = "SADemo.WebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        this.setTitle("打通 App 和 H5 & 注入 JS SDK");
        initWebView();
    }


    /**
     * 打通 App 和 H5 文档：https://www.sensorsdata.cn/manual/app_h5.html
     */
    private WebView webView = null;
    private void initWebView() {
        webView = new WebView(this);
        ViewGroup viewGroup = findViewById(R.id.webview);
        viewGroup.addView(webView);
        // TODO 打通 App 和 H5
        // 打通 App 和 H5，enableVerify 指定为 true 开启数据接收地址 URL 安全校验
        SensorsDataAPI.sharedInstance().showUpWebView(webView,false,true);
        webView.loadUrl("file:///android_asset/index.html");// 这里使用一个本地 html 来模拟验证 App 和 H5打通
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // TODO 注入 JavaScript SDK
                // 可以在这里，注入 JavaScript SDK
                //webView.loadUrl("javascript:"+INJECT_JS_SDK_CODE);
            }
        });
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
    private final static String INJECT_JS_SDK_CODE ="(function(para) {\n" +
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
