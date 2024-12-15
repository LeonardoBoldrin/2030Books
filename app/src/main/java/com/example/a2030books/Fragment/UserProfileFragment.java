package com.example.a2030books.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2030books.DashboardActivity;
import com.example.a2030books.R;
import com.example.a2030books.RegisterActivity;
import com.example.a2030books.databinding.FragmentUserProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;

    private FirebaseDatabase db;
    private DatabaseReference usersRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance("https://a2030books-default-rtdb.europe-west1.firebasedatabase.app");

        usersRef = db.getReference("Users");

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((DashboardActivity) requireActivity()).changePositionText("Home");
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);

        String userId = FirebaseAuth.getInstance().getUid();

        binding.ivChangeNicknameUP.setOnClickListener(view -> {
            String nickname = binding.etNicknameUP.getText().toString();

            if (!nickname.isEmpty()) {
                usersRef.child(userId).child("Info").child("Nickname").setValue(nickname);
                Toast.makeText(getActivity(), "Nickname cambiato con successo!", Toast.LENGTH_SHORT).show();
                ((DashboardActivity) requireActivity()).loadFragment(new UserProfileFragment());
            }
        });

        binding.btnChangePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Impostare il luogo in cui si è adesso come luogo di incontro per vendere e prestare libri?")
                        .setTitle("Posizione");

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // Makes the dialog disappear
                        ((DashboardActivity) requireActivity()).checkAndRequestLocationPermission();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(getActivity(), "Premi questo pulsante quando sei nel posto scelto", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        binding.srDayWeekUP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    usersRef.child(userId).child("Info")
                            .child("Day").setValue(adapterView.getItemAtPosition(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.srHourUP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    usersRef.child(userId).child("Info")
                            .child("Hour").setValue(adapterView.getItemAtPosition(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.btnMoreUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) requireActivity()).loadFragment(new UserSettingsFragment());
            }
        });

        binding.ivLocationUP.setOnClickListener(view -> {
            if (userId != null) {
                usersRef.child(userId).child("Info").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot infoNode = task.getResult();
                        Double latitude = infoNode.child("Latitude").getValue(Double.class);
                        Double longitude = infoNode.child("Longitude").getValue(Double.class);

                        if (latitude != null && longitude != null) {
                            String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);
                            intent.setPackage("com.google.android.apps.maps"); // Ensures only Google Maps is used
                            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Google Maps non è installato", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireActivity(), "Imposta un luogo di incontro prima!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Errore durante il recupero dei dati.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // Retrieve user data from Firebase asynchronously
        if (userId != null) {
            usersRef.child(userId).child("Info").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot infoNode = task.getResult();

                    String email = infoNode.child("Email").getValue(String.class);
                    String nickname = infoNode.child("Nickname").getValue(String.class);
                    String day = infoNode.child("Day").getValue(String.class);
                    String hour = infoNode.child("Hour").getValue(String.class);

                    // Set data to views
                    binding.tvEmailUP.setText(email != null ? email : "");
                    binding.etNicknameUP.setHint(nickname != null ? nickname : "");

                    String[] dayOptions = getResources().getStringArray(R.array.day_options);
                    for (int j = 0; j < dayOptions.length; j++) {
                        if (dayOptions[j].equals(day)) {
                            binding.srDayWeekUP.setSelection(j);
                            break;
                        }
                    }

                    String[] hourOptions = getResources().getStringArray(R.array.hour_options);
                    for (int j = 0; j < hourOptions.length; j++) {
                        if (hourOptions[j].equals(hour)) {
                            binding.srHourUP.setSelection(j);
                            break;
                        }
                    }
                }
            });
        }

        return binding.getRoot();
    }
}
