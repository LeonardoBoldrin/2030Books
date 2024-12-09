package com.example.a2030books;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.Fragment.DashboardButtonsFragment;
import com.example.a2030books.Fragment.MyBooksFragment;
import com.example.a2030books.Fragment.UserProfileFragment;
import com.example.a2030books.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private TextView tvPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvPosition = findViewById(R.id.tvPosition);

        binding.vHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new DashboardButtonsFragment());
                changePositionText("Home");
            }
        });

        binding.vSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new UserProfileFragment());

                changePositionText("Impostazioni");
            }
        });

        String fragmentToLoad = getIntent().getStringExtra("FRAGMENT_TO_LOAD");

        if (fragmentToLoad != null) {
            if (fragmentToLoad.equals("MyBooksFragment"))
                loadFragment(new MyBooksFragment());
        }
        else
            loadFragment(new DashboardButtonsFragment());
    }

    // Reusable method for fragment transactions
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null) // Adds to back stack for navigation
                .commit();
    }

    public void changePositionText(String text){
        binding.tvPosition.setText(text);
    }


}