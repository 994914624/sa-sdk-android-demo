package cn.sa.demo.utils_G;

import android.app.Activity;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.growingio.android.sdk.base.event.DiagnoseEvent;
import com.growingio.android.sdk.collection.AbstractGrowingIO;
import com.growingio.android.sdk.collection.CoreInitialize;
import com.growingio.android.sdk.collection.GConfig;
import com.growingio.android.sdk.collection.MessageProcessor;
import com.growingio.android.sdk.models.ActionEvent;
import com.growingio.android.sdk.models.ActionStruct;
//import com.growingio.android.sdk.models.ViewNode;
//import com.growingio.android.sdk.models.ViewTraveler;
import com.growingio.android.sdk.utils.ActivityUtil;
import com.growingio.android.sdk.utils.ClassExistHelper;
import com.growingio.android.sdk.utils.LinkedString;
import com.growingio.android.sdk.utils.LogUtil;
import com.growingio.android.sdk.utils.Util;
//import com.growingio.android.sdk.utils.WindowHelper;
import com.growingio.android.sdk.view.FloatViewContainer;
import com.growingio.eventcenter.bus.EventBus;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ViewHelper {
    private static final String TAG = "GIO.ViewHelper";
    private static ViewNodeTraveler changeTraveler = new ViewNodeTraveler();
    private static ViewNodeTraveler sClickTraveler = new ViewNodeTraveler() {
        public boolean needTraverse(ViewNode viewNode) {
            return super.needTraverse(viewNode) && !Util.isViewClickable(viewNode.mView);
        }
    };

    private static class ViewNodeTraveler extends ViewTraveler {
        private ArrayList<ActionStruct> actionStructList;
        private long currentTime;

        private ViewNodeTraveler() {
            this.actionStructList = new ArrayList();
        }

        //这里搞一个空实现，只是为了避免编译报错
        @Override
        public void traverseCallBack(com.growingio.android.sdk.models.ViewNode viewNode) {
            Log.i("Imp ViewHelper","traverseCallBack(com.growingio.android.sdk.models.ViewNode viewNode)");
            //traverseCallBack(viewNode);
            if (this.actionStructList != null) {
                ActionStruct struct = new ActionStruct();
                struct.xpath = viewNode.mParentXPath;
                struct.content = viewNode.mViewContent;
                struct.index = viewNode.mLastListPos;
                struct.time = this.currentTime;
                struct.obj = viewNode.mInheritableGrowingInfo;
                this.actionStructList.add(struct);
            }
        }

        public void resetActionStructList() {
            this.currentTime = System.currentTimeMillis();
            this.actionStructList.clear();
        }
        //这里搞一个实现，为了避免编译报错
//        public void traverseCallBack(ViewNode viewNode) {
//            if (this.actionStructList != null) {
//                ActionStruct struct = new ActionStruct();
//                struct.xpath = viewNode.mParentXPath;
//                struct.content = viewNode.mViewContent;
//                struct.index = viewNode.mLastListPos;
//                struct.time = this.currentTime;
//                struct.obj = viewNode.mInheritableGrowingInfo;
//                this.actionStructList.add(struct);
//            }
//        }
    }

    public static boolean isWindowNeedTraverse(View root, String prefix, boolean skipOtherActivity) {
        if (root.hashCode() == CoreInitialize.coreAppState().getCurrentRootWindowsHashCode()) {
            return true;
        }
        if (!(root instanceof FloatViewContainer) && (root instanceof ViewGroup)) {
            if (!skipOtherActivity) {
                return true;
            }
            if (!(root.getWindowVisibility() == GONE || root.getVisibility() != VISIBLE || TextUtils.equals(prefix, WindowHelper.getMainWindowPrefix()) || root.getWidth() == 0 || root.getHeight() == 0)) {
                return true;
            }
        }
        return false;
    }

    public static int getMainWindowCount(View[] windowRootViews) {
        int mainWindowCount = 0;
        WindowHelper.init();
        for (View windowRootView : windowRootViews) {
            if (windowRootView != null) {
                mainWindowCount += WindowHelper.getWindowPrefix(windowRootView).equals(WindowHelper.getMainWindowPrefix()) ? 1 : 0;
            }
        }
        return mainWindowCount;
    }

    public static void traverseWindows(View[] windowRootViews, ViewTraveler traverseHover) {
        boolean skipOtherActivity = true;
        int i = 0;
        if (getMainWindowCount(windowRootViews) <= 1) {
            skipOtherActivity = false;
        }
        WindowHelper.init();
        try {
            int length = windowRootViews.length;
            while (i < length) {
                View root = windowRootViews[i];
                String prefix = WindowHelper.getWindowPrefix(root);
                if (isWindowNeedTraverse(root, prefix, skipOtherActivity)) {
                    traverseWindow(root, prefix, traverseHover);
                }
                i++;
            }
        } catch (OutOfMemoryError e) {
            EventBus.getDefault().post(new DiagnoseEvent("oomt"));
        }
    }

    /**
     *
     */
    public static void traverseWindow(View rootView, String windowPrefix, ViewTraveler callBack) {//
        if (rootView != null) {
            int[] offset = new int[2];
            rootView.getLocationOnScreen(offset);//计算 rootView x/y 坐标
            boolean fullscreen = offset[0] == 0 && offset[1] == 0;
            // ViewNode 真正的遍历。把 rootView 信息给 ViewNode
            ViewNode rootNode = new ViewNode(rootView, 0, -1, Util.isListView(rootView), fullscreen, false, false, LinkedString.fromString(windowPrefix), LinkedString.fromString(windowPrefix), windowPrefix, callBack);
            Object inheritableObject = rootView.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY);
            if (inheritableObject instanceof String) {
                rootNode.mInheritableGrowingInfo = (String) inheritableObject;
            }
            if (!rootNode.isNeedTrack()) {// view 自身可见，且没有被忽略
                return;
            }
            if (WindowHelper.isDecorView(rootView)) {
                Log.i("imp","WindowHelper.isDecorView(rootView)");
                rootNode.traverseChildren();//遍历 DecorView
            } else {
                rootNode.traverseViewsRecur();//
            }
        }
    }

    public static ViewNode getClickViewNode(MenuItem menuItem) {
        int i = 0;
        if (menuItem == null) {
            return null;
        }
        WindowHelper.init();
        View[] windows = WindowHelper.getWindowViews();
        try {
            View window;
            View menuView;
            for (View window2 : windows) {
                if (window2.getClass() == WindowHelper.sPopupWindowClazz) {
                    menuView = findMenuItemView(window2, menuItem);
                    if (menuView != null) {
                        return getClickViewNode(menuView);
                    }
                }
            }
            int length = windows.length;
            while (i < length) {
                View window2 = windows[i];//创建了变量，反编译这里没有变量
                if (window2.getClass() != WindowHelper.sPopupWindowClazz) {
                    menuView = findMenuItemView(window2, menuItem);
                    if (menuView != null) {
                        return getClickViewNode(menuView);
                    }
                }
                i++;
            }
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前 view 的 ViewNode
     * 点击函数里，会获取
     */
    public static ViewNode getClickViewNode(View view) {
        if (!CoreInitialize.config().isEnabled() || view == null) {
            return null;
        }
        if (CoreInitialize.coreAppState().getForegroundActivity() == null || Util.isIgnoredView(view)) {
            return null;
        }
        ViewNode viewNode = getViewNode(view, sClickTraveler);
        if (viewNode == null) {
            return null;
        }
        sClickTraveler.resetActionStructList();
        sClickTraveler.traverseCallBack(viewNode);
        viewNode.traverseChildren();//遍历
        return viewNode;
    }

    @RequiresApi(api = 11)
    public static boolean isViewSelfVisible(View mView) {
        if (mView == null) {
            return false;
        }
        if (WindowHelper.isDecorView(mView)) {
            return true;
        }
        if (mView.getWidth() <= 0 || mView.getHeight() <= 0 || mView.getAlpha() <= 0.0f || !mView.getLocalVisibleRect(new Rect())) {
            return false;
        }
        if ((mView.getVisibility() == VISIBLE || mView.getAnimation() == null || !mView.getAnimation().getFillAfter()) && mView.getVisibility() != VISIBLE) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = 11)
    public static boolean viewVisibilityInParents(View view) {
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
                LogUtil.d(TAG, "Hit detached view: ", viewParent);
                return false;
            }
        }
        return true;
    }

    public static ActionEvent getClickActionEvent(ViewNode viewNode) {
        if (viewNode == null || viewNode.mView == null || !CoreInitialize.config().isEnabled() || CoreInitialize.coreAppState().getForegroundActivity() == null || Util.isIgnoredView(viewNode.mView)) {
            return null;
        }
        ActionEvent click = ActionEvent.makeClickEvent();
        MessageProcessor messageProcessor = CoreInitialize.messageProcessor();
        click.mPageName = messageProcessor.getPageNameWithPending();
        click.elems = sClickTraveler.actionStructList;
        click.setPageTime(messageProcessor.getPTMWithPending());
        return click;
    }

    public static void persistClickEvent(ActionEvent click, ViewNode viewNode) {
        EventBus.getDefault().post(click);
    }

    private static boolean shouldChangeOn(View view, ViewNode viewNode) {
        if (!(view instanceof EditText)) {
            return false;
        }
        Object tag = view.getTag(AbstractGrowingIO.GROWING_MONITORING_FOCUS_KEY);
        String lastText = tag == null ? "" : tag.toString();
        String nowText = ((EditText) view).getText().toString();
        if ((TextUtils.isEmpty(nowText) && TextUtils.isEmpty(lastText)) || lastText.equals(nowText)) {
            return false;
        }
        view.setTag(AbstractGrowingIO.GROWING_MONITORING_FOCUS_KEY, nowText);
        return true;
    }

    public static void changeOn(View view) {
        if (GConfig.sCanHook && CoreInitialize.config().isEnabled() && ActivityUtil.findActivity(view.getContext()) != null && !Util.isIgnoredView(view)) {
            ViewNode viewNode = getViewNode(view, changeTraveler);
            if (viewNode != null && shouldChangeOn(view, viewNode)) {
                changeTraveler.resetActionStructList();
                changeTraveler.traverseCallBack(viewNode);
                ActionEvent change = ActionEvent.makeChangeEvent();
                change.mPageName = CoreInitialize.messageProcessor().getPageNameWithPending();
                change.elems = changeTraveler.actionStructList;
                change.setPageTime(CoreInitialize.messageProcessor().getPTMWithPending());
                EventBus.getDefault().post(change);
            }
        }
    }

    public static ViewNode getViewNode(View view, @Nullable ViewTraveler viewTraveler) {
        String id;
        ArrayList<View> arrayList = new ArrayList(8);
        arrayList.add(view);
        for (ViewParent parent = view.getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
            if (Util.isIgnoredView((View) parent)) {
                return null;
            }
            arrayList.add((ViewGroup) parent);
        }
        int endIndex = arrayList.size() - 1;
        View rootView = (View) arrayList.get(endIndex);
        WindowHelper.init();
        String bannerText = null;
        String inheritableObjInfo = null;
        int viewPosition = 0;
        int listPos = -1;
        boolean mHasListParent = false;
        boolean mParentIdSettled = false;
        String prefix = WindowHelper.getWindowPrefix(rootView);
        String opx = prefix;
        String px = prefix;
        if (!(WindowHelper.isDecorView(rootView) || (rootView.getParent() instanceof View))) {
            opx = opx + "/" + Util.getSimpleClassName(rootView.getClass());
            px = opx;
            if (GConfig.USE_ID) {
                id = Util.getIdName(rootView, false);
                if (id != null) {
                    if (rootView.getTag(AbstractGrowingIO.GROWING_VIEW_ID_KEY) != null) {
                        mParentIdSettled = true;
                    }
                    opx = opx + "#" + id;
                    px = px + "#" + id;
                }
            }
        }
        Object inheritableObject =  rootView.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY);
        if (inheritableObject != null && (inheritableObject instanceof String)) {
            inheritableObjInfo = (String) inheritableObject;
        }
        if (rootView instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) rootView;
            for (int i = endIndex - 1; i >= 0; i--) {
                viewPosition = 0;
                View childView = (View) arrayList.get(i);
                Object viewName = childView.getTag(AbstractGrowingIO.GROWING_VIEW_NAME_KEY);
                if (viewName != null) {
                    opx = "/" + viewName;
                    px = px + "/" + viewName;
                } else {
                    String viewName2 = Util.getSimpleClassName(childView.getClass());
                    viewPosition = parentView.indexOfChild(childView);
                    if (ClassExistHelper.instanceOfAndroidXViewPager(parentView)) {
                        viewPosition = ((ViewPager) parentView).getCurrentItem();
                        mHasListParent = true;
                    } else if (ClassExistHelper.instanceOfSupportViewPager(parentView)) {
                        viewPosition = ((ViewPager) parentView).getCurrentItem();
                        mHasListParent = true;
                    } else if (parentView instanceof AdapterView) {
                        viewPosition += ((AdapterView) parentView).getFirstVisiblePosition();
                        mHasListParent = true;
                    } else if (ClassExistHelper.instanceOfRecyclerView(parentView)) {
                        int adapterPosition = getChildAdapterPositionInRecyclerView(childView, parentView);
                        if (adapterPosition >= 0) {
                            mHasListParent = true;
                            viewPosition = adapterPosition;
                        }
                    }
                    if (parentView instanceof ExpandableListView) {
                        ExpandableListView listParent = (ExpandableListView) parentView;
                        long elp = listParent.getExpandableListPosition(viewPosition);
                        if (ExpandableListView.getPackedPositionType(elp) != 2) {
                            int groupIdx = ExpandableListView.getPackedPositionGroup(elp);
                            int childIdx = ExpandableListView.getPackedPositionChild(elp);
                            if (childIdx != -1) {
                                listPos = childIdx;
                                px = opx + "/ELVG[" + groupIdx + "]/ELVC[-]/" + viewName2 + "[0]";
                                opx = opx + "/ELVG[" + groupIdx + "]/ELVC[" + childIdx + "]/" + viewName2 + "[0]";
                            } else {
                                listPos = groupIdx;
                                px = opx + "/ELVG[-]/" + viewName2 + "[0]";
                                opx = opx + "/ELVG[" + groupIdx + "]/" + viewName2 + "[0]";
                            }
                        } else if (viewPosition < listParent.getHeaderViewsCount()) {
                            opx = opx + "/ELH[" + viewPosition + "]/" + viewName2 + "[0]";
                            px = px + "/ELH[" + viewPosition + "]/" + viewName2 + "[0]";
                        } else {
                            int footerIndex = viewPosition - (listParent.getCount() - listParent.getFooterViewsCount());
                            opx = opx + "/ELF[" + footerIndex + "]/" + viewName2 + "[0]";
                            px = px + "/ELF[" + footerIndex + "]/" + viewName2 + "[0]";
                        }
                    } else if (Util.isListView(parentView)) {
                        Object bannerTag = parentView.getTag(AbstractGrowingIO.GROWING_BANNER_KEY);
                        if (bannerTag != null && (bannerTag instanceof List) && ((List) bannerTag).size() > 0) {
                            viewPosition = Util.calcBannerItemPosition((List) bannerTag, viewPosition);
                            bannerText = Util.truncateViewContent(String.valueOf(((List) bannerTag).get(viewPosition)));
                        }
                        listPos = viewPosition;
                        px = opx + "/" + viewName2 + "[-]";
                        opx = opx + "/" + viewName2 + "[" + listPos + "]";
                    } else if (ClassExistHelper.instanceofAndroidXSwipeRefreshLayout(parentView) || ClassExistHelper.instanceOfSupportSwipeRefreshLayout(parentView)) {
                        opx = opx + "/" + viewName2 + "[0]";
                        px = px + "/" + viewName2 + "[0]";
                    } else {
                        opx = opx + "/" + viewName2 + "[" + viewPosition + "]";
                        px = px + "/" + viewName2 + "[" + viewPosition + "]";
                    }
                    if (GConfig.USE_ID) {
                        id = Util.getIdName(childView, mParentIdSettled);
                        if (id != null) {
                            if (childView.getTag(AbstractGrowingIO.GROWING_VIEW_ID_KEY) != null) {
                                mParentIdSettled = true;
                            }
                            opx = opx + "#" + id;
                            px = px + "#" + id;
                        }
                    }
                }
                inheritableObject = (String) childView.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY);
                if (childView instanceof RadioGroup) {
                    RadioGroup radioGroup = (RadioGroup) childView;
                    View theView = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    if (childView != null) {
                        String childInheritableGrowingInfo = (String) theView.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY);
                        if (!TextUtils.isEmpty(childInheritableGrowingInfo)) {
                            inheritableObject = childInheritableGrowingInfo;
                        }
                    }
                }
                if (inheritableObject instanceof String) {
                    inheritableObjInfo = (String) inheritableObject;
                }
                if (!(childView instanceof ViewGroup)) {
                    break;
                }
                parentView = (ViewGroup) childView;
            }
        }
        inheritableObject = view.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY);
        if (inheritableObject instanceof String) {
            inheritableObjInfo = (String) inheritableObject;
        }
        ViewNode viewNode = new ViewNode(view, viewPosition, listPos, mHasListParent, prefix.equals(WindowHelper.getMainWindowPrefix()), true, mParentIdSettled, LinkedString.fromString(opx), LinkedString.fromString(px), prefix, viewTraveler);
        viewNode.mViewContent = Util.getViewContent(view, bannerText);
        viewNode.mInheritableGrowingInfo = inheritableObjInfo;
        viewNode.mClickableParentXPath = LinkedString.fromString(px);
        viewNode.mBannerText = bannerText;
        return viewNode;
    }

    public static int getChildAdapterPositionInRecyclerView(View childView, ViewGroup parentView) {
        if (ClassExistHelper.instanceOfAndroidXRecyclerView(parentView)) {
            return ((RecyclerView) parentView).getChildAdapterPosition(childView);
        }
        if (ClassExistHelper.instanceOfSupportRecyclerView(parentView)) {
            try {
                return ((RecyclerView) parentView).getChildAdapterPosition(childView);
            } catch (Throwable th) {
                return ((RecyclerView) parentView).getChildPosition(childView);
            }
        } else if (ClassExistHelper.sHasCustomRecyclerView) {
            return ClassExistHelper.invokeCRVGetChildAdapterPositionMethod(parentView, childView);
        } else {
            return -1;
        }
    }

    private static View findMenuItemView(View view, MenuItem item) throws InvocationTargetException, IllegalAccessException {
        if (WindowHelper.getMenuItemData(view) == item) {
            return view;
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View menuView = findMenuItemView(((ViewGroup) view).getChildAt(i), item);
                if (menuView != null) {
                    return menuView;
                }
            }
        }
        return null;
    }

    public static boolean isContentView(Activity activity, View view) {
        if (activity == null || view == null || view.getContext() == null || ActivityUtil.findActivity(view.getContext()) != activity) {
            return false;
        }
        return true;
    }
}