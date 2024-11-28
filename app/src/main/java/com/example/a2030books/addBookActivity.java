package com.example.a2030books;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class addBookActivity extends AppCompatActivity {

    private EditText fieldName;
    private EditText fieldAuthor;
    private Button btnConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_book);

        /*
        fieldName = findViewById(R.id.fieldName);
        fieldAuthor = findViewById(R.id.fieldAuthor);
        btnConfirm = findViewById(R.id.btnConfirm);*/

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fieldName.getText().toString();
                String author = fieldAuthor.getText().toString();

                if(name.isEmpty()){
                    fieldName.setError("Cannot be empty");
                } else if (author.isEmpty()){
                    fieldAuthor.setError("Cannot be empty");
                }

                addBookToDb(name, author);
            }
        });

    }

    private void addBookToDb(String name, String author) {
        // https://a2030books-default-rtdb.europe-west1.firebasedatabase.app
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference booksRef = db.getReference("Books");

        HashMap<String, Object> booksHashMap = new HashMap<>();
        booksHashMap.put("Name", name);
        booksHashMap.put("Author", author);

        booksHashMap.put("PK", name);

        if(name != null)
            booksRef.child(name).setValue(booksHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(addBookActivity.this, "Book added succesfully!", Toast.LENGTH_SHORT).show();

                    fieldName.getText().clear();
                    fieldAuthor.getText().clear();
                }
            });
        else{
            Toast.makeText(addBookActivity.this, "key = null!", Toast.LENGTH_SHORT).show();
        }
    }
}