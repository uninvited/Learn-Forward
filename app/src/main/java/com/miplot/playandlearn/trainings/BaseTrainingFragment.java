package com.miplot.playandlearn.trainings;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.miplot.playandlearn.BaseTrainingRunner;

public class BaseTrainingFragment extends Fragment {
    protected BaseTrainingRunner btr;

    public void setBtr(BaseTrainingRunner btr) {
        this.btr = btr;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }
}
