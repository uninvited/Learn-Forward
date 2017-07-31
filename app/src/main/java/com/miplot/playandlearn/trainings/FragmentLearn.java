package com.miplot.playandlearn.trainings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

public class FragmentLearn extends BaseTrainingFragment {
    private int currentIndex;
    private Word currentWord;

    private boolean onScreen = false;
    private boolean isCreated = false;

    private TextView wordEnTextView;
    private TextView transcriptionTextView;
    private TextView wordClassTextView;
    private ImageView wordImageView;
    private TextView wordRuTextView;
    private TextView exampleTextView;
    private ImageView audioButton;
    private LinearLayout indicatorsPanelLayout;
    private PageIndicatorsPanel pageIndicatorsPanel;

    private void onShow() {
        Utils.playAudio(getContext(), currentWord);
        btr.handleCorrectAnswer(currentIndex);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_training_learn, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        wordEnTextView = (TextView) rootView.findViewById(R.id.word_en);
        transcriptionTextView = (TextView) rootView.findViewById(R.id.word_transcription);
        wordClassTextView = (TextView) rootView.findViewById(R.id.word_class);
        wordImageView = (ImageView) rootView.findViewById(R.id.word_image);
        wordRuTextView = (TextView) rootView.findViewById(R.id.word_ru);
        exampleTextView = (TextView) rootView.findViewById(R.id.word_example);
        audioButton = (ImageView) rootView.findViewById(R.id.audio);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.playAudio(getContext(), currentWord);
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
        transcriptionTextView.setText(word.getTranscription());
        wordClassTextView.setText(word.getWordClass().toString());
        wordRuTextView.setText(word.getWordRu());
        exampleTextView.setText(word.getExample());
        wordEnTextView.setTextColor(Utils.getWordColor(getContext(), word));

        Bitmap bmp = Utils.getImageBitmap(getContext(), word);
        if (bmp != null) {
            wordImageView.setImageBitmap(bmp);
            wordImageView.setMinimumHeight(400);
            wordImageView.setMinimumWidth(400);
        } else {
            wordImageView.setImageResource(R.drawable.no_image);
        }
    }

    public static boolean isWordOk(Word word) {
        return true;
    }
}
