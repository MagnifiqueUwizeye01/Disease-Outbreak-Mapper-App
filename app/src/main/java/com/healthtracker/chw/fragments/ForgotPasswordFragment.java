package com.healthtracker.chw.fragments;

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

public class ForgotPasswordFragment extends Fragment {

    private TextInputEditText etEmail;
    private MaterialButton btnResetPassword;
    
    private AuthService authService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        authService = new AuthService(requireContext());
        
        initializeViews(view);
        setupClickListeners();
    }

    private void initializeViews(View view) {
        etEmail = view.findViewById(R.id.et_email);
        btnResetPassword = view.findViewById(R.id.btn_reset_password);
    }

    private void setupClickListeners() {
        View view = getView();
        if (view == null) return;
        
        if (btnResetPassword != null) {
            btnResetPassword.setOnClickListener(v -> handlePasswordReset());
        }
        
        // Back button
        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
        
        // Back to login text
        TextView tvBackToLogin = view.findViewById(R.id.tv_back_to_login);
        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void handlePasswordReset() {
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";

        // Validate email
        if (TextUtils.isEmpty(email)) {
            if (etEmail != null) {
                etEmail.setError("Email is required");
            }
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (etEmail != null) {
                etEmail.setError("Invalid email address");
            }
            return;
        }

        // Disable button
        if (btnResetPassword != null) {
            btnResetPassword.setEnabled(false);
            btnResetPassword.setText("Sending...");
        }

        // Send password reset email
        authService.resetPassword(email, new AuthService.PasswordResetCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "Password reset email sent! Please check your inbox.", 
                            Toast.LENGTH_LONG).show();
                        
                        // Go back to login
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (btnResetPassword != null) {
                            btnResetPassword.setEnabled(true);
                            btnResetPassword.setText("Send Reset Link");
                        }
                        
                        String errorMessage = formatErrorMessage(error);
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private String formatErrorMessage(String error) {
        if (error == null || error.isEmpty()) {
            return "Failed to send reset email. Please try again.";
        }
        
        String lowerError = error.toLowerCase();
        
        if (lowerError.contains("user-not-found")) {
            return "No account found with this email address.";
        } else if (lowerError.contains("invalid-email")) {
            return "Invalid email address format.";
        } else if (lowerError.contains("network")) {
            return "Network error. Please check your internet connection.";
        }
        
        return "Error: " + error;
    }
}
