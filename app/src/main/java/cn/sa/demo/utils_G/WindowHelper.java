package cn.sa.demo.utils_G;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.view.menu.ListMenuItemView;
import com.growingio.android.sdk.collection.CoreInitialize;
import com.growingio.android.sdk.utils.ClassExistHelper;
import com.growingio.android.sdk.utils.LogUtil;
import com.growingio.android.sdk.utils.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

public class WindowHelper {
    static boolean sArrayListWindowViews = false;
    private static final String sCustomWindowPrefix = "/CustomWindow";
    private static final String sDialogWindowPrefix = "/DialogWindow";
    public static final String sIgnoredWindowPrefix = "/Ignored";
    private static boolean sIsInitialized = false;
    private static Method sItemViewGetDataMethod = null;
    private static Class<?> sListMenuItemViewClazz = null;
    private static final String sMainWindowPrefix = "/MainWindow";
    static Class sPhoneWindowClazz = null;
    static Class sPopupWindowClazz = null;
    private static final String sPopupWindowPrefix = "/PopupWindow";
    static boolean sViewArrayWindowViews = false;
    private static Comparator<View> sViewSizeComparator = new Comparator<View>() {
        public int compare(View lhs, View rhs) {
            int lhsHashCode = lhs.hashCode();
            int rhsHashCode = rhs.hashCode();
            int currentHashCode = CoreInitialize.coreAppState().getCurrentRootWindowsHashCode();
            if (lhsHashCode == currentHashCode) {
                return -1;
            }
            if (rhsHashCode == currentHashCode) {
                return 1;
            }
            return (rhs.getWidth() * rhs.getHeight()) - (lhs.getWidth() * lhs.getHeight());
        }
    };
    static Object sWindowManger;
    @VisibleForTesting
    static WeakHashMap<View, Long> showingToast = new WeakHashMap();
    static Field viewsField;

    public static void init() {
        if (!sIsInitialized) {
            String windowManagerClassName;
            if (VERSION.SDK_INT >= 17) {
                windowManagerClassName = "android.view.WindowManagerGlobal";
            } else {
                windowManagerClassName = "android.view.WindowManagerImpl";
            }
            try {
                String windowManagerString;
                Class<?> windowManager = Class.forName(windowManagerClassName);
                if (VERSION.SDK_INT >= 17) {
                    windowManagerString = "sDefaultWindowManager";
                } else if (VERSION.SDK_INT >= 13) {
                    windowManagerString = "sWindowManager";
                } else {
                    windowManagerString = "mWindowManager";
                }
                viewsField = windowManager.getDeclaredField("mViews");
                Field instanceField = windowManager.getDeclaredField(windowManagerString);
                viewsField.setAccessible(true);
                if (viewsField.getType() == ArrayList.class) {
                    sArrayListWindowViews = true;
                } else if (viewsField.getType() == View[].class) {
                    sViewArrayWindowViews = true;
                }
                instanceField.setAccessible(true);
                sWindowManger = instanceField.get(null);
            } catch (NoSuchFieldException e) {
                LogUtil.d(e);
            } catch (IllegalAccessException e2) {
                LogUtil.d(e2);
            } catch (ClassNotFoundException e3) {
                LogUtil.d(e3);
            }
            try {
                sListMenuItemViewClazz = Class.forName("com.android.internal.view.menu.ListMenuItemView");
                sItemViewGetDataMethod = Class.forName("com.android.internal.view.menu.MenuView$ItemView").getDeclaredMethod("getItemData", new Class[0]);
            } catch (ClassNotFoundException e32) {
                LogUtil.d(e32);
            } catch (NoSuchMethodException e4) {
                LogUtil.d(e4);
            }
            try {
                if (VERSION.SDK_INT >= 23) {
                    try {
                        sPhoneWindowClazz = Class.forName("com.android.internal.policy.PhoneWindow$DecorView");
                    } catch (ClassNotFoundException e5) {
                        sPhoneWindowClazz = Class.forName("com.android.internal.policy.DecorView");
                    }
                    try {
                        if (VERSION.SDK_INT < 23) {
                            sPopupWindowClazz = Class.forName("android.widget.PopupWindow$PopupDecorView");
                        } else {
                            sPopupWindowClazz = Class.forName("android.widget.PopupWindow$PopupViewContainer");
                        }
                    } catch (ClassNotFoundException e322) {
                        LogUtil.d(e322);
                    }
                    sIsInitialized = true;
                }
                sPhoneWindowClazz = Class.forName("com.android.internal.policy.impl.PhoneWindow$DecorView");
                if (VERSION.SDK_INT < 23) {
                    sPopupWindowClazz = Class.forName("android.widget.PopupWindow$PopupViewContainer");
                } else {
                    sPopupWindowClazz = Class.forName("android.widget.PopupWindow$PopupDecorView");
                }
                sIsInitialized = true;
            } catch (ClassNotFoundException e3222) {
                LogUtil.d(e3222);
            }
        }
    }

