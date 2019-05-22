package cn.sa.demo.utils_G;


import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import com.growingio.android.sdk.autoburry.VdsJsBridgeManager;
import com.growingio.android.sdk.collection.AbstractGrowingIO;
import com.growingio.android.sdk.collection.CoreInitialize;
import com.growingio.android.sdk.collection.GConfig;
import com.growingio.android.sdk.models.ActionEvent;
import com.growingio.android.sdk.models.ActionStruct;
//import com.growingio.android.sdk.models.ViewNode;
//import com.growingio.android.sdk.models.ViewTraveler;
import com.growingio.android.sdk.utils.ClassExistHelper;
import com.growingio.android.sdk.utils.ImplEventAsyncExecutor;
import com.growingio.android.sdk.utils.SysTrace;
//import com.growingio.android.sdk.utils.ViewHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class ActionCalculator {
    static final String TAG = "GIO.ActionCalculator";
    private GConfig mConfig;
    private SparseBooleanArray mImpressedViews = new SparseBooleanArray();
    private List<WeakReference<View>> mImpressedWebView = new ArrayList();
    List<ActionStruct> mNewImpressViews;
    private String mPage;
    private long mPtm;
    private WeakReference<View> mRootView;
    private List<com.growingio.android.sdk.models.ViewNode> mTodoViewNode = new ArrayList();
    private ViewTraveler mViewTraveler = new ViewTraveler() {


        @Override
        public void traverseCallBack(com.growingio.android.sdk.models.ViewNode viewNode) {
            //空实现
            //traverseCallBack(viewNode);
            Log.i("imp ActionCalculator","traverseCallBack(com.growingio.android.sdk.models.ViewNode viewNode)");
            Log.i("imp ActionCalculator","当前遍历的 ---> :"+ActionCalculator.genActionStruct((cn.sa.demo.utils_G.ViewNode)viewNode).toJson().toString());
            boolean isNew = false;
            if (ActionCalculator.this.mConfig.isImageViewCollectionEnable() && (viewNode.mView instanceof ImageView) && TextUtils.isEmpty(viewNode.mViewContent)) {
                ActionCalculator.this.mTodoViewNode.add(viewNode);

                return;
            }
            Log.i("imp traverseCallBack","mImpressedViews ---> :");
            // 如果当前的 viewNode 之前没有 Imp ，会产生 Imp
            if (!ActionCalculator.this.mImpressedViews.get(viewNode.hashCode())) {
                ActionStruct actionStruct = ActionCalculator.genActionStruct((cn.sa.demo.utils_G.ViewNode)viewNode);
                // 保存已经 Imp 的 viewNode
                ActionCalculator.this.mImpressedViews.put(viewNode.hashCode(), true);
                Log.i("imp traverseCallBack","mNewImpressViews ---> :"+actionStruct.toJson().toString());
                // 记录新的 Imp
                ActionCalculator.this.mNewImpressViews.add(actionStruct);
                isNew = true;
            }


            if ((viewNode.mView instanceof WebView) || ClassExistHelper.instanceOfX5WebView(viewNode.mView)) {
                for (WeakReference<View> view : ActionCalculator.this.mImpressedWebView) {
                    if (view.get() == viewNode.mView) {
                        isNew = false;
                        break;
                    }
                }
                if (isNew) {
                    ActionCalculator.this.mImpressedWebView.add(new WeakReference(viewNode.mView));
                    VdsJsBridgeManager.refreshImpressionForce(viewNode.mView);
                }
            }
        }

//        public void traverseCallBack(ViewNode viewNode) {
//            boolean isNew = false;
//            if (ActionCalculator.this.mConfig.isImageViewCollectionEnable() && (viewNode.mView instanceof ImageView) && TextUtils.isEmpty(viewNode.mViewContent)) {
//                ActionCalculator.this.mTodoViewNode.add(viewNode);
//                return;
//            }
//            Log.i("Imp traverseCallBack","mImpressedViews ---> :");
//            // 如果当前的 viewNode 之前没有 Imp ，会产生 Imp
//            if (!ActionCalculator.this.mImpressedViews.get(viewNode.hashCode())) {
//                ActionStruct actionStruct = ActionCalculator.genActionStruct(viewNode);
//                // 保存已经 Imp 的 viewNode
//                ActionCalculator.this.mImpressedViews.put(viewNode.hashCode(), true);
//                Log.i("Imp traverseCallBack","mNewImpressViews ---> :"+actionStruct.toJson().toString());
//                // 记录新的 Imp
//                ActionCalculator.this.mNewImpressViews.add(actionStruct);
//                isNew = true;
//            }
//
//
//            if ((viewNode.mView instanceof WebView) || ClassExistHelper.instanceOfX5WebView(viewNode.mView)) {
//                for (WeakReference<View> view : ActionCalculator.this.mImpressedWebView) {
//                    if (view.get() == viewNode.mView) {
//                        isNew = false;
//                        break;
//                    }
//                }
//                if (isNew) {
//                    ActionCalculator.this.mImpressedWebView.add(new WeakReference(viewNode.mView));
//                    VdsJsBridgeManager.refreshImpressionForce(viewNode.mView);
//                }
//            }
//        }

        public boolean needTraverse(ViewNode viewNode) {
            //
            return super.needTraverse(viewNode) && !viewNode.isIgnoreImp();
        }
    };
    private final String mWindowPrefix;

    public ActionCalculator(String pageName, long ptm, View root, String windowPrefix) {
        this.mPtm = ptm;
        this.mRootView = new WeakReference(root);
        this.mPage = pageName;
        this.mWindowPrefix = windowPrefix;
        this.mConfig = CoreInitialize.config();
    }

    /**
     * 产生 Imp 关键方法
     */
    @Nullable
    public List<ActionEvent> obtainImpress() {
        SysTrace.beginSection("gio.obtainImpress");
        List<ActionEvent> events = null;
        if (this.mConfig != null && this.mConfig.shouldSendImp()) {
            //  mNewImpressViews 表示当前曝光的 View 的信息
            this.mNewImpressViews = new ArrayList();
            if (!(this.mRootView == null || this.mRootView.get() == null || ((View) this.mRootView.get()).getTag(AbstractGrowingIO.GROWING_IGNORE_VIEW_IMP_KEY) != null)) {
                // 遍历 rootView ，记录 xpath 在 ViewNode 中 （traverseCallBack 能拿到 viewNode）
                ViewHelper.traverseWindow((View) this.mRootView.get(), this.mWindowPrefix, this.mViewTraveler);
            }
            events = new ArrayList(2);

            //拿到 mNewImpressViews 构造新的 impEvents
            ActionEvent impEvents = makeActionEvent(events);
            // 构造完新的 imp 事件后 置为空
            this.mNewImpressViews = null;

            if (this.mTodoViewNode.size() > 0) {// first =0
                if (impEvents == null) {
                    impEvents = ActionEvent.makeImpEvent();
                    impEvents.setPageTime(this.mPtm);
                    impEvents.mPageName = this.mPage;
                }
                ImplEventAsyncExecutor.getInstance().execute(impEvents, this.mTodoViewNode);
                this.mTodoViewNode = new ArrayList();
                SysTrace.endSection();
                return null;
            }
        }
        SysTrace.endSection();
        return events;
    }

    ActionEvent makeActionEvent(List<ActionEvent> events) {
        int newImpSize = this.mNewImpressViews.size();
        Log.i("imp makeActionEvent","mNewImpressViews.size() ----> :"+newImpSize);
        if (newImpSize <= 0) {
            return null;
        }
        ActionEvent actionEvent = null;
        int i = 0;
        while (i < newImpSize) {
            int end = Math.min(i + 100, newImpSize);
            List<ActionStruct> actions = this.mNewImpressViews.subList(i, end);
            actionEvent = ActionEvent.makeImpEvent();
            actionEvent.elems = actions;
            actionEvent.setPageTime(this.mPtm);
            actionEvent.mPageName = this.mPage;
            events.add(actionEvent);
            i = end;
        }
        return actionEvent;
    }

    public long getPtm() {
        return this.mPtm;
    }

    public String getPage() {
        return this.mPage;
    }

    public static ActionStruct genActionStruct(ViewNode viewNode) {
        ActionStruct actionStruct = new ActionStruct();
        actionStruct.xpath = viewNode.mParentXPath;
        actionStruct.time = System.currentTimeMillis();
        actionStruct.index = viewNode.mLastListPos;
        actionStruct.content = viewNode.mViewContent;
        actionStruct.obj = viewNode.mInheritableGrowingInfo;
        actionStruct.imgHashcode = viewNode.mImageViewDHashCode;
        return actionStruct;
    }
}