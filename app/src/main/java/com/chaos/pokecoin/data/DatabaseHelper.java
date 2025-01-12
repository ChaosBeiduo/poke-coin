package com.chaos.pokecoin.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pokecoin.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        db.execSQL(
            "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "email TEXT)"
        );

        // 创建交易记录表
        db.execSQL(
            "CREATE TABLE transactions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "type TEXT NOT NULL," +
            "amount REAL NOT NULL," +
            "date INTEGER NOT NULL," +
            "FOREIGN KEY(user_id) REFERENCES users(id))"
        );

        // 插入测试用户
        db.execSQL(
                "INSERT INTO users (username, password, email) " +
                        "VALUES ('testuser', 'password123', 'testuser@example.com')"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
} 