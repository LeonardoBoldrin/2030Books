package com.example.a2030books.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.a2030books.Adapter.BooksGivenAdapter;
import com.example.a2030books.Adapter.BooksTakenAdapter;
import com.example.a2030books.DashboardActivity;
import com.example.a2030books.TabelleDB.BookGiven;
import com.example.a2030books.TabelleDB.BookTaken;
import com.example.a2030books.databinding.FragmentBooksGivenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.ArrayList;


public class BooksGivenFragment extends Fragment {

    private FragmentBooksGivenBinding binding;
    private FirebaseDatabase db;
    private DatabaseReference usersRef;
    private List<BookGiven> booksGivenList;
    private BooksGivenAdapter adapter;

    public BooksGivenFragment() {
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
        booksGivenList = new ArrayList<>();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((DashboardActivity) requireActivity()).changePositionText("Home");
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBooksGivenBinding.inflate(inflater, container, false);

        binding.recyclerViewBooksGiven.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadBooksFromDatabase();

        adapter = new BooksGivenAdapter(booksGivenList);
        binding.recyclerViewBooksGiven.setAdapter(adapter);

        //__________________________________________________________________________________________

        binding.btnMyBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) requireActivity()).loadFragment(new MyBooksFragment());
                ((DashboardActivity) requireActivity()).changePositionText("I miei libri");
            }
        });

        return binding.getRoot();
    }

    private void loadBooksFromDatabase(){
        usersRef.child(FirebaseAuth.getInstance().getUid())
                .child("Exchanges")
                .child("Given").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        booksGivenList.clear();

                        for(DataSnapshot bookSnapshot : snapshot.getChildren()){

                            String bookTitle = bookSnapshot.getKey();
                            BookGiven bookInfo = bookSnapshot.getValue(BookGiven.class);

                            if (bookTitle != null && bookInfo != null) {
                                bookInfo.setTitle(bookTitle);
                                booksGivenList.add(bookInfo);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error: "+error.getDetails(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

