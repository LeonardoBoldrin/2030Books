package com.example.a2030books;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a2030books.databinding.ActivitySearchBookBinding;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchBookActivity extends AppCompatActivity {

    private ActivitySearchBookBinding binding;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findBooks("ciao");
    }

    private void findBooks(String title){

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference usersRef = db.getReference("Users");

        bookList = new ArrayList<>();

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bookList.clear(); // Clear the list before adding new results
                for (DataSnapshot userSnapshot : Objects.requireNonNull(task.getResult()).getChildren()) {
                    for (DataSnapshot bookSnapshot : userSnapshot.child("Books").getChildren()) {
                        String bookTitle = bookSnapshot.getKey();
                        Book bookInfo = bookSnapshot.getValue(Book.class);

                        if (bookTitle != null && bookInfo != null && bookTitle.equalsIgnoreCase(title)) {
                            // Assuming 'Book' has the correct properties mapped to database fields
                            bookInfo.setTitle(bookTitle);
                            bookList.add(bookInfo);
                        }
                    }
                }
                // Populate table with filtered books
                populateTable(bookList);
            } else {
                // Handle error
                System.out.println("Error: " + task.getException());
            }
        });
    }

    private void populateTable(List<Book> books) {
        TableLayout tableLayout = binding.TableLayout;

        for (Book book : books) {
            TableRow row = (TableRow) LayoutInflater.from(SearchBookActivity.this)
                    .inflate(R.layout.table_row, tableLayout, false);

            TextView tvTitolo = row.findViewById(R.id.tvTitolo);
            TextView tvGenere = row.findViewById(R.id.tvGenere);
            TextView tvEditore = row.findViewById(R.id.tvEditore);
            TextView tvAuthor = row.findViewById(R.id.tvAuthor);
            TextView tvDisponibilePer = row.findViewById(R.id.tvDisponibilePer);

            tvTitolo.setText(book.getTitle());
            tvGenere.setText(book.getGenre());
            tvEditore.setText(book.getPublisher());
            tvAuthor.setText(book.getAuthor());
            tvDisponibilePer.setText(book.getAvailability());

            tableLayout.addView(row);
        }
    }
}