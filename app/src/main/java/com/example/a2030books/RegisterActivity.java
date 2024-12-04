package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    private String nickname;
    private String email;
    private String age;
    private String place;
    private String tel;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        email = binding.etEmail.getText().toString();
        nickname = binding.etNickname.getText().toString();
        age = binding.etAge.getText().toString();
        place = binding.etPlace.getText().toString();
        tel = binding.etPhone.getText().toString();
        pwd = binding.etPassword.getText().toString();

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void registerUser(String contentEmail, String contentPassword) {
        if (TextUtils.isEmpty(contentEmail) || TextUtils.isEmpty(contentPassword)) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contentPassword.length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // This function also logs in the user
        auth.createUserWithEmailAndPassword(contentEmail, contentPassword)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        //FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // Redirect to dashboard
                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // Email is already in use
                        Toast.makeText(RegisterActivity.this, "This email is already registered. Please sign in.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}