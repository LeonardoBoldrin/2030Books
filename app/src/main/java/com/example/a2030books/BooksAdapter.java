package com.example.a2030books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final List<Book> bookList;

    // Constructor for the adapter, taking in the list of books
    public BooksAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    // Create a new view holder instance for each item
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_book_info, parent, false);

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
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    // ViewHolder class to represent individual list items
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvGenre, tvAvailability;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
        }
    }
}
