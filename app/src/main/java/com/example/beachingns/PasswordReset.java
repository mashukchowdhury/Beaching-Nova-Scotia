package com.example.beachingns;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PasswordReset extends AppCompatActivity {
    private Button confirmBtn;
    private ImageButton backBtn;
    private EditText enterEmail;
    private FirebaseAuth beachingNSAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        beachingNSAuth = FirebaseAuth.getInstance();

        confirmBtn = findViewById(R.id.continueButton);
        enterEmail = findViewById(R.id.emailEditText);
        backBtn = findViewById(R.id.backArrow);

        setupListeners();
    }

    private void setupListeners() {
        confirmBtn.setOnClickListener(view -> attemptPasswordReset());
        backBtn.setOnClickListener(v -> startActivity(new Intent(PasswordReset.this, Login.class)));
    }

    private void attemptPasswordReset() {
        String emailAddress = enterEmail.getText().toString().trim();

        if (isEmailValid(emailAddress)) {
            sendPasswordResetEmail(emailAddress);
        }
    }

    private boolean isEmailValid(String emailAddress) {
        if (TextUtils.isEmpty(emailAddress)) {
            enterEmail.setError("Please Enter an Email!");
            return false;
        } else if (!emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]{2,3}")) {
            enterEmail.setError("Please Enter a valid Email!");
            return false;
        }
        return true;
    }

    private void sendPasswordResetEmail(String emailAddress) {
        beachingNSAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PasswordReset.this, "Password reset link has been sent to your email", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PasswordReset.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
