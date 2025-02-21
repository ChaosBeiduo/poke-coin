package com.chaos.pokecoin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaos.pokecoin.adapter.CartAdapter;
import com.chaos.pokecoin.data.CartManager;
import com.chaos.pokecoin.data.UserManager;
import com.chaos.pokecoin.model.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private TextView totalPriceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = new CartManager(this);
        initViews();
        loadCartItems();
        setupNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        totalPriceText = findViewById(R.id.total_price);
        
        cartAdapter = new CartAdapter(this, null, new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                if (newQuantity >= 1) {
                    updateCartItemQuantity(item, newQuantity);
                }
            }
        });
        
        recyclerView.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        UserManager userManager = UserManager.getInstance(this);
        if (!userManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<CartItem> cartItems = cartManager.getCartItems(userManager.getCurrentUserId());
        cartAdapter.setCartItems(cartItems);
        updateTotalPrice(cartItems);
    }

    private void updateCartItemQuantity(CartItem item, int newQuantity) {
        cartManager.updateQuantity(item.getId(), newQuantity);
        loadCartItems(); // 重新加载购物车以更新总价
    }

    private void updateTotalPrice(List<CartItem> cartItems) {
        float total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.format("总计: ￥%.2f", total));
    }

    private void setupNavigation() {
        ImageButton homeButton = findViewById(R.id.buttonHome);
        homeButton.setOnClickListener(v -> {
            finish();
        });
        
        ImageButton userButton = findViewById(R.id.buttonUser);
        userButton.setOnClickListener(v -> {
            if (!UserManager.getInstance(this).isLoggedIn()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }
} 