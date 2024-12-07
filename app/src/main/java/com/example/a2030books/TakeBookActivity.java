package com.example.a2030books;

import android.os.Bundle;
import android.os.PersistableBundle;

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
    }
}
