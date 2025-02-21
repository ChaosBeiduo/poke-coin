package com.chaos.pokecoin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaos.pokecoin.adapter.ProductAdapter;
import com.chaos.pokecoin.model.Product;
import com.chaos.pokecoin.data.UserManager;
import com.chaos.pokecoin.data.CartManager;
import com.chaos.pokecoin.data.ProductManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonCart;
    private ImageButton buttonHome;
    private ImageButton buttonUser;
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private ActivityResultLauncher<Intent> loginLauncher;
    private CartManager cartManager;
    private ProductManager productManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cartManager = new CartManager(this);
        productManager = new ProductManager(this);

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        updateUserUI();
                    }
                }
        );

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadProducts();
    }

    private void initViews() {
        buttonCart = findViewById(R.id.buttonCart);
        buttonHome = findViewById(R.id.buttonHome);
        buttonUser = findViewById(R.id.buttonUser);

        // 设置首页按钮为蓝色
        buttonHome.setColorFilter(ContextCompat.getColor(this, R.color.blue));
        TextView textHome = findViewById(R.id.textHome);
        textHome.setTextColor(ContextCompat.getColor(this, R.color.blue));
    }

    private void setupRecyclerView() {
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        
        productAdapter = new ProductAdapter(this, new ArrayList<>(), new ProductAdapter.OnProductActionListener() {
            @Override
            public void onProductClick(Product product) {
                showProductDetail(product);
            }

            @Override
            public void onAddToCartClick(Product product) {
                addToCart(product);
            }
        });
        
        productRecyclerView.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        buttonCart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        buttonHome.setOnClickListener(v -> {
            // 已经在首页，无需操作
        });

        buttonUser.setOnClickListener(v -> {
            if (!UserManager.getInstance(this).isLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                loginLauncher.launch(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {
        List<Product> products = new ArrayList<>();
        Cursor cursor = productManager.getAllProducts();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                
                Product product = new Product(id, name, price, description);
                products.add(product);
            } while (cursor.moveToNext());
            
            cursor.close();
        }
        
        productAdapter.setProducts(products);
    }

    private void addToCart(Product product) {
        UserManager userManager = UserManager.getInstance(this);
        if (!userManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            loginLauncher.launch(intent);
            return;
        }

        // 直接添加一个数量的商品到购物车
        cartManager.addToCart(userManager.getCurrentUserId(), product.getId(), 1, (float)product.getPrice());
        Toast.makeText(this, "已添加到购物车", Toast.LENGTH_SHORT).show();
    }

    private void showProductDetail(Product product) {
//        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
//        intent.putExtra("product_id", product.getId());
//        startActivity(intent);
    }

    private void updateUserUI() {
        // 更新用户相关UI
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartManager != null) {
            cartManager.close();
        }
        if (productManager != null) {
            productManager.close();
        }
    }
}
