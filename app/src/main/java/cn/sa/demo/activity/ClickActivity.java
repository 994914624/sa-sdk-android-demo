package cn.sa.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding.view.RxView;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick;
import java.util.concurrent.TimeUnit;
import androidx.databinding.DataBindingUtil;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sa.demo.R;
import cn.sa.demo.databinding.ActivityClickBinding;
import cn.sa.demo.entity.BindingEntity;
import rx.functions.Action1;

public class ClickActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder = null;
    private  ActivityClickBinding activityClickBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_click);
        activityClickBinding = DataBindingUtil.setContentView(this, R.layout.activity_click);
        this.setTitle("设置点击方式");
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        type1();
        type2();
        type3();
        type5();
        type7();
        type8();
        type9();
    }

    /**
     * 1. 匿名内部类方式。
     */
    private void type1() {
        findViewById(R.id.tv_click_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ClickActivity.this, "方式1", Toast.LENGTH_SHORT).show();
                //ElementDisplayManager2.saveAllWindowImpress(false);
            }
        });
    }

    /**
     * 2. 实现 View.OnClickListener 接口重写 onClick 方式。
     */
    private void type2() {
        findViewById(R.id.tv_click_2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(ClickActivity.this, "方式2", Toast.LENGTH_SHORT).show();
    }


    /**
     * 3. View.OnClickListener 的实现类方式。
     */
    private void type3() {
        findViewById(R.id.tv_click_3).setOnClickListener(new ButtonListener());
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(ClickActivity.this, "方式3", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 4. XML 中 OnClick 方式。
     * 属性名字对应为方法名
     */
    @SensorsDataTrackViewOnClick
    public void type4(View v) {
        Toast.makeText(this, "方式4(OnClick)", Toast.LENGTH_SHORT).show();
    }

    /**
     * 5. lambda 方式。
     */
    private void type5() {
        findViewById(R.id.tv_click_5).setOnClickListener(v -> Toast.makeText(ClickActivity.this, "方式5(Lambda)", Toast.LENGTH_SHORT).show());
    }

    /**
     * 6. ButterKnife 方式。
     */
//    view = Utils.findRequiredView(source, R.id.tv_click_6, "method 'butterKnife'");
//    view7f0800e7 = view;
//    view.setOnClickListener(new DebouncingOnClickListener() {
//        @Override
//        public void doClick(View p0) {
//            target.butterKnife();
//        }
//    });
    @OnClick(R.id.tv_click_6)
    public void butterKnife() {
        Toast.makeText(this, "方式5(ButterKnife)", Toast.LENGTH_SHORT).show();
    }

    /**
     * 7. RxView.clicks 方式。
     */
    private void type7() {
        RxView.clicks(findViewById(R.id.tv_click_7))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(ClickActivity.this, "方式7(RxView.clicks) ", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    /**
     * 8. dataBinding 方式。
     */
    private void type8() {
        BindingEntity bindingEntity = new BindingEntity("方式8(dataBinding)");
        activityClickBinding.setData(bindingEntity);
        activityClickBinding.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClickActivity.this, "方式8(dataBinding)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 9. Kotlin 方式。
     */
    private void type9() {
        TextView textView = findViewById(R.id.tv_click_9);
        SensorsDataAPI.sharedInstance().ignoreView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickActivity.this, KotlinActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
