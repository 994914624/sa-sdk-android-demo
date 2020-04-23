package cn.sa.demo.utils.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by yzk on 2020/4/2
 * 反射的方式 hook onClick
 */

public class ClickProxy  {

    private static final String TAG = "ClickProxy";

    public static void hookActivity(Activity activity){
        if(activity == null)return;
        View view = activity.getWindow().getDecorView();
        traverse(view);
    }

    /**
     * 遍历
     */
    private static void traverse(View view) {
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView.isClickable() ) {
                    hookView(childView);
                }
                traverse(childView);
            }
        }
    }

    /*
     * 反射替换 View 的 mOnClickListener
     */
    @SuppressLint("PrivateApi")
    private static void hookView(View view) {
        try {
            // 反射获取 listenerInfo 对象
            Class clazz = Class.forName("android.view.View");
            Method method = clazz.getDeclaredMethod("getListenerInfo");
            method.setAccessible(true);
            Object listenerInfoObject = method.invoke(view);

            Class listenerInfo = Class.forName("android.view.View$ListenerInfo");
            Field fieldOnClickListener = listenerInfo.getDeclaredField("mOnClickListener");
            fieldOnClickListener.setAccessible(true);
            // 反射获取 mOnClickListener 成员变量
            View.OnClickListener targetOnClickListener = (View.OnClickListener) fieldOnClickListener.get(listenerInfoObject);
            // 把 mOnClickListener 传给代理 Listener
            View.OnClickListener proxyOnClickListener = new ClickProxyListener(targetOnClickListener);
            // 替换 mOnClickListener 成员变量
            fieldOnClickListener.set(listenerInfoObject, proxyOnClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 代理的接口 imp
     */
    static class ClickProxyListener implements View.OnClickListener {

        private View.OnClickListener target;

        ClickProxyListener(View.OnClickListener target) {
            this.target = target;
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG,"-------- onClick ---------");
            if(target!=null)target.onClick(v);
        }
    }

    /**
     * 代理的接口
     */
    interface OnClickProxyListener {
        void onProxyClick(View v);
    }
}
