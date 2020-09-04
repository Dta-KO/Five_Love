package com.d.fivelove.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.d.fivelove.databinding.ActivitySendOTPBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {
    public static final String VERIFY_FAIL = "Xác nhận không thành công! Vui lòng kiểm tra lại số điện và thử lại sau";
    private ActivitySendOTPBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set enable btn send
        binding.inputNumberPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    binding.btnSendPhoneNumber.setEnabled(true);
                } else {
                    binding.btnSendPhoneNumber.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //set on click btn send
        binding.btnSendPhoneNumber.setOnClickListener(view -> {
            binding.btnSendPhoneNumber.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            PhoneAuthProvider.getInstance()
                    .verifyPhoneNumber("+84" + binding.inputNumberPhone.getText().toString().trim(), 60, TimeUnit.SECONDS, SendOTPActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    binding.btnSendPhoneNumber.setVisibility(View.VISIBLE);
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    binding.btnSendPhoneNumber.setVisibility(View.VISIBLE);
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), VERIFY_FAIL, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    if (verificationId.length() != 0) {
                                        Intent intent = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
                                        intent.putExtra("phoneNumber", binding.inputNumberPhone.getText().toString().trim());
                                        intent.putExtra("verificationId", verificationId);
                                        startActivity(intent);
                                    }
                                }
                            });

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.btnSendPhoneNumber.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        if (!binding.inputNumberPhone.getText().toString().isEmpty()) {
            binding.btnSendPhoneNumber.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.btnSendPhoneNumber.setEnabled(false);
    }


}