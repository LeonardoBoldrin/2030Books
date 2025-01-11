package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.databinding.ActivityBuyBookBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class BuyBookActivity extends AppCompatActivity {

    private ActivityBuyBookBinding binding;

    private String title;
    private String author;
    private String genre;
    private float price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBuyBookBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Intent intent = getIntent();

        title = intent.getStringExtra("BOOK_TITLE");
        author = intent.getStringExtra("BOOK_AUTHOR");
        genre = intent.getStringExtra("BOOK_GENRE");
        price = intent.getFloatExtra("BOOK_PRICE", 0.0f);

        binding.tvBookBB.setText(title);
        binding.tvAuthorBB.setText(author);
        binding.tvGenreBB.setText(genre);
        binding.tvPriceBB.setText(price + " €");


        binding.btnTakeBookBB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> toAdd = new HashMap<>();

                toAdd.put("Type", "Vendita");
                toAdd.put("Author", author);
                toAdd.put("Owner", String.valueOf(price));  // because in BooksTakenAdapter if Availability == Vendita
                                            // then "Restituire a" becomes "Price"
                toAdd.put("End", "Vendita");// and "Entro" becomes "Tipo"

                DatabaseReference userRef = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app")
                                                            .getReference("Users");

                // Now set the value in Firebase
                userRef.child(FirebaseAuth.getInstance().getUid())
                        .child("Exchanges")
                        .child("Taken")
                        .child(title).setValue(toAdd).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(BuyBookActivity.this, "Libro aggiunto correttamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BuyBookActivity.this, DashboardActivity.class);
                                intent.putExtra("FRAGMENT_TO_LOAD", "BooksTakenFragment");
                                intent.putExtra("CHANGE_POSITION_TEXT", "Libri presi");
                                startActivity(intent);
                            } else {
                                Toast.makeText(BuyBookActivity.this, "Failed to add book: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                toAdd.remove("Owner");
                toAdd.remove("Type");

                userRef.child(FirebaseAuth.getInstance().getUid())
                        .child("Info/Nickname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        toAdd.put("OtherUser", task.getResult().getValue(String.class));
                    }
                });

                String owner = intent.getStringExtra("USER_ID");

                userRef.child(owner)
                        .child("Books")
                        .child(title).removeValue();

                userRef.child(owner)
                        .child("Exchanges")
                        .child("Given")
                        .child(title)
                        .setValue(toAdd);
            }
        });
    }
}