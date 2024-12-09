package com.example.a2030books.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.a2030books.DashboardActivity;
import com.example.a2030books.Fragment.MyBooksFragment;
import com.example.a2030books.databinding.FragmentBooksGivenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


public class BooksGivenFragment extends Fragment {

    private FragmentBooksGivenBinding binding;

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
}

