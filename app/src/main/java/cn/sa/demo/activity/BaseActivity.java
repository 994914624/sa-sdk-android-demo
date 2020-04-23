package cn.sa.demo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sa.demo.App;
import cn.sa.demo.R;
import cn.sa.demo.custom.SensorsDataUtil;

public class BaseActivity extends AppCompatActivity   {


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            try {
                int x= (int) event.getRawX();
                int y= (int) event.getRawY();
                JSONObject jsonObject = new JSONObject();

                long now = SystemClock.elapsedRealtime();
                long interval = now - App.ADB_TIME;
                // 此次点击 - 上次点击 > 24小时，则是首次点击
                if(interval> 24*60*60*1000){
                    jsonObject.put("xy",String.format("%s %s|%s",x,y,""));
                    Log.d("adbEvent", String.format("onTouchEvent %s %s|%s", x, y,""));
                } else {
                    jsonObject.put("xy",String.format("%s %s|%s",x,y,interval));
                    Log.d("adbEvent", String.format("onTouchEvent %s %s|%s", x, y,interval));
                }
                // 更新上次点击时间戳
                App.ADB_TIME = now;
//                SensorsDataAPI.sharedInstance().track("adbEvent",jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.dispatchTouchEvent(event);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            Log.d("BaseActivity","onOptionsItemSelected "+getResources().getResourceEntryName(item.getItemId()));
//            android.R.id.home is a menu ID,

        return super.onOptionsItemSelected(item);

    }

}
