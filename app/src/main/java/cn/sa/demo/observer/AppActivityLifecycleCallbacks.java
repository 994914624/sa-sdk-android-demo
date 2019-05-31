package cn.sa.demo.observer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import cn.sa.demo.utils.ElementDisplayManager;
import cn.sa.demo.utils.FragmentPageManager;
//import cn.sa.demo.utils_G.ElementDisplayManager2;


/**
 * Created by yzk on 2019/5/13
 */

public class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {



    private static final String TAG_ACTIVITY = "nice Activity ---> :";//过滤关键字 nice

    private static WeakReference<Activity> mResumeActivity = new WeakReference(null);

    private void setResumeActivity(Activity activity){
        mResumeActivity = new WeakReference(activity);
    }

    /**
     * 返回当前 resume 的 Activity
     */
    public static Activity getResumedActivity() {
        return (Activity) mResumeActivity.get();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivityResumed");
        setResumeActivity(activity);
        // registerViewTreeObserver
        registerViewTreeObserver(activity);
        // Display 曝光
        //ElementDisplayManager.saveDisplayOnResume();
        //ElementDisplayManager2.saveImpOnResume();

    }


    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivityPaused");
        setResumeActivity(null);
        // unRegisterViewTreeObserver
        unRegisterViewTreeObserver(activity);
        // Display clean
        //ElementDisplayManager.cleanDisplayOnPause();
        //ElementDisplayManager2.cleanImpOnPause();

        // Fragment 页面
        FragmentPageManager.cleanFragmentPageCalcultorOnPause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG_ACTIVITY, activity.getClass().getSimpleName()+": onActivityDestroyed");
    }

    /**
     * registerViewTreeObserver
     */
    private void registerViewTreeObserver(Activity activity) {
        ViewTreeObserver viewTreeObserver = activity.getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalFocusChangeListener(AppViewTreeObserver.getInstance());
        viewTreeObserver.addOnGlobalLayoutListener(AppViewTreeObserver.getInstance());
        viewTreeObserver.addOnScrollChangedListener(AppViewTreeObserver.getInstance());
    }

    /**
     * unRegisterViewTreeObserver
     */
    private void unRegisterViewTreeObserver(Activity activity) {
        ViewTreeObserver viewTreeObserver = activity.getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.removeOnGlobalFocusChangeListener(AppViewTreeObserver.getInstance());
        viewTreeObserver.removeOnGlobalLayoutListener(AppViewTreeObserver.getInstance());
        viewTreeObserver.removeOnScrollChangedListener(AppViewTreeObserver.getInstance());
    }
}
