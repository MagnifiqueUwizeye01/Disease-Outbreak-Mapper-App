package com.healthtracker.chw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.healthtracker.chw.R;
import com.healthtracker.chw.activities.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        // Set up login button
        View btnLogin = findViewById(R.id.btn_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> navigateToMainActivity());
        }

        // Set up register text click
        TextView tvRegister = findViewById(R.id.tv_register);
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> navigateToRegisterFragment());
        }
    }

    public void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close login activity so user can't go back with back button
    }

    public void navigateToRegisterFragment() {
        RegisterFragment fragment = new RegisterFragment();
        replaceFragment(fragment, "RegisterFragment");
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }
}