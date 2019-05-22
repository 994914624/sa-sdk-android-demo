package cn.sa.demo.utils;

import android.text.TextUtils;
import android.util.Log;
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
import cn.sa.demo.entity.ViewNodeEntity;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by yzk on 2019/5/16
 */

public class ElementDisplayCalcultor {

    private final static String TAG = "ElementDisplayCalcultor";



    private String mActivityName;
    private WeakReference<View> mDecorViewRef;
    private long timeStamp;
    // 存储已曝光过的 View 的 hashcode
    private SparseBooleanArray mCachedViewNode = new SparseBooleanArray();
    // 待曝光的 view
    private List<ViewNodeEntity> mNewViewNode;

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getmActivityName() {
        return mActivityName;
    }


    public List<ViewNodeEntity> getNewViewNode() {
        return mNewViewNode;
    }

    public void resetNewViewNode() {
        this.mNewViewNode = new ArrayList<>();
    }

    ElementDisplayCalcultor(View root, String activityName, long time) {
        this.mDecorViewRef = new WeakReference<>(root);
        this.mActivityName = activityName;
        this.timeStamp = time;
        mNewViewNode = new ArrayList();
    }

    /**
     * 遍历 ViewTree
     */
    public void traverseViewTree() {
        if (mDecorViewRef == null || mDecorViewRef.get() == null) {
            return;
        }
        View view = mDecorViewRef.get();
        if (view instanceof ViewGroup) {//|| view.getWidth() == 0 || view.getHeight() == 0|| view.getVisibility() != VISIBLE||view.getWindowVisibility() == GONE
            if (view.getWindowVisibility() != GONE && view.getWidth() != 0 && view.getHeight() != 0 && view.getVisibility() == VISIBLE) {
                traverseView(mDecorViewRef.get(), null);
            }
        }
    }

    /**
     * 遍历 View
     */
    private void traverseView(View view, ViewNodeEntity viewNodeEntity) {
        if (view instanceof ViewGroup && ! (view instanceof Spinner)) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);

                // viewPath & 元素内容
                viewNodeEntity = collectViewPath(view, viewNodeEntity, i);

                // childView 自身可见
                if (ViewUtil.isViewSelfVisible(childView)) {
                    // 遍历完成
                    traverseOK(childView, viewNodeEntity);
                    // 递归，并把 viewPath 向下传递
                    traverseView(childView, new ViewNodeEntity(viewNodeEntity.getViewPath()).setViewPosition(viewNodeEntity.getViewPosition()));

                }
            }
        }


    }

    private ViewNodeEntity collectViewPath(View view, ViewNodeEntity viewNodeEntity, int index) {
        // view 的 index
        int position = index;
        if (view.getParent() != null && (view.getParent() instanceof ViewGroup)) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (ViewUtil.instanceOfViewPager(parent)) {
                position = ((ViewPager) parent).getCurrentItem();
            } else if (parent instanceof AdapterView) {
                position = ((AdapterView) parent).getFirstVisiblePosition() + index;
            } else if (ViewUtil.instanceOfRecyclerView(parent)) {
                int adapterPosition = ViewUtil.getChildAdapterPositionInRecyclerView(view, parent);
                if (adapterPosition >= 0) {
                    position = adapterPosition;
                }
            }
        }
        // viewPath
        if (viewNodeEntity == null) {
            viewNodeEntity = new ViewNodeEntity("\\/" + ViewUtil.getSimpleClassName(view.getClass()) + "[0]");
        } else {
            if (index == 0) {// 第一次时设置 ViewGroup 的 ViewPath
                //加上 ViewID
                String viewId = ViewUtil.getViewId(view);
                if (!TextUtils.isEmpty(viewId)) {
                    viewId = "#" + viewId;
                }
                viewNodeEntity.setViewPath(ViewUtil.getSimpleClassName(view.getClass()) + "[" + viewNodeEntity.getViewPosition() + "]" + viewId);
            }
            viewNodeEntity.setViewPosition(position);
        }
        return viewNodeEntity;
    }

    /**
     * 遍历完成
     */
    private void traverseOK(View view, ViewNodeEntity viewNodeEntity) {
        ViewParent viewParent = view.getParent();
        if (viewParent == null) {
            return;
        }

        if (view.isClickable() || view instanceof TextView || view instanceof ImageView || view instanceof WebView || view instanceof Spinner || view instanceof
                RatingBar || view instanceof SeekBar || viewParent instanceof AdapterView || viewParent instanceof RadioGroup) {
            //遍历好一个 View
            //加上 ViewID
            String viewId = ViewUtil.getViewId(view);
            if (!TextUtils.isEmpty(viewId)) {
                viewId = "#" + viewId;
            }
            String viewPath = viewNodeEntity.getViewPath() + "\\/" + ViewUtil.getSimpleClassName(view.getClass()) + "[" + viewNodeEntity.getViewPosition() + "]" + viewId;
            String viewContent = ViewUtil.getViewContent(view);
            Log.e(TAG, mActivityName + "(^o^) 遍历好一个 ViewPath: --> " + viewPath + "---@" + viewContent);

            //缓存 hashcode
            ViewNodeEntity okViewNodeEntity = new ViewNodeEntity(mActivityName,viewPath,viewContent);
            int hashCode = okViewNodeEntity.hashCode();
            if(!mCachedViewNode.get(hashCode)){
                mNewViewNode.add(okViewNodeEntity);
                mCachedViewNode.put(hashCode,true);
            }
        }
    }


}
