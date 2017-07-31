package com.miplot.playandlearn.trainings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

public class FragmentReadAndRecall extends BaseTrainingFragment {
    private int currentIndex;
    private Word currentWord;

    private boolean onScreen = false;
    private boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private TextView wordEnTextView;
    private ImageView wordImageView;
    private TextView wordRuTextView;
    private TextView questionMarkView;
    private RelativeLayout relativeLayout;

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
                R.layout.fragment_training_read_and_recall, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        wordEnTextView = (TextView) rootView.findViewById(R.id.word_en);
        wordImageView = (ImageView) rootView.findViewById(R.id.word_image);
        wordRuTextView = (TextView) rootView.findViewById(R.id.word_ru);
        questionMarkView = (TextView) rootView.findViewById(R.id.question_mark);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Answer is always correct in this training
                showAnswer();
                btr.handleCorrectAnswer(currentIndex);
                pageIndicatorsPanel.refresh();
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

    private void displayWord(Word word) {
        wordEnTextView.setText(word.getWordEn());
        wordRuTextView.setText(word.getWordRu());
        wordEnTextView.setTextColor(Utils.getWordColor(getContext(), word));

        Bitmap bmp = Utils.getImageBitmap(getContext(), word);
        if (bmp != null) {
            wordImageView.setImageBitmap(bmp);
            wordImageView.setMinimumHeight(400);
            wordImageView.setMinimumWidth(400);
        } else {
            wordImageView.setImageResource(R.drawable.no_image);
        }

        hideAnswer();
    }

    public void showAnswer() {
        if (hidden) {
            wordImageView.setVisibility(View.VISIBLE);
            wordRuTextView.setVisibility(View.VISIBLE);
            questionMarkView.setVisibility(View.INVISIBLE);
            Utils.playAudio(getContext(), currentWord);
            hidden = false;
        }
    }

    public void hideAnswer() {
        wordImageView.setVisibility(View.INVISIBLE);
        wordRuTextView.setVisibility(View.INVISIBLE);
        questionMarkView.setVisibility(View.VISIBLE);
        hidden = true;
    }

    public static boolean isWordOk(Word word) {
        return true;
    }
}
