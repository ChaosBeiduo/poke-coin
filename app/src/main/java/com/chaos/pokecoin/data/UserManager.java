package com.chaos.pokecoin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserManager {
    private DatabaseHelper dbHelper;
    private static UserManager instance;
    private Integer currentUserId = null;

    private UserManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            currentUserId = cursor.getInt(0);
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean register(String username, String password, String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("email", email);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public void logout() {
        currentUserId = null;
    }

    public boolean isLoggedIn() {
        return currentUserId != null;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }
} 