package com.example.a2030books;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import com.example.a2030books.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        
        auth = FirebaseAuth.getInstance();

        // Check if the user has as session in Firebase
        if (auth.getCurrentUser() != null) {
            // User is signed in, navigate to the main app screen
            startActivity(new Intent(this, DashboardActivity.class));
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentEmail = binding.usernameInput.getText().toString();
                String contentPassword = binding.passwordInput.getText().toString();
                
                loginUser(contentEmail, contentPassword);
            }
        });


        binding.createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loginUser(String contentEmail, String contentPassword) {
        if (TextUtils.isEmpty(contentEmail) || TextUtils.isEmpty(contentPassword)) {
            Toast.makeText(LoginActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(contentEmail, contentPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        Toast.makeText(LoginActivity.this, "Benvenut*!", Toast.LENGTH_SHORT).show();
                        // Redirect to the dashboard
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        // Sign-in failed; handle specific cases
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            // User account with the email does not exist
                            Toast.makeText(LoginActivity.this, "Email non esistente", Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // Password is incorrect
                            Toast.makeText(LoginActivity.this, "Password non corretta. Riprova", Toast.LENGTH_LONG).show();
                        } else {
                            // Other errors
                            Toast.makeText(LoginActivity.this, "Autenticazione fallita: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }


}