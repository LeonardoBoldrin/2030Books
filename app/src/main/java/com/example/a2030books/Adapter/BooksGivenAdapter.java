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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        if(bookGiven.getEnd().equals("Vendita")){
            holder.tvStringEnd.setText("Tipo:");

            holder.tvStringOtherUser.setVisibility(View.GONE);
            holder.tvOtherUser.setVisibility(View.GONE);

            holder.btnReturned.setVisibility(View.GONE);
        }
        else {
            holder.btnReturned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentPosition = holder.getAdapterPosition(); // Get the correct position
                    if (currentPosition != RecyclerView.NO_POSITION) { // Check if the position is valid
                        BookGiven currentBookGiven = booksGivenList.get(currentPosition);

                        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("Users");

                        DatabaseReference bookRef = usersRef.child(FirebaseAuth.getInstance().getUid())
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

                        getTakenReferenceByNickname(bookGiven.getOtherUser());

                    }
                }
            });
        }
        holder.tvEnd.setText(bookGiven.getEnd());
    }

    @Override
    public int getItemCount() {
        return booksGivenList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBook, tvAuthor, tvOtherUser, tvStringOtherUser, tvEnd, tvStringEnd;

        Button btnReturned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBook = itemView.findViewById(R.id.tvBook_BG);
            tvAuthor = itemView.findViewById(R.id.tvAuthor_BG);
            tvOtherUser = itemView.findViewById(R.id.tvOtherUser_BG);
            tvEnd = itemView.findViewById(R.id.tvEnd_BG);
            tvStringEnd = itemView.findViewById(R.id.tvStringEnd_BG);
            tvStringOtherUser = itemView.findViewById(R.id.tvStringOtherUser_BG);

            btnReturned = itemView.findViewById(R.id.btnReturned_BG);
        }
    }

    public void getTakenReferenceByNickname(String nickname) {
        // Get a reference to the root of the database
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users");

        // Query to find the user with the specified nickname
        Query query = rootRef.orderByChild("Info/Nickname").equalTo(nickname);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                        // Get the reference to the Taken node
                        DatabaseReference takenRef = userSnapshot.child("Exchanges/Taken").getRef();

                        takenRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("TAG", "onComplete: takenRef cancelled ");
                            }
                        });

                        // Break after finding the first match
                        break;
                    }
                } else {
                    Log.d("TAG", "No user found with nickname: " + nickname);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error querying database: " + databaseError.getMessage());
            }
        });
    }
}
