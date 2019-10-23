package cn.sa.demo.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import static android.os.Looper.getMainLooper;

/**
 * Created by yzk on 2019-09-05
 */

public class TestHandler {
    private static final HandlerThread handlerThread = new HandlerThread("11111");
    private static Handler handler;

    private static Handler getHandler() {
        Handler handler2;
            if (handler == null) {
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
            }
            handler2 = handler;
        return handler2;
    }

    private static Runnable task = new Runnable() {
        @Override
        public void run() {
            Log.e("TestHandler","--------------111-------------");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("TestHandler","---------------222------------");
        }
    };

    private static void runTask(){
        //getHandler().removeCallbacks(task);
        getHandler().postDelayed(task,190);
    }

    public static void recycle(){
        getHandler().postDelayed(task,80);;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getHandler().removeCallbacks(task);
    }
}
