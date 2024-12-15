package com.example.a2030books;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2030books.databinding.ActivityTakeBookBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class TakeBookActivity extends AppCompatActivity {

    private ActivityTakeBookBinding binding;
    private FirebaseDatabase db;
    private DatabaseReference userRef;
    private int newDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTakeBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");
        userRef = db.getReference("Users");

        Intent intent = getIntent();
        String ownerId = intent.getStringExtra("USER_ID");
        String title = intent.getStringExtra("BOOK_TITLE");

        binding.tvBookTB.setText(title);
        binding.tvTimeTB.setText(intent.getStringExtra("USER_HOUR"));
        binding.tvPriceTB.setText(String.valueOf(intent.getFloatExtra("BOOK_PRICE", 0.0f)) + "€");

        String userDay = intent.getStringExtra("USER_DAY");

        HashMap<String, Object> toAdd = new HashMap<>();

        int nextDay = findNextDay(userDay);

        binding.spEndTB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0) {
                    Toast.makeText(TakeBookActivity.this, "Scegli quanto vuoi far durare il prestito", Toast.LENGTH_SHORT).show();
                    return;
                }

                newDay = addDaysToCalculatedDay(nextDay, Integer.parseInt(adapterView.getItemAtPosition(i).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.tvStartTB.setText(userDay + " " + nextDay);

        binding.btnTakeBookTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userRef.child(ownerId).child("Info").child("Nickname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            toAdd.put("Type", intent.getStringExtra("BOOK_AVAILABILITY"));
                            toAdd.put("Author", intent.getStringExtra("BOOK_AUTHOR"));
                            toAdd.put("Owner", task.getResult().getValue(String.class));

                            if (newDay != 0) {
                                toAdd.put("End", userDay + " " + newDay);
                            } else {
                                Toast.makeText(TakeBookActivity.this, "Seleziona per quanti giorni vuoi il libro", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Now set the value in Firebase
                            userRef.child(FirebaseAuth.getInstance().getUid())
                                    .child("Exchanges")
                                    .child("Taken")
                                    .child(title).setValue(toAdd).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(TakeBookActivity.this, "Libro aggiunto correttamente", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(TakeBookActivity.this, DashboardActivity.class);
                                            intent.putExtra("FRAGMENT_TO_LOAD", "BooksTakenFragment");
                                            intent.putExtra("CHANGE_POSITION_TEXT", "Libri presi"); // TODO
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(TakeBookActivity.this, "Failed to add book: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }
        });

    }

    private int findNextDay(String targetDay) {

        int targetDayInt = getDayOfWeek(targetDay);

        if (targetDayInt == -1) {
            throw new IllegalArgumentException("Giorno non valido: " + targetDay);
        }

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        int daysUntilTarget;
        if (targetDayInt > currentDay) {
            daysUntilTarget = targetDayInt - currentDay;
        } else {
            daysUntilTarget = (7 - currentDay) + targetDayInt;
        }

        calendar.add(Calendar.DAY_OF_YEAR, daysUntilTarget);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getDayOfWeek(String day) {
        switch (day.toLowerCase()) {
            case "domenica":
                return Calendar.SUNDAY;
            case "lunedì":
                return Calendar.MONDAY;
            case "martedì":
                return Calendar.TUESDAY;
            case "mercoledì":
                return Calendar.WEDNESDAY;
            case "giovedì":
                return Calendar.THURSDAY;
            case "venerdì":
                return Calendar.FRIDAY;
            case "sabato":
                return Calendar.SATURDAY;
            default:
                return -1;
        }
    }

    private int addDaysToCalculatedDay(int calculatedDay, int daysToAdd) {
        // Initialize a calendar instance
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the date corresponding to the calculated day
        calendar.set(Calendar.DAY_OF_MONTH, calculatedDay);

        // Add the specified number of days to the calendar
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);

        // Return the resulting day of the month
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

}
