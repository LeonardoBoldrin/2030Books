package com.example.a2030books;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.R;

public class DashboardActivity extends AppCompatActivity {

    private View btnGive;
    private View btnLookForBooks;
    private TextView tvPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        tvPosition = findViewById(R.id.tvPosition);

        tvPosition.setText("Home");


        /*

        btnGive = findViewById(R.id.btnGive);

        btnLookForBooks = findViewById(R.id.btnLookForBooks);

        btnGive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, .class);
                startActivity(intent);
            }
        });

        btnLookForBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, .class);
                startActivity(intent);
            }
        });

        */
    }

}