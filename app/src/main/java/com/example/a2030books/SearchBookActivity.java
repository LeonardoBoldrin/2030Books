package com.example.a2030books;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a2030books.TabelleDB.Book;
import com.example.a2030books.databinding.ActivitySearchBookBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class SearchBookActivity extends AppCompatActivity {

    private ActivitySearchBookBinding binding;
    private List<Book> bookList;
    private FirebaseDatabase db;
    private DatabaseReference usersRef;
    private List<String> usersIds;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation; // Location memorized
    private static final float RADIUS_METERS = 5.0f * 1000; // range km in meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usersIds = new ArrayList<>();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = db.getReference("Users");

        binding.btnSearchSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestLocationPermission();
            }
        });
    }

    // FUNZIONI PER LA POSIZIONE

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(SearchBookActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(SearchBookActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Permission is already granted
            DashboardActivity dA = new DashboardActivity();
            dA.checkEnabledLocation(SearchBookActivity.this);
            Log.d("TAG", "onClick: premuto");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchBookActivity.this);
                builder.setMessage("Permessi di posizione necessari per utilizzare l'applicazione")
                        .setTitle("Errore");

                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss(); // Makes the dialog disappear
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(SearchBookActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = location;
                            findBooks(binding.etSearchBookSB.getText().toString());
                        } else {
                            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                                    .setMinUpdateIntervalMillis(5000)
                                    .build();

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    Location location = locationResult.getLastLocation();
                                    if (location != null) {
                                        currentLocation = location;
                                        findBooks(binding.etSearchBookSB.getText().toString());
                                        fusedLocationClient.removeLocationUpdates(this);
                                    }
                                }
                            };

                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        }
                    });

        } else {
            Toast.makeText(this, "Permessi di posizione non accettati", Toast.LENGTH_SHORT).show();
        }
    }


    // FUNZIONI PER CERCARE I LIBRI NEL DB

    private void findBooks(String title){

        bookList = new ArrayList<>();

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bookList.clear(); // Clear the list before adding new results
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {

                    String user = userSnapshot.getKey();

                    for (DataSnapshot bookSnapshot : userSnapshot.child("Books").getChildren()) {
                        String bookTitle = bookSnapshot.getKey();
                        Book bookInfo = bookSnapshot.getValue(Book.class);
                        if (bookTitle != null && bookInfo != null && bookTitle.equalsIgnoreCase(title) && !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user)) {
                            if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user)) {
                                usersIds.add(user);
                                // Assuming 'Book' has the correct properties mapped to database fields
                                bookInfo.setTitle(bookTitle);
                                bookList.add(bookInfo);
                            }
                        }
                    }
                }
                deleteDynamicRows();
                findUsersInRange();
            } else {
                Log.d("Error: ", task.getException().getMessage());
            }
        });
    }

    public void findUsersInRange() {
        List<Book> booksInRange = new ArrayList<>();

        // Use a counter to track async operations
        AtomicInteger completedOperations = new AtomicInteger(0);

        int i = 0;


        if(bookList.isEmpty()) {
            Toast.makeText(SearchBookActivity.this, "Nessun libro trovato nelle vicinanze", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Book book : bookList) {

            String userId = usersIds.get(i);

            float[] results = new float[1];

            usersRef.child(userId)
                    .child("Info").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DataSnapshot snapshot = task.getResult();
                            Double latitude = snapshot.child("Latitude").getValue(Double.class);
                            Double longitude = snapshot.child("Longitude").getValue(Double.class);

                            if (currentLocation != null && latitude != null && longitude != null) {
                                Location.distanceBetween(
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude(),
                                        latitude,
                                        longitude,
                                        results
                                );

                                float distance = results[0]; // Distance in meters

                                // Add the book if distance is less than 5Km
                                if (distance <= RADIUS_METERS) {
                                    synchronized (booksInRange) {
                                        booksInRange.add(book);
                                    }
                                }
                            }
                        }

                        // Check if all operations are complete
                        if (completedOperations.incrementAndGet() == bookList.size()) {
                            bookList = booksInRange;
                            populateTable();
                        }
                    });

            i += 1;
        }
    }
    // FUNZIONI PER IL FRONTEND

    private void populateTable() {

        TableLayout tableLayout = binding.TableLayout;

        int i = 0;

        if(bookList.isEmpty()){
            Toast.makeText(this, "Nessun libro trovato nelle vicinanze", Toast.LENGTH_SHORT).show();
        }

        for (Book book : bookList) {

            TableRow row = (TableRow) LayoutInflater.from(SearchBookActivity.this)
                    .inflate(R.layout.table_row, tableLayout, false);

            row.setTag("DYNAMIC_ROW");

            TextView tvTitle = row.findViewById(R.id.tvTitle_search);
            TextView tvGenre = row.findViewById(R.id.tvGenre);
            TextView tvPublisher= row.findViewById(R.id.tvPublisher);
            TextView tvAuthor = row.findViewById(R.id.tvAuthor);
            TextView btnAvailability = row.findViewById(R.id.btnAvailability);

            tvTitle.setText(book.getTitle());
            tvGenre.setText(book.getGenre());
            tvPublisher.setText(book.getPublisher());
            tvAuthor.setText(book.getAuthor());
            btnAvailability.setText(book.getAvailability());

            int i_copy = i;

            btnAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start the "TakeBookActivity" and pass the info about this book
                    Intent intent;

                    if(book.getAvailability().equals("Prestito")){
                        intent = new Intent(SearchBookActivity.this, TakeBookActivity.class);

                        // Get user info asynchronously
                        usersRef.child(usersIds.get(i_copy))
                                .child("Info")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        DataSnapshot infoNode = task.getResult();
                                        String userHour = infoNode.child("Hour").getValue(String.class);
                                        String userDay = infoNode.child("Day").getValue(String.class);
                                        String userNickname = infoNode.child("Nickname").getValue(String.class);

                                        // Pass user information to the intent
                                        intent.putExtra("USER_HOUR", userHour);
                                        intent.putExtra("USER_DAY", userDay);
                                        intent.putExtra("USER_NICKNAME", userNickname);

                                        addCommonIntentExtras(intent, book, i_copy);

                                    } else {
                                        // Handle failure to get user info
                                        Toast.makeText(SearchBookActivity.this, "Failed to load user information", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        intent = new Intent(SearchBookActivity.this, BuyBookActivity.class);
                        intent.putExtra("BOOK_GENRE", book.getGenre());

                        addCommonIntentExtras(intent, book, i_copy);
                    }
                }
            });


            tableLayout.addView(row);

            i += 1;
        }

    }

    private void deleteDynamicRows() {

        TableLayout tableLayout = binding.TableLayout;

        for (int i = tableLayout.getChildCount() - 1; i >= 0; i--) {
            View child = tableLayout.getChildAt(i);

            // Check if the row has the tag "DYNAMIC_ROW" and remove it if true
            if (child.getTag() != null && child.getTag().equals("DYNAMIC_ROW")) {
                tableLayout.removeViewAt(i);
            }
        }
    }

    private void addCommonIntentExtras(Intent intent, Book book, int index) {
        intent.putExtra("BOOK_AUTHOR", book.getAuthor());
        intent.putExtra("BOOK_TITLE", book.getTitle());
        intent.putExtra("BOOK_PRICE", book.getPrice());
        intent.putExtra("USER_ID", usersIds.get(index));

        startActivity(intent);
    }
}