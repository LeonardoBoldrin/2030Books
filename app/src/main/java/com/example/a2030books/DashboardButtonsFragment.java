package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a2030books.databinding.FragmentDashboardButtonsBinding;

public class DashboardButtonsFragment extends Fragment {

    private FragmentDashboardButtonsBinding binding;

    public DashboardButtonsFragment() {
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
        // Inflate the layout for this fragment
        binding = FragmentDashboardButtonsBinding.inflate(inflater, container, false);

        binding.btnGive.setOnClickListener(v -> {
            // Use the activity's loadFragment method to switch to FragmentB
            ((DashboardActivity) requireActivity()).loadFragment(new BooksGivenFragment());
            ((DashboardActivity) requireActivity()).changePositionText("Prestiti");

        });

        binding.btnAddBookContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
                ((DashboardActivity) requireActivity()).changePositionText("Aggiungi libro");
            }
        });

        return binding.getRoot();
    }
}