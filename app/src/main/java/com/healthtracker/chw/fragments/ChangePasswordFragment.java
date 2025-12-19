package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

public class ChangePasswordFragment extends Fragment {

    private com.google.android.material.textfield.TextInputEditText etCurrent, etNew, etConfirm;
    private android.view.View btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Views
        etCurrent = view.findViewById(R.id.et_current_password);
        etNew = view.findViewById(R.id.et_new_password);
        etConfirm = view.findViewById(R.id.et_confirm_password);
        btnSave = view.findViewById(R.id.btn_save_password);

        // Save Listener
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> changePassword());
        }
    }

    private void changePassword() {
        String current = etCurrent.getText().toString();
        String newPass = etNew.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (current.isEmpty()) {
            etCurrent.setError("Current password required");
            return;
        }

        if (newPass.isEmpty()) {
            etNew.setError("New password required");
            return;
        }

        if (newPass.length() < 8) {
            etNew.setError("Password must be at least 8 characters");
            return;
        }

        if (!newPass.equals(confirm)) {
            etConfirm.setError("Passwords do not match");
            return;
        }

        // Mock Save
        android.widget.Toast
                .makeText(requireContext(), "Password Updated Successfully", android.widget.Toast.LENGTH_SHORT).show();

        // Navigate Back
        androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
    }
}
