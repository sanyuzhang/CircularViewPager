package com.sanyuzhang.circular.viewpager.sample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sanyuzhang.circular.viewpager.cvp.view.CircularTabLayout;
import com.sanyuzhang.circular.viewpager.cvp.view.CircularViewPager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CircularTabLayout tabLayout = (CircularTabLayout) findViewById(R.id.circular_tab);
        CircularViewPager viewPager = (CircularViewPager) findViewById(R.id.circular_viewpager);

        SampleFragmentPagerAdapter adapter = new SampleFragmentPagerAdapter(getFragmentManager());

        viewPager.setFragmentAdapter(adapter, getFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> mFragments = new ArrayList<>();

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int position = 0; position < 5; position++) {
                mFragments.add(SampleFragment.newInstance(String.format("%d", position)));
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
            return String.format("Page %d", position);
        }

    }
}
