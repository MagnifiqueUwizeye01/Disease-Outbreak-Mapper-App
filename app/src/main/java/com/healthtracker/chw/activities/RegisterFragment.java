package com.healthtracker.chw.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.healthtracker.chw.R;
import com.healthtracker.chw.services.AuthService;
import com.healthtracker.chw.utils.SessionManager;

public class RegisterFragment extends Fragment {

    private TextInputEditText etFullName;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etEmail;
    private TextInputEditText etChwCode;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    
    private AuthService authService;
    private SessionManager sessionManager;
    
    // For Google Sign-In completion
    private String googleUid = null;
    private String googleEmail = null;
    private String googleName = null;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_register_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize services
        authService = new AuthService(requireContext());
        sessionManager = new SessionManager(requireContext());
        
        initializeViews(view);
        setupClickListeners();
        
        // Check if coming from Google Sign-In
        Bundle args = getArguments();
        if (args != null) {
            googleUid = args.getString("googleUid");
            googleEmail = args.getString("email");
            googleName = args.getString("name");
            if (googleEmail != null && etEmail != null) {
                etEmail.setText(googleEmail);
                etEmail.setEnabled(false); // Can't change email from Google account
            }
            if (googleName != null && etFullName != null) {
                etFullName.setText(googleName);
            }
        }
    }

    private void initializeViews(View view) {
        etFullName = view.findViewById(R.id.et_full_name);
        etPhoneNumber = view.findViewById(R.id.et_phone_number);
        etEmail = view.findViewById(R.id.et_email);
        etChwCode = view.findViewById(R.id.et_chw_code);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        tvLogin = view.findViewById(R.id.tv_login);
    }

    private void setupClickListeners() {
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> handleRegister());
        }

        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void handleRegister() {
        // Get input values
        String fullName = etFullName != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhoneNumber != null ? etPhoneNumber.getText().toString().trim() : "";
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String chwCode = etChwCode != null ? etChwCode.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString() : "";

        // Validate inputs
        if (!validateInputs(fullName, phone, email, chwCode, password, confirmPassword)) {
            return;
        }

        // Disable button during registration
        if (btnRegister != null) {
            btnRegister.setEnabled(false);
            btnRegister.setText("Registering...");
        }

        // Check if this is Google Sign-In completion
        if (googleUid != null && !googleUid.isEmpty()) {
            // Complete Google registration
            authService.completeGoogleRegistration(googleUid, email, fullName, phone, chwCode,
                new AuthService.RegistrationCallback() {
                    @Override
                    public void onSuccess(String userId, String userEmail, String name, String chwCode, String phone) {
                        // Don't save session - user needs to login
                        runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                "Registration successful! Please login to continue.", 
                                Toast.LENGTH_LONG).show();
                            navigateToLogin();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            if (btnRegister != null) {
                                btnRegister.setEnabled(true);
                                btnRegister.setText("Register");
                            }
                            Toast.makeText(requireContext(), "Registration failed: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
        } else {
            // Regular email/password registration
            authService.register(email, password, fullName, phone, chwCode,
                new AuthService.RegistrationCallback() {
                    @Override
                    public void onSuccess(String userId, String userEmail, String name, String chwCode, String phone) {
                        // Don't save session - user needs to login
                        runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                "Registration successful! Please login to continue.", 
                                Toast.LENGTH_LONG).show();
                            navigateToLogin();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            if (btnRegister != null) {
                                btnRegister.setEnabled(true);
                                btnRegister.setText("Register");
                            }
                            String errorMessage = formatErrorMessage(error);
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                });
        }
    }

    private boolean validateInputs(String fullName, String phone, String email, 
                                  String chwCode, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            if (etFullName != null) {
                etFullName.setError("Full name is required");
            }
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            if (etPhoneNumber != null) {
                etPhoneNumber.setError("Phone number is required");
            }
            isValid = false;
        } else if (!isValidPhoneNumber(phone)) {
            if (etPhoneNumber != null) {
                etPhoneNumber.setError("Please enter a valid phone number");
            }
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            if (etEmail != null) {
                etEmail.setError("Email is required");
            }
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (etEmail != null) {
                etEmail.setError("Invalid email address");
            }
            isValid = false;
        }

        if (TextUtils.isEmpty(chwCode)) {
            if (etChwCode != null) {
                etChwCode.setError("CHW Code is required");
            }
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            if (etPassword != null) {
                etPassword.setError("Password is required");
            }
            isValid = false;
        } else if (password.length() < 6) {
            if (etPassword != null) {
                etPassword.setError("Password must be at least 6 characters");
            }
            isValid = false;
        } else if (!isStrongPassword(password)) {
            if (etPassword != null) {
                etPassword.setError("Password should contain at least one letter and one number");
            }
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            if (etConfirmPassword != null) {
                etConfirmPassword.setError("Please confirm your password");
            }
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            if (etConfirmPassword != null) {
                etConfirmPassword.setError("Passwords do not match");
            }
            isValid = false;
        }

        return isValid;
    }

    private void navigateToMainActivity() {
        if (getActivity() != null) {
            android.content.Intent intent = new android.content.Intent(getActivity(), 
                com.healthtracker.chw.activities.MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void navigateToLogin() {
        if (getActivity() != null) {
            android.content.Intent intent = new android.content.Intent(getActivity(), 
                com.healthtracker.chw.activities.LoginActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void runOnUiThread(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }

    private String formatErrorMessage(String error) {
        if (error == null || error.isEmpty()) {
            return "Registration failed. Please try again.";
        }
        
        String lowerError = error.toLowerCase();
        
        if (lowerError.contains("invalid") && lowerError.contains("chw code")) {
            return "Invalid or inactive CHW Code. Please check your code and try again.";
        } else if (lowerError.contains("email-already-in-use")) {
            return "An account with this email already exists. Please login instead.";
        } else if (lowerError.contains("weak-password")) {
            return "Password is too weak. Please use a stronger password.";
        } else if (lowerError.contains("invalid-email")) {
            return "Invalid email address format.";
        } else if (lowerError.contains("network")) {
            return "Network error. Please check your internet connection.";
        }
        
        return error;
    }

    /**
     * Validate phone number format
     * Accepts international format with or without +, digits only, 10-15 digits
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // Remove spaces, dashes, and parentheses
        String cleaned = phone.replaceAll("[\\s\\-\\(\\)]", "");
        // Remove leading + if present
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }
        // Check if it's all digits and has reasonable length (10-15 digits)
        return cleaned.matches("\\d{10,15}");
    }

    /**
     * Check if password is strong enough
     * At least 6 characters, contains at least one letter and one number
     */
    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        return hasLetter && hasNumber;
    }
}