package cn.sa.demo.activity;

import android.os.Bundle;
import cn.sa.demo.R;

public class ClickActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);
        this.setTitle("控件设置点击的方式");
    }
}
