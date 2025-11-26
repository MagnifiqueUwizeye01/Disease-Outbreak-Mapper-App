package com.healthmapper.chwapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.healthmapper.chwapp.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        setupUI();

        // Simulate CHW login
        simulateLogin();
    }

    private void setupUI() {
        setTitle("CHW Disease Reporter");

        // Create simple programmatic layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);

        // Welcome text
        TextView welcomeText = new TextView(this);
        welcomeText.setText("Welcome to CHW Disease Reporter\n\nCommunity Health Worker Portal");
        welcomeText.setTextSize(18);
        welcomeText.setPadding(0, 0, 0, 50);

        // Profile button
        Button profileBtn = new Button(this);
        profileBtn.setText("CHW Profile");
        profileBtn.setOnClickListener(v -> openProfile());
        profileBtn.setPadding(0, 20, 0, 20);

        // History button
        Button historyBtn = new Button(this);
        historyBtn.setText("Reports History");
        historyBtn.setOnClickListener(v -> openHistory());
        historyBtn.setPadding(0, 20, 0, 20);

        // Add views to layout
        mainLayout.addView(welcomeText);
        mainLayout.addView(profileBtn);
        mainLayout.addView(historyBtn);

        setContentView(mainLayout);
    }

    private void simulateLogin() {
        // Simulate CHW login for demo purposes
        preferenceManager.setCurrentCHWId("CHW001");
        preferenceManager.setCurrentCHWName("Louis Uwizeyimana");
        preferenceManager.setLoggedIn(true);

        Toast.makeText(this, "Logged in as: " + preferenceManager.getCurrentCHWName(), Toast.LENGTH_SHORT).show();
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}