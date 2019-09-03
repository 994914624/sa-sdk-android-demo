package cn.sa.demo.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import cn.sa.demo.R;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by yzk on 2019-08-26
 */

public class FloatView {

    private static WindowManager mWindowManager;
    private static View floatView;

    @SuppressLint("ClickableViewAccessibility")
    public static void createView(Context context) {
        try {
            if (context == null)return;
            if (floatView == null) {
                floatView = LayoutInflater.from(context).inflate(R.layout.floating_view, null);
            }
            //设置WindowManger布局参数以及相关属性
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


            if (Build.VERSION.SDK_INT > 25) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }

            //初始化位置
            params.gravity = Gravity.START|Gravity.TOP;
            params.x = 240;
            params.y = 20;

            //关闭 FloatView
            floatView.findViewById(R.id.tv_float).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFloatView();
                }
            });


            Button button = floatView.findViewById(R.id.btn_float);
            button.getBackground().setAlpha(0);
            // 拖动
            button.setOnTouchListener(new View.OnTouchListener() {
                //获取X坐标
                private int startX;
                //获取Y坐标
                private int startY;
                //初始化X的touch坐标
                private float startTouchX;
                //初始化Y的touch坐标
                private float startTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = params.x;
                            startY = params.y;
                            startTouchX = event.getRawX();
                            startTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x = startX + (int) (event.getRawX() - startTouchX);
                            params.y = startY + (int) (event.getRawY() - startTouchY);
                            //更新View的位置
                            mWindowManager.updateViewLayout(floatView, params);
                            return true;
                    }
                    return false;
                }
            });

            //获取 WindowManager 对象
            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(floatView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFloatView() {
        try {
            //移除 FloatView
            mWindowManager.removeViewImmediate(floatView);
            floatView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
