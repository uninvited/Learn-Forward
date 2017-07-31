package com.miplot.playandlearn.trainings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class FragmentChooseTranslation extends BaseTrainingFragment {
    private static final int NUM_VARIANTS = 4;

    private int currentIndex;
    private Word currentWord;
    private List<Word> wordsPool;
    private int correctAnswerIndex;

    private boolean onScreen = false;
    private boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private TextView wordEnTextView;
    private TextView[] textViews = new TextView[NUM_VARIANTS];
    private TextView wordRuTextView;
    private ImageView audioButton;

    private LinearLayout indicatorsPanelLayout;
    private PageIndicatorsPanel pageIndicatorsPanel;

    private void onShow() {
        Utils.playAudio(getContext(), currentWord);
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
                R.layout.fragment_training_choose_translation, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        wordEnTextView = (TextView) rootView.findViewById(R.id.word_en);
        textViews[0] = (TextView) rootView.findViewById(R.id.answer_variant1);
        textViews[1] = (TextView) rootView.findViewById(R.id.answer_variant2);
        textViews[2] = (TextView) rootView.findViewById(R.id.answer_variant3);
        textViews[3] = (TextView) rootView.findViewById(R.id.answer_variant4);
        wordRuTextView = (TextView) rootView.findViewById(R.id.word_ru);
        for (TextView tv : textViews) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVariantClick(v);
                }
            });
        }
        audioButton = (ImageView) rootView.findViewById(R.id.audio);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.playAudio(getContext(), currentWord);
            }
        });
        indicatorsPanelLayout = (LinearLayout) rootView.findViewById(R.id.indicators_panel);
        pageIndicatorsPanel = new PageIndicatorsPanel(getActivity(), indicatorsPanelLayout, btr, currentIndex);

        List<Word> words = btr.getWords();
        wordsPool = new ArrayList<>(words.size());
        for (Word word : words) {
            wordsPool.add(word);
        }

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
            Utils.playAudio(getContext(), currentWord);
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
        wordEnTextView.setText(word.getWordEn());
        wordRuTextView.setText(word.getWordRu());
        wordEnTextView.setTextColor(Utils.getWordColor(getContext(), word));

        List<Word> variants = new ArrayList<>();
        // As long as the pool is small it's Ok to reshuffle it entirely

        Random rnd = new Random(System.nanoTime());

        Collections.shuffle(wordsPool, rnd);
        variants.add(wordsPool.get(0));
        variants.add(wordsPool.get(1));
        variants.add(wordsPool.get(2));
        // If we have added our word, replace it
        int index = variants.indexOf(word);
        if (index >= 0) {
            variants.set(index, wordsPool.get(3));
        }
        variants.add(word);

        Collections.shuffle(variants, rnd);
        correctAnswerIndex = variants.indexOf(word);

        for (int i = 0; i < NUM_VARIANTS; i++) {
            showAnswerVariant(textViews[i], variants.get(i));
        }
        hideAnswer();
    }

    private void showAnswerVariant(TextView view, Word word) {
        view.setText(word.getWordRu());
        view.setAlpha(1f);
        view.setBackgroundColor(getResources().getColor(R.color.light_grey));
    }

    private void showAnswer() {
        if (hidden) {
            wordRuTextView.setVisibility(View.VISIBLE);
            for (TextView tv : textViews) {
                tv.setClickable(false);
            }
            hidden = false;
        }
    }

    public void hideAnswer() {
        wordRuTextView.setVisibility(View.INVISIBLE);
        for (TextView tv : textViews) {
            tv.setClickable(true);
            tv.setAlpha(1f);
            tv.setBackgroundColor(getResources().getColor(R.color.light_grey));
        }
        hidden = true;
    }

    public static boolean isWordOk(Word word) {
        return true;
    }
}
