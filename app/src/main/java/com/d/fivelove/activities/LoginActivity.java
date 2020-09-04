package com.d.fivelove.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.d.fivelove.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private Button btnLoginFb, btnLoginPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setupBtnLoginPhone();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //initialize field in here
    private void init() {
        btnLoginFb = binding.btnLoginByFb;
        btnLoginPhone = binding.btnLoginByPhone;
    }

    private void setupBtnLoginPhone() {
        btnLoginPhone.setOnClickListener(view -> {
            Intent intent = new Intent(this, SendOTPActivity.class);
            startActivity(intent);
        });
    }

}