package com.example.a2030books;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDynamicRows();
                findBooks(binding.searchView.getText().toString());
            }
        });
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
                Log.d("Error: ", task.getException().getMessage());
            }
        });
    }

    private void populateTable(List<Book> books) {
        TableLayout tableLayout = binding.TableLayout;

        for (Book book : books) {

            TableRow row = (TableRow) LayoutInflater.from(SearchBookActivity.this)
                    .inflate(R.layout.table_row, tableLayout, false);

            row.setTag("DYNAMIC_ROW");

            TextView tvTitle = row.findViewById(R.id.tvTitle);
            TextView tvGenre = row.findViewById(R.id.tvGenre);
            TextView tvPublisher= row.findViewById(R.id.tvPublisher);
            TextView tvAuthor = row.findViewById(R.id.tvAuthor);
            TextView tvAvailability = row.findViewById(R.id.tvAvailability);

            tvTitle.setText(book.getTitle());
            tvGenre.setText(book.getGenre());
            tvPublisher.setText(book.getPublisher());
            tvAuthor.setText(book.getAuthor());
            tvAvailability.setText(book.getAvailability());

            tvAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // start the "TakeBookActivity" and pass the info about this book
                }
            });

            tableLayout.addView(row);
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
}