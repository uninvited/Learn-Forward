package com.miplot.playandlearn;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BaseTrainingRunner implements Serializable {

    public enum PassState {
        NEW,
        FAILED,
        PASSED
    }

    private Db db;
    private Unit unit;
    private TrainingType trainingType;
    private List<Word> words;
    private List<PassState> passStates;

    private long startTs = -1;

    private Map<TrainingType, Integer> curProgressMap;

    public BaseTrainingRunner(Db db, Unit unit, TrainingType trainingType, int wordsCount) {
        this.unit = unit;
        this.trainingType = trainingType;
        this.db = db;
        words = db.getWords(unit.getId());
        curProgressMap = db.getProgress(unit.getId());

        Collections.shuffle(words, new Random(System.nanoTime()));
        if (wordsCount > 0 && words.size() > wordsCount) {
            words = words.subList(0, wordsCount);
        }

        if (words.isEmpty()) {
            return;
        }
        this.passStates = makePassStates(words.size());
    }

    public List<Word> getWords() {
        return words;
    }

    public int getWordsCount() {
        return words.size();
    }

    public void setWords(List<Word> words) {
        this.words = words;
        this.passStates = makePassStates(words.size());
    }

    private static List<PassState> makePassStates(int size) {
        List<PassState> passStates = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            passStates.add(PassState.NEW);
        }
        return passStates;
    }

    public Word getWord(int index) {
        if (index < words.size()) {
            return words.get(index);
        } else {
            Log.e("TAG", "Word index out of range");
            return words.get(words.size() - 1);
        }
    }

    public void handleIncorrectAnswer(int index) {
        // If the task was already passed, don't set it as failed
        if (isAnswered(index)) {
            return;
        }
        passStates.set(index, PassState.FAILED);
    }

    public void handleCorrectAnswer(int index) {
        passStates.set(index, PassState.PASSED);
    }

    public PassState getPassState(int index) {
        return passStates.get(index);
    }

    private boolean isAnswered(int index) {
        return passStates.get(index) == PassState.PASSED;
    }

    public void startDurationCounting() {
        startTs = System.currentTimeMillis();
    }

    public void stopDurationCounting() {
        int numCorrectAnswers = 0;
        for (PassState ps : passStates) {
            if (ps == PassState.PASSED) {
                numCorrectAnswers++;
            }
        }
        int percent = numCorrectAnswers * 100 / passStates.size();

        if (curProgressMap.get(trainingType) < percent) {
            db.updateProgress(unit.getId(), trainingType, percent);
        }
        if (startTs > 0) {
            int durationSec = (int) ((System.currentTimeMillis() - startTs) / 1000.);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            db.addStat(year, month, day, 1, durationSec);
        }
        startTs = -1;
    }
}
