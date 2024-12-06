package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a2030books.databinding.ActivitySearchBookBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SearchBookActivity extends AppCompatActivity {

    private ActivitySearchBookBinding binding;
    private List<Book> bookList;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = db.getReference("Users");

        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDynamicRows();
                findBooks(binding.searchView.getText().toString());
            }
        });
    }

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

                        if (bookTitle != null && bookInfo != null && bookTitle.equalsIgnoreCase(title) && user.equals(auth.getCurrentUser().toString())) {
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

    private void populateTable(List<Book> books, List<String> usersIds) {
        TableLayout tableLayout = binding.TableLayout;

        int i = 0;

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

            int i_copy = i;

            tvAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // start the "TakeBookActivity" and pass the info about this book
                    Intent intent = new Intent(SearchBookActivity.this, TakeBookActivity.class);
                    intent.putExtra("BOOK_TITLE", book.getTitle());
                    intent.putExtra("BOOK_PRICE", book.getPrice());
                    startActivity(intent);
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
}