package com.chaos.pokecoin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProductManager {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public ProductManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public void addProduct(String name, float price, String description) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("description", description);

        db.insert("product", null, values);
    }

    public Cursor getAllProducts() {
        return db.query("product", 
            null,  // 所有列
            null,  // 没有WHERE子句
            null,  // 没有WHERE参数
            null,  // 没有GROUP BY
            null,  // 没有HAVING
            "id ASC" // 按ID排序
        );
    }

    public Cursor getProduct(int id) {
        return db.query("product", 
            null, 
            "id = ?", 
            new String[]{String.valueOf(id)}, 
            null, 
            null, 
            null
        );
    }

    public void updateProduct(int id, String name, float price, String description) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("description", description);

        db.update("product", 
            values, 
            "id = ?", 
            new String[]{String.valueOf(id)}
        );
    }

    public void deleteProduct(int id) {
        db.delete("product", 
            "id = ?", 
            new String[]{String.valueOf(id)}
        );
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}