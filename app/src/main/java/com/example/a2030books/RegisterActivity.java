package com.example.a2030books;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a2030books.databinding.ActivityRegisterBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

                    if(el == null || el.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                registerUser();
            }
        });
    }

    private void registerUser() {
        auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Impostare il luogo in cui si è adesso come luogo di incontro per vendere e prestare libri?")
                                .setTitle("Posizione");

                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss(); // Makes the dialog disappear
                                checkAndRequestLocationPermission();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                createInfoNode();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
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


    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(RegisterActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Permission is already granted
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getUserLocation();
            } else {
                // Permission denied
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("La posizione è importante perchè ci serve per trovare i libri vicini a te, " +
                                   "ricordati che puoi sempre attivare i permessi di posizione da App->2030Books->Autorizzazioni ")
                        .setTitle("Posizione");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // Makes the dialog disappear
                    }
                });

                builder.create().show();
            }
        }
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = location;
                            createInfoNode(); // Call createInfoNode after location is retrieved
                        } else {
                            requestLocationUpdates(); // Trigger location updates if the last location is unavailable
                        }
                    });

        } else {
            Toast.makeText(this, "Permessi di posizione non accettati", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    currentLocation = location;
                    fusedLocationClient.removeLocationUpdates(this);
                    createInfoNode(); // Call createInfoNode after location is retrieved

                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    } else{
            Toast.makeText(this, "Permessi di posizione non accettati", Toast.LENGTH_SHORT).show();
        }
    }

    private void createInfoNode() {
        userRef = db.getReference("Users");

        HashMap<String, Object> infoHash = new HashMap<>();
        infoHash.put("Day", selectedDay);
        infoHash.put("Hour", selectedHour);
        infoHash.put("Email", email);
        infoHash.put("Nickname", nickname);

        if (currentLocation != null) {
            infoHash.put("Latitude", currentLocation.getLatitude());
            infoHash.put("Longitude", currentLocation.getLongitude());
        }

        userRef.child(auth.getUid())
                .child("Info").setValue(infoHash)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("RegisterActivity", "Info node created successfully");
                    } else {
                        Log.d("RegisterActivity", "Failed to create info node: " + task.getException().getMessage());
                    }
                });

        Toast.makeText(RegisterActivity.this, "Registrat* correttamente!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

}