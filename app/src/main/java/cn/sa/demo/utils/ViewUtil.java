package cn.sa.demo.utils;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import static android.view.View.VISIBLE;

/**
 * Created by yzk on 2019/5/21
 */

public class ViewUtil {

    private static boolean mHaveCustomRecyclerView = false;
    private static boolean mHaveRecyclerView = haveRecyclerView();
    private static Method mRecyclerViewGetChildAdapterPositionMethod;
    private static Class mRecyclerViewClass;


    /**
     * 获取 class name
     */
    public static String getSimpleClassName(Class clazz) {
        String name = clazz.getSimpleName();
        if (TextUtils.isEmpty(name)) {
            name = "Anonymous";
        }
        checkCustomRecyclerView(clazz, name);
        return name;
    }


    /**
     * 获取 view id
     */
    public static String getViewId(View view) {
        int id = view.getId();
        if (id > 2130706432) {
            String ViewId = "";
            try {
                ViewId = view.getResources().getResourceEntryName(id);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ViewId;
        }
        return "";
    }

    /**
     * 获取 view  text
     */
    public static String getViewContent(View view) {
        String value = "";
        View selected = null;
        if (view instanceof RatingBar) {
            value = String.valueOf(((RatingBar) view).getRating());
        } else if (view instanceof Spinner) {
            Object item = ((Spinner) view).getSelectedItem();
            if (item instanceof String) {
                value = (String) item;
            } else {
                selected = ((Spinner) view).getSelectedView();
                if ((selected instanceof TextView) && ((TextView) selected).getText() != null) {
                    value = ((TextView) selected).getText().toString();
                }
            }
        } else if (view instanceof SeekBar) {
            value = String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof RadioGroup) {
            RadioGroup group = (RadioGroup) view;
            selected = group.findViewById(group.getCheckedRadioButtonId());
            if ((selected instanceof RadioButton) && ((RadioButton) selected).getText() != null) {
                value = ((RadioButton) selected).getText().toString();
            }
        } else if (view instanceof TextView) {
            if (((TextView) view).getText() != null) {
                value = ((TextView) view).getText().toString();
            }
        }

        if (TextUtils.isEmpty(value)) {
           if (view.getContentDescription() != null) {
                value = view.getContentDescription().toString();
            }
        }
        return value;
    }

    /**
     * View 自身是否可见
     *
     * @return
     *         View 宽、高、透明度 有一个 < 0 时，或 getLocalVisibleRect 为 false 时；返回 false 。
     *         View getVisibility 不可见，且有 Animation getFillAfter 为  false 时；返回 false 。
     *         View 无 Animation 时 getVisibility 不可见时返回 false 。
     */
    @RequiresApi(api = 11)
    public static boolean isViewSelfVisible(View view) {
        if (view == null) {
            return false;
        }
        if (view.getWidth() <= 0 || view.getHeight() <= 0 || view.getAlpha() <= 0.0f || !view.getLocalVisibleRect(new Rect())) {
            return false;
        }
        return (view.getVisibility() != VISIBLE && view.getAnimation() != null && view.getAnimation().getFillAfter()) || view.getVisibility() == VISIBLE;
    }

    /**
     * instanceOf ViewPager
     */
    public static boolean instanceOfViewPager(Object view) {
        Class clazz;
        try {
            clazz = Class.forName("android.support.v4.view.ViewPager");
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName("androidx.viewpager.widget.ViewPager");
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
        return clazz.isInstance(view);
    }

    /**
     * is RecyclerView
     */
    public static boolean instanceOfRecyclerView(Object view) {
        Class clazz;
        try {
            clazz = Class.forName("android.support.v7.widget.RecyclerView");
        } catch (ClassNotFoundException th) {
            try {
                clazz = Class.forName("androidx.recyclerview.widget.RecyclerView");
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
        return clazz.isInstance(view);
    }


    /**
     * position RecyclerView item
     */
    public static int getChildAdapterPositionInRecyclerView(View childView, ViewGroup parentView) {

        if (instanceOfRecyclerView(parentView)) {
            try {
                return ((RecyclerView) parentView).getChildAdapterPosition(childView);
            } catch (Throwable th) {
                return ((RecyclerView) parentView).getChildPosition(childView);
            }
        } else if (mHaveCustomRecyclerView) {
            return invokeCRVGetChildAdapterPositionMethod(parentView, childView);
        } else {
            return -1;
        }
    }

    private static boolean haveRecyclerView() {
        try {
            Class.forName("android.support.v7.widget.RecyclerView");
            return true;
        } catch (ClassNotFoundException th) {
            try {
                Class.forName("androidx.recyclerview.widget.RecyclerView");
                return true;
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }

    @TargetApi(9)
    private static void checkCustomRecyclerView(Class<?> viewClass, String viewName) {
        if (!mHaveRecyclerView && !mHaveCustomRecyclerView && viewName != null && viewName.contains("RecyclerView")) {
            try {
                if (findRecyclerInSuper(viewClass) != null && mRecyclerViewGetChildAdapterPositionMethod != null) {
                    mRecyclerViewClass = viewClass;
                    mHaveCustomRecyclerView = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Class<?> findRecyclerInSuper(Class<?> viewClass) {
        while (viewClass != null && !viewClass.equals(ViewGroup.class)) {
            try {
                mRecyclerViewGetChildAdapterPositionMethod = viewClass.getDeclaredMethod("getChildAdapterPosition", new Class[]{View.class});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (mRecyclerViewGetChildAdapterPositionMethod == null) {
                try {
                    mRecyclerViewGetChildAdapterPositionMethod = viewClass.getDeclaredMethod("getChildPosition", new Class[]{View.class});
                } catch (NoSuchMethodException e2) {
                    e2.printStackTrace();
                }
            }
            if (mRecyclerViewGetChildAdapterPositionMethod != null) {
                return viewClass;
            }
            viewClass = viewClass.getSuperclass();
        }
        return null;
    }

    private static int invokeCRVGetChildAdapterPositionMethod(View customRecyclerView, View childView) {
        try {
            if (customRecyclerView.getClass() == mRecyclerViewClass) {
                return ((Integer) mRecyclerViewGetChildAdapterPositionMethod.invoke(customRecyclerView, new Object[]{childView})).intValue();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        }
        return -1;
    }
}
