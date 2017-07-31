package com.miplot.playandlearn.trainings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

public class FragmentTranslate extends BaseTrainingFragment {
    private int currentIndex;
    private Word currentWord;

    private boolean onScreen = false;
    private boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private TextView wordEnTextView;
    private TextView wordRuTextView;
    private TextView exampleView;
    private EditText userInput;
    private Button submitButton;

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
                R.layout.fragment_training_translate, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        wordEnTextView = (TextView) rootView.findViewById(R.id.word_en);
        wordRuTextView = (TextView) rootView.findViewById(R.id.word_ru);
        exampleView = (TextView) rootView.findViewById(R.id.word_example);
        userInput = (EditText) rootView.findViewById(R.id.user_input);
        submitButton = (Button) rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });

        indicatorsPanelLayout = (LinearLayout) rootView.findViewById(R.id.indicators_panel);
        pageIndicatorsPanel = new PageIndicatorsPanel(getActivity(), indicatorsPanelLayout, btr, currentIndex);

        displayWord(currentWord);
        isCreated = true;
        if (onScreen) {
            onShow();
        }
        return rootView;
    }


    public void onSubmitClick() {
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        String userAnswer = userInput.getText().toString();
        if (userAnswer.equalsIgnoreCase(currentWord.getWordEn())) {
            showAnswer();
            btr.handleCorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
        } else {
            btr.handleIncorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
            userInput.setText("");
            Toast.makeText(getContext(), R.string.keep_trying, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayWord(Word word) {
        wordEnTextView.setText(word.getWordEn());
        wordRuTextView.setText(word.getWordRu());
        wordEnTextView.setTextColor(Utils.getWordColor(getContext(), word));
        exampleView.setText(word.getExample());
        userInput.setHint(dashify(currentWord.getWordEn()));

        hideAnswer();
        hidden = true;
    }

    private void showAnswer() {
        if (hidden) {
            wordEnTextView.setVisibility(View.VISIBLE);
            exampleView.setVisibility(View.VISIBLE);
            userInput.setTextColor(Utils.getWordColor(getContext(), currentWord));
            userInput.setEnabled(false);
            submitButton.setEnabled(false);
            Utils.playAudio(getContext(), currentWord);
            hidden = false;
        }
    }

    public void hideAnswer() {
        wordEnTextView.setVisibility(View.INVISIBLE);
        exampleView.setVisibility(View.INVISIBLE);
        userInput.setText("");
        userInput.setTextColor(getResources().getColor(android.R.color.black));
        userInput.setEnabled(true);
        submitButton.setEnabled(true);
        hidden = true;
    }

    private static String dashify(String word) {
        StringBuilder result = new StringBuilder();
        result.append(word.charAt(0));
        for (int i = 1; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                result.append(' ');
            } else {
                result.append("—");
            }
        }
        return result.toString();
    }

    public static boolean isWordOk(Word word) {
        return true;
    }
}
