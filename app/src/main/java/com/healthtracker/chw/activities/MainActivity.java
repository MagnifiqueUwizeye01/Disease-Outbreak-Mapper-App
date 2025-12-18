package com.healthtracker.chw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.healthtracker.chw.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.healthtracker.chw.utils.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.dashboardFragment,
                R.id.mapFragment,
                R.id.analyticsFragment).setOpenableLayout(drawerLayout).build();

        if (navController != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Handle drawer menu item selection
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                // Close drawer first
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                // Handle logout separately
                if (item.getItemId() == R.id.nav_logout) {
                    handleLogout();
                    return true;
                }

                // Let NavigationUI handle other menu items
                if (navController != null) {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
                return false;
            });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;
        if (navController != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;

        if (navController != null) {
            if (id == R.id.action_profile) {
                navController.navigate(R.id.profileFragment);
                return true;
            } else if (id == R.id.action_notifications) {
                // For now route notifications to submission history for testing
                navController.navigate(R.id.caseHistoryFragment);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handle logout - show confirmation dialog before logging out
     */
    private void handleLogout() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Perform the actual logout - clear session and return to login
     */
    private void performLogout() {
        // Clear session
        com.healthtracker.chw.utils.SessionManager sessionManager = new com.healthtracker.chw.utils.SessionManager(
                this);
        sessionManager.clearSession();

        // Logout from Firebase
        com.healthtracker.chw.services.AuthService authService = new com.healthtracker.chw.services.AuthService(this);
        authService.logout();

        // Navigate to login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
