package com.miplot.playandlearn.trainings;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miplot.playandlearn.BaseTrainingRunner;
import com.miplot.playandlearn.MainApplication;
import com.miplot.playandlearn.PageIndicatorsPanel;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Unit;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

public class FragmentSpeak extends BaseTrainingFragment {
    private int currentIndex;
    private Word currentWord;

    private boolean onScreen = false;
    private boolean isCreated = false;

    // Whether the answer is hidden
    private boolean hidden = true;

    private MainApplication app;
    private TextToSpeech tts;

    private TextView questionView;
    private TextView answerView;
    private TextView questionMarkView;
    private ImageView audioButton;
    private RelativeLayout relativeLayout;

    private LinearLayout indicatorsPanelLayout;
    private PageIndicatorsPanel pageIndicatorsPanel;

    private void onShow() {
        say(currentWord.getExample());
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
                R.layout.fragment_training_speak, container, false);

        app = (MainApplication) getActivity().getApplication();
        tts = app.getTts();

        Unit unit = (Unit)getArguments().getSerializable(ScreenSlidePagerAdapter.ARG_KEY_UNIT);
        currentIndex = getArguments().getInt(ScreenSlidePagerAdapter.ARG_KEY_POS);
        currentWord = btr.getWord(currentIndex);

        questionView = (TextView) rootView.findViewById(R.id.question);
        answerView = (TextView) rootView.findViewById(R.id.answer);
        questionMarkView = (TextView) rootView.findViewById(R.id.question_mark);

        audioButton = (ImageView) rootView.findViewById(R.id.audio);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.playAudio(getContext(), currentWord);
            }
        });

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

        // TODO: hardcode. make it nicer
        TextView hintTextView = (TextView) rootView.findViewById(R.id.hint);
        hintTextView.setText(getHint((int)unit.getId()));

        displayWord(currentWord);
        isCreated = true;
        if (onScreen) {
            onShow();
        }
        return rootView;
    }

    private void displayWord(Word word) {
        questionView.setText(word.getExample());
        answerView.setText(word.getExampleAnswer());
        hideAnswer();
        hidden = true;
    }

    public void showAnswer() {
        if (hidden) {
            answerView.setVisibility(View.VISIBLE);
            questionMarkView.setVisibility(View.INVISIBLE);
            say(currentWord.getExampleAnswer());
            hidden = false;
        }
    }

    public void hideAnswer() {
        answerView.setVisibility(View.INVISIBLE);
        questionMarkView.setVisibility(View.VISIBLE);
        hidden = true;
    }

    public void say(String phrase) {
        if (app.isTtsIsAvailable()) {
            tts.speak(phrase, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private String getHint(int unitId) {
        switch (unitId) {
            case 1:
                return "Придумай ответ на вопрос. Прослушай ответ и проверь себя.";
            case 2:
                return "Придумай противоположное по смыслу предложение. Прослушай ответ и проверь себя.";
            case 3:
                return "Догадайся, кто эти люди по профессии. Прослушай ответ и проверь себя.";
            case 4:
                return "Перефразируй предложение, используя конструкцию there is / there are. Прослушай ответ и проверь себя.";
            case 5:
                return "Проиллюстрируй эти предложения примерами. Прослушай ответ и проверь себя.";
            case 6:
                return "Ответь на вопрос и проверь себя, прослушав образец ответа.";
            case 7:
                return "Перефразируй эти предложения так, чтобы они были о тебе. Прослушай примеры и проверь себя.";
            default:
                return "";
        }
    }

    public static boolean isWordOk(Word word) {
        return word.getExampleAnswer() != null;
    }
}
