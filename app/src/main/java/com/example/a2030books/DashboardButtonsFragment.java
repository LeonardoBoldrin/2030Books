package com.example.a2030books;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a2030books.databinding.FragmentDashboardButtonsBinding;

public class DashboardButtonsFragment extends Fragment {

    private FragmentDashboardButtonsBinding binding;

    private boolean isBackPressed;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (isBackPressed) {
                    finishAffinity(getActivity());
                    return;
                }

                Toast.makeText(getActivity(), "Premi ancora per chiudere l'app", Toast.LENGTH_SHORT).show();
                isBackPressed = true;

                binding.getRoot().postDelayed(() -> isBackPressed = false, 2000);
            }
        });
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
            }
        });

        binding.btnLookForBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchBookActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}