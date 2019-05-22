package cn.sa.demo.utils_G;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsSeekBar;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.growingio.android.sdk.collection.AbstractGrowingIO;
import com.growingio.android.sdk.collection.Constants;
import com.growingio.android.sdk.collection.CoreInitialize;
import com.growingio.android.sdk.collection.GConfig;
import com.growingio.android.sdk.models.ActionStruct;
import com.growingio.android.sdk.models.ViewAttrs;
import com.growingio.android.sdk.models.ViewNode;
import com.growingio.android.sdk.utils.ClassExistHelper;
import com.growingio.android.sdk.utils.CustomerInterface.Encryption;
import com.growingio.android.sdk.utils.GJSONStringer;
import com.growingio.android.sdk.utils.LogUtil;
import com.growingio.android.sdk.utils.ThreadUtils;
import com.growingio.android.sdk.utils.WebViewUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

@TargetApi(12)
public class Util {
    public static final Matcher ID_PATTERN_MATCHER = Pattern.compile("#[\\+\\.a-zA-Z0-9_-]+").matcher("");
    private static final int MAX_CONTENT_LENGTH = 100;
    private static Set<Integer> mBlackListId;
    private static SparseArray<String> mIdMap;
    private static LruCache<Class, String> sClassNameCache = new LruCache(100);

    public static String getSimpleClassName(Class clazz) {
        String name = (String) sClassNameCache.get(clazz);
        if (TextUtils.isEmpty(name)) {
            name = clazz.getSimpleName();
            if (TextUtils.isEmpty(name)) {
                name = ViewNode.ANONYMOUS_CLASS_NAME;
            }
            synchronized (Util.class) {
                sClassNameCache.put(clazz, name);
            }
            ClassExistHelper.checkCustomRecyclerView(clazz, name);
        }
        return name;
    }

    public static String getViewContent(View view, String bannerText) {
        String value = "";
        View selected =null;
        Object contentTag = view.getTag(AbstractGrowingIO.GROWING_CONTENT_KEY);
        if (contentTag != null) {
            value = String.valueOf(contentTag);
        } else {
            if (view instanceof EditText) {
                if (!(view.getTag(AbstractGrowingIO.GROWING_TRACK_TEXT) == null || isPasswordInputType(((EditText) view).getInputType()))) {
                    CharSequence sequence = getEditTextText((EditText) view);
                    value = sequence == null ? "" : sequence.toString();
                }
            } else if (view instanceof RatingBar) {
                value = String.valueOf(((RatingBar) view).getRating());
            } else if (view instanceof Spinner) {
                Object item = ((Spinner) view).getSelectedItem();
                if (item instanceof String) {
                    value = (String) item;
                } else {
                    selected = ((Spinner) view).getSelectedView();
                    if ((selected instanceof TextView) && ((TextView) selected).getText() != null) {
                        value = ((TextView) selected).getText().toString();
                    }
                }
            } else if (view instanceof SeekBar) {
                value = String.valueOf(((SeekBar) view).getProgress());
            } else if (view instanceof RadioGroup) {
                RadioGroup group = (RadioGroup) view;
                selected = group.findViewById(group.getCheckedRadioButtonId());
                if ((selected instanceof RadioButton) && ((RadioButton) selected).getText() != null) {
                    value = ((RadioButton) selected).getText().toString();
                }
            } else if (view instanceof TextView) {
                if (((TextView) view).getText() != null) {
                    value = ((TextView) view).getText().toString();
                }
            } else if (view instanceof ImageView) {
                if (!TextUtils.isEmpty(bannerText)) {
                    value = bannerText;
                }
            } else if (((view instanceof WebView) && !WebViewUtil.isDestroyed((WebView) view)) || ClassExistHelper.instanceOfX5WebView(view)) {
//                String url = view.getTag(AbstractGrowingIO.GROWING_WEB_VIEW_URL);
//                if (url == null) {
//                    if (!com.growingio.android.sdk.utils.ThreadUtils.runningOnUiThread()) {
//                        postCheckWebViewStatus(view);
//                        throw new RuntimeException("WebView getUrl must called on UI Thread");
//                    } else if (view instanceof WebView) {
//                        url = ((WebView) view).getUrl();
//                    } else {
//                        url = ((com.tencent.smtt.sdk.WebView) view).getUrl();
//                    }
//                }
//                if (url instanceof String) {
//                    value = url;
//                }
            }
            if (TextUtils.isEmpty(value)) {
                if (bannerText != null) {
                    value = bannerText;
                } else if (view.getContentDescription() != null) {
                    value = view.getContentDescription().toString();
                }
            }
        }
        return truncateViewContent(value);
    }

