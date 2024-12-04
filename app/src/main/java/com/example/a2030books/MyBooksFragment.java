package com.example.a2030books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.a2030books.databinding.FragmentMyBooksBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyBooksFragment extends Fragment {

    private FragmentMyBooksBinding binding;

    private FirebaseDatabase  db;
    private DatabaseReference usersRef;

    private List<Book> bookList;
    private BooksAdapter adapter;


    public MyBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");

        usersRef = db.getReference("Users");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentMyBooksBinding.inflate(inflater, container, false);

        binding.recyclerViewBooks2.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookList = new ArrayList<>(); // find all the books of a user and add the info here

        loadBooksFromDatabase();

        adapter = new BooksAdapter(bookList);
        binding.recyclerViewBooks2.setAdapter(adapter);

        return binding.getRoot();
    }

    public void loadBooksFromDatabase() {

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        usersRef.child(userId)
                .child("Books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookList.clear();

                        for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                            String title = bookSnapshot.getKey();
                            String author = bookSnapshot.child("Author").getValue(String.class);
                            String genre = bookSnapshot.child("Genre").getValue(String.class);
                            String availability = bookSnapshot.child("Availability").getValue(String.class);
                            String publisher = bookSnapshot.child("Publisher").getValue(String.class);
                            float price = bookSnapshot.child("Price").getValue(Float.class);

                            // Assuming your book model includes title, author, genre, and availability
                            bookList.add(new Book(title, author, genre,publisher, availability, price));
                        }
                        adapter.notifyDataSetChanged();  // Update RecyclerView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
