package com.miplot.playandlearn;

import com.miplot.playandlearn.trainings.BaseTrainingFragment;

import java.io.Serializable;

public class TrainingDescriptor implements Serializable{
    private TrainingType trainingType;
    private int titleResourceId;
    private int colorResourceId;
    private Class<BaseTrainingFragment> fragmentClass;

    TrainingDescriptor(TrainingType trainingType,
                       int titleResourceId,
                       int colorResourceId,
                       Class fragmentClass) {
        this.trainingType = trainingType;
        this.titleResourceId = titleResourceId;
        this.colorResourceId = colorResourceId;
        this.fragmentClass = fragmentClass;
    }

    public TrainingType getTrainingType() { return trainingType; }
    public int getTitleResourceId() { return titleResourceId; }
    public int getColorResourceId() { return colorResourceId; }
    public Class getFragmentClass() { return fragmentClass; }
}