    private static void postCheckWebViewStatus(final View webView) {
        LogUtil.d("GIO.Util", "postCheckWebViewStatus: ", webView);
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                String url = null;
                if (webView instanceof WebView) {
                    url = ((WebView) webView).getUrl();
                } else if (ClassExistHelper.instanceOfX5WebView(webView)) {
                    //url = ((com.tencent.smtt.sdk.WebView) webView).getUrl();
                }
                if (url != null) {
                    webView.setTag(AbstractGrowingIO.GROWING_WEB_VIEW_URL, url);
                }
            }
        });
    }

    public static String truncateViewContent(String value) {
        if (value == null) {
            return "";
        }
        if (!TextUtils.isEmpty(value) && value.length() > 100) {
            value = value.substring(0, 100);
        }
        return encryptContent(value);
    }

    public static boolean isListView(View view) {
        return (view instanceof AdapterView) || ClassExistHelper.instanceOfAndroidXRecyclerView(view) || ClassExistHelper.instanceOfAndroidXViewPager(view) || ClassExistHelper.instanceOfSupportRecyclerView(view) || ClassExistHelper.instanceOfSupportViewPager(view);
    }

    public static boolean isInstant(JSONObject elem, ArrayList<ViewAttrs> filters, String domain) throws JSONException {
        Iterator it = filters.iterator();
        while (it.hasNext()) {
            ViewAttrs filter = (ViewAttrs) it.next();
            if (filter.webElem && filter.domain.equals(domain)) {
                if ((filter.xpath == null || isIdentifyXPath(filter.xpath, elem.getString("x"))) && ((filter.index == null || filter.index.equals(String.valueOf(elem.optInt("idx", -1)))) && ((filter.content == null || filter.content.equals(elem.optString("v"))) && (filter.href == null || (filter.href.equals(elem.optString("h")) && (filter.query == null || filter.query.equals(elem.optString("q")))))))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInstant(ActionStruct elem, ArrayList<ViewAttrs> filters) {
        Iterator it = filters.iterator();
        while (it.hasNext()) {
            ViewAttrs filter = (ViewAttrs) it.next();
            if (!filter.webElem) {
                if (filter.xpath != null) {
                    String str;
                    String str2 = filter.xpath;
                    if (elem.xpath == null) {
                        str = null;
                    } else {
                        str = elem.xpath.toStringValue();
                    }
                    if (!isIdentifyXPath(str2, str)) {
                        continue;
                    }
                }
                if ((filter.index == null || filter.index.equals(String.valueOf(elem.index))) && (filter.content == null || filter.content.equals(elem.content))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isIdentifyXPath(String filterXPath, String elemXPath) {
        if (filterXPath.charAt(0) == '*') {
            if (GConfig.USE_ID) {
                return elemXPath.endsWith(filterXPath.substring(1));
            }
            return false;
        } else if (filterXPath.charAt(0) != '/') {
            return false;
        } else {
            if (isIdentifyPatternServerXPath(filterXPath, elemXPath) || filterXPath.equals(ID_PATTERN_MATCHER.reset(elemXPath).replaceAll(""))) {
                return true;
            }
            return false;
        }
    }

    public static boolean isIdentifyPatternServerXPath(String filterXpath, String elemXpath) {
        boolean z = true;
        if (filterXpath == null || elemXpath == null) {
            if (filterXpath != elemXpath) {
                z = false;
            }
            return z;
        }
        int filterIndex = 0;
        int elemIndex = 0;
        while (elemIndex < elemXpath.length()) {
            if (filterIndex == filterXpath.length()) {
                return false;
            }
            char filterChar = filterXpath.charAt(filterIndex);
            char elemChar = elemXpath.charAt(elemIndex);
            if (filterChar == elemChar) {
                filterIndex++;
            } else if (filterChar != '*' || (('0' > elemChar && elemChar != '-') || elemChar > '9')) {
                if (filterChar != '*' || elemChar != ']') {
                    return false;
                }
                filterIndex++;
                elemIndex--;
            }
            elemIndex++;
        }
        if (filterIndex != filterXpath.length()) {
            z = false;
        }
        return z;
    }

    public static String getIdName(View view, boolean fromTagOnly) {
        Object idTag = view.getTag(AbstractGrowingIO.GROWING_VIEW_ID_KEY);
        if (idTag instanceof String) {
            return (String) idTag;
        }
        if (fromTagOnly) {
            return null;
        }
        if (mIdMap == null) {
            mIdMap = new SparseArray();
        }
        if (mBlackListId == null) {
            mBlackListId = new HashSet();
        }
        int id = view.getId();
        if (id > 2130706432 && !mBlackListId.contains(Integer.valueOf(id))) {
            String idName = (String) mIdMap.get(id);
            if (idName != null) {
                return idName;
            }
            synchronized (Util.class) {
                try {
                    idName = view.getResources().getResourceEntryName(id);
                    mIdMap.put(id, idName);
                } catch (Exception e) {
                    mBlackListId.add(Integer.valueOf(id));
                }
            }
            return idName;
        }
        return null;
    }

    public static Bundle getMetaData(Context context) {
        String packageName = context.getPackageName();
        try {
            Bundle configBundle = context.getPackageManager().getApplicationInfo(packageName, 128).metaData;
            if (configBundle == null) {
                return new Bundle();
            }
            return configBundle;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Can't configure GrowingIO with package name " + packageName, e);
        }
    }

    public static boolean isPackageManagerDiedException(Throwable e) {
        if (!(e instanceof RuntimeException) || e.getMessage() == null) {
            return false;
        }
        if (!e.getMessage().contains("Package manager has died") && !e.getMessage().contains("DeadSystemException")) {
            return false;
        }
        Throwable cause = getLastCause(e);
        if (cause == null) {
            return false;
        }
        if ((cause instanceof DeadObjectException) || cause.getClass().getName().equals("android.os.TransactionTooLargeException")) {
            return true;
        }
        return false;
    }

    public static Throwable getLastCause(Throwable cause) {
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static int calcBannerItemPosition(@NonNull List bannerContent, int position) {
        return position % bannerContent.size();
    }

    @TargetApi(9)
    public static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")), 0, s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            return String.format("%0" + (magnitude.length << 1) + "x", new Object[]{bi});
        } catch (Throwable e) {
            LogUtil.d("util", e);
            return "";
        }
    }

    public static boolean isInSampling(String deviceId, double sampling) {
        if (sampling <= 0.0d) {
            return false;
        }
        if (sampling >= 0.9999d) {
            return true;
        }
        char[] uuid = md5(deviceId).toCharArray();
        long rightValue = (long) ((((double) (1.0f / ((float) 100000))) + sampling) * ((double) 100000));
        long value = 1;
        for (int i = uuid.length - 1; i >= 0; i--) {
            value = ((256 * value) + ((long) uuid[i])) % 100000;
        }
        return value < rightValue;
    }

    public static boolean isIgnoredView(View view) {
        return view.getTag(AbstractGrowingIO.GROWING_IGNORE_VIEW_KEY) != null;
    }

    @TargetApi(14)
    public static String getViewName(View view) {
        if ((view instanceof Switch) || (view instanceof ToggleButton)) {
            return "开关";
        }
        if (view instanceof CheckBox) {
            return "复选框";
        }
        if (view instanceof RadioGroup) {
            return "单选框";
        }
        if (view instanceof Button) {
            return "按钮";
        }
        if (view instanceof EditText) {
            return "输入框";
        }
        if (view instanceof ImageView) {
            return "图片";
        }
        if ((view instanceof WebView) || ClassExistHelper.instanceOfX5WebView(view)) {
            return "H5元素";
        }
        if (view instanceof TextView) {
            return "文字";
        }
        return "其他元素";
    }

    public static boolean isViewClickable(View view) {
        return view.isClickable() || (view instanceof RadioGroup) || (view instanceof Spinner) || (view instanceof AbsSeekBar) || (view.getParent() != null && (view.getParent() instanceof AdapterView) && ((AdapterView) view.getParent()).isClickable());
    }

    public static int dp2Px(Context context, float dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2Px(Context context, float sp) {
        return (int) ((sp * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static String getProcessNameForDB(Context context) {
        Exception e;
        Throwable th;
        String processName = null;
        if (VERSION.SDK_INT <= 26 && VERSION.SDK_INT >= 19) {
            try {
                processName = (String) Class.forName("android.app.ActivityThread").getDeclaredMethod("currentProcessName", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e2) {
                LogUtil.d(e2);
            }
        }
        if (TextUtils.isEmpty(processName)) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(new File("/proc/" + Process.myPid() + "/cmdline")));
                try {
                    processName = reader2.readLine().trim();
                    if (reader2 != null) {
                        try {
                            reader2.close();
                        } catch (IOException e3) {
                        }
                    }
                } catch (Exception e4) {
                    e = e4;
                    reader = reader2;
                    try {
                        LogUtil.d(e);
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e5) {
                            }
                        }
                        if (!TextUtils.isEmpty(processName)) {
                        }
                        return "";
                    } catch (Throwable th2) {
                        th = th2;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e6) {
                            }
                        }
                        //throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    reader = reader2;
                    if (reader != null) {
                        reader.close();
                    }
                    //throw th;
                }
            } catch (Exception e7) {
                //e2 = e7;
                //LogUtil.d(e2);
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(processName)) {
                }
                return "";
            }
        }
        if (TextUtils.isEmpty(processName) || processName.equals(context.getPackageName())) {
            return "";
        }
        return processName + ".";
    }

    public static void getVisibleRectOnScreen(View view, Rect rect, boolean ignoreOffset, int[] screenLocation) {
        if (ignoreOffset) {
            view.getGlobalVisibleRect(rect);
            return;
        }
        if (screenLocation == null || screenLocation.length != 2) {
            screenLocation = new int[2];
        }
        view.getLocationOnScreen(screenLocation);
        rect.set(0, 0, view.getWidth(), view.getHeight());
        rect.offset(screenLocation[0], screenLocation[1]);
    }

    public static void getVisibleRectOnScreen(View view, Rect rect, boolean ignoreOffset) {
        getVisibleRectOnScreen(view, rect, ignoreOffset, null);
    }

    @TargetApi(9)
    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (((rotation == 0 || rotation == 2) && height > width) || ((rotation == 1 || rotation == 3) && width > height)) {
            switch (rotation) {
                case 0:
                    return 1;
                case 1:
                    return 0;
                case 2:
                    return 9;
                case 3:
                    return 8;
                default:
                    return 1;
            }
        }
        switch (rotation) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 8;
            case 3:
                return 9;
            default:
                return 0;
        }
    }

    public static void sendMessage(Handler handler, int what, Object... obj) {
        if (handler != null) {
            handler.obtainMessage(what, obj).sendToTarget();
        }
    }

    @TargetApi(19)
    public static void callJavaScript(View view, String methodName, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("try{(function(){");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        String separator = "";
        for (Object obj : params) {
            Object obj2 = null;
            stringBuilder.append(separator);
            separator = ",";
            if (obj2 instanceof String) {
                stringBuilder.append("'");
                String param = ((String) obj2).replace("'", "'");
                StringBuilder builder = new StringBuilder();
                GJSONStringer.stringWithoutQuotation(builder, param);
                obj2 = builder.toString();
            }
            stringBuilder.append(obj2);
            if (obj2 instanceof String) {
                stringBuilder.append("'");
            }
        }
        stringBuilder.append(");})()}catch(ex){console.log(ex);}");
        try {
            String jsCode = stringBuilder.toString();
            if (view instanceof WebView) {
                WebView webView = (WebView) view;
                if (VERSION.SDK_INT >= 19) {
                    webView.evaluateJavascript(jsCode, null);
                } else {
                    webView.loadUrl("javascript:" + jsCode);
                }
            } else if (ClassExistHelper.instanceOfX5WebView(view)) {
                //((com.tencent.smtt.sdk.WebView) view).evaluateJavascript(jsCode, null);
            }
        } catch (Exception e) {
            LogUtil.d("WebView", "call javascript failed ", e);
        }
    }

    @TargetApi(9)
    public static void saveToFile(byte[] data, String dest) throws IOException {
        Throwable th;
        File destFile = new File(dest);
        File parentFile = destFile.getParentFile();
        if (parentFile.isDirectory() || parentFile.mkdirs()) {
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(destFile);
                try {
                    fileOutputStream2.write(data);
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e) {
                            LogUtil.i("Util", e.getMessage());
                        }
                        destFile.setReadable(true);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e2) {
                            LogUtil.i("Util", e2.getMessage());
                        }
                        destFile.setReadable(true);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    destFile.setReadable(true);
                }
                //throw th;
            }
        }
    }

    @TargetApi(9)
    public static void saveToFile(InputStream stream, String dest) throws IOException {
        Throwable th;
        File destFile = new File(dest);
        File parentFile = destFile.getParentFile();
        if (parentFile.isDirectory() || parentFile.mkdirs()) {
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(destFile);
                try {
                    byte[] buffer = new byte[8192];
                    int read = stream.read(buffer);
                    while (read > 0) {
                        fileOutputStream2.write(buffer, 0, read);
                        read = stream.read(buffer);
                    }
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e) {
                            LogUtil.d("Util", e);
                        }
                        destFile.setReadable(true);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e2) {
                            LogUtil.d("Util", e2);
                        }
                        destFile.setReadable(true);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    destFile.setReadable(true);
                }
                //throw th;
            }
        }
    }

    public static boolean isHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith(Constants.HTTP_PROTOCOL_PREFIX);
    }

    public static boolean isHttpsUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith(Constants.HTTPS_PROTOCOL_PREFIX);
    }

    public static int getVersionCode(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            LogUtil.d("Util", e);
            return i;
        }
    }

    public static boolean shouldSetLocation(double currentLatitude, double currentLongitude, double lastLatitude, double lastLongitude, long currentTime, long lastSetLocationTime) {
        double locationDiffAbsSum = Math.abs(currentLatitude - lastLatitude) + Math.abs(currentLongitude - lastLongitude);
        if (locationDiffAbsSum == 0.0d) {
            return false;
        }
        if (locationDiffAbsSum > 0.05d || currentTime - lastSetLocationTime > 300000) {
            return true;
        }
        return false;
    }

    @TargetApi(11)
    public static boolean isPasswordInputType(int inputType) {
        int variation = inputType & 4095;
        return variation == 129 || variation == 225 || variation == 18 || variation == 145;
    }

    public static CharSequence getEditTextText(TextView textView) {
        try {
            Field mText = TextView.class.getDeclaredField("mText");
            mText.setAccessible(true);
            return (CharSequence) mText.get(textView);
        } catch (Throwable e) {
            LogUtil.d("Util", e);
            return null;
        }
    }

    public static String encryptContent(String content) {
        Encryption entity = CoreInitialize.config().getEncryptEntity();
        if (!(entity == null || TextUtils.isEmpty(content))) {
            try {
                content = entity.encrypt(content);
            } catch (Exception e) {
                LogUtil.e("加密失败", "V字段加密算法崩溃，传回content");
            }
        }
        return content;
    }
}