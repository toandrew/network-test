package com.xiaoyezi.tools.networktest;

import android.content.Context;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import com.xiaoyezi.tools.networktest.ui.AnalyticsFragment;
import com.xiaoyezi.tools.networktest.ui.LogFragment;
import com.xiaoyezi.tools.networktest.ui.TestFragment;
import com.xiaoyezi.tools.networktest.utils.Constants;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary(Constants.ENET_LIB_NAME);
    }

    private static final String TAG = "MainActivity";

    // Objects for WAKE-LOCK
    private PowerManager mPwrManager;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // WakeLock Initialization
        mPwrManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPwrManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "NetworkTester");

        ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.table_title, tab, false));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(getString(R.string.test_tab), TestFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.analytics_tab), AnalyticsFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.log_tab), LogFragment.class));

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();

        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
