package com.miplot.playandlearn.trainings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FragmentChooseOdd extends BaseTrainingFragment {
    private static final int NUM_VARIANTS = 4;

    private int currentIndex;
    private Word currentWord;
    private int correctAnswerIndex;

    private boolean onScreen = false;
    private boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private TextView[] textViews = new TextView[NUM_VARIANTS];

    private LinearLayout indicatorsPanelLayout;
    private PageIndicatorsPanel pageIndicatorsPanel;

    private void onShow() {
        pageIndicatorsPanel.refresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onScreen = true;
            if (isCreated) {
                onShow();
                onScreen = false;
            }
        }
        else {
            if (isCreated) {
                hideAnswer();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_training_choose_odd, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        textViews[0] = (TextView) rootView.findViewById(R.id.answer_variant1);
        textViews[1] = (TextView) rootView.findViewById(R.id.answer_variant2);
        textViews[2] = (TextView) rootView.findViewById(R.id.answer_variant3);
        textViews[3] = (TextView) rootView.findViewById(R.id.answer_variant4);
        for (TextView tv : textViews) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVariantClick(v);
                }
            });
        }
        indicatorsPanelLayout = (LinearLayout) rootView.findViewById(R.id.indicators_panel);
        pageIndicatorsPanel = new PageIndicatorsPanel(getActivity(), indicatorsPanelLayout, btr, currentIndex);


        displayWord(currentWord);
        isCreated = true;
        if (onScreen) {
            onShow();
        }
        return rootView;
    }

    public void onVariantClick(View view) {
        int correctImageViewId = textViews[correctAnswerIndex].getId();
        if (view.getId() == correctImageViewId) {
            showAnswer();
            btr.handleCorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
            view.setBackgroundColor(getResources().getColor(R.color.light_green));
        } else {
            view.setAlpha(0.5f);
            view.setClickable(false);
            btr.handleIncorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
            Toast.makeText(getContext(), R.string.keep_trying, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayWord(Word word) {
        Random rnd = new Random(System.nanoTime());

        if (word.getSiblings() == null) {
            Log.e("ChooseOdd", "No siblings found");
            return;
        }

        String[] siblings = word.getSiblings().split(", ");

        if (siblings.length + 1 < NUM_VARIANTS) {
            Log.e("ChooseOdd", "Wrong number of siblings: " + siblings.length);
            return;
        }

        List<String> variants = new ArrayList<>();
        variants.add(word.getWordEn());
        variants.add(siblings[0]);
        variants.add(siblings[1]);
        variants.add(siblings[2]);

        // The last word among siblings is odd (input data is organized this way)
        String odd = siblings[2];
        Collections.shuffle(variants, rnd);
        correctAnswerIndex = variants.indexOf(odd);

        for (int i = 0; i < NUM_VARIANTS; i++) {
            showAnswerVariant(textViews[i], variants.get(i));
            textViews[i].setClickable(true);
        }
        hidden = true;
        hideAnswer();
    }

    private void showAnswerVariant(TextView view, String variant) {
        view.setText(variant);
        view.setAlpha(1f);
        view.setBackgroundColor(getResources().getColor(R.color.light_grey));
    }

    private void showAnswer() {
        if (hidden) {
            for (TextView tv : textViews) {
                tv.setClickable(false);
            }
            hidden = false;
        }
    }

    public void hideAnswer() {
        for (TextView tv : textViews) {
            tv.setClickable(true);
            tv.setAlpha(1f);
            tv.setBackgroundColor(getResources().getColor(R.color.light_grey));
        }
        hidden = true;
    }

    public static boolean isWordOk(Word word) {
        return word.getSiblings() != null;
    }
}
