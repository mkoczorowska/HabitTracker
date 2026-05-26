package com.example.habittracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.habittracker.models.User;

public class UserDao {
    private final DatabaseHelper dbHelper;

    public UserDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EMAIL, email.trim().toLowerCase());
        values.put(DatabaseHelper.COL_PASSWORD, password);
        try {
            return db.insertOrThrow(DatabaseHelper.TABLE_USERS, null, values);
        } catch (Exception e) {
            return -1; // email already exists
        }
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COL_EMAIL + "=?",
                new String[]{email.trim().toLowerCase()},
                null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD)));
        }
        cursor.close();
        return user;
    }

    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }
}