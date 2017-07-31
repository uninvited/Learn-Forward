package com.miplot.playandlearn;


import java.io.IOException;

public enum TrainingType {
    LEARN(1),
    READ_AND_RECALL(2),
    LISTEN_AND_CHOOSE(3),
    LISTEN_AND_PRINT(4),
    TRANSLATE(5),
    CHOOSE_TRANSLATION(6),
    CHOOSE_WORD_IN_PHRASE(7),
    CHOOSE_ODD(8),
    SORT_BY_CLASS(9),
    SPEAK(10);

    private final int value;

    TrainingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TrainingType fromInteger(int value) throws IOException {
        switch (value) {
            case 1:
                return LEARN;
            case 2:
                return READ_AND_RECALL;
            case 3:
                return LISTEN_AND_CHOOSE;
            case 4:
                return LISTEN_AND_PRINT;
            case 5:
                return TRANSLATE;
            case 6:
                return CHOOSE_TRANSLATION;
            case 7:
                return CHOOSE_WORD_IN_PHRASE;
            case 8:
                return CHOOSE_ODD;
            case 9:
                return SORT_BY_CLASS;
            case 10:
                return SPEAK;
            default:
                throw new IOException();
        }
    }
}