package com.healthmapper.chwapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.healthmapper.chwapp.data.entities.CHW;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileInitials;
    private EditText etFullName, etPhone, etEmail, etNationalId, etChwCode;
    private Button btnEditProfile, btnSaveProfile, btnCancelEdit, btnLogout;

    private CHW currentCHW;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("CHW Profile");
        setupUI();
        loadProfile();
    }

    private void setupUI() {
        // For now, we'll work without XML layout
        // Later we can add proper UI when layout issues are resolved

        // Initialize sample data
        loadProfile();
    }

    private void loadProfile() {
        // Create sample CHW profile for testing
        currentCHW = new CHW(
                "CHW001",
                "Louis Uwizeyimana",
                "+250788123456",
                "louis.uwizeyimana@health.rw",
                "1199880012345678",
                "CHW001",
                "securePassword123"
        );

        populateProfile(currentCHW);
        showProfileData();
    }

    private void populateProfile(CHW chw) {
        if (chw == null) return;

        // Generate initials
        String initials = getInitials(chw.getFullName());

        // Store profile data - will bind to UI components when layout is ready
        Toast.makeText(this, "Profile loaded: " + chw.getFullName(), Toast.LENGTH_SHORT).show();
    }

    private void showProfileData() {
        if (currentCHW == null) return;

        // Display profile information in a simple way for now
        String profileInfo = "CHW Profile:\n\n" +
                "Name: " + currentCHW.getFullName() + "\n" +
                "Phone: " + currentCHW.getPhoneNumber() + "\n" +
                "Email: " + currentCHW.getEmail() + "\n" +
                "National ID: " + currentCHW.getNationalId() + "\n" +
                "CHW Code: " + currentCHW.getChwCode();

        // For demonstration - show in Toast (later will be in proper UI)
        Toast.makeText(this, "Profile data ready for UI binding", Toast.LENGTH_LONG).show();
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "CHW";

        String[] names = fullName.split(" ");
        StringBuilder initials = new StringBuilder();

        for (int i = 0; i < Math.min(names.length, 2); i++) {
            if (!names[i].isEmpty()) {
                initials.append(names[i].charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }

    public void enableEditMode() {
        isEditMode = true;
        Toast.makeText(this, "Edit mode enabled - ready for UI implementation", Toast.LENGTH_SHORT).show();
    }

    public void disableEditMode() {
        isEditMode = false;
        Toast.makeText(this, "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }

    public void saveProfile() {
        if (currentCHW == null) return;

        // Validate and save profile changes
        // TODO: Integrate with database when Room is fully configured
        Toast.makeText(this, "Profile saved successfully (demo mode)", Toast.LENGTH_SHORT).show();

        disableEditMode();
    }

    public void logout() {
        // Clear session and return to login
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Method to demonstrate profile update functionality
    public void updateProfile(String name, String phone, String email) {
        if (currentCHW != null) {
            currentCHW.setFullName(name);
            currentCHW.setPhoneNumber(phone);
            currentCHW.setEmail(email);

            populateProfile(currentCHW);
        }
    }

    // Getter for testing
    public CHW getCurrentCHW() {
        return currentCHW;
    }

    public boolean isInEditMode() {
        return isEditMode;
    }
}