package com.miplot.playandlearn.stat;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miplot.playandlearn.DailyStat;
import com.miplot.playandlearn.Db;
import com.miplot.playandlearn.MainApplication;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.UnitsActivity;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private final static String TAG = "StatisticsActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Db db = ((MainApplication)(getApplication())).getDb();
                sendStatistics(getApplicationContext(), db);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StatisticsDaysFragment();
                case 1:
                    return new StatisticsMonthsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "за 7 дней";
                case 1:
                    return "по месяцам";
            }
            return null;
        }
    }

    public static void sendStatistics(Context context, Db db) {
        String body = formatStats(db.getFullStat());
        Log.i(TAG, "Sending statistics: " + body);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Learn Forward statistics");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    private static String formatStats(List<DailyStat> stats) {
        String result = "";
        for (DailyStat stat : stats) {
            result += stat.toString() + "\n";
        }
        return result;
    }
}
