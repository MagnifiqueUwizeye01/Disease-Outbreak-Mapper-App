package com.healthmapper.chwapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.healthmapper.chwapp.data.entities.CHW;
import com.healthmapper.chwapp.utils.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileInitials;
    private EditText etFullName, etPhone, etEmail, etNationalId, etChwCode;
    private Button btnEditProfile, btnSaveProfile, btnCancelEdit, btnLogout;

    private CHW currentCHW;
    private boolean isEditMode = false;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            preferenceManager = new PreferenceManager(this);
            createSimpleUI();
            loadProfile();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void createSimpleUI() {
        setTitle("CHW Profile - Prince Bimenyimana");

        // Main scroll container
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#FAFAFA"));

        // Main container
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(30, 30, 30, 30);

        // Profile header
        LinearLayout headerSection = createHeader();

        // Form section
        LinearLayout formSection = createForm();

        // Buttons section
        LinearLayout buttonsSection = createButtons();

        mainContainer.addView(headerSection);
        mainContainer.addView(formSection);
        mainContainer.addView(buttonsSection);

        scrollView.addView(mainContainer);
        setContentView(scrollView);
    }

    private LinearLayout createHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setPadding(20, 40, 20, 40);
        header.setBackgroundColor(Color.parseColor("#2E7D32"));

        // Profile avatar
        tvProfileInitials = new TextView(this);
        tvProfileInitials.setText("PB");
        tvProfileInitials.setTextSize(36);
        tvProfileInitials.setTextColor(Color.WHITE);
        tvProfileInitials.setGravity(Gravity.CENTER);
        tvProfileInitials.setPadding(30, 30, 30, 30);
        tvProfileInitials.setBackgroundColor(Color.parseColor("#FF6B35"));

        // User info
        TextView userName = new TextView(this);
        userName.setText("Prince Bimenyimana");
        userName.setTextColor(Color.WHITE);
        userName.setTextSize(24);
        userName.setGravity(Gravity.CENTER);
        userName.setPadding(0, 20, 0, 0);

        TextView userRole = new TextView(this);
        userRole.setText("Senior Community Health Worker");
        userRole.setTextColor(Color.parseColor("#C8E6C9"));
        userRole.setTextSize(16);
        userRole.setGravity(Gravity.CENTER);

        header.addView(tvProfileInitials);
        header.addView(userName);
        header.addView(userRole);

        return header;
    }

    private LinearLayout createForm() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(20, 30, 20, 30);
        form.setBackgroundColor(Color.WHITE);

        // Form title
        TextView title = new TextView(this);
        title.setText("Personal Information");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#2E7D32"));
        title.setPadding(0, 0, 0, 20);

        // Create form fields
        etFullName = createField("Full Name");
        etPhone = createField("Phone Number");
        etEmail = createField("Email Address");
        etNationalId = createField("National ID");
        etChwCode = createField("CHW Code");

        // Make read-only fields
        etNationalId.setEnabled(false);
        etChwCode.setEnabled(false);
        etNationalId.setBackgroundColor(Color.parseColor("#F0F0F0"));
        etChwCode.setBackgroundColor(Color.parseColor("#F0F0F0"));

        form.addView(title);
        form.addView(etFullName);
        form.addView(etPhone);
        form.addView(etEmail);
        form.addView(etNationalId);
        form.addView(etChwCode);

        return form;
    }

    private EditText createField(String hint) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setPadding(20, 15, 20, 15);
        field.setTextSize(16);
        field.setEnabled(false); // Initially disabled

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 15);
        field.setLayoutParams(params);

        return field;
    }

    private LinearLayout createButtons() {
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.VERTICAL);
        buttons.setPadding(20, 20, 20, 20);

        // Edit/Save/Cancel row
        LinearLayout editRow = new LinearLayout(this);
        editRow.setOrientation(LinearLayout.HORIZONTAL);

        btnEditProfile = createButton("Edit Profile", "#2196F3");
        btnSaveProfile = createButton("Save Changes", "#4CAF50");
        btnCancelEdit = createButton("Cancel", "#9E9E9E");

        btnSaveProfile.setEnabled(false);
        btnCancelEdit.setEnabled(false);

        btnEditProfile.setOnClickListener(v -> enableEditMode());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnCancelEdit.setOnClickListener(v -> disableEditMode());

        // Set equal weights
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        buttonParams.setMargins(5, 0, 5, 0);

        editRow.addView(btnEditProfile, buttonParams);
        editRow.addView(btnSaveProfile, buttonParams);
        editRow.addView(btnCancelEdit, buttonParams);

        // Logout button
        btnLogout = createButton("Logout", "#F44336");
        btnLogout.setOnClickListener(v -> logout());

        LinearLayout.LayoutParams logoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        logoutParams.setMargins(5, 20, 5, 0);

        buttons.addView(editRow);
        buttons.addView(btnLogout, logoutParams);

        return buttons;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.parseColor(color));
        button.setAllCaps(false);
        button.setPadding(15, 15, 15, 15);
        return button;
    }

    private void loadProfile() {
        // Create sample profile
        currentCHW = new CHW(
                "CHW001",
                "Prince Bimenyimana",
                "+250788567890",
                "prince.bimenyimana@health.rw",
                "1199880012345678",
                "CHW001",
                "password"
        );

        populateProfile(currentCHW);
        Toast.makeText(this, "Profile loaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void populateProfile(CHW chw) {
        if (chw == null) return;

        etFullName.setText(chw.getFullName());
        etPhone.setText(chw.getPhoneNumber());
        etEmail.setText(chw.getEmail());
        etNationalId.setText(chw.getNationalId());
        etChwCode.setText(chw.getChwCode());
    }

    private void enableEditMode() {
        isEditMode = true;

        // Enable fields
        etFullName.setEnabled(true);
        etPhone.setEnabled(true);
        etEmail.setEnabled(true);

        // Update buttons
        btnEditProfile.setEnabled(false);
        btnSaveProfile.setEnabled(true);
        btnCancelEdit.setEnabled(true);

        Toast.makeText(this, "Edit mode enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableEditMode() {
        isEditMode = false;

        // Disable fields
        etFullName.setEnabled(false);
        etPhone.setEnabled(false);
        etEmail.setEnabled(false);

        // Update buttons
        btnEditProfile.setEnabled(true);
        btnSaveProfile.setEnabled(false);
        btnCancelEdit.setEnabled(false);

        // Reload data
        populateProfile(currentCHW);

        Toast.makeText(this, "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }

    private void saveProfile() {
        try {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "Phone is required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update profile
            currentCHW.setFullName(fullName);
            currentCHW.setPhoneNumber(phone);
            currentCHW.setEmail(email);

            // Save to preferences
            preferenceManager.setCurrentCHWName(fullName);

            disableEditMode();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void logout() {
        try {
            preferenceManager.clearSession();
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Logout error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}