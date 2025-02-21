package com.chaos.pokecoin.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pokeMall.db";
    private static final int DATABASE_VERSION = 4;

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

        // 创建商品表
        db.execSQL(
                "CREATE TABLE product (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "price REAL NOT NULL," +
                        "description TEXT NOT NULL)"
        );

        // 插入初始数据
        db.execSQL(
                "INSERT INTO product (name, price, description) VALUES " +
                        "('太平洋橘汁汽水 330mL*6罐', 28.90, '红桔榨汁，冰镇更够味。')," +
                        "('旧希望可可牛奶 200g/袋', 9.90, '生牛乳含量>80%')," +
                        "('有豆志内酯豆腐 400g/份', 2.58, '精选苏北一级大豆')," +
                        "('美式橙香坚果磅蛋糕 260g/盒', 15.90, '好吃')," +
                        "('暴风港虾饺(6只装) 150g', 18.50, '在家享受暴风港风味')"
        );

        // 创建购物车表
        db.execSQL(
                "CREATE TABLE cart (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "product_id INTEGER," +
                        "quantity INTEGER NOT NULL," +
                        "price REAL NOT NULL," +
                        "FOREIGN KEY(user_id) REFERENCES users(id)," +
                        "FOREIGN KEY(product_id) REFERENCES product(id))"
        );

        // 插入测试用户
        db.execSQL(
                "INSERT INTO users (username, password, email) " +
                        "VALUES ('testuser', 'password123', 'testuser@example.com')"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS product");
        db.execSQL("DROP TABLE IF EXISTS cart");

        onCreate(db);
    }
} 