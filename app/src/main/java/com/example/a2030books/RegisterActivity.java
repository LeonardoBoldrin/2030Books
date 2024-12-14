package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    private String nickname;
    private String email;
    private String age;
    private String selectedDay;
    private String selectedHour;
    private Spinner srDay;
    private Spinner srHour;
    private String pwd;
    private FirebaseDatabase db;
    private DatabaseReference userRef;

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        srDay = binding.srDayWeekREG;
        srHour = binding.srHourREG;

        srDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Handle the selection of a valid genre
                srDay.setSelection(position);

                selectedDay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                srDay.setSelection(0);
            }
        });

        srHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Handle the selection of a valid genre
                srHour.setSelection(position);

                selectedHour = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                srHour.setSelection(0);
            }
        });


        binding.btnNewAccountREG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = binding.etEmailREG.getText().toString();
                nickname = binding.etNicknameREG.getText().toString();
                age = binding.etAgeREG.getText().toString();
                pwd = binding.etPasswordREG.getText().toString();

                if(selectedDay.equals(srDay.getItemAtPosition(0))){
                    Toast.makeText(RegisterActivity.this, "Seleziona un giorno", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(selectedHour.equals(srHour.getItemAtPosition(0))){
                    Toast.makeText(RegisterActivity.this, "Seleziona un'ora", Toast.LENGTH_SHORT).show();
                    return;
                }

                data = new String[]{email, pwd, nickname, age, selectedDay, selectedHour};

                for(String el : data){

                    if(el == null) {
                        Toast.makeText(RegisterActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                registerUser();
            }
        });
    }

    private void registerUser() {

        // This function also logs in the user
        auth.createUserWithEmailAndPassword(data[0], data[1])
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registrat* correttamente!", Toast.LENGTH_SHORT).show();
                        createInfoNode(data);
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "Email già in uso", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(RegisterActivity.this, "Formato email non valido", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registrazione fallita: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createInfoNode(String[] data) {
        userRef = db.getReference("Users");

        HashMap<String, Object> infoHash = new HashMap<>();
        infoHash.put("Day", selectedDay);
        infoHash.put("Hour", selectedHour);
        infoHash.put("Email", email);
        infoHash.put("Nickname", nickname);
        // infoHash.put("Latitude", latitude);
        // infoHash.put("Longitude", longitude);

        userRef.child(auth.getUid())
                .child("Info").setValue(infoHash);
    }
}