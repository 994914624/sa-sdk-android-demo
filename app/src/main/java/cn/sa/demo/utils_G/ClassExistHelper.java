package cn.sa.demo.utils_G;

import android.annotation.TargetApi;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.growingio.android.sdk.utils.LogUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

public class ClassExistHelper {
    private static final String TAG = "GIO.ClassExist";
    private static Class sCRVClass;
    private static Method sCRVGetChildAdapterPositionMethod;
    private static boolean sHasAndroidXAlertDialog = hasClass("androidx.appcompat.app.AlertDialog");
    private static boolean sHasAndroidXFragment = hasClass("androidx.fragment.app.Fragment");
    private static boolean sHasAndroidXFragmentActivity = hasClass("androidx.fragment.app.FragmentActivity");
    public static boolean sHasAndroidXListMenuItemView = hasClass("androidx.appcompat.view.menu.ListMenuItemView");
    private static boolean sHasAndroidXRecyclerView = hasClass("androidx.recyclerview.widget.RecyclerView");
    private static boolean sHasAndroidXSwipeRefreshLayoutView = hasClass("androidx.swiperefreshlayout.widget.SwipeRefreshLayout");
    public static boolean sHasAndroidXViewPager = hasClass("androidx.viewpager.widget.ViewPager");
    public static boolean sHasCustomRecyclerView = false;
    private static boolean sHasSupportAlertDialog = hasClass("android.support.v7.app.AlertDialog");
    private static boolean sHasSupportFragment = hasClass("android.support.v4.app.Fragment");
    private static boolean sHasSupportFragmentActivity = hasClass("android.support.v4.app.FragmentActivity");
    public static boolean sHasSupportListMenuItemView = hasClass("android.support.v7.view.menu.ListMenuItemView");
    private static boolean sHasSupportRecyclerView = hasClass("android.support.v7.widget.RecyclerView");
    private static boolean sHasSupportSwipeRefreshLayoutView = hasClass("android.support.v4.widget.SwipeRefreshLayout");
    public static boolean sHasSupportViewPager = hasClass("android.support.v4.view.ViewPager");
    private static boolean sHasTransform;
    private static boolean sHasX5WebView = hasClass("com.tencent.smtt.sdk.WebView");

