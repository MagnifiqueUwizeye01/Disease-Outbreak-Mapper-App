package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

public class ProfileFragment extends Fragment {

    private com.healthtracker.chw.utils.SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new com.healthtracker.chw.utils.SessionManager(requireContext());

        // Bind Views
        android.widget.TextView tvName = view.findViewById(R.id.tv_profile_name);
        android.widget.TextView tvRole = view.findViewById(R.id.tv_profile_role);
        android.widget.TextView tvCode = view.findViewById(R.id.tv_profile_code);
        android.widget.TextView tvInitials = view.findViewById(R.id.tv_profile_initials);
        View btnLogout = view.findViewById(R.id.menu_logout);
        View btnEdit = view.findViewById(R.id.menu_edit_profile);
        View btnAbout = view.findViewById(R.id.menu_about);

        // Populate Data
        String name = sessionManager.getUserName();
        if (name == null || name.isEmpty())
            name = "Community Health Worker";

        String code = sessionManager.getChwCode();
        if (code == null || code.isEmpty())
            code = "CHW-UNKNOWN";

        tvName.setText(name);
        tvRole.setText("CHW - Field Officer"); // Static for now, or could store in session
        tvCode.setText(code);
        tvInitials.setText(getInitials(name));

        // Logout Listener
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Confirm Logout
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            sessionManager.clearSession();
                            // Navigate to Login (pop back stack to clear history)
                            try {
                                androidx.navigation.NavController navController = androidx.navigation.Navigation
                                        .findNavController(view);
                                // Clear back stack and navigate to login
                                androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                                        .setPopUpTo(R.id.nav_graph, true)
                                        .build();
                                navController.navigate(R.id.loginScreenFragment, null, navOptions);
                            } catch (Exception e) {
                                android.util.Log.e("ProfileFragment", "Logout navigation failed", e);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        // Edit Profile
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(view)
                    .navigate(R.id.updateAccountInfoFragment));
        }

        // Change Password
        View btnPassword = view.findViewById(R.id.menu_change_password);
        if (btnPassword != null) {
            btnPassword.setOnClickListener(
                    v -> androidx.navigation.Navigation.findNavController(view).navigate(R.id.changePasswordFragment));
        }

        // Help & Support
        View btnHelp = view.findViewById(R.id.menu_help_support);
        if (btnHelp != null) {
            btnHelp.setOnClickListener(
                    v -> androidx.navigation.Navigation.findNavController(view).navigate(R.id.helpSupportFragment));
        }

        // About
        if (btnAbout != null) {
            btnAbout.setOnClickListener(
                    v -> androidx.navigation.Navigation.findNavController(view).navigate(R.id.aboutFragment));
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty())
            return "CH";
        String[] parts = name.split("\\s+");
        StringBuilder init = new StringBuilder();
        if (parts.length > 0 && !parts[0].isEmpty())
            init.append(parts[0].charAt(0));
        if (parts.length > 1 && !parts[1].isEmpty())
            init.append(parts[1].charAt(0));
        return init.toString().toUpperCase();
    }
}
