package com.example.a2030books.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.activity.OnBackPressedCallback;

import com.example.a2030books.Adapter.MyBooksAdapter;
import com.example.a2030books.DashboardActivity;
import com.example.a2030books.TabelleDB.Book;
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
    private MyBooksAdapter adapter;


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

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((DashboardActivity) requireActivity()).changePositionText("Prestiti");
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentMyBooksBinding.inflate(inflater, container, false);

        binding.rvMyBooks.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookList = new ArrayList<>(); // find all the books of a user and add the info here

        loadBooksFromDatabase();

        adapter = new MyBooksAdapter(bookList);
        binding.rvMyBooks.setAdapter(adapter);

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
                            String bookTitle = bookSnapshot.getKey();
                            Book bookInfo = bookSnapshot.getValue(Book.class);

                            if (bookTitle != null && bookInfo != null) {
                                // Assuming 'Book' has the correct properties mapped to database fields
                                bookInfo.setTitle(bookTitle);
                                bookList.add(bookInfo);
                        }
                        adapter.notifyDataSetChanged();  // Update RecyclerView
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error: "+error.getDetails(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
