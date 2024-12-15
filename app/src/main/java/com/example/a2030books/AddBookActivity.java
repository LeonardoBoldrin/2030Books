package com.example.a2030books;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a2030books.databinding.ActivityAddBookBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;

    private String selectedTitle;
    private String selectedAuthor;
    private Spinner srPublisher;
    private String selectedPublisher;
    private Spinner srGenre;
    private String selectedGenre;
    private String selectedAvailability;
    private float selectedPrice;

    private FirebaseDatabase db;
    private DatabaseReference usersRef;

    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");

        usersRef = db.getReference("Users");

        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkLocation();

        // _________________________________________________________________________________________
        // Editore


        srPublisher = binding.srPublisherAB;

        srPublisher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                srPublisher.setSelection(position);
                selectedPublisher = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                srPublisher.setSelection(0);
            }
        });

        // _________________________________________________________________________________________
        // Genere


        srGenre = binding.srGenreAB;

        srGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Handle the selection of a valid genre
                srGenre.setSelection(position);

                selectedGenre = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                srGenre.setSelection(0);
            }
        });

        // _________________________________________________________________________________________
        // radio button => prezzo / cauzione

        binding.radioGroupDisponibilePer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    RadioButton selectedRadioButton = AddBookActivity.this.findViewById(checkedId);

                    selectedAvailability = selectedRadioButton.getText().toString();

                    if (selectedAvailability.equals("Vendita")) {
                        binding.etPriceLoanAB.setHint("Prezzo");
                    } else {
                        binding.etPriceLoanAB.setHint("Cauzione");
                    }
                }
            }
        });

        // _________________________________________________________________________________________

        binding.btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTitle = binding.etTitleAB.getText().toString();

                if(selectedTitle.isEmpty()){
                    Toast.makeText(AddBookActivity.this, "Per favore, inserisci un titolo", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedAuthor = binding.etAuthorAB.getText().toString();

                if(selectedAuthor.isEmpty()){
                    Toast.makeText(AddBookActivity.this, "Per favore, inserisci un autore", Toast.LENGTH_SHORT).show();
                    return;
                }

                //__________________________________________________________________________________

                String priceString = binding.etPriceLoanAB.getText().toString();

                if (priceString.isEmpty()) {
                    Toast.makeText(AddBookActivity.this, "Per favore, inserisci un prezzo", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    selectedPrice = Float.parseFloat(priceString);
                }

                if(selectedPublisher.equals(srPublisher.getItemAtPosition(0))){
                    Toast.makeText(AddBookActivity.this, "Per favore, seleziona un editore", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(selectedGenre.equals(srGenre.getItemAtPosition(0))) {
                    Toast.makeText(AddBookActivity.this, "Per favore, seleziona un genere", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(selectedAvailability == null){
                    Toast.makeText(AddBookActivity.this, "Per favore, seleziona la disponibilità", Toast.LENGTH_SHORT).show();
                    return;
                }

                addBookToDb();
            }
        });
    }

    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(AddBookActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
            builder.setMessage("Non puoi aggiungere libri se non hai fornito i permessi di posizione!")
                    .setTitle("Errore");

            builder.setPositiveButton("Fornisci posizione", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss(); // Makes the dialog disappear

                    // Request permission
                    ActivityCompat.requestPermissions(AddBookActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                }
            });

            builder.setNegativeButton("Non fornire posizione",  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss(); // Makes the dialog disappear
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                {
                    // Permission denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
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
    }

    // _________________________________________________________________________________________

    private void addBookToDb() {

        HashMap<String, Object> bookDetails = new HashMap<>();
        bookDetails.put("Author", selectedAuthor);
        bookDetails.put("Genre", selectedGenre);
        bookDetails.put("Publisher", selectedPublisher);
        bookDetails.put("Availability", selectedAvailability);
        bookDetails.put("Price", selectedPrice);

        // https://a2030books-default-rtdb.europe-west1.firebasedatabase.app


        usersRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Books")
                .child(selectedTitle).setValue(bookDetails)
                    .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddBookActivity.this, "Libro aggiunto correttamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddBookActivity.this, DashboardActivity.class);
                                intent.putExtra("FRAGMENT_TO_LOAD", "MyBooksFragment");
                                intent.putExtra("CHANGE_POSITION_TEXT", "I miei libri");
                                startActivity(intent);
                            } else
                                Toast.makeText(AddBookActivity.this, "Failed to add book: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                    });
    }

}