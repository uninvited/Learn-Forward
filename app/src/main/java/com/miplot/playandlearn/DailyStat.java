package com.miplot.playandlearn;

public class DailyStat {
    public int year;
    public int month;
    public int day;
    public int trainingsCount;
    public int durationSec;

    public DailyStat(int y, int m, int d, int trainingsCount, int durationSec) {
        this.year = y;
        this.month = m;
        this.day = d;
        this.trainingsCount = trainingsCount;
        this.durationSec = durationSec;
    }

    @Override
    public String toString() {
        return "Дата: " + year + "-" + pad(month) + "-" + pad(day) +
                ", выполнено тренировок: " + trainingsCount +
                ", длительность (мин): " + Math.round(durationSec / 60.) +
                ".";
    }

    private String pad(int val) {
        return (val < 10 ? "0" : "") + Integer.toString(val);
    }
}

