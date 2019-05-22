package cn.sa.demo.utils_G;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import com.growingio.android.sdk.collection.AbstractGrowingIO;
import com.growingio.android.sdk.collection.GConfig;
import com.growingio.android.sdk.models.Screenshot;
//import com.growingio.android.sdk.models.ViewTraveler;
import com.growingio.android.sdk.utils.ClassExistHelper;
import com.growingio.android.sdk.utils.LinkedString;
import com.growingio.android.sdk.utils.LogUtil;
import com.growingio.android.sdk.utils.Util;
//import com.growingio.android.sdk.utils.ViewHelper;
//import com.growingio.android.sdk.utils.WindowHelper;
import java.util.List;

public class ViewNode extends com.growingio.android.sdk.models.ViewNode{
    public static final String ANONYMOUS_CLASS_NAME = "Anonymous";
    private static final String TAG = "GIO.ViewNode";
    public String mBannerText;
    public LinkedString mClickableParentXPath;
    public Rect mClipRect;
    public boolean mFullScreen;
    public boolean mHasListParent;
    private int mHashCode = -1;
    public String mImageViewDHashCode;
    public boolean mInClickableGroup;
    public String mInheritableGrowingInfo;
    public int mLastListPos = -1;

    public LinkedString mOriginalParentXpath;
    public boolean mParentIdSettled = false;

    public LinkedString mParentXPath;

    public String mPatternXPath;
    public Screenshot mScreenshot;
    public View mView;
    public String mViewContent;
    private int mViewIndex;
    public String mViewName;
    public int mViewPosition;
    ViewTraveler mViewTraveler;
    public WebElementInfo mWebElementInfo;
    public String mWindowPrefix;

    public static class WebElementInfo {
        public String mHost;
        public String mHref;
        public String mNodeType;
        public String mPath;
        public String mQuery;
    }

    public void setViewTraveler(ViewTraveler viewTraveler) {
        this.mViewTraveler = viewTraveler;
    }

    public ViewNode(View view, int viewIndex, int lastListPos, boolean hasListParent, boolean fullScreen, boolean inClickableGroup, boolean parentIdSettled, LinkedString originalParentXPath, LinkedString parentXPath, String windowPrefix, ViewTraveler viewTraveler) {
        this.mView = view;
        this.mLastListPos = lastListPos;
        this.mFullScreen = fullScreen;
        this.mViewIndex = viewIndex;
        this.mHasListParent = hasListParent;
        this.mInClickableGroup = inClickableGroup;
        this.mParentIdSettled = parentIdSettled;
        this.mParentXPath = parentXPath;
        this.mOriginalParentXpath = originalParentXPath;
        this.mWindowPrefix = windowPrefix;
        this.mViewTraveler = viewTraveler;
        if (GConfig.ISRN() && GConfig.isUsingRNOptimizedPath() && this.mView != null) {
            identifyRNChangeablePath();
        }
    }

    /**
     *
     */
    public void traverseViewsRecur() {//
        if (this.mViewTraveler != null && this.mViewTraveler.needTraverse(this)) {//关键的判断，isNeedTrack
            this.mViewName = Util.getSimpleClassName(this.mView.getClass());
            Log.i("imp"," traverseViewsRecur: "+this.mView.hashCode());
            viewPosition();
            calcXPath();
            viewContent();
            if (needTrack()) {
                Log.i("imp","------------遍历好一个 viewNode -----------:"+this.mView.hashCode());
                this.mViewTraveler.traverseCallBack(this);//遍历好 viewNode 时回调
            }
            if (!ClassExistHelper.instanceOfX5WebView(this.mView)) {
                Log.i("imp","no instanceOfX5WebView traverseViewsRecur:"+this.mView.hashCode());
                traverseChildren();
            }
        }
    }

