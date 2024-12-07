package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.databinding.ActivityTakeBookBinding;

public class TakeBookActivity extends AppCompatActivity {

    private ActivityTakeBookBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTakeBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        // intent.getStringExtra("USER_ID");

        binding.tvTitleTakeBook.setText(intent.getStringExtra("BOOK_TITLE"));
        binding.tvStringPriceTakebook.setText(String.valueOf(intent.getFloatExtra("BOOK_PRICE", 0.0f)) + "€");
    }
}
