package com.miplot.playandlearn;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InputParser {
    private static final String TAG = "InputParser";

    private static final String KEY_VERSION = "version";
    private static final String KEY_TOPICS = "topics";
    private static final String KEY_UNITS = "units";
    private static final String KEY_WORDS = "words";

    private JsonReader jsonReader;

    public InputParser(InputStream stream) {
        jsonReader = new JsonReader(new InputStreamReader(stream));
    }

    public String parseVersion() {
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                if (jsonReader.nextName().equals(KEY_VERSION)) {
                    return jsonReader.nextString();
                }
                jsonReader.skipValue();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputData parse() {
        InputData data = new InputData();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                Log.i(TAG, "Key: " + key);
                switch (key) {
                    case KEY_VERSION:
                        data.version = jsonReader.nextString();
                        break;
                    case KEY_TOPICS:
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            Topic topic = parseTopic(jsonReader);
                            data.topics.put(topic.getId(), topic);
                        }
                        jsonReader.endArray();
                        break;
                    case KEY_UNITS:
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            Unit unit = parseUnit(jsonReader);
                            data.units.put(unit.getId(), unit);
                        }
                        jsonReader.endArray();
                        break;
                    case KEY_WORDS:
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            Word word = parseWord(jsonReader);
                            Log.i(TAG, "Parsed word: " + word);
                            data.words.add(word);
                        }
                        jsonReader.endArray();
                        break;
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    private Topic parseTopic(JsonReader reader) throws IOException {
        final String KEY_ID = "id";
        final String KEY_NAME_RU = "name_ru";

        reader.beginObject();
        Long id = null;
        String nameRu = null;
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case KEY_ID:
                    id = reader.nextLong();
                    break;
                case KEY_NAME_RU:
                    nameRu = reader.nextString();
                    break;
            }
        }
        reader.endObject();
        return new Topic(id, null, nameRu);
    }

    private Unit parseUnit(JsonReader reader) throws IOException {
        final String KEY_ID = "id";
        final String KEY_NAME_EN = "name_en";

        reader.beginObject();

        Long unitId = null;
        String unitNameEn = null;
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case KEY_ID:
                    unitId = reader.nextLong();
                    break;
                case KEY_NAME_EN:
                    unitNameEn = reader.nextString();
                    break;
            }
        }
        reader.endObject();
        return new Unit(unitId, unitNameEn);
    }


    private Word parseWord(JsonReader reader) throws IOException {
        final String KEY_ID = "id";
        final String KEY_NAME_EN = "name_en";
        final String KEY_NAME_RU = "name_ru";
        final String KEY_TRANSCRIPTION = "tscp";
        final String KEY_EXAMPLE = "example";
        final String KEY_WORD_CLASS = "class";
        final String KEY_TOPICS = "topics";
        final String KEY_UNIT_ID = "unit_id";
        final String KEY_SIBLINGS = "siblings";
        final String KEY_EXAMPLE_ANSWER = "example_answer";

        reader.beginObject();

        Long id = null;
        String nameEn = null;
        String nameRu = null;
        String transcription = null;
        String example = null;
        Word.WordClass wordClass = null;
        List<Long> topicIds = new ArrayList<Long>();
        Long unitId = null;
        String siblings = null;
        String exampleAnswer = null;

        while (reader.hasNext()) {
            String key = reader.nextName();

            switch (key) {
                case KEY_ID:
                    id = reader.nextLong();
                    break;
                case KEY_NAME_EN:
                    nameEn = reader.nextString();
                    break;
                case KEY_NAME_RU:
                    nameRu = reader.nextString();
                    break;
                case KEY_TRANSCRIPTION:
                    transcription = reader.nextString();
                    break;
                case KEY_EXAMPLE:
                    example = reader.nextString();
                    break;
                case KEY_WORD_CLASS:
                    wordClass = Word.WordClass.fromString(reader.nextString());
                    break;
                case KEY_TOPICS:
                    reader.beginArray();
                    while (reader.hasNext()) {
                        topicIds.add(reader.nextLong());
                    }
                    reader.endArray();
                    break;
                case KEY_UNIT_ID:
                    unitId = reader.nextLong();
                    break;
                case KEY_SIBLINGS:
                    siblings = reader.nextString();
                    if (siblings.isEmpty()) siblings = null;
                    break;
                case KEY_EXAMPLE_ANSWER:
                    exampleAnswer = reader.nextString();
                    if (exampleAnswer.isEmpty()) exampleAnswer = null;
                    break;
            }
        }

        reader.endObject();
        return new Word(
                id, nameEn, nameRu, transcription, wordClass, example,
                unitId, topicIds, siblings, exampleAnswer);
    }
}
