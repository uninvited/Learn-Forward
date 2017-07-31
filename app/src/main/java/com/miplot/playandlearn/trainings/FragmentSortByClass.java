package com.miplot.playandlearn.trainings;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

public class FragmentSortByClass extends BaseTrainingFragment {
    private int currentIndex;
    private Word currentWord;

    boolean onScreen = false;
    boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private ViewGroup rootLayout;
    private TextView wordEnView;
    private ImageView audioButton;
    private int correctChestId;

    private LinearLayout indicatorsPanelLayout;
    private PageIndicatorsPanel pageIndicatorsPanel;

    int dragDeltaX;
    int dragDeltaY;
    int tailX;

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
                R.layout.fragment_training_sort_by_class, container, false);

        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        rootLayout = (ViewGroup) rootView.findViewById(R.id.root_layout);
        wordEnView = (TextView) rootView.findViewById(R.id.word_en);
        wordEnView.bringToFront();
        wordEnView.setOnTouchListener(onTouchListener);

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

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int x = (int)event.getRawX();
            final int y = (int)event.getRawY();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) wordEnView.getLayoutParams();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    dragDeltaX = x - (int)wordEnView.getX();
                    dragDeltaY = y - (int)wordEnView.getY();
                    tailX = wordEnView.getWidth() - dragDeltaX;
                    break;
                case MotionEvent.ACTION_UP:
                    drop(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.leftMargin = x - dragDeltaX;
                    params.rightMargin = getActivity().getWindowManager().getDefaultDisplay().getWidth() - (x + tailX);
                    params.topMargin = y - dragDeltaY;
                    params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                    wordEnView.setLayoutParams(params);
                    break;
                default:
                    break;
            }
            rootLayout.invalidate();
            return true;
        }
    };

    void drop(int x, int y) {
        ViewGroup chestsLayout = (ViewGroup) rootLayout.findViewById(R.id.chests);
        ImageView ivNoun = (ImageView) rootLayout.findViewById(R.id.chest_noun);
        ImageView ivVerb = (ImageView) rootLayout.findViewById(R.id.chest_verb);
        ImageView ivAdj = (ImageView) rootLayout.findViewById(R.id.chest_adjective);

        int[] coords = new int[2];
        chestsLayout.getLocationOnScreen(coords);

        if (y < coords[1] || y > coords[1] + chestsLayout.getHeight()) {
            resetWordPosition();
            return;
        }

        if (x < ivVerb.getX()) {
            onChestChosen(ivNoun);
        } else if (x < ivAdj.getX()) {
            onChestChosen(ivVerb);
        } else {
            onChestChosen(ivAdj);
        }
    }

    private void onChestChosen(View view) {
        if (view.getId() == correctChestId) {
            btr.handleCorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
            Utils.playEffect(getContext(), Utils.AUDIO_EFFECT_CLICK);
            wordEnView.setText("");
            wordEnView.setVisibility(View.INVISIBLE);
            // TODO: how to automatially show next word?
        } else {
            resetWordPosition();
            btr.handleIncorrectAnswer(currentIndex);
            pageIndicatorsPanel.refresh();
            Toast.makeText(getContext(), R.string.keep_trying, Toast.LENGTH_SHORT).show();
        }
    }

    private void resetWordPosition() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) wordEnView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.leftMargin = 0;
        params.rightMargin = 0;
        params.topMargin = (int)((80 * Resources.getSystem().getDisplayMetrics().density));
        wordEnView.setLayoutParams(params);
        rootLayout.invalidate();
    }

    private void displayWord(Word word) {
        wordEnView.setText(word.getWordEn());
        wordEnView.setVisibility(View.VISIBLE);
        resetWordPosition();

        switch (word.getWordClass()) {
            case NOUN:
                correctChestId = R.id.chest_noun;
                break;
            case VERB:
                correctChestId = R.id.chest_verb;
                break;
            case ADJ:
                correctChestId = R.id.chest_adjective;
                break;
            default:
                break;
        }
    }

    public void hideAnswer() {
        wordEnView.setText(currentWord.getWordEn());
        wordEnView.setVisibility(View.VISIBLE);
        resetWordPosition();
        hidden = true;
    }

    public static boolean isWordOk(Word word) {
        switch (word.getWordClass()) {
            case NOUN:
            case VERB:
            case ADJ:
                return true;
            default:
                return false;
        }
    }
}
