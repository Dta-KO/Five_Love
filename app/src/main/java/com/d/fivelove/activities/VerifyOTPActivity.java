package com.d.fivelove.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.d.fivelove.databinding.ActivityVerifyOTPBinding;
import com.d.fivelove.model.User;
import com.d.fivelove.received.SmsReceiver;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity implements SmsReceiver.OTPListening {
    private static final String FAIL_LISTENER = "Xác thực không thành công! Vui lòng kiểm tra lại mã OTP.";
    private static final String REQUIRE_VALID_CODE = "Vui lòng nhập đúng mã OTP!";
    private ActivityVerifyOTPBinding binding;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private Button btnVerify;
    private ProgressBar progressBar;
    private TextView phoneNumber, resendOTP;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SmsReceiver.listening = this;

        verificationId = getIntent().getStringExtra("verificationId");

        init();
        setupOTPInput();
        setupPhoneNumberTxt();
        setupBtnVerify();
        setupResendOTP();
        requestSMSPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnVerify.setEnabled(false);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    private void requestSMSPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[1];
            permissions[0] = permission;
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void init() {
        inputCode1 = binding.inputCode1;
        inputCode2 = binding.inputCode2;
        inputCode3 = binding.inputCode3;
        inputCode4 = binding.inputCode4;
        inputCode5 = binding.inputCode5;
        inputCode6 = binding.inputCode6;
        btnVerify = binding.btnVerify;
        phoneNumber = binding.phoneNumber;
        progressBar = binding.progressBar;
        resendOTP = binding.resendOTP;
    }

    private void setupPhoneNumberTxt() {
        phoneNumber.setText(String.format("+84-%s", getIntent().getStringExtra("phoneNumber")));
    }

    private void setupOTPInput() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    inputCode2.requestFocus();
                } else {
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    inputCode3.requestFocus();
                } else {
                    inputCode1.requestFocus();
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    inputCode4.requestFocus();
                } else {
                    inputCode2.requestFocus();
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    inputCode5.requestFocus();
                } else {
                    inputCode3.requestFocus();
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    inputCode6.requestFocus();
                } else {
                    inputCode4.requestFocus();
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0 && verificationId != null) {

                    btnVerify.requestFocus();
                    btnVerify.setEnabled(true);
                } else {
                    inputCode5.requestFocus();
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setupBtnVerify() {
        btnVerify.setOnClickListener(view -> {
            btnVerify.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (inputCode1.getText().toString().trim().isEmpty()
                    || inputCode2.getText().toString().trim().isEmpty()
                    || inputCode3.getText().toString().trim().isEmpty()
                    || inputCode4.getText().toString().trim().isEmpty()
                    || inputCode5.getText().toString().trim().isEmpty()
                    || inputCode6.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, REQUIRE_VALID_CODE, Toast.LENGTH_SHORT).show();
                return;
            }
            String codeOTP = inputCode1.getText().toString().trim() +
                    inputCode2.getText().toString().trim() +
                    inputCode3.getText().toString().trim() +
                    inputCode4.getText().toString().trim() +
                    inputCode5.getText().toString().trim() +
                    inputCode6.getText().toString().trim();
            if (verificationId != null) {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, codeOTP);
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnSuccessListener(authResult -> {
                            btnVerify.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            registerUserToDB();
                            Intent intent = new Intent(VerifyOTPActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            btnVerify.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, FAIL_LISTENER, Toast.LENGTH_SHORT).show();
                        })
                ;
            }
        });
    }

    private void setupResendOTP() {
        resendOTP.setOnClickListener(view -> {
            PhoneAuthProvider.getInstance()
                    .verifyPhoneNumber("+84" + getIntent().getStringExtra("phoneNumber"), 60, TimeUnit.SECONDS, this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    binding.btnVerify.setVisibility(View.VISIBLE);
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    binding.btnVerify.setVisibility(View.VISIBLE);
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), SendOTPActivity.VERIFY_FAIL, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    verificationId = newVerificationId;
                                    Toast.makeText(getApplicationContext(), "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show();
                                }
                            });

        });

    }

    private void registerUserToDB() {
        String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String tel = getIntent().getStringExtra("phoneNumber");
        FirebaseFirestore.getInstance().collection("users").document(id).get()
                .addOnCompleteListener(documentSnapshot -> {
                    if (!Objects.requireNonNull(documentSnapshot.getResult()).exists()) {
                        User user = new User(id, tel, "", null, null);
                        FirebaseFirestore.getInstance().collection("users").document(id).set(user)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_SHORT).show();
                                });
                    }
                });


    }


    @Override
    public void onSuccess(Intent intent) {
        if (intent.getAction().equals("otp")) {
            String otp = intent.getStringExtra("message");
            inputCode1.setText(otp.substring(0, 1));
            inputCode2.setText(otp.substring(1, 2));
            inputCode3.setText(otp.substring(2, 3));
            inputCode4.setText(otp.substring(3, 4));
            inputCode5.setText(otp.substring(4, 5));
            inputCode6.setText(otp.substring(5));
        }
    }


}