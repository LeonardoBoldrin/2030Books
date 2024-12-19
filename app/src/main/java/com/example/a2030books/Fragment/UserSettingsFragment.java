package com.example.a2030books.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2030books.DashboardActivity;
import com.example.a2030books.LoginActivity;
import com.example.a2030books.databinding.FragmentUserSettingsBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettingsFragment extends Fragment {

    private FragmentUserSettingsBinding binding;
    private FirebaseUser user;

    public UserSettingsFragment(){}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((DashboardActivity) requireActivity()).changePositionText("Impostazioni");
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false);

        binding.btnChangePwdUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current = binding.etActualPasswordUS.getText().toString();
                String newPwd = binding.etNewPasswordUS.getText().toString();
                String confirm = binding.tvRepeatUS.getText().toString();

                if(current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty())
                    Toast.makeText(getActivity(), "Riempi tutti i campi", Toast.LENGTH_SHORT).show();

                if(newPwd.equals(confirm))
                    reauthenticateUser(current, newPwd);
            }
        });

        binding.btnLogoutUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void reauthenticateUser(final String current, final String newPassword) {
        // Get the current user's email and password
        String email = user.getEmail();

        // Create AuthCredential with current email and password
        AuthCredential credential = EmailAuthProvider.getCredential(email, current);

        // Reauthenticate the user
        user.reauthenticate(credential).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                // User reauthenticated successfully, now change the password
                changePassword(newPassword);
            } else {
                // Failed to reauthenticate
                Toast.makeText(getActivity(), "Password non corretta, riprova", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword(String newPassword) {
        // Update the user's password
        user.updatePassword(newPassword).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                // Password changed successfully
                Toast.makeText(getActivity(), "Password aggiornata correttamente!", Toast.LENGTH_SHORT).show();
            } else {
                // Failed to update password
                Toast.makeText(getActivity(), "Errore. Riprova.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
