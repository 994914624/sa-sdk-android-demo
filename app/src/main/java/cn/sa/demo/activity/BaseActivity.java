package cn.sa.demo.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sa.demo.R;
import cn.sa.demo.custom.SensorsDataUtil;

public class BaseActivity extends AppCompatActivity {


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 对于 Activity 计时开始
//        SensorsDataAPI.sharedInstance().trackTimerStart("ViewAppPage");
//        SensorsDataUtil.onPageStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        try {
//           JSONObject properties = new JSONObject();
//            properties.put("pageName",this.getClass().getSimpleName());
//            // 对于 Activity 计时结束触发事件，并将时长写入的事件的 event_duration 属性中
//            SensorsDataAPI.sharedInstance().trackTimerEnd("ViewAppPage",properties);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SensorsDataUtil.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
