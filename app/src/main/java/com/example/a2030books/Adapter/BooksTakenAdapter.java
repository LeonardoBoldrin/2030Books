package com.example.a2030books.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2030books.R;
import com.example.a2030books.TabelleDB.BookTaken;

import java.util.List;

public class BooksTakenAdapter extends RecyclerView.Adapter<BooksTakenAdapter.ViewHolder> {

    private final List<BookTaken> booksTaken;

    public BooksTakenAdapter(List<BookTaken> booksTaken) {
        this.booksTaken = booksTaken;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_booktaken_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookTaken bookTaken = booksTaken.get(position);

        holder.tvBook.setText(bookTaken.getTitle());
        holder.tvAuthor.setText(bookTaken.getAuthor());
        holder.tvOwner.setText(bookTaken.getOwner());
        holder.tvEnd.setText(bookTaken.getEnd());
    }

    @Override
    public int getItemCount() {
        return booksTaken.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBook, tvAuthor, tvOwner, tvEnd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBook = itemView.findViewById(R.id.tvBook_BT);
            tvAuthor = itemView.findViewById(R.id.tvAuthor_BT);
            tvOwner = itemView.findViewById(R.id.tvOwner_BT);
            tvEnd = itemView.findViewById(R.id.tvEnd_BT);
        }
    }
}
