package com.miplot.playandlearn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputData {
    String version;
    public Map<Long, Topic> topics = new HashMap<>();
    public Map<Long, Unit> units = new HashMap<>();
    public List<Word> words = new ArrayList<>();
}
