package cn.sa.demo.utils_G;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import static android.view.View.VISIBLE;

/**
 * Created by yzk on 2019/5/11
 */

public class FrgUtil {

    /**
     * Fragment 是否可见
     * @return true 表示可见。
     * （ 当 fragment.isVisible 且 fragment.getView view 自身可见且所有的 ViewParent 可见，且所有 getParentFragment 也要符合此条件，返回 true ）
     */
    @TargetApi(11)
    public static boolean isVisible(@NonNull Fragment fragment) {
        if (Build.VERSION.SDK_INT < 17) {
            return fragment.isVisible();
        }
        for (Fragment current = fragment; current != null; current = current.getParentFragment()) {
            View view = current.getView();
            if (!current.isVisible() || !viewVisibilityInParents(view)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 当前 View 在父容器中是否可见
     *
     * @return true 表示可见
     * （当 View 自身可见，且所有的 ViewParent 可见时，返回true）
     */
    @RequiresApi(api = 11)
    private static boolean viewVisibilityInParents(View view) {
        if (view == null) {
            return false;
        }
        if (!isViewSelfVisible(view)) {
            return false;
        }
        ViewParent viewParent = view.getParent();
        while (viewParent instanceof View) {
            if (!isViewSelfVisible((View) viewParent)) {
                return false;
            }
            viewParent = viewParent.getParent();
            if (viewParent == null) {
                //LogUtil.d(TAG, "Hit detached view: ", viewParent);
                return false;
            }
        }
        return true;
    }

    /**
     * View 自身是否可见
     *
     * @return DecorView 时返回 true
     *         View 宽、高、透明度 有一个 < 0 时，或 getLocalVisibleRect 为 false 时；返回 false 。
     *         View getVisibility 不可见，且有 Animation getFillAfter 为  false 时；返回 false 。
     *         View 无 Animation 时 getVisibility 不可见时返回 false 。
     */
    @RequiresApi(api = 11)
    private static boolean isViewSelfVisible(View mView) {
        if (mView == null) {
            return false;
        }
        if (WindowHelper.isDecorView(mView)) {
            return true;
        }
        if (mView.getWidth() <= 0 || mView.getHeight() <= 0 || mView.getAlpha() <= 0.0f || !mView.getLocalVisibleRect(new Rect())) {
            return false;
        }
        return (mView.getVisibility() != VISIBLE && mView.getAnimation() != null && mView.getAnimation().getFillAfter()) || mView.getVisibility() == VISIBLE;
    }


}
