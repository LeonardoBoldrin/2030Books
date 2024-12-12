package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
    // private String day;
    // private String hour;
    private String tel;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        email = binding.etEmail.getText().toString();
        nickname = binding.etNicknameREG.getText().toString();
        age = binding.etAgeREG.getText().toString();
        tel = binding.etPhoneREG.getText().toString();
        pwd = binding.etPassword.getText().toString();

        String[] data = new String[]{email, pwd, nickname, age, place /*, day, hour*/, tel};

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnNewAccountREG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(data);
            }
        });
    }

    private void registerUser(String[] data) {

        for(String el : data){
            if(el.isEmpty())
                Toast.makeText(RegisterActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
        }

        // This function also logs in the user
        auth.createUserWithEmailAndPassword(data[1], data[2])
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registrazione effettuata", Toast.LENGTH_SHORT).show();
                        // creates the "Info" node in the db
                        // createInfoNode(data)
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // Email is already in use
                        Toast.makeText(RegisterActivity.this, "Email già esistente", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registrazione fallita: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}