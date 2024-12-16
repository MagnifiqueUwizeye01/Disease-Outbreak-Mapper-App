package com.healthtracker.chw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.healthtracker.chw.R;
import com.healthtracker.chw.activities.RegisterFragment;
import com.healthtracker.chw.services.AuthService;
import com.healthtracker.chw.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnGoogleSignIn;
    private TextView tvRegister;
    private TextView tvForgotPassword;
    
    private AuthService authService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        // Initialize services
        authService = new AuthService(this);
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignIn = findViewById(R.id.btn_google_signin);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void setupClickListeners() {
        // Login button
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> handleLogin());
        }

        // Register link
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> navigateToRegisterFragment());
        }

        // Forgot password
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
        }

        // Google Sign-In button
        if (btnGoogleSignIn != null) {
            btnGoogleSignIn.setOnClickListener(v -> handleGoogleSignIn());
        }
    }

    private void handleLogin() {
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString() : "";

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            if (etEmail != null) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
            }
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (etEmail != null) {
                etEmail.setError("Please enter a valid email address");
                etEmail.requestFocus();
            }
            return;
        }

        if (TextUtils.isEmpty(password)) {
            if (etPassword != null) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
            }
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            if (etPassword != null) {
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
            }
            return;
        }

        // Disable button during login
        if (btnLogin != null) {
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");
        }

        // Perform login
        authService.login(email, password, new AuthService.LoginCallback() {
            @Override
            public void onSuccess(String userId, String userEmail, String name, String chwCode, String phone) {
                // Save session
                sessionManager.saveSession(userId, userEmail, name, chwCode, phone);
                
                // Navigate to main activity
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Re-enable button
                    if (btnLogin != null) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Sign In");
                    }
                    
                    // Show error
                    String errorMessage = formatErrorMessage(error);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void handleGoogleSignIn() {
        // Start Google Sign-In
        Intent signInIntent = authService.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 100) {
            com.google.android.gms.tasks.Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task = 
                com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            authService.handleGoogleSignIn(task, new AuthService.GoogleSignInCallback() {
                @Override
                public void onSuccess(String userId, String email, String name, String chwCode, String phone) {
                    // Save session
                    sessionManager.saveSession(userId, email, name, chwCode, phone);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    });
                }

                @Override
                public void onNeedsCHWCode(String uid, String email, String name) {
                    // New Google user - need to collect CHW code
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, 
                            "Please complete registration with your CHW Code", 
                            Toast.LENGTH_LONG).show();
                        // Navigate to registration with pre-filled data
                        navigateToRegisterFragmentWithGoogleData(uid, email, name);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close login activity so user can't go back with back button
    }

    private void navigateToRegisterFragment() {
        RegisterFragment fragment = new RegisterFragment();
        replaceFragment(fragment, "RegisterFragment");
    }

    private void navigateToRegisterFragmentWithGoogleData(String uid, String email, String name) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString("googleUid", uid);
        args.putString("email", email);
        args.putString("name", name);
        fragment.setArguments(args);
        replaceFragment(fragment, "RegisterFragment");
    }

    private void navigateToForgotPassword() {
        // Navigate to forgot password fragment
        com.healthtracker.chw.fragments.ForgotPasswordFragment fragment = 
            new com.healthtracker.chw.fragments.ForgotPasswordFragment();
        replaceFragment(fragment, "ForgotPasswordFragment");
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private String formatErrorMessage(String error) {
        if (error == null || error.isEmpty()) {
            return "An error occurred. Please try again.";
        }
        
        String lowerError = error.toLowerCase();
        
        if (lowerError.contains("invalid") && lowerError.contains("email")) {
            return "Invalid email address";
        } else if (lowerError.contains("password") && lowerError.contains("wrong")) {
            return "Incorrect password";
        } else if (lowerError.contains("user-not-found")) {
            return "No account found with this email";
        } else if (lowerError.contains("network")) {
            return "Network error. Please check your internet connection";
        }
        
        return error;
    }
}