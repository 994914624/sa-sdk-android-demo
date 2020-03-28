package cn.sa.demo.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.Set;

/**
 * Created by yzk on 2019-10-26
 */

public class ToolBox {

    static {

    }

    /**
     * 复制内容到剪切板
     */
    public static boolean copy(String copyStr,Context context) {
        try {
            // 获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 开启 debug 模式、点击图、可视化埋点
     * <p>
     * 扫码拿到的链接
     * https://sdktest.cloud.sensorsdata.cn/h5/debugmode/index.html?connectType=debugmode&info_id=z5s7953lgi8&protocal=sa9930c860&project=yangzhankun
     * <p>
     * // debug 模式 intent 携带的参数
     * sa9930c860://debugmode?info_id=z5s7953lgi8
     * // 可视化全埋点 intent 携带的参数
     * sa9930c860://visualized?feature_code=sa9930c860mRArSnvbFf&url=https%3A%2F%2Fsdktest.cloud.sensorsdata.cn%2Fapi%2Fheat_map%2Fupload%3Fproject%3Dyangzhankun
     * // 点击图 intent 携带的参数
     * sa9930c860://heatmap?feature_code=sa9930c860mRArSnvbFf&url=https%3A%2F%2Fsdktest.cloud.sensorsdata.cn%2Fapi%2Fheat_map%2Fupload%3Fproject%3Dyangzhankun
     */
    public static boolean openDebugModeOrHeatMap(String result, Activity activity) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Uri uri = Uri.parse(result);
            Set<String> keys = uri.getQueryParameterNames();
            // debug 模式
            if (keys.contains("connectType") && "debugmode".equals(uri.getQueryParameter("connectType"))) {
                String intentData = String.format("%s://debugmode?info_id=%s", uri.getQueryParameter("protocal"), uri.getQueryParameter("info_id"));
                intent.setData(Uri.parse(intentData));
                // 打开指定的 App
                activity.startActivity(intent);
                activity.finish();
                return true;
            }
            // 可视化埋点、点击图
            if (keys.contains("feature_code") && keys.contains("protocal")) {
                //拼接 heat map api & scanning api
                String heatMapApi = String.format("%s://%s/api/heat_map/upload?project=%s", uri.getScheme(), uri.getHost(), uri.getQueryParameter("project"));
                final String scanningApi = String.format("%s://%s/api/heat_map/scanning?project=%s", uri.getScheme(), uri.getHost(), uri.getQueryParameter("project"));
                // heatmap or visualized
                String host = uri.getQueryParameter("connectType") == null?"heatmap":"visualized";
                String intentData = String.format("%s://%s?feature_code=%s&url=%s", uri.getQueryParameter("protocal"),host, uri.getQueryParameter("feature_code"), URLEncoder.encode(heatMapApi, "UTF-8"));
                intent.setData(Uri.parse(intentData));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 发出 scanning 请求
                        HttpPOST.submitPostData(scanningApi, uri.getQueryParameter("feature_code"));
                    }
                }).start();
                // 打开指定的 App
                activity.startActivity(intent);
                activity.finish();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存储数据到 SP
     */
    public static void saveServerUrlToSP(Context context, String key,String url){
        if(context == null|| TextUtils.isEmpty(key)|| TextUtils.isEmpty(url))return;
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        sp.edit().putString(key,url).apply();
    }

    public static String  getServerUrlFromSP(Context context, String key){
        if(context == null|| TextUtils.isEmpty(key))return "";
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
}
