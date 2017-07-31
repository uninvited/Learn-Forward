package com.miplot.playandlearn;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainApplication extends Application {
    private static final String TAG = "Application";

    private Db db;
    private TextToSpeech tts;
    private boolean ttsIsAvailable = false;

    private TextToSpeech.OnInitListener onTtsInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("en", "US"));
                ttsIsAvailable = (result != TextToSpeech.LANG_MISSING_DATA) &&
                        (result != TextToSpeech.LANG_NOT_SUPPORTED);
                if (!ttsIsAvailable) {
                    Toast.makeText(MainApplication.this, "Annotation language not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                String message = "Failed to initialize tts engine with error " + status;
                Toast.makeText(MainApplication.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        db = new Db(this);
        tts = new TextToSpeech(this, onTtsInitListener);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        String dataVersion = getInputVersion();
        if (dataVersion == null || dataVersion.equals(db.getVersion())) {
            Log.i(TAG, "DB is up to date");
            return;
        }

        try {
            Log.i(TAG, "Parse input and write to DB");
            InputParser inputParser = new InputParser(getAssets().open("source.json"));
            InputData inputData = inputParser.parse();

            populateDb(db, inputData);
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse source data", e);
            System.exit(1);
        }
    }

    private String getInputVersion() {
        try {
            InputParser inputParser = new InputParser(getAssets().open("source.json"));
            return inputParser.parseVersion();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse source data version", e);
            return null;
        }
    }

    private void populateDb(Db db, InputData data) {
        List<Unit> existingUnits = db.getUnits();

        for (Unit existingUnit : existingUnits) {
            if (data.units.get(existingUnit.getId()) != null) {
                data.units.remove(existingUnit.getId());
            }
        }

        Log.i(TAG, "Units to add: " + data.units.size());

        db.dropNonCriticalData();

        Log.i(TAG, "Populate db");
        db.addVersion(data.version);
        db.addUnits(data.units.values());
        db.addTopics(data.topics.values());
        db.addWords(data.words);

        Map<TrainingType, Integer> progressMap = new HashMap<>();
        for (TrainingType trainingType : TrainingType.values()) {
            progressMap.put(trainingType, 0);
        }

        for (long unitId : data.units.keySet()) {
            db.addProgress(unitId, progressMap);
        }
    }

    public Db getDb() {
        return db;
    }

    public TextToSpeech getTts() { return tts; }

    public boolean isTtsIsAvailable() { return ttsIsAvailable; }
}
