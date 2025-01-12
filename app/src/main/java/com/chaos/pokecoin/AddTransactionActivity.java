package com.chaos.pokecoin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class AddTransactionActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private EditText editAmount;
    private Button buttonSaveTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initViews();
        setupClickListeners();
        setupTabLayout();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        editAmount = findViewById(R.id.editAmount);
        buttonSaveTransaction = findViewById(R.id.buttonSaveTransaction);
    }

    private void setupClickListeners() {
        buttonSaveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 根据选择的标签更新UI
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void onCloseClicked(View view) {
        finish();
    }

    private void saveTransaction() {
        String amountStr = editAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            boolean isExpense = tabLayout.getSelectedTabPosition() == 0;

            // 准备返回给 MainActivity 的数据
            Intent resultIntent = new Intent();
            resultIntent.putExtra("type", isExpense ? "支出" : "收入");
            resultIntent.putExtra("amount", amount);

            // 设置结果并关闭 Activity
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效金额", Toast.LENGTH_SHORT).show();
        }
    }
}
