package cn.sa.demo.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.sa.demo.R;
import cn.sa.demo.fragment.v4.Frg_1;
import cn.sa.demo.fragment.v4.Frg_2;
import cn.sa.demo.fragment.v4.Frg_3;

/**
 * ViewPager + android.support.v4.app.Fragment;
 *
 * ViewPager1(展示 Frg_1、Frg_2、Frg_3)
 * 嵌套 ViewPager2(展示 Frg_4、Frg_5、Frg_6)
 */
public class VPVPFrgActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pger_view_pager);
        this.setTitle("ViewPager + ViewPager + v4 Fragment");
        initViewPager();
    }

    private List<Fragment> listPagerViews = null;
    private PagerAdapter pagerAdapter=null;
    private void initViewPager() {
        listPagerViews=new ArrayList<>();

        listPagerViews.add(new Frg_1());
        listPagerViews.add(new Frg_2());
        listPagerViews.add(new Frg_3());
        ViewPager viewPager1= (ViewPager) findViewById(R.id.view_pager_activity_vp);
        pagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        viewPager1.setAdapter(pagerAdapter);

    }
}
