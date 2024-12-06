package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a2030books.databinding.ActivityAddBookBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // _________________________________________________________________________________________
        // Editore

        srPublisher = binding.srPublisher;


        ArrayAdapter<String> publisherAdapter = getStringArrayAdapter(new String[]{
                "Seleziona un editore", "Mondadori", "Einaudi",
                "Feltrinelli", "Giunti", "Laterza",
                "Rizzoli", "Garzanti", "Salani"});

        srPublisher.setAdapter(publisherAdapter);

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

        srGenre = binding.srGenre;

        ArrayAdapter<String> genreAdapter = getStringArrayAdapter(new String[]{
                "Seleziona un genere", "Giallo", "Rosa", "Horror", "Fantasy",
                "Fantascientifico", "Storico", "Umoristico",
                "Avventura", "Auto_Biografia"});

        // Attach the adapter to the spinner
        srGenre.setAdapter(genreAdapter);

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
                        binding.etPrezzoCauzione.setHint("Prezzo");
                    } else {
                        binding.etPrezzoCauzione.setHint("Cauzione");
                    }
                }
            }
        });

        // _________________________________________________________________________________________

        binding.btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTitle = binding.etTitle.getText().toString();

                if(selectedTitle.isEmpty()){
                    Toast.makeText(AddBookActivity.this, "Please enter a valid title", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedAuthor = binding.etAuthor.getText().toString();

                if(selectedAuthor.isEmpty()){
                    Toast.makeText(AddBookActivity.this, "Please enter a valid author", Toast.LENGTH_SHORT).show();
                    return;
                }

                //__________________________________________________________________________________

                String priceString = binding.etPrezzoCauzione.getText().toString();

                if (priceString.isEmpty()) {
                    Toast.makeText(AddBookActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                } else {
                    selectedPrice = Float.parseFloat(priceString);
                }

                addBookToDb();
            }
        });
    }

    private @NonNull ArrayAdapter<String> getStringArrayAdapter(String[] stringArray) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                AddBookActivity.this,
                android.R.layout.simple_spinner_item,  // Built-in layout for the Spinner
                stringArray
        );

        // Set dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
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

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference usersRef = db.getReference("Users");
        usersRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Books")
                .child(selectedTitle).setValue(bookDetails)
                    .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddBookActivity.this, "Libro aggiunto correttamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddBookActivity.this, DashboardActivity.class);
                                intent.putExtra("FRAGMENT_TO_LOAD", "MyBooksFragment");
                                startActivity(intent);
                            } else
                                Toast.makeText(AddBookActivity.this, "Failed to add book: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                    });
    }



}