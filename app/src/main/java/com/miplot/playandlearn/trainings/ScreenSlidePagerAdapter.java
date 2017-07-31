package com.miplot.playandlearn.trainings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.Unit;
import com.miplot.playandlearn.Word;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    public static final String ARG_KEY_BTR = "btr";
    public static final String ARG_KEY_POS = "pos";
    public static final String ARG_KEY_UNIT = "unit";

    private BaseTrainingRunner btr;
    private Class fragmentClass;
    private Unit unit;

    public ScreenSlidePagerAdapter(FragmentManager fm, BaseTrainingRunner btr, Class fragmentClass, Unit unit) {
        super(fm);
        this.btr = btr;
        this.fragmentClass = fragmentClass;
        this.unit = unit;

        retainSuitableWords();
    }

    @Override
    public Fragment getItem(int position) {
        BaseTrainingFragment fragment;
        try {
            fragment = (BaseTrainingFragment)fragmentClass.newInstance();
            fragment.setBtr(btr);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY_UNIT, unit);
        args.putInt(ARG_KEY_POS, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return btr.getWordsCount();
    }

    // TODO: Make it nicer! Java 8 and its static interface?
    private void retainSuitableWords() {
        List<Word> originalWords = btr.getWords();
        List<Word> suitableWords = new ArrayList<>();

        if (fragmentClass == FragmentSortByClass.class) {
            for (Word word : originalWords) {
                if (FragmentSortByClass.isWordOk(word)) {
                    suitableWords.add(word);
                }
            }
        } else if (fragmentClass == FragmentChooseWordInPhrase.class) {
            for (Word word : originalWords) {
                if (FragmentChooseWordInPhrase.isWordOk(word)) {
                    suitableWords.add(word);
                }
            }
        } else if (fragmentClass == FragmentChooseOdd.class) {
            for (Word word : originalWords) {
                if (FragmentChooseOdd.isWordOk(word)) {
                    suitableWords.add(word);
                }
            }
        } else if (fragmentClass == FragmentSpeak.class) {
            for (Word word : originalWords) {
                if (FragmentSpeak.isWordOk(word)) {
                    suitableWords.add(word);
                }
            }
        } else {
            return;
        }

        btr.setWords(suitableWords);
    }
}