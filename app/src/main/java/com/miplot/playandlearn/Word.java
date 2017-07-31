package com.miplot.playandlearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Word {

    public enum WordClass {
        NOUN(0),
        VERB(1),
        ADJ(2),
        ADV(3),
        UNKNOWN(99);

        private final int value;

        WordClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static WordClass fromInteger(int value) throws IOException {
            switch (value) {
                case 0:
                    return NOUN;
                case 1:
                    return VERB;
                case 2:
                    return ADJ;
                case 3:
                    return ADV;
                default:
                    return UNKNOWN;
            }
        }

        public static WordClass fromString(String value) throws IOException {
            switch (value) {
                case "сущ.":
                    return NOUN;
                case "гл.":
                    return VERB;
                case "прил.":
                    return ADJ;
                case "нареч.":
                    return ADV;
                default:
                    return UNKNOWN;
            }
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "сущ.";
                case 1:
                    return "гл.";
                case 2:
                    return "прил.";
                case 3:
                    return "нареч.";
                default:
                    return "";
            }
        }
    };

    private long id;
    private String wordEn;
    private String wordRu;
    private String transcription;
    private WordClass wordClass;
    private String example;
    private String siblings;
    private String exampleAnswer;

    private long unitId;
    private List<Long> topicIds;

    public Word(long id,
                String en,
                String ru,
                String tscb,
                WordClass wordClass,
                String example,
                long unitId,
                List<Long> topicIds,
                String siblings,
                String exampleAnswer)
    {
        this.id = id;
        this.wordEn = en;
        this.wordRu = ru;
        this.transcription = tscb;
        this.wordClass = wordClass;
        this.example = example;
        this.unitId = unitId;
        this.topicIds = topicIds;
        this.siblings = siblings;
        this.exampleAnswer = exampleAnswer;

        if (this.topicIds == null)
            this.topicIds = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getWordEn() {
        return wordEn;
    }

    public String getWordRu() {
        return wordRu;
    }

    public String getTranscription() {
        return transcription;
    }

    public WordClass getWordClass() {
        return wordClass;
    }

    public String getExample() { return example; }

    public long getUnitId() {
        return unitId;
    }

    public List<Long> getTopics() {
        return topicIds;
    }

    public String getSiblings() { return siblings; }

    public String getExampleAnswer() { return exampleAnswer; }

    public void addTopic(Long topicId) {
        topicIds.add(topicId);
    }

    @Override
    public int hashCode() {
        return (int)id;
    }

    @Override
    public boolean equals(Object obj) {
        return id == ((Word)obj).getId();
    }

    @Override
    public String toString() {
        return "{id: " + id +
                ", word_en: " + wordEn +
                ", word_ru: " + wordRu +
                ", transcription: " + transcription +
                ", example: " + example +
                ", class: " + wordClass.toString() +
                ", unit_id: " + unitId +
                ", topics: " + topicIds +
                ", siblings: " + siblings +
                ", example_answer: " + exampleAnswer +
                "}";
    }
}

