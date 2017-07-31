package com.miplot.playandlearn;

public class Topic {
    private long id;
    private String nameEn;
    private String nameRu;

    public Topic(long id, String nameEn, String nameRu) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
    }

    public long getId() { return id; }
    public String getNameEn() { return nameEn; }
    public String getNameRu() { return nameRu; }

    @Override
    public int hashCode() {
        return (int)id;
    }

    @Override
    public String toString() {
        return "{id: " + id +
                ", name_en: " + nameEn +
                ", name_ru: " + nameRu + "}";
    }
}