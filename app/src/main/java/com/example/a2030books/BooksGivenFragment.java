package com.example.a2030books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a2030books.databinding.FragmentBooksGivenBinding;


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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBooksGivenBinding.inflate(inflater, container, false);

        // populate the table in the recycler view item



        //__________________________________________________________________________________________

        binding.btnMyBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) requireActivity()).loadFragment(new MyBooksFragment());
            }
        });

        return binding.getRoot();

    }
}

