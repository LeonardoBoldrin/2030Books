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

import com.example.a2030books.Adapter.BooksTakenAdapter;
import com.example.a2030books.DashboardActivity;
import com.example.a2030books.TabelleDB.BookTaken;
import com.example.a2030books.databinding.FragmentBooksTakenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BooksTakenFragment extends Fragment {

    private FragmentBooksTakenBinding binding;
    private BooksTakenAdapter adapter;
    private List<BookTaken> bookTakenList;

    public BooksTakenFragment() {
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

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((DashboardActivity) requireActivity()).changePositionText("Home");
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        bookTakenList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBooksTakenBinding.inflate(inflater, container, false);

        binding.rvBooksTaken.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadBooksFromDatabase();

        if(bookTakenList.isEmpty())
            Log.d("BOLFO", "onCreateView: lista vuota ");

        adapter = new BooksTakenAdapter(bookTakenList);
        binding.rvBooksTaken.setAdapter(adapter);

        return binding.getRoot();
    }

    private void loadBooksFromDatabase(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference usersRef = db.getReference("Users");

        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Exchanges")
                .child("Taken").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookTakenList.clear();

                        for(DataSnapshot bookSnapshot : snapshot.getChildren()){

                            String bookTitle = bookSnapshot.getKey();
                            BookTaken bookInfo = bookSnapshot.getValue(BookTaken.class);

                            if (bookTitle != null && bookInfo != null) {
                                bookInfo.setTitle(bookTitle);
                                bookTakenList.add(bookInfo);
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
