package cn.sa.demo.activity;

import android.app.Fragment;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import cn.sa.demo.R;
import cn.sa.demo.fragment.BaseAppFragment;
import cn.sa.demo.fragment.app.Frg_app_1;
import cn.sa.demo.fragment.app.Frg_app_2;
import cn.sa.demo.fragment.app.Frg_app_3;

/**
 * ViewPager + android.app.Fragment
 *
 * 展示 Frg_app_1、Frg_app_2、Frg_app_3
 */
public class VPAppFrgActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpapp_frg);
        this.setTitle("ViewPager + app.Fragment");
        initViewPager();
    }


    private List<BaseAppFragment> listPagerViews = null;
    private FragmentPagerAdapter pagerAdapter=null;
    private void initViewPager() {
        listPagerViews=new ArrayList<>();

        listPagerViews.add(new Frg_app_1());
        listPagerViews.add(new Frg_app_2());
        listPagerViews.add(new Frg_app_3());
        ViewPager viewPager= findViewById(R.id.vp_app_frg);
        pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return listPagerViews.size();
            }

            @Override
            public Fragment getItem(int i) {
                return listPagerViews.get(i);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
}
