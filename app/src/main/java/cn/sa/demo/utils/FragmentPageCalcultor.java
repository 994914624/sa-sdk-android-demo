package cn.sa.demo.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import cn.sa.demo.R;
import cn.sa.demo.entity.ViewNodeEntity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by yzk on 2019/5/31
 */

public class FragmentPageCalcultor {

    private final static String TAG = "FragmentPageCalcultor";
    private WeakReference<View> mDecorViewRef;
    // 存储上次曝光的 frgs
    private SparseBooleanArray mCachedLastFrgs = new SparseBooleanArray();
    // 当前页面的 frgs
    private SparseArray currentFrgs = new SparseArray();


    FragmentPageCalcultor(View root, String activityName, long time) {
        this.mDecorViewRef = new WeakReference<>(root);
    }

    /**
     * 遍历 ViewTree
     */
    public void traverseViewTree() {
        if (mDecorViewRef == null || mDecorViewRef.get() == null) {
            return;
        }
        View view = mDecorViewRef.get();
        if (view instanceof ViewGroup) {
            if (view.getWindowVisibility() != GONE && view.getWidth() != 0 && view.getHeight() != 0 && view.getVisibility() == VISIBLE) {
                Log.e(TAG, "traverseViewTree");
                traverseView(mDecorViewRef.get());
                //遍历完后，可以处理 Frg 浏览页面事件
                handleFrgs();
            }
        }
    }


    /**
     * 遍历 View
     */
    private void traverseView(View view) {
        if (view instanceof ViewGroup && !(view instanceof Spinner)) {
            Log.e(TAG, "traverseView: " + view.getClass().getSimpleName());
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                // 收集 frg 页面
                collectFrgs(childView);
                // childView 自身可见
                if (ViewUtil.isViewSelfVisible(childView)) {
                    // 递归
                    traverseView(childView);
                }
            }
        }


    }

    /**
     * 收集 frg 页面
     */
    private void collectFrgs(View childView) {
        if (childView.getTag(R.id.fragment_root_view) != null) {
            Object fragment = childView.getTag(R.id.fragment_root_view);
            if (FrgUtil.isVisible(fragment)) {
                int position = -1;
                if (childView.getParent() != null && childView.getParent() instanceof ViewPager) {
                    ViewPager viewPager = (ViewPager) childView.getParent();
                    position = viewPager.getCurrentItem();
                }
                int hashCode = fragment.hashCode();
                // 加上 frg 在 ViewPager 中的位置
                hashCode = hashCode + position;
                // 把当前页面中 frg 的 hashcode 存起来
                currentFrgs.append(hashCode, fragment);
                Log.i("yyyy", " 遍历到了一个 fragment 页面：(" + hashCode + ")" + fragment.getClass().getSimpleName());
            }
        }
    }

    /**
     * 处理 frg 页面
     */
    private void handleFrgs() {
        for (int i = 0; i < currentFrgs.size(); i++) {
            if (!mCachedLastFrgs.get(currentFrgs.keyAt(i))) {
                // TODO 如果当前页面中 frag 的 hashcode 和上次缓存不一致时，触发 frg 浏览页面事件
                Log.i("yyyy", "----------------< 触发 Fragment 浏览页面事件 >------------:" + currentFrgs.valueAt(i).getClass().getSimpleName());
            }
        }
        // 清空之前
        mCachedLastFrgs.clear();
        // 存储当前
        for (int i = 0; i < currentFrgs.size(); i++) {
            mCachedLastFrgs.put(currentFrgs.keyAt(i), true);
        }
        //当前的也清空
        currentFrgs.clear();
    }

}
