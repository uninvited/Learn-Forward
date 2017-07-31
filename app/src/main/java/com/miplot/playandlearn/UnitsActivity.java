package com.miplot.playandlearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.miplot.playandlearn.dict.DictionaryActivity;
import com.miplot.playandlearn.stat.StatisticsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnitsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Db db;
    private RecyclerView unitListView;
    private UnitListAdapter unitListAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((MainApplication)getApplication()).getDb();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layoutManager = new LinearLayoutManager(this);
        unitListView = (RecyclerView) findViewById(R.id.units_list);
        unitListView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        unitListAdapter = new UnitListAdapter(this, getUnits());
        unitListView.setAdapter(unitListAdapter);
    }

    private List<Pair<Unit, Integer>> getUnits() {
        List<Pair<Unit, Integer>> result = new ArrayList<>();
        List<Unit> units = db.getUnits();
        for (Unit unit : units) {
            if (db.getWordsCount(unit.getId()) == 0)
                continue;

            Map<TrainingType, Integer> progressMap = db.getProgress(unit.getId());
            int progress = 0;

            if (progressMap != null && progressMap.size() > 0) {
                int size = progressMap.size();
                for (Integer p : progressMap.values()) {
                    progress += p;
                }
                progress /= size;
            }
            result.add(new Pair<>(unit, progress));
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_play:
                break;
            case R.id.nav_stat:
                intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_dict:
                intent = new Intent(this, DictionaryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
