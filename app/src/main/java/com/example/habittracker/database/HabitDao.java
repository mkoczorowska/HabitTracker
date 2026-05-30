package com.example.habittracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.habittracker.models.Habit;
import java.util.ArrayList;
import java.util.List;

public class HabitDao {
    private final DatabaseHelper dbHelper;

    public HabitDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertHabit(Habit habit) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_ID, habit.getUserId());
        values.put(DatabaseHelper.COL_TITLE, habit.getTitle());
        values.put(DatabaseHelper.COL_DESCRIPTION, habit.getDescription());
        values.put(DatabaseHelper.COL_DAYS, habit.getDays());
        values.put(DatabaseHelper.COL_CREATED_AT, habit.getCreatedAt());
        values.put(DatabaseHelper.COL_STREAK, 0);
        return db.insert(DatabaseHelper.TABLE_HABITS, null, values);
    }

    public List<Habit> getHabitsForUserAndDay(int userId, int dayOfWeek) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Habit> habits = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_HABITS, null,
                DatabaseHelper.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, DatabaseHelper.COL_TITLE + " ASC");

        while (cursor.moveToNext()) {
            String days = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DAYS));
            if (containsDay(days, dayOfWeek)) {
                Habit habit = cursorToHabit(cursor);
                habits.add(habit);
            }
        }
        cursor.close();
        return habits;
    }

    public List<Habit> getAllHabitsForUser(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Habit> habits = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_HABITS, null,
                DatabaseHelper.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, DatabaseHelper.COL_STREAK + " DESC");

        while (cursor.moveToNext()) {
            habits.add(cursorToHabit(cursor));
        }
        cursor.close();
        return habits;
    }

    public Habit getHabitById(int habitId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_HABITS, null,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(habitId)},
                null, null, null);
        Habit habit = null;
        if (cursor.moveToFirst()) {
            habit = cursorToHabit(cursor);
        }
        cursor.close();
        return habit;
    }

    public void deleteHabit(int habitId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_COMPLETIONS,
                DatabaseHelper.COL_HABIT_ID + "=?",
                new String[]{String.valueOf(habitId)});
        db.delete(DatabaseHelper.TABLE_HABITS,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(habitId)});
    }

    public boolean isCompletedOnDate(int habitId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_COMPLETIONS, null,
                DatabaseHelper.COL_HABIT_ID + "=? AND " + DatabaseHelper.COL_DATE + "=?",
                new String[]{String.valueOf(habitId), date},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void toggleCompletion(int habitId, int userId, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (isCompletedOnDate(habitId, date)) {
            db.delete(DatabaseHelper.TABLE_COMPLETIONS,
                    DatabaseHelper.COL_HABIT_ID + "=? AND " + DatabaseHelper.COL_DATE + "=?",
                    new String[]{String.valueOf(habitId), date});
        } else {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_HABIT_ID, habitId);
            values.put(DatabaseHelper.COL_USER_ID, userId);
            values.put(DatabaseHelper.COL_DATE, date);
            db.insert(DatabaseHelper.TABLE_COMPLETIONS, null, values);
        }
        recalculateStreak(habitId);
    }

    private void recalculateStreak(int habitId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_COMPLETIONS,
                new String[]{DatabaseHelper.COL_DATE},
                DatabaseHelper.COL_HABIT_ID + "=?",
                new String[]{String.valueOf(habitId)},
                null, null, DatabaseHelper.COL_DATE + " DESC");

        int streak = 0;
        if (cursor.moveToFirst()) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            String today = sdf.format(cal.getTime());
            String firstDate = cursor.getString(0);
            if (firstDate.equals(today)) {
                streak = 1;
                String prevDate = today;
                while (cursor.moveToNext()) {
                    String d = cursor.getString(0);

                    try {
                        java.util.Date prev = sdf.parse(prevDate);
                        java.util.Date curr = sdf.parse(d);
                        long diff = prev.getTime() - curr.getTime();
                        long days = diff / (1000 * 60 * 60 * 24);
                        if (days == 1) {
                            streak++;
                            prevDate = d;
                        } else break;
                    } catch (Exception e) { break; }
                }
            }
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_STREAK, streak);
        db.update(DatabaseHelper.TABLE_HABITS, values,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(habitId)});
    }

    private Habit cursorToHabit(Cursor cursor) {
        Habit habit = new Habit();
        habit.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        habit.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)));
        habit.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)));
        habit.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)));
        habit.setDays(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DAYS)));
        habit.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT)));
        habit.setStreak(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STREAK)));
        return habit;
    }

    private boolean containsDay(String days, int day) {
        if (days == null || days.isEmpty()) return false;
        for (String d : days.split(",")) {
            try {
                if (Integer.parseInt(d.trim()) == day) return true;
            } catch (NumberFormatException ignored) {}
        }
        return false;
    }
}