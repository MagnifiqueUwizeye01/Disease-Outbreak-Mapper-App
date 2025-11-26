package com.healthmapper.chwapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.healthmapper.chwapp.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        createWorldClassUI();
        simulateLogin();
    }

    private void createWorldClassUI() {
        // Hide action bar for immersive design
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set status bar color
        getWindow().setStatusBarColor(Color.parseColor("#1B5E20"));

        // Main scroll container with premium background
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#FAFBFC"));

        // Main container
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);

        // Premium header section
        LinearLayout headerSection = createPremiumHeader();

        // User profile section
        LinearLayout userSection = createUserProfileSection();

        // Quick metrics dashboard
        LinearLayout metricsSection = createQuickMetrics();

        // Main navigation grid
        LinearLayout navigationSection = createNavigationGrid();

        // Recent activity section
        LinearLayout recentSection = createRecentActivity();

        // Footer section
        LinearLayout footerSection = createFooterSection();

        // Assemble the premium layout
        mainContainer.addView(headerSection);
        mainContainer.addView(userSection);
        mainContainer.addView(metricsSection);
        mainContainer.addView(navigationSection);
        mainContainer.addView(recentSection);
        mainContainer.addView(footerSection);

        scrollView.addView(mainContainer);
        setContentView(scrollView);
    }

    private LinearLayout createPremiumHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(0, 40, 0, 0);

        // Premium gradient background
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        Color.parseColor("#1B5E20"),
                        Color.parseColor("#2E7D32"),
                        Color.parseColor("#388E3C")
                }
        );
        header.setBackground(gradient);

        // Header content container
        LinearLayout headerContent = new LinearLayout(this);
        headerContent.setOrientation(LinearLayout.HORIZONTAL);
        headerContent.setPadding(25, 30, 25, 25);
        headerContent.setWeightSum(10);

        // Logo/Icon section
        LinearLayout logoSection = new LinearLayout(this);
        logoSection.setOrientation(LinearLayout.VERTICAL);
        logoSection.setGravity(Gravity.CENTER);

        // Create app icon
        TextView appIcon = new TextView(this);
        appIcon.setText("🏥");
        appIcon.setTextSize(32);
        appIcon.setGravity(Gravity.CENTER);

        // App title section
        LinearLayout titleSection = new LinearLayout(this);
        titleSection.setOrientation(LinearLayout.VERTICAL);
        titleSection.setPadding(20, 0, 0, 0);

        TextView appTitle = new TextView(this);
        appTitle.setText("CHW Reporter");
        appTitle.setTextColor(Color.WHITE);
        appTitle.setTextSize(24);
        appTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView appSubtitle = new TextView(this);
        appSubtitle.setText("Disease Surveillance Platform");
        appSubtitle.setTextColor(Color.parseColor("#C8E6C9"));
        appSubtitle.setTextSize(13);

        TextView versionText = new TextView(this);
        versionText.setText("v2.1.0 • Ministry of Health");
        versionText.setTextColor(Color.parseColor("#A5D6A7"));
        versionText.setTextSize(11);

        titleSection.addView(appTitle);
        titleSection.addView(appSubtitle);
        titleSection.addView(versionText);

        logoSection.addView(appIcon);

        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 8);

        headerContent.addView(logoSection, logoParams);
        headerContent.addView(titleSection, titleParams);

        header.addView(headerContent);

        return header;
    }

    private LinearLayout createUserProfileSection() {
        LinearLayout userContainer = new LinearLayout(this);
        userContainer.setOrientation(LinearLayout.VERTICAL);
        userContainer.setPadding(20, 0, 20, 0);

        // User card with shadow effect
        LinearLayout userCard = new LinearLayout(this);
        userCard.setOrientation(LinearLayout.HORIZONTAL);
        userCard.setPadding(25, 25, 25, 25);

        // Premium card styling
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(20);
        cardBg.setStroke(1, Color.parseColor("#E8F5E8"));
        userCard.setBackground(cardBg);

        // Profile avatar
        LinearLayout avatarContainer = new LinearLayout(this);
        avatarContainer.setOrientation(LinearLayout.VERTICAL);
        avatarContainer.setGravity(Gravity.CENTER);

        TextView avatar = new TextView(this);
        avatar.setText("PB");
        avatar.setTextSize(20);
        avatar.setTextColor(Color.WHITE);
        avatar.setTypeface(null, android.graphics.Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        avatar.setPadding(20, 20, 20, 20);

        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setColor(Color.parseColor("#FF6B35"));
        avatarBg.setCornerRadius(35);
        avatar.setBackground(avatarBg);

        avatarContainer.addView(avatar);

        // User info section
        LinearLayout userInfo = new LinearLayout(this);
        userInfo.setOrientation(LinearLayout.VERTICAL);
        userInfo.setPadding(20, 5, 0, 0);

        TextView userName = new TextView(this);
        userName.setText("Prince Bimenyimana");
        userName.setTextSize(20);
        userName.setTextColor(Color.parseColor("#1A1A1A"));
        userName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView userRole = new TextView(this);
        userRole.setText("Senior Community Health Worker");
        userRole.setTextSize(14);
        userRole.setTextColor(Color.parseColor("#2E7D32"));
        userRole.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView userDetails = new TextView(this);
        userDetails.setText("ID: CHW001 • Kigali Health District\nCertified • Active Status");
        userDetails.setTextSize(12);
        userDetails.setTextColor(Color.parseColor("#666666"));
        userDetails.setLineSpacing(6, 1);

        // Status indicator
        TextView statusIndicator = new TextView(this);
        statusIndicator.setText("🟢 Online");
        statusIndicator.setTextSize(12);
        statusIndicator.setTextColor(Color.parseColor("#4CAF50"));
        statusIndicator.setTypeface(null, android.graphics.Typeface.BOLD);

        userInfo.addView(userName);
        userInfo.addView(userRole);
        userInfo.addView(userDetails);
        userInfo.addView(statusIndicator);

        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        userCard.addView(avatarContainer, avatarParams);
        userCard.addView(userInfo, infoParams);

        // Add subtle margin for shadow effect
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, -15, 0, 25);

        userContainer.addView(userCard, cardParams);

        return userContainer;
    }

    private LinearLayout createQuickMetrics() {
        LinearLayout metricsContainer = new LinearLayout(this);
        metricsContainer.setOrientation(LinearLayout.VERTICAL);
        metricsContainer.setPadding(20, 0, 20, 25);

        // Section title
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("📊 Today's Overview");
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        sectionTitle.setTextColor(Color.parseColor("#2C2C2C"));
        sectionTitle.setPadding(5, 0, 0, 15);

        // Metrics row
        LinearLayout metricsRow = new LinearLayout(this);
        metricsRow.setOrientation(LinearLayout.HORIZONTAL);
        metricsRow.setWeightSum(3);

        LinearLayout metric1 = createMetricCard("7", "Reports\nSubmitted", "#4CAF50", "📝");
        LinearLayout metric2 = createMetricCard("12", "Patients\nScreened", "#FF9800", "👥");
        LinearLayout metric3 = createMetricCard("3", "Urgent\nCases", "#F44336", "⚠️");

        LinearLayout.LayoutParams metricParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        metricParams.setMargins(5, 0, 5, 0);

        metricsRow.addView(metric1, metricParams);
        metricsRow.addView(metric2, metricParams);
        metricsRow.addView(metric3, metricParams);

        metricsContainer.addView(sectionTitle);
        metricsContainer.addView(metricsRow);

        return metricsContainer;
    }

    private LinearLayout createMetricCard(String value, String label, String color, String icon) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(15, 20, 15, 20);
        card.setGravity(Gravity.CENTER);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(15);
        cardBg.setStroke(2, Color.parseColor(color));
        card.setBackground(cardBg);

        TextView iconText = new TextView(this);
        iconText.setText(icon);
        iconText.setTextSize(24);
        iconText.setGravity(Gravity.CENTER);

        TextView valueText = new TextView(this);
        valueText.setText(value);
        valueText.setTextSize(24);
        valueText.setTypeface(null, android.graphics.Typeface.BOLD);
        valueText.setTextColor(Color.parseColor(color));
        valueText.setGravity(Gravity.CENTER);

        TextView labelText = new TextView(this);
        labelText.setText(label);
        labelText.setTextSize(11);
        labelText.setTextColor(Color.parseColor("#666666"));
        labelText.setGravity(Gravity.CENTER);
        labelText.setLineSpacing(4, 1);

        card.addView(iconText);
        card.addView(valueText);
        card.addView(labelText);

        return card;
    }

    private LinearLayout createNavigationGrid() {
        LinearLayout navContainer = new LinearLayout(this);
        navContainer.setOrientation(LinearLayout.VERTICAL);
        navContainer.setPadding(20, 10, 20, 25);

        // Section title
        TextView navTitle = new TextView(this);
        navTitle.setText("🚀 Quick Actions");
        navTitle.setTextSize(18);
        navTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        navTitle.setTextColor(Color.parseColor("#2C2C2C"));
        navTitle.setPadding(5, 0, 0, 20);

        // Navigation grid - 2x2
        LinearLayout navRow1 = new LinearLayout(this);
        navRow1.setOrientation(LinearLayout.HORIZONTAL);
        navRow1.setWeightSum(2);

        LinearLayout navRow2 = new LinearLayout(this);
        navRow2.setOrientation(LinearLayout.HORIZONTAL);
        navRow2.setWeightSum(2);

        // Create premium navigation cards
        Button profileCard = createPremiumNavCard("👤", "My Profile", "Manage information", "#6366F1");
        Button historyCard = createPremiumNavCard("📋", "Reports History", "View all reports", "#EC4899");
        Button newReportCard = createPremiumNavCard("🏥", "New Report", "Submit new case", "#10B981");
        Button analyticsCard = createPremiumNavCard("📈", "Analytics", "View insights", "#F59E0B");

        profileCard.setOnClickListener(v -> openProfile());
        historyCard.setOnClickListener(v -> openHistory());
        newReportCard.setOnClickListener(v -> Toast.makeText(this, "New Report - Coming Soon", Toast.LENGTH_SHORT).show());
        analyticsCard.setOnClickListener(v -> Toast.makeText(this, "Analytics Dashboard - Coming Soon", Toast.LENGTH_SHORT).show());

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        cardParams.setMargins(8, 0, 8, 16);

        navRow1.addView(profileCard, cardParams);
        navRow1.addView(historyCard, cardParams);
        navRow2.addView(newReportCard, cardParams);
        navRow2.addView(analyticsCard, cardParams);

        navContainer.addView(navTitle);
        navContainer.addView(navRow1);
        navContainer.addView(navRow2);

        return navContainer;
    }

    private Button createPremiumNavCard(String icon, String title, String subtitle, String color) {
        Button card = new Button(this);
        card.setText(icon + "\n\n" + title + "\n" + subtitle);
        card.setTextColor(Color.WHITE);
        card.setTextSize(14);
        card.setGravity(Gravity.CENTER);
        card.setPadding(20, 30, 20, 30);
        card.setAllCaps(false);
        card.setMinHeight(140);

        // Premium gradient background
        GradientDrawable cardBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor(color), adjustBrightness(Color.parseColor(color), 0.8f)}
        );
        cardBg.setCornerRadius(16);
        card.setBackground(cardBg);

        return card;
    }

    private LinearLayout createRecentActivity() {
        LinearLayout activityContainer = new LinearLayout(this);
        activityContainer.setOrientation(LinearLayout.VERTICAL);
        activityContainer.setPadding(20, 0, 20, 25);

        TextView activityTitle = new TextView(this);
        activityTitle.setText("🕐 Recent Activity");
        activityTitle.setTextSize(18);
        activityTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        activityTitle.setTextColor(Color.parseColor("#2C2C2C"));
        activityTitle.setPadding(5, 0, 0, 15);

        // Activity items
        LinearLayout activityList = new LinearLayout(this);
        activityList.setOrientation(LinearLayout.VERTICAL);

        LinearLayout activity1 = createActivityItem("Cholera case reported", "2 hours ago", "🦠");
        LinearLayout activity2 = createActivityItem("Profile updated successfully", "1 day ago", "✅");
        LinearLayout activity3 = createActivityItem("Training completed", "3 days ago", "🎓");

        activityList.addView(activity1);
        activityList.addView(activity2);
        activityList.addView(activity3);

        activityContainer.addView(activityTitle);
        activityContainer.addView(activityList);

        return activityContainer;
    }

    private LinearLayout createActivityItem(String text, String time, String icon) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(15, 15, 15, 15);
        item.setGravity(Gravity.CENTER_VERTICAL);

        GradientDrawable itemBg = new GradientDrawable();
        itemBg.setColor(Color.WHITE);
        itemBg.setCornerRadius(12);
        itemBg.setStroke(1, Color.parseColor("#F0F0F0"));
        item.setBackground(itemBg);

        TextView iconText = new TextView(this);
        iconText.setText(icon);
        iconText.setTextSize(20);
        iconText.setPadding(0, 0, 15, 0);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);

        TextView titleText = new TextView(this);
        titleText.setText(text);
        titleText.setTextSize(14);
        titleText.setTextColor(Color.parseColor("#333333"));

        TextView timeText = new TextView(this);
        timeText.setText(time);
        timeText.setTextSize(12);
        timeText.setTextColor(Color.parseColor("#888888"));

        textContainer.addView(titleText);
        textContainer.addView(timeText);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        item.addView(iconText);
        item.addView(textContainer, textParams);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMargins(0, 0, 0, 10);

        return item;
    }

    private LinearLayout createFooterSection() {
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setPadding(20, 20, 20, 40);
        footer.setGravity(Gravity.CENTER);

        TextView footerText = new TextView(this);
        footerText.setText("Rwanda Ministry of Health\nGeospatial Disease Outbreak Mapper\n© 2024 All Rights Reserved");
        footerText.setTextSize(11);
        footerText.setTextColor(Color.parseColor("#999999"));
        footerText.setGravity(Gravity.CENTER);
        footerText.setLineSpacing(4, 1);

        footer.addView(footerText);

        return footer;
    }

    // Utility method to adjust color brightness
    private int adjustBrightness(int color, float factor) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        red = (int) (red * factor);
        green = (int) (green * factor);
        blue = (int) (blue * factor);

        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return Color.rgb(red, green, blue);
    }

    private void simulateLogin() {
        preferenceManager.setCurrentCHWId("CHW001");
        preferenceManager.setCurrentCHWName("Prince Bimenyimana");
        preferenceManager.setLoggedIn(true);
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
