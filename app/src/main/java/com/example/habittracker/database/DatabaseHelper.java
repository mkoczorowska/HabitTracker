package com.example.habittracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "habittracker.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_HABITS = "habits";
    public static final String TABLE_COMPLETIONS = "completions";

    public static final String COL_ID = "id";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_DAYS = "days";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_STREAK = "streak";
    public static final String COL_HABIT_ID = "habit_id";
    public static final String COL_DATE = "date";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_HABITS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DAYS + " TEXT NOT NULL, " +
                COL_CREATED_AT + " TEXT NOT NULL, " +
                COL_STREAK + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_COMPLETIONS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HABIT_ID + " INTEGER NOT NULL, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_DATE + " TEXT NOT NULL, " +
                "UNIQUE(" + COL_HABIT_ID + ", " + COL_DATE + "), " +
                "FOREIGN KEY(" + COL_HABIT_ID + ") REFERENCES " + TABLE_HABITS + "(" + COL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLETIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}