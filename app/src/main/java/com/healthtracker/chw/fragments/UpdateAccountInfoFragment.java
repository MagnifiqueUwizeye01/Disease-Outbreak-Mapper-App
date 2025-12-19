package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

public class UpdateAccountInfoFragment extends Fragment {

    private com.healthtracker.chw.utils.SessionManager sessionManager;
    private com.google.android.material.textfield.TextInputEditText etName, etPhone, etEmail, etFacility, etRegion;
    private android.widget.TextView tvChwCode;
    private android.view.View btnSave, tvCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new com.healthtracker.chw.utils.SessionManager(requireContext());

        // Bind Views
        etName = view.findViewById(R.id.et_full_name);
        etPhone = view.findViewById(R.id.et_phone_number);
        etEmail = view.findViewById(R.id.et_email);
        etFacility = view.findViewById(R.id.et_facility);
        etRegion = view.findViewById(R.id.et_region);
        tvChwCode = view.findViewById(R.id.tv_chw_code);
        btnSave = view.findViewById(R.id.btn_save_account);
        tvCancel = view.findViewById(R.id.tv_cancel);

        // Populate Data
        String currentName = sessionManager.getUserName();
        String currentEmail = sessionManager.getUserEmail();
        String currentPhone = sessionManager.getPhone();
        String currentCode = sessionManager.getChwCode();

        if (etName != null)
            etName.setText(currentName);
        if (etEmail != null)
            etEmail.setText(currentEmail);
        if (etPhone != null)
            etPhone.setText(currentPhone);
        if (tvChwCode != null)
            tvChwCode.setText(currentCode);

        // Facility and Region are not currently in SessionManager, leaving blank or
        // placeholders
        if (etFacility != null)
            etFacility.setText("Kicukiro Health Center"); // Example default
        if (etRegion != null)
            etRegion.setText("Kigali City");

        // Save Listener
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveChanges());
        }

        // Cancel Listener
        if (tvCancel != null) {
            tvCancel.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(view).navigateUp());
        }
    }

    private void saveChanges() {
        String newName = etName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        if (newName.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        // Update Session
        // Note: We need the User ID to call saveSession.
        String userId = sessionManager.getUserId();
        String chwCode = sessionManager.getChwCode();

        sessionManager.saveSession(userId, newEmail, newName, chwCode, newPhone);

        android.widget.Toast
                .makeText(requireContext(), "Profile Updated Successfully", android.widget.Toast.LENGTH_SHORT).show();

        // Navigate Back
        androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
    }
}
