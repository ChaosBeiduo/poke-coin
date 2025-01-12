package com.chaos.pokecoin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chaos.pokecoin.model.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionManager {
    private DatabaseHelper dbHelper;
    private static TransactionManager instance;

    private TransactionManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized TransactionManager getInstance(Context context) {
        if (instance == null) {
            instance = new TransactionManager(context.getApplicationContext());
        }
        return instance;
    }

    public long addTransaction(Transaction transaction, Integer userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", transaction.getType());
        values.put("amount", transaction.getAmount());
        values.put("date", transaction.getDate().getTime());
        values.put("user_id", userId);

        return db.insert("transactions", null, values);
    }

    public List<Transaction> getTransactions(Integer userId) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = userId == null ? "user_id IS NULL" : "user_id = ?";
        String[] selectionArgs = userId == null ? null : new String[]{String.valueOf(userId)};
        
        Cursor cursor = db.query(
            "transactions",
            null,
            selection,
            selectionArgs,
            null,
            null,
            "date DESC"
        );

        while (cursor.moveToNext()) {
            transactions.add(new Transaction(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date"))),
                cursor.isNull(cursor.getColumnIndexOrThrow("user_id")) ? 
                    null : cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            ));
        }
        cursor.close();
        return transactions;
    }
} 