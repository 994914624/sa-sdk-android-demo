package cn.sa.demo.utils_G;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ThreadUtils {
    static final /* synthetic */ boolean $assertionsDisabled = (!ThreadUtils.class.desiredAssertionStatus());
    private static final Object sLock = new Object();
    private static Handler sUiThreadHandler = null;
    private static boolean sWillOverride = false;

    public static void setWillOverrideUiThread() {
        synchronized (sLock) {
            sWillOverride = true;
        }
    }

    public static void setUiThread(Looper looper) {
        synchronized (sLock) {
            if (sUiThreadHandler == null || sUiThreadHandler.getLooper() == looper) {
                sUiThreadHandler = new Handler(looper);
            } else {
                throw new RuntimeException("UI thread looper is already set to " + sUiThreadHandler.getLooper() + " (Main thread looper is " + Looper.getMainLooper() + "), cannot set to new looper " + looper);
            }
        }
    }

    private static Handler getUiThreadHandler() {
        Handler handler;
        synchronized (sLock) {
            if (sUiThreadHandler == null) {
                if (sWillOverride) {
                    throw new RuntimeException("Did not yet override the UI thread");
                }
                sUiThreadHandler = new Handler(Looper.getMainLooper());
            }
            handler = sUiThreadHandler;
        }
        return handler;
    }

    public static void runOnUiThreadBlocking(Runnable r) {
        if (runningOnUiThread()) {
            r.run();
            return;
        }
        FutureTask task = new FutureTask(r, null);
        postOnUiThread(task);
        try {
            task.get();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured while waiting for runnable", e);
        }
    }

    public static <T> T runOnUiThreadBlockingNoException(Callable<T> c) {
        try {
            return (T) runOnUiThreadBlocking((Callable) c);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error occured waiting for callable", e);
        }
    }

    public static <T> T runOnUiThreadBlocking(Callable<T> c) throws ExecutionException {
        FutureTask task = new FutureTask(c);
        runOnUiThread(task);
        try {
            return (T) task.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for callable", e);
        }
    }

    public static <T> FutureTask<T> runOnUiThread(FutureTask<T> task) {
        if (runningOnUiThread()) {
            task.run();
        } else {
            postOnUiThread((FutureTask) task);
        }
        return task;
    }

    public static <T> FutureTask<T> runOnUiThread(Callable<T> c) {
        return runOnUiThread(new FutureTask(c));
    }

    public static void runOnUiThread(Runnable r) {
        if (runningOnUiThread()) {
            r.run();
        } else {
            getUiThreadHandler().post(r);
        }
    }

    public static <T> FutureTask<T> postOnUiThread(FutureTask<T> task) {
        getUiThreadHandler().post(task);
        return task;
    }

    public static void postOnUiThread(Runnable task) {
        getUiThreadHandler().post(task);
    }

    public static void postOnUiThreadDelayed(Runnable task, long delayMillis) {
        getUiThreadHandler().postDelayed(task, delayMillis);
    }

    public static void cancelTaskOnUiThread(Runnable task) {
        getUiThreadHandler().removeCallbacks(task);
    }

    @SuppressLint({"Assert"})
    public static void assertOnUiThread() {
        if (!$assertionsDisabled && !runningOnUiThread()) {
            throw new AssertionError();
        }
    }

    public static boolean runningOnUiThread() {
        return getUiThreadHandler().getLooper() == Looper.myLooper();
    }

    public static Looper getUiThreadLooper() {
        return getUiThreadHandler().getLooper();
    }
}