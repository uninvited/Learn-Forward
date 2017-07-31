package com.miplot.playandlearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Db {
    private static final String TAG = "DB";

    private final DbHelper dbHelper;

    public Db(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void dropNonCriticalData() {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.dropWords(db);
        dbHelper.dropTopics(db);
        dbHelper.dropVersion(db);
        dbHelper.recreateTables(db);
    }

    public String getVersion() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String[] COLUMNS = { DbHelper.COL_DATA_VERSION };
        Cursor cursor = db.query(DbHelper.TBL_VERSION, COLUMNS, null, null, null, null, null);
        final int index = cursor.getColumnIndex(DbHelper.COL_DATA_VERSION);
        try {
            while (cursor.moveToNext()) {
                return cursor.getString(index);
            }
            return null;
        } finally {
            cursor.close();
            db.close();
        }
    }

    public List<Unit> getUnits() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] COLUMNS = { DbHelper.COL_UNIT_ID, DbHelper.COL_UNIT_NAME };
        Cursor cursor = db.query(DbHelper.TBL_UNIT, COLUMNS, null, null, null, null, null);

        final int idIndex = cursor.getColumnIndex(DbHelper.COL_UNIT_ID);
        final int nameIndex = cursor.getColumnIndex(DbHelper.COL_UNIT_NAME);

        List<Unit> units = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIndex);
            String name = cursor.getString(nameIndex);
            units.add(new Unit(id, name));
        }
        cursor.close();
        db.close();
        return units;
    }

    public Map<Long, Topic> getTopics() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] COLUMNS = { DbHelper.COL_TOPIC_ID, DbHelper.COL_TOPIC_NAME_EN, DbHelper.COL_TOPIC_NAME_RU };
        Cursor cursor = db.query(DbHelper.TBL_TOPIC, COLUMNS, null, null, null, null, null);

        final int idIndex = cursor.getColumnIndex(DbHelper.COL_TOPIC_ID);
        final int nameEnIndex = cursor.getColumnIndex(DbHelper.COL_TOPIC_NAME_EN);
        final int nameRuIndex = cursor.getColumnIndex(DbHelper.COL_TOPIC_NAME_RU);

        Map<Long, Topic> topics = new HashMap<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIndex);
            String nameEn = cursor.getString(nameEnIndex);
            String nameRu = cursor.getString(nameRuIndex);
            topics.put(id, new Topic(id, nameEn, nameRu));
        }
        cursor.close();
        db.close();
        return topics;
    }

    public int getWordsCount(long unitId) {
        final String COL_COUNT = "cnt";
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT count(*) AS " + COL_COUNT + " FROM " + DbHelper.TBL_WORD + "\n" +
                "WHERE " + DbHelper.COL_WORD_UNIT_ID + "=" + Long.toString(unitId), null);
        if (cursor.moveToNext()) {
            final int index = cursor.getColumnIndex(COL_COUNT);
            return cursor.getInt(index);
        }
        return 0;
    }

    public List<Word> getWords(Long unitId) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] WORD_COLUMNS = {
                DbHelper.COL_WORD_ID,
                DbHelper.COL_WORD_EN,
                DbHelper.COL_WORD_RU,
                DbHelper.COL_WORD_TSCP,
                DbHelper.COL_WORD_CLASS,
                DbHelper.COL_WORD_EXAMPLE,
                DbHelper.COL_WORD_SIBLINGS,
                DbHelper.COL_WORD_EXAMPLE_ANSWER,
                DbHelper.COL_WORD_UNIT_ID
        };
        String whereClause = null;
        if (unitId != null)
            whereClause = DbHelper.COL_WORD_UNIT_ID + "=" + unitId;

        Cursor cursor = db.query(DbHelper.TBL_WORD, WORD_COLUMNS, whereClause, null, null, null, null);

        final int wordIdIndex = cursor.getColumnIndex(DbHelper.COL_WORD_ID);
        final int wordEnIndex = cursor.getColumnIndex(DbHelper.COL_WORD_EN);
        final int wordRuIndex = cursor.getColumnIndex(DbHelper.COL_WORD_RU);
        final int wordTscpIndex = cursor.getColumnIndex(DbHelper.COL_WORD_TSCP);
        final int wordClassIndex = cursor.getColumnIndex(DbHelper.COL_WORD_CLASS);
        final int wordExampleIndex = cursor.getColumnIndex(DbHelper.COL_WORD_EXAMPLE);
        final int wordSiblingsIndex = cursor.getColumnIndex(DbHelper.COL_WORD_SIBLINGS);
        final int wordExampleAnswerIndex = cursor.getColumnIndex(DbHelper.COL_WORD_EXAMPLE_ANSWER);
        final int wordUnitIdIndex = cursor.getColumnIndex(DbHelper.COL_WORD_UNIT_ID);

        Map<Long, Word> wordsMap = new HashMap<>();
        List<Long> wordIds = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(wordIdIndex);
                String wordEn = cursor.getString(wordEnIndex);
                String wordRu = cursor.getString(wordRuIndex);
                String wordTscp = cursor.getString(wordTscpIndex);
                Word.WordClass wordClass = Word.WordClass.fromInteger(cursor.getInt((wordClassIndex)));
                String example = cursor.getString(wordExampleIndex);
                String wordSiblings = cursor.getString(wordSiblingsIndex);
                String wordExampleAnswer = cursor.getString(wordExampleAnswerIndex);
                long wordUnitId = cursor.getLong(wordUnitIdIndex);
                wordsMap.put(id, new Word(
                        id, wordEn, wordRu, wordTscp, wordClass, example,
                        wordUnitId, null, wordSiblings, wordExampleAnswer));
                wordIds.add(id);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cursor.close();

        final String[] WORD_TOPIC_COLUMNS = { DbHelper.COL_WORD_ID, DbHelper.COL_TOPIC_ID };
        whereClause = DbHelper.COL_WORD_ID + " IN (" + join(wordIds, ", ") + ")";

        Cursor wtCursor = db.query(DbHelper.TBL_WORD_TOPIC, WORD_TOPIC_COLUMNS, whereClause, null, null, null, null);

        final int wordIdIdx = wtCursor.getColumnIndex(DbHelper.COL_WORD_ID);
        final int topicIdIdx = wtCursor.getColumnIndex(DbHelper.COL_TOPIC_ID);
        while (wtCursor.moveToNext()) {
            long wordId = wtCursor.getLong(wordIdIdx);
            long topicId = wtCursor.getLong(topicIdIdx);
            Word w = wordsMap.get(wordId);
            if (w == null) {
                Log.e(TAG, "Missing WORD " + wordId);
                continue;
            }
            w.addTopic(topicId);
        }
        wtCursor.close();
        db.close();
        return new ArrayList<>(wordsMap.values());
    }


    public Map<TrainingType, Integer> getProgress(long unitId) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] COLUMNS = { DbHelper.COL_PROGRESS_TRAINING_ID, DbHelper.COL_PROGRESS_COMPLETED };

        String whereClause = DbHelper.COL_PROGRESS_UNIT_ID + "=" + unitId;
        Cursor cursor = db.query(DbHelper.TBL_PROGRESS, COLUMNS, whereClause, null, null, null, null);

        final int trainingIdIndex = cursor.getColumnIndex(DbHelper.COL_PROGRESS_TRAINING_ID);
        final int completedIndex = cursor.getColumnIndex(DbHelper.COL_PROGRESS_COMPLETED);

        Map<TrainingType, Integer> progressMap = new HashMap<>();
        while (cursor.moveToNext()) {
            int trainingId = cursor.getInt(trainingIdIndex);
            int completed = cursor.getInt(completedIndex);
            try {
                progressMap.put(TrainingType.fromInteger(trainingId), completed);
            } catch (IOException e) {
                Log.w(TAG, "Unknown training type id read from DB: " + trainingId);
            }
        }
        cursor.close();
        db.close();
        return progressMap;
    }

    public DailyStat getStatForDay(int year, int month, int day) {
        List<DailyStat> stats = getStatImpl(year, month, day);
        if (stats.isEmpty()) {
            return new DailyStat(year, month, day, 0, 0);
        } else {
            return stats.get(0);
        }
    }

    public List<DailyStat> getStatForMonth(int year, int month) {
        return getStatImpl(year, month, -1);
    }

    public List<DailyStat> getFullStat() {
        return getStatImpl(-1, -1, -1);
    }

    private List<DailyStat> getStatImpl(int year, int month, int day) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String[] COLUMNS = {
                DbHelper.COL_DAILY_STAT_YEAR,
                DbHelper.COL_DAILY_STAT_MONTH,
                DbHelper.COL_DAILY_STAT_DAY,
                DbHelper.COL_DAILY_STAT_TRAIN_CNT,
                DbHelper.COL_DAILY_STAT_DURATION };

        String whereClause = "1=1";
        if (year != -1) {
            whereClause += " AND " + DbHelper.COL_DAILY_STAT_YEAR + "=" + year;
        }
        if (month != -1) {
            whereClause += " AND " + DbHelper.COL_DAILY_STAT_MONTH + "=" + month;
        }
        if (day != -1) {
            whereClause += " AND " + DbHelper.COL_DAILY_STAT_DAY + "=" + day;
        }

        Cursor cursor = db.query(DbHelper.TBL_DAILY_STAT, COLUMNS, whereClause, null, null, null, null);
        final int yearIndex = cursor.getColumnIndex(DbHelper.COL_DAILY_STAT_YEAR);
        final int monthIndex = cursor.getColumnIndex(DbHelper.COL_DAILY_STAT_MONTH);
        final int dayIndex = cursor.getColumnIndex(DbHelper.COL_DAILY_STAT_DAY);
        final int trainCntIndex = cursor.getColumnIndex(DbHelper.COL_DAILY_STAT_TRAIN_CNT);
        final int durationIndex = cursor.getColumnIndex(DbHelper.COL_DAILY_STAT_DURATION);

        List<DailyStat> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            int curYear = cursor.getInt(yearIndex);
            int curMonth = cursor.getInt(monthIndex);
            int curDay = cursor.getInt(dayIndex);
            int trainCnt = cursor.getInt(trainCntIndex);
            int duration = cursor.getInt(durationIndex);

            result.add(new DailyStat(curYear, curMonth, curDay, trainCnt, duration));
        }
        cursor.close();
        db.close();
        return result;
    }

    void addVersion(String version) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COL_DATA_VERSION, version);
        db.insert(DbHelper.TBL_VERSION, null, values);
    }

    void addUnits(Iterable<Unit> units) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Unit unit : units) {
            final ContentValues values = new ContentValues();
            values.put(DbHelper.COL_UNIT_ID, unit.getId());
            values.put(DbHelper.COL_UNIT_NAME, unit.getName());
            db.insert(DbHelper.TBL_UNIT, null, values);
        }
    }

    void addTopics(Iterable<Topic> topics) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Topic topic : topics) {
            final ContentValues values = new ContentValues();
            values.put(DbHelper.COL_TOPIC_ID, topic.getId());
            values.put(DbHelper.COL_TOPIC_NAME_EN, topic.getNameEn());
            values.put(DbHelper.COL_TOPIC_NAME_RU, topic.getNameRu());
            db.insert(DbHelper.TBL_TOPIC, null, values);
        }
    }

    void addWords(Iterable<Word> words) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Word word : words) {
            final ContentValues values = new ContentValues();
            values.put(DbHelper.COL_WORD_ID, word.getId());
            values.put(DbHelper.COL_WORD_EN, word.getWordEn());
            values.put(DbHelper.COL_WORD_RU, word.getWordRu());
            values.put(DbHelper.COL_WORD_TSCP, word.getTranscription());
            values.put(DbHelper.COL_WORD_CLASS, word.getWordClass().getValue());
            values.put(DbHelper.COL_WORD_EXAMPLE, word.getExample());
            values.put(DbHelper.COL_WORD_SIBLINGS, word.getSiblings());
            values.put(DbHelper.COL_WORD_EXAMPLE_ANSWER, word.getExampleAnswer());
            values.put(DbHelper.COL_WORD_UNIT_ID, word.getUnitId());
            db.insert(DbHelper.TBL_WORD, null, values);

            for (Long topicId : word.getTopics()) {
                final ContentValues wtValues = new ContentValues();
                wtValues.put(DbHelper.COL_WORD_ID, word.getId());
                wtValues.put(DbHelper.COL_TOPIC_ID, topicId);
                db.insert(DbHelper.TBL_WORD_TOPIC, null, wtValues);
            }

        }
    }

    void updateProgress(long unitId, TrainingType trainingType, int progress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COL_PROGRESS_UNIT_ID, unitId);
        values.put(DbHelper.COL_PROGRESS_TRAINING_ID, trainingType.getValue());
        values.put(DbHelper.COL_PROGRESS_COMPLETED, progress);

        String whereClause = DbHelper.COL_PROGRESS_UNIT_ID + "=" + unitId +
                " AND " + DbHelper.COL_PROGRESS_TRAINING_ID + "=" + trainingType.getValue();
        db.update(DbHelper.TBL_PROGRESS, values, whereClause, null);
    }

    void addProgress(long unitId, Map<TrainingType, Integer> progressMap) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Map.Entry<TrainingType, Integer> entry : progressMap.entrySet()) {
            final ContentValues values = new ContentValues();
            values.put(DbHelper.COL_PROGRESS_UNIT_ID, unitId);
            values.put(DbHelper.COL_PROGRESS_TRAINING_ID, entry.getKey().getValue());
            values.put(DbHelper.COL_PROGRESS_COMPLETED, entry.getValue());
            db.insert(DbHelper.TBL_PROGRESS, null, values);
        }
    }

    public void addStat(int year, int month, int day, int trainingsCount, int durationSec) {
        List<DailyStat> curStats = getStatImpl(year, month, day);

        if (!curStats.isEmpty()) {
            trainingsCount += curStats.get(0).trainingsCount;
            durationSec += curStats.get(0).durationSec;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(DbHelper.COL_DAILY_STAT_YEAR, year);
        values.put(DbHelper.COL_DAILY_STAT_MONTH, month);
        values.put(DbHelper.COL_DAILY_STAT_DAY, day);
        values.put(DbHelper.COL_DAILY_STAT_TRAIN_CNT, trainingsCount);
        values.put(DbHelper.COL_DAILY_STAT_DURATION, durationSec);

        if (curStats.isEmpty()) {
            db.insert(DbHelper.TBL_DAILY_STAT, null, values);
        } else {
            String whereClause = DbHelper.COL_DAILY_STAT_YEAR + "=" + year +
                    " AND " + DbHelper.COL_DAILY_STAT_MONTH + "=" + month +
                    " AND " + DbHelper.COL_DAILY_STAT_DAY + " = " + day;
            db.update(DbHelper.TBL_DAILY_STAT, values, whereClause, null);
        }
    }


    public static class DbHelper extends SQLiteOpenHelper {
        private static final String TAG = "DbHelper";

        private static final int DB_VERSION = 2;
        private static final String DB_NAME = "db";

        public static final String TBL_VERSION = "version";
        public static final String TBL_UNIT = "unit";
        public static final String TBL_WORD = "word";
        public static final String TBL_TOPIC = "topic";
        public static final String TBL_WORD_TOPIC = "word_topic";
        public static final String TBL_PROGRESS = "progress";
        public static final String TBL_DAILY_STAT = "daily_stat";

        public static final String COL_DATA_VERSION = "data_version";

        public static final String COL_UNIT_ID = "unit_id";
        public static final String COL_UNIT_NAME = "unit_name";

        public static final String COL_WORD_ID = "id";
        public static final String COL_WORD_UNIT_ID = "unit_id";
        public static final String COL_WORD_EN = "en";
        public static final String COL_WORD_RU = "ru";
        public static final String COL_WORD_TSCP = "tscp";
        public static final String COL_WORD_CLASS = "class";
        public static final String COL_WORD_EXAMPLE = "example";
        public static final String COL_WORD_SIBLINGS = "siblings";
        public static final String COL_WORD_EXAMPLE_ANSWER = "example_answer";

        public static final String COL_TOPIC_ID = "topic_id";
        public static final String COL_TOPIC_NAME_EN = "topic_name_en";
        public static final String COL_TOPIC_NAME_RU = "topic_name_ru";

        public static final String COL_PROGRESS_UNIT_ID = "unit_id";
        public static final String COL_PROGRESS_TRAINING_ID = "training_id";
        public static final String COL_PROGRESS_COMPLETED = "completed";

        public static final String COL_DAILY_STAT_YEAR = "year";
        public static final String COL_DAILY_STAT_MONTH = "month";
        public static final String COL_DAILY_STAT_DAY = "day";
        public static final String COL_DAILY_STAT_TRAIN_CNT = "train_cnt";
        public static final String COL_DAILY_STAT_DURATION = "duration";


        public static final String IDX_WORD_UNIT_ID = "idx_word_unit_id";

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "onCreate");

            db.execSQL("CREATE TABLE " + TBL_VERSION + " (" +
                    COL_DATA_VERSION + " TEXT PRIMARY KEY);"
            );

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_UNIT + " (" +
                    COL_UNIT_ID + " INTEGER PRIMARY KEY, " +
                    COL_UNIT_NAME + " TEXT);"
            );

            db.execSQL("CREATE TABLE " + TBL_WORD + " (" +
                    COL_WORD_ID + " INTEGER PRIMARY KEY, " +
                    COL_WORD_UNIT_ID + " INTEGER, " +
                    COL_WORD_EN + " TEXT, " +
                    COL_WORD_RU + " TEXT, " +
                    COL_WORD_TSCP + " TEXT, " +
                    COL_WORD_CLASS + " INTEGER, " +
                    COL_WORD_EXAMPLE + " TEXT, " +
                    COL_WORD_SIBLINGS + " TEXT, " +
                    COL_WORD_EXAMPLE_ANSWER + " TEXT, " +
                    "FOREIGN KEY(" + COL_WORD_UNIT_ID + ") REFERENCES " + TBL_UNIT + "(" + COL_UNIT_ID + ")" +
                    ");"
            );

            db.execSQL("CREATE TABLE " + TBL_TOPIC + " (" +
                    COL_TOPIC_ID + " INTEGER PRIMARY KEY, " +
                    COL_TOPIC_NAME_EN + " TEXT, " +
                    COL_TOPIC_NAME_RU + " TEXT" +
                    ");"
            );

            db.execSQL("CREATE TABLE " + TBL_WORD_TOPIC + " (" +
                    COL_WORD_ID + " INTEGER, " +
                    COL_TOPIC_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COL_WORD_ID + ") REFERENCES " + TBL_WORD + "(" + COL_WORD_ID + ")," +
                    "FOREIGN KEY(" + COL_TOPIC_ID + ") REFERENCES " + TBL_TOPIC + "(" + COL_TOPIC_ID + ")" +
                    ");"
            );

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_PROGRESS + " (" +
                    COL_PROGRESS_UNIT_ID + " INTEGER, " +
                    COL_PROGRESS_TRAINING_ID + " INTEGER, " +
                    COL_PROGRESS_COMPLETED + " INTEGER, " +
                    "FOREIGN KEY(" + COL_PROGRESS_UNIT_ID + ") REFERENCES " + TBL_UNIT + "(" + COL_UNIT_ID + ")" +
                    ");"
            );

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_DAILY_STAT + " (" +
                    COL_DAILY_STAT_YEAR + " INTEGER, " +
                    COL_DAILY_STAT_MONTH + " INTEGER, " +
                    COL_DAILY_STAT_DAY + " INTEGER, " +
                    COL_DAILY_STAT_TRAIN_CNT + " INTEGER, " +
                    COL_DAILY_STAT_DURATION + " INTEGER, " +
                    "PRIMARY KEY(" + COL_DAILY_STAT_YEAR + ", " + COL_DAILY_STAT_MONTH + ", " + COL_DAILY_STAT_DAY + ")" +
                    ");"
            );

            db.execSQL("CREATE INDEX " + IDX_WORD_UNIT_ID + " ON " + TBL_WORD + " (" + COL_WORD_UNIT_ID + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "onUpgrade");
            dropTables(db);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "onDowngrade");
            dropTables(db);
            onCreate(db);
        }


        private void dropTables(SQLiteDatabase db) {
            db.execSQL("DROP INDEX IF EXISTS " + IDX_WORD_UNIT_ID);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_WORD_TOPIC);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_WORD);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_TOPIC);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_UNIT);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_VERSION);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_DAILY_STAT);
        }

        private void dropWords(SQLiteDatabase db) {
            db.execSQL("DROP INDEX IF EXISTS " + IDX_WORD_UNIT_ID);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_WORD_TOPIC);
            db.execSQL("DROP TABLE IF EXISTS " + TBL_WORD);
        }

        private void dropTopics(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TBL_TOPIC);
        }

        private void dropVersion(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TBL_VERSION);
        }

        private void recreateTables(SQLiteDatabase db) {
            onCreate(db);
        }
    }

    private static String join(Iterable<?> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object s : list) {
            if (!first) {
                sb.append(delimiter);
            }
            sb.append(s.toString());
            first = false;
        }
        return sb.toString();
    }
}