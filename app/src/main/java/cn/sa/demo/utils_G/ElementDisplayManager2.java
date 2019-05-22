package cn.sa.demo.utils_G;

import android.app.Activity;
import android.util.Log;
import android.view.View;
//import com.growingio.android.sdk.collection.ActionCalculator;
import com.growingio.android.sdk.collection.CoreInitialize;
import com.growingio.android.sdk.collection.MessageProcessor;
import com.growingio.android.sdk.models.ActionEvent;
//import com.growingio.android.sdk.utils.ThreadUtils;
//import com.growingio.android.sdk.utils.ViewHelper;
//import com.growingio.android.sdk.utils.WindowHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yzk on 2019/5/13
 *
 * 原理：在 onScrollChanged、onGlobalLayout、onResume 时 延迟去获取当前窗口可视区内所有的 View。
 *
 * 每个可交互的 View 一个作为一个 ViewNode 对象。ViewNode 中记录 ViewPath，显示的文本，hashCode 方法。
 * 每个 Activity 有一个 ActionCalculator 对象，此对象用一个 mList 记录着曝光的 viewNode.hashCode()
 * 如果一个 viewNode.hashCode() 不在 mList 中，测产生曝光事件。
 *
 * onPause 时，清空 ActionCalculator 对象。
 */


public class ElementDisplayManager2 {

    private static final String TAG = "ElementDisplayManager2";
    private static Map<WeakReference<View>, ActionCalculator> mActionCalculatorMap = new LinkedHashMap();
    private static long mNextForceSaveAllImpressionTime = -1;
    private static long mViewTreeChangeDownTime = -1;
    private static boolean mIsInFirstImpressionTime;

    /**
     *  滚动时延迟 250ms save Imp，
     *  每次 save/clear Imp 会给 mNextForceSaveAllImpressionTime + 3000ms，
     *  当 mNextForceSaveAllImpressionTime 小于当前时间戳时，立即 save Imp
     *
     */
    private static void saveAllWindowImpress(boolean onlyNewWindow){
        // 更新
        updateNextForceSaveAllImpressionTime();
        Activity activity = CoreInitialize.coreAppState().getForegroundActivity();
        boolean skipOtherActivity = true;
        if (activity != null) {
            Collection<ActionCalculator> calculators;
            WindowHelper.init();
            View[] windowRootViews = WindowHelper.getWindowViews();
            ArrayList<ActionCalculator> newWindowCalculators = new ArrayList();
            Log.i(TAG,"saveAllWindowImpress windowRootViews ①--> : "+windowRootViews.length);
            if (ViewHelper.getMainWindowCount(windowRootViews) <= 1) {
                skipOtherActivity = false;
            }
            WindowHelper.init();

            for (View root : windowRootViews) {
                if (root != null) {
                    String prefix = WindowHelper.getWindowPrefix(root);
                    Log.i(TAG,"getWindowPrefix ②--> :"+prefix);
                    Log.i(TAG,"root ③--> :"+root.hashCode());
                    //如果 rootView 对象没变，第二次不会进入下边 if
                    if (!WindowHelper.sIgnoredWindowPrefix.equals(prefix) && ViewHelper.isWindowNeedTraverse(root, prefix, skipOtherActivity)  && findCalculatorByWindow(root) == null  ) {
                        // TODO 把 rootView 放进实体类进行计算
                        ActionCalculator actionCalculator = new ActionCalculator("ClickActivity", System.currentTimeMillis(), root, prefix);
                        Log.i(TAG,"actionCalculator ④--> :"+root.hashCode());
                        mActionCalculatorMap.put(new WeakReference(root), actionCalculator);
                        newWindowCalculators.add(actionCalculator);
                    }
                }
            }

            if (onlyNewWindow) {
                calculators = newWindowCalculators;
            } else {
                calculators = mActionCalculatorMap.values();//这里的值，成员变量中的，calculator 没有变
            }


            Log.i(TAG,"size ⑤--> :"+calculators.size()+" hashCode:"+calculators.hashCode());
            Log.i(TAG,"values ⑥--> :"+calculators.toString());

            //saveImpInBg(calculators);
            for (ActionCalculator calculator : calculators) {
                //saveImpressInBgMyThrowException(ActionCalculator calculator)
                MessageProcessor messageProcessor = CoreInitialize.messageProcessor();
                if (calculator != null) {
                    Log.i(TAG,"(calculator != null ⑦--> :"+calculator.hashCode());

                    // TODO 关键点!!! obtainImpress 遍历
                    List<ActionEvent> events = calculator.obtainImpress();
                    if (events != null) {
                        Log.i(TAG,"events != null ⑧--> :"+events.size() +"  "+events.hashCode());
                        for (ActionEvent event : events) {
                            Log.i(TAG,"event ⑨--> :"+event.toJson().toString());
                            // persistEvent
                            messageProcessor.persistEvent(event);
                        }
                    }
                }
            }


        }

    }

    /**
     * && findCalculatorByWindow(root) == null
     *
     * 缓存，用于保存上一次的 Imp 保存的 rootView。如果有缓存，说明触发过了。
     */
    private static ActionCalculator findCalculatorByWindow(View root) {
        for (WeakReference viewReference : mActionCalculatorMap.keySet()) {
            if (viewReference.get() == root) {
                return (ActionCalculator) mActionCalculatorMap.get(viewReference);
            }
        }
        return null;
    }

    private static void updateNextForceSaveAllImpressionTime() {
        mNextForceSaveAllImpressionTime = System.currentTimeMillis() + 3000;
    }












    /**
     * 清除 Calculator 对象
     */
    private static void clearActionCalculatorMap(){
        mActionCalculatorMap.clear();
        updateNextForceSaveAllImpressionTime();
    }


    private static Runnable mSaveAllWindowImpression = new Runnable() {
        public void run() {
            mIsInFirstImpressionTime = false;
            saveAllWindowImpress(false);
        }
    };

    /**
     * ViewTreeObserver 时，延迟 250ms 保存 Imp
     */
    public static void saveImpOnViewTreeObserver(){
        if (!mIsInFirstImpressionTime) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mViewTreeChangeDownTime > 4000) {
                updateNextForceSaveAllImpressionTime();
            }
            if (mNextForceSaveAllImpressionTime < currentTime) {
                mSaveAllWindowImpression.run();
                return;
            }

            ThreadUtils.cancelTaskOnUiThread(mSaveAllWindowImpression);
            ThreadUtils.postOnUiThreadDelayed(mSaveAllWindowImpression, 250);
        }
    }

    /**
     * onResume 时，时先 clear，再延迟 500ms 保存 Imp
     */
    public static void saveImpOnResume(){
        clearActionCalculatorMap();
        mIsInFirstImpressionTime = true;
        ThreadUtils.cancelTaskOnUiThread(mSaveAllWindowImpression);
        ThreadUtils.postOnUiThreadDelayed(mSaveAllWindowImpression, 500);
    }

    /**
     * onPause 时 clear。
     */
    public static void cleanImpOnPause(){
        clearActionCalculatorMap();
        mIsInFirstImpressionTime = true;
        ThreadUtils.cancelTaskOnUiThread(mSaveAllWindowImpression);
    }

}
