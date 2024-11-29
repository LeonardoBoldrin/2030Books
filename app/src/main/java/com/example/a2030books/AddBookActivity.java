package com.example.a2030books;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;

    private String selectedTitle;
    private Spinner srPublisher;
    private String selectedPublisher;
    private Spinner srGenre;
    private String selectGenre;
    private String selectedAvailability;
    private float selectedPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // _________________________________________________________________________________________
        // Nome

        selectedTitle = binding.etTitle.getText().toString();

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
                "Avventura", "Auto/Biografia"});

        // Attach the adapter to the spinner
        srGenre.setAdapter(genreAdapter);

        srGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Handle the selection of a valid genre
                srGenre.setSelection(position);

                selectGenre = parent.getItemAtPosition(position).toString();
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

        String priceString = binding.etPrezzoCauzione.getText().toString();
        selectedPrice = Float.parseFloat(priceString);

        // _________________________________________________________________________________________

        binding.btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        // https://a2030books-default-rtdb.europe-west1.firebasedatabase.app

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference booksRef = db.getReference("Books");

        HashMap<String, Object> booksHashMap = new HashMap<>();
        booksHashMap.put("Titolo", selectedTitle);
        booksHashMap.put("Editore", selectedPublisher);
        booksHashMap.put("Genere", selectGenre);
        booksHashMap.put("Prezzo", selectedPrice);

       
    }
}