    public static String getWindowPrefix(View root) {
        if (Util.isIgnoredView(root)) {
            return sIgnoredWindowPrefix;
        }
        if (root.hashCode() == CoreInitialize.coreAppState().getCurrentRootWindowsHashCode()) {
            return getMainWindowPrefix();
        }
        return getSubWindowPrefix(root);
    }

    public static View[] getWindowViews() {
        View[] result = new View[0];
        if (sWindowManger == null) {
            if (CoreInitialize.coreAppState().getForegroundActivity() == null) {
                return result;
            }
            return new View[]{CoreInitialize.coreAppState().getForegroundActivity().getWindow().getDecorView()};
        }
        View[] views = null;
        try {
            if (sArrayListWindowViews) {
                views = (View[]) ((ArrayList) viewsField.get(sWindowManger)).toArray(result);
            } else if (sViewArrayWindowViews) {
                views = (View[]) viewsField.get(sWindowManger);
            }
            if (views != null) {
                result = views;
            }
        } catch (Exception e) {
            LogUtil.d(e);
        }
        return filterNullAndDismissToastView(result);
    }

    public static View[] filterNullAndDismissToastView(View[] array) {
        List<View> list = new ArrayList(array.length);
        long currentTime = System.currentTimeMillis();
        for (View view : array) {
            if (view != null) {
                if (!showingToast.isEmpty()) {
                    Long deadline = (Long) showingToast.get(view);
                    if (deadline != null && currentTime > deadline.longValue()) {
                    }
                }
                list.add(view);
            }
        }
        View[] result = new View[list.size()];
        list.toArray(result);
        return result;
    }

    public static void onToastShow(@NonNull Toast toast) {
        try {
            View nextView = toast.getView();
            int duration = toast.getDuration();
            if (nextView != null) {
                showingToast.put(nextView, Long.valueOf(((long) Math.max(8000, duration)) + System.currentTimeMillis()));
            }
        } catch (Exception e) {
            LogUtil.d("GIO.Window", "onToastShow, failed: ", e);
        }
    }

    @TargetApi(9)
    public static View[] getSortedWindowViews() {
        View[] views = getWindowViews();
        if (views.length <= 1) {
            return views;
        }
        views = (View[]) Arrays.copyOf(views, views.length);
        Arrays.sort(views, sViewSizeComparator);
        return views;
    }

    public static String getMainWindowPrefix() {
        return sMainWindowPrefix;
    }

    public static String getSubWindowPrefix(View root) {
        LayoutParams params = root.getLayoutParams();
        if (params != null && (params instanceof WindowManager.LayoutParams)) {
            int type = ((WindowManager.LayoutParams) params).type;
            if (type == 1) {
                return sMainWindowPrefix;
            }
            if (type < 99 && root.getClass() == sPhoneWindowClazz) {
                return sDialogWindowPrefix;
            }
            if (type < 1999 && root.getClass() == sPopupWindowClazz) {
                return sPopupWindowPrefix;
            }
            if (type < 2999) {
                return sCustomWindowPrefix;
            }
        }
        Class rootClazz = root.getClass();
        if (rootClazz == sPhoneWindowClazz) {
            return sDialogWindowPrefix;
        }
        if (rootClazz == sPopupWindowClazz) {
            return sPopupWindowPrefix;
        }
        return sCustomWindowPrefix;
    }

    public static boolean isDecorView(View rootView) {
        if (!sIsInitialized) {
            init();
        }
        Class rootClass = rootView.getClass();
        return rootClass == sPhoneWindowClazz || rootClass == sPopupWindowClazz;
    }

    @SuppressLint({"RestrictedApi"})
    public static Object getMenuItemData(View view) throws InvocationTargetException, IllegalAccessException {
        if (view.getClass() == sListMenuItemViewClazz) {
            return sItemViewGetDataMethod.invoke(view, new Object[0]);
        }
        if (ClassExistHelper.instanceOfAndroidXListMenuItemView(view)) {
            return ((ListMenuItemView) view).getItemData();
        }
        if (ClassExistHelper.instanceOfSupportListMenuItemView(view)) {
            return ((ListMenuItemView) view).getItemData();
        }
        return null;
    }
}