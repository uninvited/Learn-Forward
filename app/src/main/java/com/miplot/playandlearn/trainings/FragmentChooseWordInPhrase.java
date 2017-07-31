package com.miplot.playandlearn.trainings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

import org.apmem.tools.layouts.FlowLayout;

public class FragmentChooseWordInPhrase extends BaseTrainingFragment {
    private int currentIndex;
    private Word currentWord;
    private int correctWordIndex;

    private boolean onScreen = false;
    private boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private TextView wordRuTextView;
    private FlowLayout phraseLayout;

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
                R.layout.fragment_training_choose_word_in_phrase, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        wordRuTextView = (TextView) rootView.findViewById(R.id.word_ru);
        phraseLayout = (FlowLayout) rootView.findViewById(R.id.phrase_layout);

        indicatorsPanelLayout = (LinearLayout) rootView.findViewById(R.id.indicators_panel);
        pageIndicatorsPanel = new PageIndicatorsPanel(getActivity(), indicatorsPanelLayout, btr, currentIndex);

        displayWord(currentWord);
        isCreated = true;
        if (onScreen) {
            onShow();
        }
        return rootView;
    }

    public void onWordClick(View view) {
        if (!hidden)
            return;

        TextView textView = (TextView) view;
        if (view.getId() == correctWordIndex) {
            String text = textView.getText().toString();

            SpannableString spannableText = new SpannableString(text);
            spannableText.setSpan(
                    new BackgroundColorSpan(ContextCompat.getColor(getContext(), R.color.light_green)),
                    0, getLastLetterPos(text) + 1, 0);
            textView.setText(spannableText);

            showAnswer();
            btr.handleCorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
        } else {
            textView.setTextColor(Color.RED);
            textView.setClickable(false);
            btr.handleIncorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
        }
    }

    private int getLastLetterPos(String text) {
        int lastLetterPos = text.length() - 1;
        for (; lastLetterPos >= 0; lastLetterPos--) {
            char ch = text.charAt(lastLetterPos);
            if (Character.isLetter(ch)) break;
        }
        return lastLetterPos;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onWordClick(v);
        }
    };

    private void displayWord(Word word) {
        wordRuTextView.setText(word.getWordRu());
        phraseLayout.removeAllViews();
        correctWordIndex = -1;

        String example = word.getExample();

        String[] words = example.split(" ");
        int index = 0;
        for (String variantWord : words) {
            TextView tv = new TextView(getContext());
            tv.setId(index);
            tv.setText(variantWord + " ");
            tv.setTextSize((int)getResources().getDimension(R.dimen.training_word_en_text_size)
                    / getResources().getDisplayMetrics().density);
            tv.setPadding(0, 0, 0, 6);
            tv.setClickable(true);
            tv.setOnClickListener(onClickListener);
            phraseLayout.addView(tv);

            String match = word.getWordEn().toLowerCase();
            // Hack for words like (study -> studies, fly -> flies)
            if (match.endsWith("y")) {
                match = match.substring(0, match.length() - 1);
            }
            if (variantWord.toLowerCase().startsWith(match)) {
                correctWordIndex = index;
            }
            index++;
        }
        hideAnswer();
        hidden = true;
    }

    private void showAnswer() {
        if (hidden) {
            Utils.playAudio(getContext(), currentWord);
            hidden = false;
        }
    }

    public void hideAnswer() {
        for (int i = 0; i < phraseLayout.getChildCount(); i++) {
            TextView tv = (TextView) phraseLayout.getChildAt(i);
            tv.setText(tv.getText().toString());
            tv.setTextColor(Color.DKGRAY);
        }
        hidden = true;
    }

    public static boolean isWordOk(Word word) {
        return !word.getWordEn().contains(" ");
    }
}
