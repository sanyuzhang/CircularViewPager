package com.umizhang.android.view.circularviewpager;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private BaseFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager();
    }

    private void setupViewPager() {
        if (mAdapter == null) {
            mAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        }
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    class BaseFragmentAdapter extends FragmentStatePagerAdapter {

        String[] mTitles = new String[]{
                "Theme1", "Theme2", "Theme3", "Theme4", "Theme5"
        };

        List<Fragment> mFragments = new ArrayList<>();

        public BaseFragmentAdapter(FragmentManager fm) {
            super(fm);
            for (String title : mTitles) {
                SampleFragment sampleFragment = new SampleFragment();
                sampleFragment.setContent(title);
                mFragments.add(sampleFragment);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

    }

}
