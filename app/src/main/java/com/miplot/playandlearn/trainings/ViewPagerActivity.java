package com.miplot.playandlearn.trainings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.MainApplication;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.TrainingDescriptor;
import com.miplot.playandlearn.TrainingsListActivity;
import com.miplot.playandlearn.Unit;

public class ViewPagerActivity extends AppCompatActivity {
    private BaseTrainingRunner btr;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        Intent intent = getIntent();
        Unit unit = (Unit)intent.getSerializableExtra(TrainingsListActivity.INTENT_EXTRA_UNIT);
        TrainingDescriptor descriptor = (TrainingDescriptor)intent.getSerializableExtra(
                TrainingsListActivity.INTENT_EXTRA_TRAINING_DESCR);

        btr = new BaseTrainingRunner(
                ((MainApplication) getApplication()).getDb(),
                unit,
                descriptor.getTrainingType(),
                0);

        if (btr.getWordsCount() < 4) {
            finish();
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(descriptor.getTitleResourceId());
        getSupportActionBar().setSubtitle(getString(R.string.unit) + " " + unit.getId() + ". " + unit.getName());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(
                getSupportFragmentManager(), btr, descriptor.getFragmentClass(), unit);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btr.stopDurationCounting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btr.startDurationCounting();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
