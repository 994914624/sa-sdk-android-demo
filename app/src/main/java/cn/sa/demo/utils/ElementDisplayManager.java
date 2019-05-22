package cn.sa.demo.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import cn.sa.demo.observer.AppActivityLifecycleCallbacks;



/**
 * Created by yzk on 2019/5/13
 * <p>
 *      原理：在 onScrollChanged、onGlobalLayout、onResume 时 延迟去获取当前窗口可视区内所有的 View。
 * <p>
 *     每个 Activity 有一个 ElementDisplayCalcultor 对象，此对象用一个 mList 记录着曝光的 viewNodeEntity.hashCode()
 *     如果一个 viewNodeEntity.hashCode() 不在 mList 中，测产生曝光事件。
 *
 *     每个可交互的 View 一个作为一个 ViewNodeEntity 对象。ViewNodeEntity 中记录 viewPath、文本、pageName ,hashCode 方法。
 *
 * <p>
 *     onPause 时，清空 ElementDisplayCalcultor 对象。
 */


public class ElementDisplayManager {

    private static final String TAG = "ElementDisplayManager";
    private static String mTagPage;
    private static volatile ElementDisplayManager instance;
    // 缓存每个页面的 ElementDisplayCalcultor
    private static Map<WeakReference<View>, ElementDisplayCalcultor> mElementDisplayCalcultor = new LinkedHashMap();

    private static ElementDisplayManager getInstance() {
        if (instance == null) {
            synchronized (ElementDisplayManager.class) {
                if (instance == null) {
                    instance = new ElementDisplayManager();
                }
            }
        }
        return instance;
    }

    /**
     * 开始 task
     */
    private void taskOn() {
        Activity activity = AppActivityLifecycleCallbacks.getResumedActivity();
        if (activity != null) {
            mTagPage = activity.getClass().getSimpleName();
            View[] decorView = new View[]{activity.getWindow().getDecorView()};
            for (View root : decorView) {
                ElementDisplayCalcultor elementDisplayCalcultor = getCurrentElementDisplayCalcultor(root, activity);
                // 遍历
                elementDisplayCalcultor.traverseViewTree();

                // 遍历完 新的 viewNode 触发事件
                List list = elementDisplayCalcultor.getNewViewNode();
                Log.e(TAG, list.toString());
                elementDisplayCalcultor.resetNewViewNode();//这里相当于触发完曝光事件后，重置 list
            }

        }
    }

    /**
     * 获取当前 Activity 的 ElementDisplayCalcultor
     */
    private ElementDisplayCalcultor getCurrentElementDisplayCalcultor(View root, Activity activity) {
        for (WeakReference viewReference : mElementDisplayCalcultor.keySet()) {
            if (viewReference.get() == root) {
                return (ElementDisplayCalcultor) mElementDisplayCalcultor.get(viewReference);
            }
        }
        ElementDisplayCalcultor elementDisplayCalcultor = new ElementDisplayCalcultor(root, activity.getClass().getSimpleName(), System.currentTimeMillis());
        mElementDisplayCalcultor.put(new WeakReference(root), elementDisplayCalcultor);
        return elementDisplayCalcultor;
    }


    /**
     * 清除 ElementDisplayCalcultor 对象
     */
    private static void clearElementDisplayCalcultor() {
        mElementDisplayCalcultor.clear();
    }


    private static Runnable mDisplayRunable = new Runnable() {
        public void run() {
            ElementDisplayManager.getInstance().taskOn();
        }
    };

    /**
     * ViewTreeObserver 时，延迟 250ms 保存 Display
     */
    public static void saveDisplayOnViewTreeObserver() {
        getUiThreadHandler().removeCallbacks(mDisplayRunable);
        getUiThreadHandler().postDelayed(mDisplayRunable,250);
    }

    /**
     * onResume 时，时先 clear，再延迟 500ms 保存 Display
     */
    public static void saveDisplayOnResume() {
        clearElementDisplayCalcultor();
        getUiThreadHandler().removeCallbacks(mDisplayRunable);
        getUiThreadHandler().postDelayed(mDisplayRunable,500);
    }

    /**
     * onPause 时 clearElementDisplayCalcultor
     */
    public static void cleanDisplayOnPause() {
        Log.e(TAG,"cleanDisplayOnPause:"+mTagPage);
        clearElementDisplayCalcultor();
        getUiThreadHandler().removeCallbacks(mDisplayRunable);
    }

    private static final Object mHandlerLock = new Object();
    private static Handler mUiThreadHandler = null;
    private static Handler getUiThreadHandler() {
        Handler handler;
        synchronized (mHandlerLock) {
            if (mUiThreadHandler == null) {
                mUiThreadHandler = new Handler(Looper.getMainLooper());
            }
            handler = mUiThreadHandler;
        }
        return handler;
    }
}
