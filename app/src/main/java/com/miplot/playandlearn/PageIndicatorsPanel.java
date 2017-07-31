package com.miplot.playandlearn;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndicatorsPanel {
    private final static int MIN_PAGE_INDICATOR_SOCKETS = 20;
    private final float INDICATORS_PANEL_WIDTH_RATIO = 0.75f;

    private Activity activity;
    private LinearLayout panelLayout;
    private int size;
    private BaseTrainingRunner btr;
    private int currentIndex;

    public PageIndicatorsPanel(Activity activity, LinearLayout panelLayout, BaseTrainingRunner btr, int currentIndex) {
        this.activity = activity;
        this.panelLayout = panelLayout;
        this.btr = btr;
        this.currentIndex = currentIndex;
        this.size = btr.getWordsCount();

        setupInitial();
    }

    public void refresh() {
        for (int i = 0; i < btr.getWordsCount(); i++) {
            BaseTrainingRunner.PassState passState = btr.getPassState(i);
            ImageView led = (ImageView) panelLayout.getChildAt(i);
            switch (passState) {
                case PASSED:
                    led.setImageDrawable(ContextCompat.getDrawable(
                            activity, i == currentIndex ? R.drawable.lamp_large_green : R.drawable.lamp_small_green));
                    break;
                case FAILED:
                    led.setImageDrawable(ContextCompat.getDrawable(
                            activity, i == currentIndex ? R.drawable.lamp_large_red : R.drawable.lamp_small_red));
                    break;
                case NEW:
                    led.setImageDrawable(ContextCompat.getDrawable(
                            activity, i == currentIndex ? R.drawable.lamp_large_grey : R.drawable.lamp_small_grey));
                    break;
            }
        }
    }

    private void setupInitial() {
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        int numPages = Math.max(btr.getWordsCount(), MIN_PAGE_INDICATOR_SOCKETS);
        int indicatorSize = (int)((INDICATORS_PANEL_WIDTH_RATIO * screenWidth) / numPages);

        for (int i = 0; i < size; i++) {
            ImageView led = new ImageView(activity);
            panelLayout.addView(led);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)led.getLayoutParams();
            params.gravity = Gravity.CENTER;
            params.width = indicatorSize;
            params.height = indicatorSize;
            led.setLayoutParams(params);
        }
        refresh();
    }
}
