package cn.sa.demo.observer;

import android.util.Log;
import android.view.View;

import cn.sa.demo.activity.VPVPFrgActivity;
import cn.sa.demo.fragment.v4.Frg_2;
import cn.sa.demo.utils.ElementDisplayManager;
//import cn.sa.demo.utils_G.ElementDisplayManager2;
//import cn.sa.demo.utils.ElementDisplayManager2;
import cn.sa.demo.utils.FragmentPageManager;
import cn.sa.demo.utils.FrgUtil;

/**
 * Created by yzk on 2019/5/13
 */

public class AppViewTreeObserver implements android.view.ViewTreeObserver.OnGlobalLayoutListener, android.view.ViewTreeObserver.OnScrollChangedListener, android.view.ViewTreeObserver.OnGlobalFocusChangeListener {

    private static final String TAG_VTO = "nice ViewTreeOb ---> :";//过滤关键字 nice
    public static volatile AppViewTreeObserver appViewTreeObserver;

    public static AppViewTreeObserver getInstance() {
        if (appViewTreeObserver == null) {
            synchronized (AppViewTreeObserver.class) {
                if (appViewTreeObserver == null) {
                    appViewTreeObserver = new AppViewTreeObserver();
                }
            }
        }
        return appViewTreeObserver;
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Log.i(TAG_VTO, "onGlobalFocusChanged");
    }

    @Override
    public void onGlobalLayout() {
        Log.i(TAG_VTO, "onGlobalLayout");
//        if(VPVPFrgActivity.getFragmentPagerAdapter() !=null){
//            Log.i("nice Frg_1 --->", ""+ FrgUtil.isVisible(VPVPFrgActivity.getFragmentPagerAdapter().getItem(0)));
//            Log.i("nice Frg_2 --->", ""+ FrgUtil.isVisible(VPVPFrgActivity.getFragmentPagerAdapter().getItem(1)));
//            Log.i("nice Frg_3 --->", ""+ FrgUtil.isVisible(VPVPFrgActivity.getFragmentPagerAdapter().getItem(2)));
//        }
//        if(Frg_2.getFragmentPagerAdapter() !=null){
//            Log.i("nice Frg_4 --->", ""+ FrgUtil.isVisible(Frg_2.getFragmentPagerAdapter().getItem(0)));
//            Log.i("nice Frg_5 --->", ""+ FrgUtil.isVisible(Frg_2.getFragmentPagerAdapter().getItem(1)));
//            Log.i("nice Frg_6 --->", ""+ FrgUtil.isVisible(Frg_2.getFragmentPagerAdapter().getItem(2)));
//        }
        // Display 曝光
        //ElementDisplayManager.saveDisplayOnViewTreeObserver();
        //ElementDisplayManager2.saveImpOnViewTreeObserver();

        // Fragment 页面
        FragmentPageManager.saveFragmentPageOnViewTreeObserver();
    }

    @Override
    public void onScrollChanged() {
        Log.i(TAG_VTO, "onScrollChanged");
        // Display 曝光
        //ElementDisplayManager.saveDisplayOnViewTreeObserver();
        //ElementDisplayManager2.saveImpOnViewTreeObserver();
    }
}
