package com.chaos.pokecoin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaos.pokecoin.adapter.TransactionAdapter;
import com.chaos.pokecoin.model.Transaction;
import com.chaos.pokecoin.data.UserManager;
import com.chaos.pokecoin.data.TransactionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textTotalIncome;
    private TextView textTotalExpense;
    private ImageButton buttonAddTransaction;
    private ImageButton buttonHome;
    private ImageButton buttonUser;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private double totalIncome = 0;
    private double totalExpense = 0;

    private ActivityResultLauncher<Intent> addTransactionLauncher;
    private ActivityResultLauncher<Intent> loginLauncher;

    private TransactionManager transactionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transactionManager = TransactionManager.getInstance(this);

        // 初始化 ActivityResultLauncher
        addTransactionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String type = data.getStringExtra("type");
                        double amount = data.getDoubleExtra("amount", 0);

                        Transaction transaction = new Transaction(type, amount);
                        // 保存到数据库
                        Integer userId = UserManager.getInstance(this).getCurrentUserId();
                        long id = transactionManager.addTransaction(transaction, userId);
                        
                        if (id != -1) {
                            transactionAdapter.addTransaction(transaction);
                            // 更新总额
                            if (type.equals("收入")) {
                                totalIncome += amount;
                            } else {
                                totalExpense += amount;
                            }
                            updateTotalAmounts();
                        }
                    }
                }
        );

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // 登录成功后的处理
                        updateUserUI();
                    }
                }
        );

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadTransactions();
        updateTotalAmounts();
    }

    private void initViews() {
        textTotalIncome = findViewById(R.id.textTotalIncome);
        textTotalExpense = findViewById(R.id.textTotalExpense);
        buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
        buttonHome = findViewById(R.id.buttonHome);
        buttonUser = findViewById(R.id.buttonUser);

        // 设置首页按钮为蓝色
        buttonHome.setColorFilter(ContextCompat.getColor(this, R.color.blue));
        TextView textHome = findViewById(R.id.textHome);
        textHome.setTextColor(ContextCompat.getColor(this, R.color.blue));
    }

    private void setupRecyclerView() {
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter();
        transactionRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupClickListeners() {
        buttonAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            addTransactionLauncher.launch(intent);
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
                loginLauncher.launch(intent);
            }
        });
    }

    private void updateTotalAmounts() {
        textTotalIncome.setText(String.format("总收入: ￥%.2f", totalIncome));
        textTotalExpense.setText(String.format("总支出: ￥%.2f", totalExpense));
    }

    private void updateUserUI() {
        UserManager userManager = UserManager.getInstance(this);
        // 同步未登录时的交易记录
        userManager.syncGuestTransactions();
        
        // 清空当前数据
        totalIncome = 0;
        totalExpense = 0;
        
        // 加载用户相关的交易记录
        loadTransactions();
    }

    private void loadTransactions() {
        List<Transaction> transactions = transactionManager.getTransactions(
            UserManager.getInstance(this).getCurrentUserId()
        );
        
        // 清空现有数据
        transactionAdapter.clearTransactions();
        
        // 添加所有交易记录并计算总额
        for (Transaction transaction : transactions) {
            transactionAdapter.addTransaction(transaction);
            if (transaction.getType().equals("收入")) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }
        
        // 更新总额显示
        updateTotalAmounts();
    }
}
