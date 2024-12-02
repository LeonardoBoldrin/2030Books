package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a2030books.databinding.FragmentBooksTakenBinding;
import com.example.a2030books.databinding.FragmentDashboardButtonsBinding;

public class BooksTakenFragment extends Fragment {

    private FragmentBooksTakenBinding binding;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBooksTakenBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}
