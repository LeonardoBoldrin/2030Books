package com.example.a2030books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a2030books.databinding.ActivitySearchBookBinding;

import java.util.List;


public class SearchBookActivity extends AppCompatActivity {

    private ActivitySearchBookBinding binding;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookList = List.of(
                new Book("Harry Potter e i doni della morte, parte 1", "Author 1", "Genre 1", "Publisher 1", "Available", 10.0f),
                new Book("Book 2", "Author 2", "Genre 2", "Publisher 2", "Borrowed", 15.0f)
        );

        populateTable(bookList);
    }

        private void populateTable(List<Book> books) {
            TableLayout tableLayout = binding.TableLayout;

            for (Book book : books) {
                TableRow row = (TableRow) LayoutInflater.from(SearchBookActivity.this)
                        .inflate(R.layout.table_row, tableLayout, false);

                TextView tvTitolo = row.findViewById(R.id.tvTitolo);
                TextView tvGenere = row.findViewById(R.id.tvGenere);
                TextView tvEditore = row.findViewById(R.id.tvEditore);
                TextView tvUtente = row.findViewById(R.id.tvUtente);
                TextView tvDisponibilePer = row.findViewById(R.id.tvDisponibilePer);

                tvTitolo.setText(book.getTitle());
                tvGenere.setText(book.getGenre());
                tvEditore.setText(book.getPublisher());
                tvUtente.setText(book.getAuthor());
                tvDisponibilePer.setText(book.getAvailability());

                tableLayout.addView(row);
            }
        }
}