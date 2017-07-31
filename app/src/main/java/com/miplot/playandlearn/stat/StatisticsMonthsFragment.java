package com.miplot.playandlearn.stat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miplot.playandlearn.DailyStat;
import com.miplot.playandlearn.Db;
import com.miplot.playandlearn.MainApplication;
import com.miplot.playandlearn.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StatisticsMonthsFragment extends Fragment {
    private final static int NUM_LAST_MONTHS = 6;
    private Db db;

    private RecyclerView monthsListView;
    private MonthStatAdapter monthsListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistic_months, container, false);

        layoutManager = new LinearLayoutManager(getContext());
        monthsListView = (RecyclerView) rootView.findViewById(R.id.months_list);
        db = ((MainApplication)(getActivity().getApplication())).getDb();

        List<DailyStat> stats = new ArrayList<>();
        Calendar gCal = new GregorianCalendar();
        int year = gCal.get(Calendar.YEAR);
        int month = gCal.get(Calendar.MONTH) + 1;
        for (int i = 0; i < NUM_LAST_MONTHS; i++) {
            List<DailyStat> perDayStats = db.getStatForMonth(year, month);
            stats.add(aggregateStats(year, month, perDayStats));
            month--;
            if (month == 0) {
                year--;
                month = 12;
            }
        }

        monthsListAdapter = new MonthStatAdapter(stats);
        monthsListView.setLayoutManager(layoutManager);
        monthsListView.setAdapter(monthsListAdapter);

        return rootView;
    }

    private DailyStat aggregateStats(int year, int month, List<DailyStat> perDayStats) {
        DailyStat result = new DailyStat(year, month, 0, 0, 0);
        if (perDayStats == null)
            return result;

        for (DailyStat dayStat : perDayStats) {
            result.trainingsCount += dayStat.trainingsCount;
            result.durationSec += dayStat.durationSec;
        }
        return result;
    }
}
