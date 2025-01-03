package com.example.a2030books.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2030books.R;
import com.example.a2030books.TabelleDB.BookGiven;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BooksGivenAdapter extends RecyclerView.Adapter<BooksGivenAdapter.ViewHolder> {
    
    private final List<BookGiven> booksGivenList;

    public BooksGivenAdapter(List<BookGiven> booksGiven) {
        this.booksGivenList = booksGiven;
    }

    @NonNull
    @Override
    public BooksGivenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_bookgiven_info, parent, false);
        return new BooksGivenAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksGivenAdapter.ViewHolder holder, int position) {
        BookGiven bookGiven = booksGivenList.get(position);

        holder.tvBook.setText(bookGiven.getTitle());
        holder.tvAuthor.setText(bookGiven.getAuthor());
        holder.tvOtherUser.setText(bookGiven.getOtherUser());
        holder.tvEnd.setText(bookGiven.getEnd());

        holder.btnReturned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition(); // Get the correct position
                if (currentPosition != RecyclerView.NO_POSITION) { // Check if the position is valid
                    BookGiven currentBookGiven = booksGivenList.get(currentPosition);
                    DatabaseReference bookRef = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child("Exchanges")
                            .child("Given")
                            .child(currentBookGiven.getTitle());


                    bookRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            booksGivenList.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, booksGivenList.size());
                            Toast.makeText(view.getContext(), bookGiven.getTitle() + " ti è stato restituito", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return booksGivenList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBook, tvAuthor, tvOtherUser, tvEnd;

        Button btnReturned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBook = itemView.findViewById(R.id.tvBook_BG);
            tvAuthor = itemView.findViewById(R.id.tvAuthor_BG);
            tvOtherUser = itemView.findViewById(R.id.tvOtherUser_BG);
            tvEnd = itemView.findViewById(R.id.tvEnd_BG);

            btnReturned = itemView.findViewById(R.id.btnReturned_BG);
        }
    }
}
