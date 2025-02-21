package com.chaos.pokecoin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chaos.pokecoin.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public CartManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public void addToCart(int userId, int productId, int quantity, float price) {
        // 先检查购物车是否已经有该商品
        Cursor cursor = db.query("cart", 
            new String[]{"id", "quantity"}, 
            "user_id = ? AND product_id = ?", 
            new String[]{String.valueOf(userId), String.valueOf(productId)}, 
            null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // 如果已存在，更新数量
            int existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            int cartItemId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            
            ContentValues values = new ContentValues();
            values.put("quantity", existingQuantity + quantity);
            
            db.update("cart", values, "id = ?", new String[]{String.valueOf(cartItemId)});
            cursor.close();
        } else {
            // 如果不存在，新增记录
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("product_id", productId);
            values.put("quantity", quantity);
            values.put("price", price);

            db.insert("cart", null, values);
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        
        Cursor cursor = db.rawQuery(
            "SELECT c.id, c.user_id, c.product_id, c.quantity, c.price, p.name, p.description " +
            "FROM cart c " +
            "JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = ?",
            new String[]{String.valueOf(userId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("price")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
                );
                cartItems.add(item);
            } while (cursor.moveToNext());
            
            cursor.close();
        }

        return cartItems;
    }

    public void updateCartItemQuantity(int cartItemId, int quantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        
        db.update("cart", values, "id = ?", new String[]{String.valueOf(cartItemId)});
    }

    public void removeFromCart(int cartItemId) {
        db.delete("cart", "id = ?", new String[]{String.valueOf(cartItemId)});
    }

    public void clearCart(int userId) {
        db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void updateQuantity(int cartItemId, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        
        db.update("cart", 
                 values, 
                 "id = ?", 
                 new String[]{String.valueOf(cartItemId)});
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