    static {
        if (!sHasTransform) {
        }
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public static int invokeCRVGetChildAdapterPositionMethod(View customRecyclerView, View childView) {
        try {
            if (customRecyclerView.getClass() == sCRVClass) {
                return ((Integer) sCRVGetChildAdapterPositionMethod.invoke(customRecyclerView, new Object[]{childView})).intValue();
            }
        } catch (IllegalAccessException e) {
            LogUtil.d(e);
        } catch (InvocationTargetException ignored) {
            LogUtil.d(ignored);
        }
        return -1;
    }

    @TargetApi(9)
    public static void checkCustomRecyclerView(Class<?> viewClass, String viewName) {
        if (!sHasAndroidXRecyclerView && !sHasSupportRecyclerView && !sHasCustomRecyclerView && viewName != null && viewName.contains("RecyclerView")) {
            try {
                if (findRecyclerInSuper(viewClass) != null && sCRVGetChildAdapterPositionMethod != null) {
                    sCRVClass = viewClass;
                    sHasCustomRecyclerView = true;
                }
            } catch (Exception ignore) {
                LogUtil.d(ignore);
            }
        }
    }

    private static Class<?> findRecyclerInSuper(Class<?> viewClass) {
        while (viewClass != null && !viewClass.equals(ViewGroup.class)) {
            try {
                sCRVGetChildAdapterPositionMethod = viewClass.getDeclaredMethod("getChildAdapterPosition", new Class[]{View.class});
            } catch (NoSuchMethodException e) {
            }
            if (sCRVGetChildAdapterPositionMethod == null) {
                try {
                    sCRVGetChildAdapterPositionMethod = viewClass.getDeclaredMethod("getChildPosition", new Class[]{View.class});
                } catch (NoSuchMethodException e2) {
                }
            }
            if (sCRVGetChildAdapterPositionMethod != null) {
                return viewClass;
            }
            viewClass = viewClass.getSuperclass();
        }
        return null;
    }

    public static boolean instanceOfRecyclerView(Object view) {
        return instanceOfAndroidXRecyclerView(view) || instanceOfSupportRecyclerView(view) || (sHasCustomRecyclerView && view != null && sCRVClass.isAssignableFrom(view.getClass()));
    }

    public static boolean instanceOfSupportRecyclerView(Object view) {
        return sHasSupportRecyclerView && (view instanceof RecyclerView);
    }

    public static boolean instanceOfAndroidXRecyclerView(Object view) {
        return sHasAndroidXRecyclerView && (view instanceof androidx.recyclerview.widget.RecyclerView);
    }

    public static boolean instanceOfSupportViewPager(Object view) {
        return sHasSupportViewPager && (view instanceof ViewPager);
    }

    public static boolean instanceOfAndroidXViewPager(Object view) {
        return sHasAndroidXViewPager && (view instanceof androidx.viewpager.widget.ViewPager);
    }

    public static boolean instanceOfSupportSwipeRefreshLayout(Object view) {
        return sHasSupportSwipeRefreshLayoutView && (view instanceof SwipeRefreshLayout);
    }

    public static boolean instanceofAndroidXSwipeRefreshLayout(Object view) {
        return sHasAndroidXSwipeRefreshLayoutView && (view instanceof androidx.swiperefreshlayout.widget.SwipeRefreshLayout);
    }

    public static boolean instanceOfX5WebView(Object view) {
        return sHasX5WebView && (view instanceof WebView);
    }

    public static boolean instanceOfX5ChromeClient(Object client) {
        return sHasX5WebView && (client instanceof WebChromeClient);
    }

    public static boolean instanceOfSupportAlertDialog(Object dialog) {
        return sHasSupportAlertDialog && (dialog instanceof AlertDialog);
    }

    public static boolean instanceOfAndroidXAlertDialog(Object dialog) {
        return sHasAndroidXAlertDialog && (dialog instanceof androidx.appcompat.app.AlertDialog);
    }

    public static boolean instanceOfSupportFragmentActivity(Object activity) {
        return sHasSupportFragmentActivity && (activity instanceof FragmentActivity);
    }

    public static boolean instanceOfAndroidXFragmentActivity(Object activity) {
        return sHasAndroidXFragmentActivity && (activity instanceof androidx.fragment.app.FragmentActivity);
    }

    public static boolean instanceOfSupportFragment(Object fragment) {
        return sHasSupportFragment && (fragment instanceof Fragment);
    }

    public static boolean instanceOfAndroidXFragment(Object fragment) {
        return sHasAndroidXFragment && (fragment instanceof androidx.fragment.app.Fragment);
    }

    public static boolean instanceOfSupportListMenuItemView(Object itemView) {
        return sHasSupportListMenuItemView && (itemView instanceof ListMenuItemView);
    }

    public static boolean instanceOfAndroidXListMenuItemView(Object itemView) {
        return sHasAndroidXListMenuItemView && (itemView instanceof androidx.appcompat.view.menu.ListMenuItemView);
    }

    public static void dumpClassInfo() {
        String info = "For Support Class: \nsHasSupportRecyclerView=" + sHasSupportRecyclerView + ", sHasSupportFragmentActivity=" + sHasSupportFragmentActivity + "\nsHasSupportFragment=" + sHasSupportFragment + ", sHasSupportAlertDialog=" + sHasSupportAlertDialog + "\nsHasSupportSwipeRefreshLayoutView=" + sHasSupportSwipeRefreshLayoutView + ", sHasSupportViewPager=" + sHasSupportViewPager + "\nsHasSupportListMenuItemView=" + sHasSupportListMenuItemView + "\nFor AndroidX Class: \nsHasAndroidXRecyclerView=" + sHasAndroidXRecyclerView + ", sHasAndroidXFragmentActivity=" + sHasAndroidXFragmentActivity + "\nsHasAndroidXFragment=" + sHasAndroidXFragment + ", sHasAndroidXAlertDialog=" + sHasAndroidXAlertDialog + "\nsHasAndroidXSwipeRefreshLayoutView=" + sHasAndroidXSwipeRefreshLayoutView + ", sHasAndroidXViewPager=" + sHasAndroidXViewPager + "\nsHasAndroidXListMenuItemView=" + sHasAndroidXListMenuItemView + "\nAnd sHasTransform=" + sHasTransform;
        LogUtil.d(TAG, info);
    }
}