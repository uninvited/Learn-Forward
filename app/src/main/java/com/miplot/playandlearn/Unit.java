package com.miplot.playandlearn;

import java.io.Serializable;

public class Unit implements Serializable {
    private long id;
    private String name;

    public Unit(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "{id: " + id + ", name: " + name + "}";
    }

    @Override
    public int hashCode() {
        return (int)id;
    }
}
