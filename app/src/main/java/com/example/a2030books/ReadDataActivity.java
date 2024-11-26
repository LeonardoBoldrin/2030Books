package com.example.a2030books;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.databinding.ActivityReadDataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReadDataActivity extends AppCompatActivity {

    private ActivityReadDataBinding binding;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnReadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookName = binding.fieldNameData.getText().toString();
                
                if(!bookName.isEmpty()){
                    Log.d("ReadDataActivity", "debug");
                    readData(bookName);
                }
                else{
                    Toast.makeText(ReadDataActivity.this, "Insert boook name",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void readData(String bookName) {

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app").getReference("Books");

        db.child(bookName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if(task.isSuccessful()){ // questo non ci assicura che esista una entry con quel nome nel db

                    if(task.getResult().exists()){ // se esiste quel libro nel db

                        Toast.makeText(ReadDataActivity.this, "Book successfully found", Toast.LENGTH_SHORT).show();

                        DataSnapshot dataSnapshot = task.getResult();

                        String bookAuthor = String.valueOf(dataSnapshot.child("Author").getValue());

                        binding.tvAuthorData.setText(bookAuthor);
                        

                    }
                    else{
                        Toast.makeText(ReadDataActivity.this, "Book doesn't exist", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(ReadDataActivity.this, "Failed to read", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}