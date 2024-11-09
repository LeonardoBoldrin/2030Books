package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.a2030books.databinding.ActivityMainBinding;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        
        auth = FirebaseAuth.getInstance();
        
        fieldEmail = findViewById(R.id.fieldEmail);
        fieldPassword = findViewById(R.id.fieldPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        
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

                registerUser(contentEmail, contentPassword);
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

    private void registerUser(String contentEmail, String contentPassword) {
        if (TextUtils.isEmpty(contentEmail) || TextUtils.isEmpty(contentPassword)) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contentPassword.length() < 6) {
            Toast.makeText(MainActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // This function also logs in the user
        auth.createUserWithEmailAndPassword(contentEmail, contentPassword)
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        //FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // Redirect to dashboard
                        redirectToDashboard();
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // Email is already in use
                        Toast.makeText(MainActivity.this, "This email is already registered. Please sign in.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToDashboard(){
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
    }


}