package cn.sa.demo.activity;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.sa.demo.R;
import cn.sa.demo.fragment.v4.Frg_4;
import cn.sa.demo.fragment.v4.Frg_5;
import cn.sa.demo.fragment.v4.Frg_6;

/**
 * ViewPager + android.support.v4.app.Fragment;
 *
 * 展示 Frg_4、 Frg_5、 Frg_6
 */
public class VPFrgActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpfrg);
        this.setTitle("ViewPager + v4 Fragment");
        initViewPager();
    }


    private List<Fragment> listPagerViews = null;
    private PagerAdapter pagerAdapter=null;
    private void initViewPager() {
        listPagerViews=new ArrayList<>();

        listPagerViews.add(new Frg_4());
        listPagerViews.add(new Frg_5());
        listPagerViews.add(new Frg_6());
        ViewPager viewPager= findViewById(R.id.vp_frg);
        pagerAdapter=new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return listPagerViews.get(position);
            }

            @Override
            public int getCount() {
                return listPagerViews.size();
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
}
