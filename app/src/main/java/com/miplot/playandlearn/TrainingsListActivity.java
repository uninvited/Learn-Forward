package com.miplot.playandlearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class TrainingsListActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_UNIT = "com.miplot.playandlearn.INTENT_EXTRA_UNIT";
    public static final String INTENT_EXTRA_TRAINING_DESCR = "com.miplot.playandlearn.INTENT_EXTRA_TRAINING_DESCR";

    private Unit unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);
        Intent intent = getIntent();
        unit = (Unit)intent.getSerializableExtra(INTENT_EXTRA_UNIT);
        setTitle(getString(R.string.unit) + " " + unit.getId() + ". " + unit.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.trainings_list);

        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(lm);

        TrainingListAdaptor trainingListAdaptor = new TrainingListAdaptor(this, unit);
        recyclerView.setAdapter(trainingListAdaptor);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
