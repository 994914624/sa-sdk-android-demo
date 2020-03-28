package cn.sa.demo.utils;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by yzk on 2020-01-02
 */

public class WeChatXposedHookUtil implements IXposedHookLoadPackage {

    private static final String TAG = "WeChatXposedHookUtil...";
    private static final String TU_LING_API = "http://openapi.tuling123.com/openapi/api/v2";
    private static ExecutorService thread = Executors.newSingleThreadExecutor();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null || !"com.tencent.mm".equals(lpparam.packageName)) {
            return;
        }
        Log.e(TAG, "hook 微信 handleLoadPackage: -----> " + lpparam.packageName);
        // hook 微信数据库，insert 的消息
        // https://tencent.github.io/wcdb/references/android/reference/com/tencent/wcdb/database/SQLiteDatabase.html#insert(java.lang.String,%20java.lang.String,%20android.content.ContentValues)
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase",
                lpparam.classLoader,
                "insert",
                String.class,
                String.class,
                ContentValues.class,
                new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                try {
                    if (param == null) return;
                    if (param.args == null) return;
                    String table = (String) param.args[0];
                    ContentValues values = (ContentValues) param.args[2];
                    // message 是聊天记录表
                    if (!"message".equals(table)) return;
                    if (values == null) return;
                    String talker = values.getAsString("talker");
                    String content = values.getAsString("content");
                    if (content == null) return;
                    Log.e(TAG, "hook 收到的消息为:-----> " + content);

                    // 回复消息
                    replyMessage(talker, content, lpparam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // hook 自己发送的消息
        XposedHelpers.findAndHookMethod(
                "com.tencent.mm.ui.chatting.p",
                lpparam.classLoader,
                "TQ",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String str = (String) param.args[0];
                        Log.e(TAG, "发送的消息内容为: " + str);
                    }
                });
    }

    /*
     * 调用微信 API 发出消息
     */
    private void callWXAPISendMessage(String talker, String content, XC_LoadPackage.LoadPackageParam lpparam) {
        Class clz_h = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", lpparam.classLoader);
        Object message = XposedHelpers.newInstance(clz_h, talker, content, 1);
        Class clz_aw = XposedHelpers.findClass("com.tencent.mm.model.aw", lpparam.classLoader);
        Object clz_p = XposedHelpers.callStaticMethod(clz_aw, "Vs");
        XposedHelpers.callMethod(clz_p, "a", message, 0);
    }

    /**
     * 回复消息
     */
    private void replyMessage(String talker, String content, XC_LoadPackage.LoadPackageParam lpparam) {
        if(content.contains("<msg>")){
            callWXAPISendMessage(talker, "不许发图！", lpparam);
            return;
        }
        if(content.contains("@")){
            callWXAPISendMessage(talker, "好的，稍等！", lpparam);
            return;
        }
        // 搞个图灵 API 回复
        thread.execute(new Runnable() {
            @Override
            public void run() {
                String tulingReply = getTulingReply(content);
                if (!TextUtils.isEmpty(tulingReply)) {
                    // 调用微信接口发出消息
                    callWXAPISendMessage(talker, tulingReply, lpparam);
                }
            }
        });
    }

    /**
     * 获取图灵机器的回复
     */
    private String getTulingReply(String content) {
        try {
            String payload = "{\n" +
                    "    \"perception\":{\n" +
                    "        \"inputText\":{\n" +
                    "            \"text\":\"" + content + "\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"userInfo\":{\n" +
                    "        \"apiKey\":\"a7412af37b014862a45859a3f2d44398\",\n" +
                    "        \"userId\":\"123\"\n" +
                    "    }\n" +
                    "}";
            String callback = HttpPOST.submitPostData(TU_LING_API, payload);
            JSONObject results = (JSONObject) new JSONObject(callback).optJSONArray("results").opt(0);
            Log.e(TAG, "图灵回复内容为: " + callback);
            return results.optJSONObject("values").optString("text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
