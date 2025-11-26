package com.healthtracker.chw;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("Welcome to HealthTracker!\n\nDashboard Screen\n• Disease Reporting\n• Outbreak Maps\n• Analytics\n\nThis will be implemented by dashboard team member.");
        textView.setTextSize(18);
        textView.setPadding(50, 50, 50, 50);
        textView.setTextColor(getResources().getColor(android.R.color.black));

        setContentView(textView);
    }
}