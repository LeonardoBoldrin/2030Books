package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.a2030books.R;
import com.example.a2030books.databinding.ActivityLoginBinding;
import com.example.a2030books.databinding.LoginBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLogin;
    private EditText fieldEmail;
    private EditText fieldPassword;

    private FirebaseAuth auth;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        
        auth = FirebaseAuth.getInstance();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentEmail = fieldEmail.getText().toString();
                String contentPassword = fieldPassword.getText().toString();
                
                loginUser(contentEmail, contentPassword);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentEmail = fieldEmail.getText().toString();
                String contentPassword = fieldPassword.getText().toString();
            }
        });

    }

    private void loginUser(String contentEmail, String contentPassword) {
        if (TextUtils.isEmpty(contentEmail) || TextUtils.isEmpty(contentPassword)) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(contentEmail, contentPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Redirect to the dashboard
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        // Sign-in failed; handle specific cases
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            // User account with the email does not exist
                            Toast.makeText(MainActivity.this, "This email is not registered. Please sign up or use a different email.", Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // Password is incorrect
                            Toast.makeText(MainActivity.this, "Invalid password. Please try again.", Toast.LENGTH_LONG).show();
                        } else {
                            // Other errors
                            Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}