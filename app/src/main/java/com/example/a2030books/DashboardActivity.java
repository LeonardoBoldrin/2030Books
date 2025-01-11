package com.example.a2030books;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.Fragment.BooksTakenFragment;
import com.example.a2030books.Fragment.DashboardButtonsFragment;
import com.example.a2030books.Fragment.MyBooksFragment;
import com.example.a2030books.Fragment.UserProfileFragment;
import com.example.a2030books.databinding.ActivityDashboardBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    private FirebaseDatabase db;
    private DatabaseReference userRef;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        userRef = db.getReference("Users");

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkEnabledLocation(this);

        binding.vHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new DashboardButtonsFragment());
                changePositionText("Home");
            }
        });

        binding.vSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new UserProfileFragment());

                changePositionText("Impostazioni");
            }
        });

        String fragmentToLoad = getIntent().getStringExtra("FRAGMENT_TO_LOAD");
        String tvPositionString = getIntent().getStringExtra("CHANGE_POSITION_TEXT");

        if(tvPositionString != null){ // TODO
            changePositionText(tvPositionString);
        }

        if (fragmentToLoad != null) {
            if (fragmentToLoad.equals("MyBooksFragment"))
                loadFragment(new MyBooksFragment());
            else if(fragmentToLoad.equals("BooksTakenFragment")){
                loadFragment(new BooksTakenFragment());
            }
        }
        else
            loadFragment(new DashboardButtonsFragment());

    }

    // Reusable method for fragment transactions
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null) // Adds to back stack for navigation
                .commit();
    }

    public void changePositionText(String text){
        binding.tvPosition.setText(text);
    }


    // LOCATION FUNCTIONS

    public void checkAndRequestLocationPermission(Activity activity) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Permission is already granted
            getUserLocation(activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getUserLocation(this);
            } else {
                createAlertDialog("La posizione è importante perchè ci serve per trovare i libri vicini a te, " +
                        "ricordati che puoi sempre attivare i permessi di posizione da App->2030Books->Autorizzazioni ", "Posizione", this);
            }
        }
    }

    public void getUserLocation(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = location;
                            changeInfoNode();
                        } else {
                            requestLocationUpdates(activity); // Trigger location updates if the last location is unavailable
                        }
                    });

        } else {
            Toast.makeText(activity, "Permessi di posizione non accettati", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationUpdates(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
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
                        changeInfoNode();
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        } else{
            Toast.makeText(this, "Permessi di posizione non accettati", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeInfoNode(){
        DatabaseReference infoNode = userRef.child(FirebaseAuth.getInstance().getUid())
                                            .child("Info");

        if(currentLocation != null) {
            infoNode.child("Latitude").setValue(currentLocation.getLatitude());
            infoNode.child("Longitude").setValue(currentLocation.getLongitude());
        }
    }

    public void checkEnabledLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isEnabled){
            new AlertDialog.Builder(context)
                    .setTitle("Posizione")
                    .setMessage("La geolocalizzazione è necessaria per quest'app, per favore, attivala.")
                    .setPositiveButton("Impostazioni", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Chiudi", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    public void createAlertDialog(String msg, String title, Activity activity){
        // Permission denied
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(msg)
                .setTitle(title);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Makes the dialog disappear
            }
        });

        builder.create().show();
    }

}