package cn.sa.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import cn.sa.demo.R;

public class FragmentActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        this.setTitle("Fragment");
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        startActivity(new Intent(FragmentActivity.this, ClickActivity.class));
//        finish();

    }

    private void initView() {
        // v4
        findViewById(R.id.tv_frg_v4_viewpager).setOnClickListener(this);
        findViewById(R.id.tv_frg_v4_tab).setOnClickListener(this);
        findViewById(R.id.tv_frg_v4_viewpager_viewpager).setOnClickListener(this);
        // app.Fragment
        findViewById(R.id.tv_frg_app_viewpager).setOnClickListener(this);
        findViewById(R.id.tv_frg_app_tab).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_frg_v4_viewpager:
                openVPFragment_v4();//
                break;
            case R.id.tv_frg_v4_tab:
                openTabFrgActivity_v4();//
                break;
            case R.id.tv_frg_v4_viewpager_viewpager:
                openVPVPActivity_v4();//
                break;
            case R.id.tv_frg_app_viewpager:
                openVPAppFrgActivity_app();//
                break;
            case R.id.tv_frg_app_tab:
                openTabAppFrgActivity_app();//
                break;
            default:
                break;
        }
    }

    /**
     * 打开 VPFrgActivity
     */
    private void openVPFragment_v4() {
        startActivity(new Intent(this, VPFrgActivity.class));
    }

    /**
     * 打开 TabFrgActivity
     */
    private void openTabFrgActivity_v4() {
        startActivity(new Intent(this, TabFrgActivity.class));
    }

    /**
     * 打开 VPVPFrgActivity
     */
    private void openVPVPActivity_v4() {
        startActivity(new Intent(this, VPVPFrgActivity.class));
    }

    /**
     * 打开 TabAppFrgActivity
     */
    private void openTabAppFrgActivity_app() {
        startActivity(new Intent(this, TabAppFrgActivity.class));
    }

    /**
     * 打开 VPAppFrgActivity
     */
    private void openVPAppFrgActivity_app() {
        startActivity(new Intent(this, VPAppFrgActivity.class));
    }
}
