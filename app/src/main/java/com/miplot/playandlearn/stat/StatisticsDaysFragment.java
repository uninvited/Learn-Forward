package com.miplot.playandlearn.stat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.miplot.playandlearn.DailyStat;
import com.miplot.playandlearn.Db;
import com.miplot.playandlearn.MainApplication;
import com.miplot.playandlearn.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StatisticsDaysFragment extends Fragment {
    private final static int NUM_DAYS = 7;

    private Db db;

    private LinearLayout numTrainingsBarsLayout;
    private LinearLayout numTrainingsDaysLayout;
    private LinearLayout durationsBarsLayout;
    private LinearLayout durationsDaysLayout;

    private TextView[] numTrainingsBars = new TextView[NUM_DAYS];
    private TextView[] numTrainingsDays = new TextView[NUM_DAYS];
    private TextView[] durationsBars = new TextView[NUM_DAYS];
    private TextView[] durationsDays = new TextView[NUM_DAYS];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = ((MainApplication) getActivity().getApplication()).getDb();
        View rootView = inflater.inflate(R.layout.fragment_statistic_days, container, false);
        numTrainingsBarsLayout = (LinearLayout)rootView.findViewById(R.id.num_trainings_bars_layout);
        numTrainingsDaysLayout = (LinearLayout)rootView.findViewById(R.id.num_trainings_days_layout);
        durationsBarsLayout = (LinearLayout)rootView.findViewById(R.id.durations_bars_layout);
        durationsDaysLayout = (LinearLayout)rootView.findViewById(R.id.durations_days_layout);

        for (int i = 0; i < NUM_DAYS; ++i) {
            numTrainingsBars[i] = (TextView) numTrainingsBarsLayout.getChildAt(i);
            numTrainingsDays[i] = (TextView) numTrainingsDaysLayout.getChildAt(i);
            durationsBars[i] = (TextView) durationsBarsLayout.getChildAt(i);
            durationsDays[i] = (TextView) durationsDaysLayout.getChildAt(i);
        }

        final Calendar gCal = new GregorianCalendar();
        final List<DailyStat> dailyStats = new ArrayList<>();

        int maxNumTrainings = 0;
        int maxDuration = 0;

        for (int i = 0; i < NUM_DAYS; i++) {
            int day = gCal.get(Calendar.DAY_OF_MONTH);
            int month = gCal.get(Calendar.MONTH) + 1;
            int year = gCal.get(Calendar.YEAR);

            String date = day + "/" + (month > 9 ? "" : "0") + month;
            numTrainingsDays[NUM_DAYS - 1 - i].setText(date);
            durationsDays[NUM_DAYS - 1 - i].setText(date);

            DailyStat stat = db.getStatForDay(year, month, day);
            if (stat.trainingsCount > maxNumTrainings) {
                maxNumTrainings = stat.trainingsCount;
            }
            if (toMinutes(stat.durationSec) > maxDuration) {
                maxDuration = toMinutes(stat.durationSec);
            }
            dailyStats.add(stat);
            gCal.add(Calendar.DATE, -1);
        }

        int maxBarHeight = numTrainingsBarsLayout.getLayoutParams().height;

        for (int i = 0; i < NUM_DAYS; i++) {
            int value = dailyStats.get(i).trainingsCount;
            TextView bar = numTrainingsBars[NUM_DAYS - 1 - i];
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)bar.getLayoutParams();
            if (maxNumTrainings > 0) {
                params.height = (int) (maxBarHeight * (float) value / (float) maxNumTrainings);
            } else {
                params.height = 0;
            }
            bar.setLayoutParams(params);
            bar.setText(Integer.toString(value));

            value = toMinutes(dailyStats.get(i).durationSec);
            bar = durationsBars[NUM_DAYS - 1 - i];
            params = (LinearLayout.LayoutParams)bar.getLayoutParams();
            if (maxDuration > 0) {
                params.height = (int) (maxBarHeight * (float) value / (float) maxDuration);
            } else {
                params.height = 0;
            }
            bar.setLayoutParams(params);
            bar.setText(String.valueOf(value));
        }

        return rootView;
    }

    private int toMinutes(int seconds) {
        if (seconds == 0) {
            return 0;
        }

        if (seconds < 60) {
            return 1;
        } else {
            return (int)Math.round(seconds / 60.);
        }
    }
}
