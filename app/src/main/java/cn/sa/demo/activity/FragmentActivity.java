package cn.sa.demo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.sa.demo.R;

public class FragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        this.setTitle("Fragment");
    }
}
