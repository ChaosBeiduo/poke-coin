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

    public class UserInfo {
        private String username;
        private String email;

        public UserInfo(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }

    public UserInfo getCurrentUserInfo() {
        if (!isLoggedIn()) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"username", "email"};
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(currentUserId)};
        
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            UserInfo userInfo = new UserInfo(
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getString(cursor.getColumnIndexOrThrow("email"))
            );
            cursor.close();
            return userInfo;
        }
        cursor.close();
        return null;
    }

    public void syncGuestTransactions() {
        if (!isLoggedIn()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 开启事务
        db.beginTransaction();
        try {
            // 更新所有未关联用户的交易记录，将其关联到当前用户
            ContentValues values = new ContentValues();
            values.put("user_id", currentUserId);
            
            db.update("transactions", 
                     values, 
                     "user_id IS NULL", 
                     null);
            
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
} 