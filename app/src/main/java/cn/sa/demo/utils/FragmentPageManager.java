package cn.sa.demo.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sa.demo.observer.AppActivityLifecycleCallbacks;


/**
 * Created by yzk on 2019/5/31
 */


public class FragmentPageManager {

    private static final String TAG = "FragmentPageManager";
    private static String mTagPage;
    private static volatile FragmentPageManager instance;
    // 缓存每个页面的 FragmentPageCalcultor
    private static Map<WeakReference<View>, FragmentPageCalcultor> mFragmentPageCalcultor = new LinkedHashMap<WeakReference<View>, FragmentPageCalcultor>();

    private static FragmentPageManager getInstance() {
        if (instance == null) {
            synchronized (FragmentPageManager.class) {
                if (instance == null) {
                    instance = new FragmentPageManager();
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
                FragmentPageCalcultor fragmentPageCalcultor = getCurrentCalcultor(root, activity);
                // 遍历
                fragmentPageCalcultor.traverseViewTree();
            }

        }
    }

    /**
     * 获取当前 Activity 的 calcultor
     */
    private FragmentPageCalcultor getCurrentCalcultor(View root, Activity activity) {
        for (WeakReference viewReference : mFragmentPageCalcultor.keySet()) {
            if (viewReference.get() == root) {
                return (FragmentPageCalcultor) mFragmentPageCalcultor.get(viewReference);
            }
        }
        FragmentPageCalcultor fragmentPageCalcultor = new FragmentPageCalcultor(root, activity.getClass().getSimpleName(), System.currentTimeMillis());
        mFragmentPageCalcultor.put(new WeakReference<View>(root), fragmentPageCalcultor);
        return fragmentPageCalcultor;
    }


    /**
     * 清除 FragmentPageCalcultor 对象
     */
    private static void clearFragmentPageCalcultor() {
        mFragmentPageCalcultor.clear();
    }

    private static final HandlerThread mFragmentPageHandlerThread = new HandlerThread("FragmentPageThread");
    private static final Object mHandlerLock = new Object();
    private static Handler mFragmentPageHandler = null;

    private static Handler getFragmentPagHandler() {
        Handler handler;
        synchronized (mHandlerLock) {
            if (mFragmentPageHandler == null) {
                mFragmentPageHandlerThread.start();
                mFragmentPageHandler = new Handler(mFragmentPageHandlerThread.getLooper());
            }
            handler = mFragmentPageHandler;
        }
        return handler;
    }

    private static Runnable mFrgRunable = new Runnable() {
        public void run() {
            FragmentPageManager.getInstance().taskOn();
        }
    };


    /**
     * ViewTreeObserver 时，延迟 250ms 保存 Fragment
     */
    public static void saveFragmentPageOnViewTreeObserver() {
        getFragmentPagHandler().removeCallbacks(mFrgRunable);
        getFragmentPagHandler().postDelayed(mFrgRunable, 250);
    }


    /**
     * onPause 时 clear Calcultor
     */
    public static void cleanFragmentPageCalcultorOnPause() {
        Log.e(TAG, "cleanFragmentPageCalcultorOnPause:" + mTagPage);
        clearFragmentPageCalcultor();
        getFragmentPagHandler().removeCallbacks(mFrgRunable);
    }

}
