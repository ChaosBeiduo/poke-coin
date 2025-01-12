package com.chaos.pokecoin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.chaos.pokecoin.data.UserManager;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textUsername;
    private TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
        loadUserInfo();
    }

    private void initViews() {
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
    }

    private void loadUserInfo() {
        UserManager userManager = UserManager.getInstance(this);
        if (userManager.isLoggedIn()) {
            UserManager.UserInfo userInfo = userManager.getCurrentUserInfo();
            if (userInfo != null) {
                textUsername.setText(userInfo.getUsername());
                textEmail.setText(userInfo.getEmail());
            }
        }
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onLogoutClicked(View view) {
        UserManager.getInstance(this).logout();
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
} 