    /**
     *
     */
    public void traverseChildren() {
        Log.i("imp","traverseChildren: "+this.mView.hashCode());
        if ((this.mView instanceof ViewGroup) && !(this.mView instanceof Spinner)) {
            ViewGroup viewGroup = (ViewGroup) this.mView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                boolean z;
                boolean z2;
                View childView = viewGroup.getChildAt(i);
                int i2 = this.mLastListPos;
                if (this.mHasListParent || Util.isListView(this.mView)) {
                    z = true;
                } else {
                    z = false;
                }
                boolean z3 = this.mFullScreen;
                if (this.mInClickableGroup || Util.isViewClickable(this.mView)) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                Log.i("imp","childViewNode："+viewGroup.getChildCount()+" "+this.mView.hashCode());
                ViewNode childViewNode = new ViewNode(childView, i, i2, z, z3, z2, this.mParentIdSettled, LinkedString.copy(this.mOriginalParentXpath), LinkedString.copy(this.mParentXPath), this.mWindowPrefix, this.mViewTraveler);
                if (Util.isViewClickable(this.mView)) {
                    childViewNode.mClickableParentXPath = this.mParentXPath;
                } else {
                    childViewNode.mClickableParentXPath = this.mClickableParentXPath;
                }
                childViewNode.mBannerText = this.mBannerText;
                childViewNode.mInheritableGrowingInfo = this.mInheritableGrowingInfo;
                childViewNode.traverseViewsRecur();
            }
        }
    }

    /**
     *
     * mViewContent + mParentXPath  的 hashCode
     */
    public int hashCode() {
        int i = 0;
        if (this.mHashCode == -1) {
            int hashCode;
            if (this.mViewContent != null) {
                hashCode = this.mViewContent.hashCode();
            } else {
                hashCode = 0;
            }
            int i2 = (hashCode + 527) * 31;
            if (this.mParentXPath != null) {
                hashCode = this.mParentXPath.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (i2 + hashCode) * 31;
            if (this.mInheritableGrowingInfo != null) {
                i = this.mInheritableGrowingInfo.hashCode();
            }
            this.mHashCode = ((hashCode + i) * 31) + this.mLastListPos;
        }
        return this.mHashCode;
    }

    public boolean equals(Object object) {
        return (object instanceof ViewNode) && object.hashCode() == hashCode();
    }

    public ViewNode copyWithoutView() {
        return new ViewNode(null, this.mViewIndex, this.mLastListPos, this.mHasListParent, this.mFullScreen, this.mInClickableGroup, this.mParentIdSettled, LinkedString.fromString(this.mOriginalParentXpath.toStringValue()), LinkedString.fromString(this.mParentXPath.toStringValue()), this.mWindowPrefix, null);
    }

    @RequiresApi(api = 11)
    public boolean isNeedTrack() {
        return ViewHelper.isViewSelfVisible(this.mView) && !Util.isIgnoredView(this.mView);
    }

    public boolean isIgnoreImp() {
        return this.mView.getTag(AbstractGrowingIO.GROWING_IGNORE_VIEW_IMP_KEY) != null;
    }

    private void viewContent() {
        if (this.mView.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY) != null) {
            this.mInheritableGrowingInfo = (String) this.mView.getTag(AbstractGrowingIO.GROWING_INHERIT_INFO_KEY);
        }
        this.mViewContent = Util.getViewContent(this.mView, this.mBannerText);
    }

    private void viewPosition() {
        int idx = this.mViewIndex;
        if (this.mView.getParent() != null && (this.mView.getParent() instanceof ViewGroup)) {
            ViewGroup parent = (ViewGroup) this.mView.getParent();
            if (ClassExistHelper.instanceOfAndroidXViewPager(parent)) {
                idx = ((ViewPager) parent).getCurrentItem();
            } else if (ClassExistHelper.instanceOfSupportViewPager(parent)) {
                idx = ((ViewPager) parent).getCurrentItem();
            } else if (parent instanceof AdapterView) {
                idx = ((AdapterView) parent).getFirstVisiblePosition() + this.mViewIndex;
            } else if (ClassExistHelper.instanceOfRecyclerView(parent)) {
                int adapterPosition = ViewHelper.getChildAdapterPositionInRecyclerView(this.mView, parent);
                if (adapterPosition >= 0) {
                    idx = adapterPosition;
                }
            }
        }
        this.mViewPosition = idx;
    }

    /**
     *
     */
    private void calcXPath() {
        ViewParent parentObject = this.mView.getParent();
        if (parentObject == null) {
            return;
        }
        if (!WindowHelper.isDecorView(this.mView) || (parentObject instanceof View)) {
            Object viewName = this.mView.getTag(AbstractGrowingIO.GROWING_VIEW_NAME_KEY);
            if (viewName != null) {
                this.mOriginalParentXpath = LinkedString.fromString("/").append(viewName);
                this.mParentXPath.append("/").append(viewName);
                return;
            }
            if (parentObject instanceof View) {
                View parent = (View) parentObject;
                if (parent instanceof ExpandableListView) {
                    ExpandableListView listParent = (ExpandableListView) parent;
                    long elp = ((ExpandableListView) this.mView.getParent()).getExpandableListPosition(this.mViewPosition);
                    if (ExpandableListView.getPackedPositionType(elp) == 2) {
                        this.mHasListParent = false;
                        if (this.mViewPosition < listParent.getHeaderViewsCount()) {
                            this.mOriginalParentXpath.append("/ELH[").append(Integer.valueOf(this.mViewPosition)).append("]/").append(this.mViewName).append("[0]");
                            this.mParentXPath.append("/ELH[").append(Integer.valueOf(this.mViewPosition)).append("]/").append(this.mViewName).append("[0]");
                        } else {
                            int footerIndex = this.mViewPosition - (listParent.getCount() - listParent.getFooterViewsCount());
                            this.mOriginalParentXpath.append("/ELF[").append(Integer.valueOf(footerIndex)).append("]/").append(this.mViewName).append("[0]");
                            this.mParentXPath.append("/ELF[").append(Integer.valueOf(footerIndex)).append("]/").append(this.mViewName).append("[0]");
                        }
                    } else {
                        int groupIdx = ExpandableListView.getPackedPositionGroup(elp);
                        int childIdx = ExpandableListView.getPackedPositionChild(elp);
                        if (childIdx != -1) {
                            this.mLastListPos = childIdx;
                            this.mParentXPath = LinkedString.fromString(this.mOriginalParentXpath.toStringValue()).append("/ELVG[").append(Integer.valueOf(groupIdx)).append("]/ELVC[-]/").append(this.mViewName).append("[0]");
                            this.mOriginalParentXpath.append("/ELVG[").append(Integer.valueOf(groupIdx)).append("]/ELVC[").append(Integer.valueOf(childIdx)).append("]/").append(this.mViewName).append("[0]");
                        } else {
                            this.mLastListPos = groupIdx;
                            this.mParentXPath = LinkedString.fromString(this.mOriginalParentXpath.toStringValue()).append("/ELVG[-]/").append(this.mViewName).append("[0]");
                            this.mOriginalParentXpath.append("/ELVG[").append(Integer.valueOf(groupIdx)).append("]/").append(this.mViewName).append("[0]");
                        }
                    }
                } else if (Util.isListView(parent)) {
                    Object bannerTag = parent.getTag(AbstractGrowingIO.GROWING_BANNER_KEY);
                    if ((bannerTag instanceof List) && ((List) bannerTag).size() > 0) {
                        this.mViewPosition = Util.calcBannerItemPosition((List) bannerTag, this.mViewPosition);
                        this.mBannerText = Util.truncateViewContent(String.valueOf(((List) bannerTag).get(this.mViewPosition)));
                    }
                    this.mLastListPos = this.mViewPosition;
                    this.mParentXPath = LinkedString.fromString(this.mOriginalParentXpath.toStringValue()).append("/").append(this.mViewName).append("[-]");
                    this.mOriginalParentXpath.append("/").append(this.mViewName).append("[").append(Integer.valueOf(this.mLastListPos)).append("]");
                } else if (ClassExistHelper.instanceofAndroidXSwipeRefreshLayout(parentObject) || ClassExistHelper.instanceOfSupportSwipeRefreshLayout(parentObject)) {
                    this.mOriginalParentXpath.append("/").append(this.mViewName).append("[0]");
                    this.mParentXPath.append("/").append(this.mViewName).append("[0]");
                } else {
                    this.mOriginalParentXpath.append("/").append(this.mViewName).append("[").append(Integer.valueOf(this.mViewPosition)).append("]");
                    this.mParentXPath.append("/").append(this.mViewName).append("[").append(Integer.valueOf(this.mViewPosition)).append("]");
                }
            } else {
                this.mOriginalParentXpath.append("/").append(this.mViewName).append("[").append(Integer.valueOf(this.mViewPosition)).append("]");
                this.mParentXPath.append("/").append(this.mViewName).append("[").append(Integer.valueOf(this.mViewPosition)).append("]");
            }
            if (GConfig.USE_ID) {
                String id = Util.getIdName(this.mView, this.mParentIdSettled);
                if (id != null) {
                    if (this.mView.getTag(AbstractGrowingIO.GROWING_VIEW_ID_KEY) != null) {
                        this.mParentIdSettled = true;
                    }
                    this.mOriginalParentXpath.append("#").append(id);
                    this.mParentXPath.append("#").append(id);
                }
            }
        }
    }

    /**
     * 关键的判断，控制遍历完成，哪些控件应该曝光
     * 如果当前 view 可点击，或者是 TextView/ImageView/Spinner/RatingBar/SeekBar/WebView/X5WebView，
     * 或者 parentView 是 AdapterView/RadioGroup 时，返回 true 。
     *
     */
    private boolean needTrack() {
        ViewParent parent = this.mView.getParent();
        return parent != null && (this.mView.isClickable() || this.mView instanceof TextView || this.mView instanceof ImageView || this.mView instanceof WebView || parent instanceof AdapterView || parent instanceof RadioGroup || this.mView instanceof Spinner || this.mView instanceof RatingBar || this.mView instanceof SeekBar || ClassExistHelper.instanceOfX5WebView(this.mView));
    }

    public void getVisibleRect(View view, Rect rect, boolean fullscreen) {
        if (fullscreen) {
            view.getGlobalVisibleRect(rect);
            return;
        }
        int[] offset = new int[2];
        view.getLocationOnScreen(offset);
        view.getLocalVisibleRect(rect);
        rect.offset(offset[0], offset[1]);
    }

    private void identifyRNChangeablePath() {
        ViewParent parent = this.mView.getParent();
        if (parent instanceof View) {
            boolean shouldRemoveChangeablePath = false;
            View viewParent = (View) parent;
            Object viewRNPage = this.mView.getTag(AbstractGrowingIO.GROWING_RN_PAGE_KEY);
            Object viewParentRNPage = viewParent.getTag(AbstractGrowingIO.GROWING_RN_PAGE_KEY);
            LogUtil.d("GIO.HandleRNView", "IdentifyRNChangeablePath: ", this.mView.getClass().getName());
            LogUtil.d("GIO.HandleRNView", "mParentXPath: ", this.mParentXPath);
            LogUtil.d("GIO.HandleRNView", "viewRNPage: ", viewRNPage);
            if (viewRNPage != null) {
                if (!viewRNPage.equals(viewParentRNPage)) {
                    shouldRemoveChangeablePath = true;
                }
            } else if (viewParentRNPage != null) {
                shouldRemoveChangeablePath = true;
            }
            if (shouldRemoveChangeablePath) {
                LogUtil.d("GIO.HandleRNView", "viewParentRNPage: ", viewParentRNPage);
                removeRNChangeablePath();
            }
        }
    }

    private void removeRNChangeablePath() {
        this.mParentXPath = new LinkedString();
        this.mOriginalParentXpath = new LinkedString();
        this.mViewIndex = 0;
    }
}