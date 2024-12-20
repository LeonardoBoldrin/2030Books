package com.example.a2030books.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2030books.R;
import com.example.a2030books.TabelleDB.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.BookViewHolder> {

    private final List<Book> bookList;

    // Constructor for the adapter, taking in the list of books
    public MyBooksAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    // Create a new view holder instance for each item
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_mybook_info, parent, false);

        return new BookViewHolder(view);
    }

    // Bind data to the view holder
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvGenre.setText(book.getGenre());
        holder.tvAvailability.setText(book.getAvailability());

        holder.btnDeleteBook.setOnClickListener(v -> {
            DatabaseReference bookRef = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("Books")
                    .child(book.getTitle());

            bookRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Remove the book from the list and notify the adapter
                    bookList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookList.size());
                    Toast.makeText(v.getContext(), book.getTitle() + " eliminato", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "C'è stato un problema, riprova", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    // ViewHolder class to represent individual list items
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvGenre, tvAvailability;

        Button btnDeleteBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);

            btnDeleteBook = itemView.findViewById(R.id.btnDeleteBook_mybooks);
        }
    }
}
