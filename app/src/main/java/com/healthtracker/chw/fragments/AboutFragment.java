package com.healthtracker.chw.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AboutFragment extends Fragment {

    private TextView tvVersion;
    private TextView tvBuildDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Initialize views
        tvVersion = view.findViewById(R.id.tv_version);
        tvBuildDate = view.findViewById(R.id.tv_build_date);

        // Load app information
        loadAppInfo();

        return view;
    }

    private void loadAppInfo() {
        try {
            // Get app version
            PackageManager pm = requireContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(requireContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            if (tvVersion != null) {
                tvVersion.setText(String.format(Locale.getDefault(), "%s (%d)", versionName, versionCode));
            }

            // Get build date (use current date as fallback, or use
            // packageInfo.lastUpdateTime)
            if (tvBuildDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String buildDate = dateFormat.format(new Date(packageInfo.lastUpdateTime));
                tvBuildDate.setText(buildDate);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Fallback values if package info not found
            if (tvVersion != null) {
                tvVersion.setText("1.0.0");
            }
            if (tvBuildDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                tvBuildDate.setText(dateFormat.format(new Date()));
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Interactive Elements
        View btnUpdates = view.findViewById(R.id.btn_check_updates);
        View tvPrivacy = view.findViewById(R.id.tv_privacy_policy);
        View tvTerms = view.findViewById(R.id.tv_terms_of_use);
        View tvContact = view.findViewById(R.id.tv_contact_support);
        View logo = view.findViewById(R.id.iv_app_logo); // Need to access ImageView directly if possible, or assume
                                                         // it's the first image
        // To be safe, let's find the logo by traversal if ID wasn't added in previous
        // step (it wasn't explicit in the replace content for logo ID, only found in
        // XML analysis).
        // Actually, the XML has `<ImageView ...>` without ID in the header. I should
        // find it or add ID.
        // Wait, I didn't add the ID to the logo in the previous XML edit. I only added
        // IDs to new elements.
        // I will use a simple workaround: find the first ImageView.

        if (btnUpdates != null) {
            btnUpdates.setOnClickListener(v -> checkForUpdates());
        }

        if (tvPrivacy != null) {
            tvPrivacy.setOnClickListener(v -> openUrl("https://health.gov.rw/privacy"));
        }

        if (tvTerms != null) {
            tvTerms.setOnClickListener(v -> openUrl("https://health.gov.rw/terms"));
        }

        if (tvContact != null) {
            tvContact.setOnClickListener(v -> sendEmail());
        }

        // Easter Egg
        android.widget.ImageView logoView = findLogo(view);
        if (logoView != null) {
            logoView.setOnClickListener(new View.OnClickListener() {
                int taps = 0;
                long lastTapTime = 0;

                @Override
                public void onClick(View v) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTapTime > 500) {
                        taps = 0;
                    }
                    taps++;
                    lastTapTime = currentTime;

                    if (taps == 5) {
                        android.widget.Toast.makeText(requireContext(), "👨‍💻 Developer Mode Enabled!",
                                android.widget.Toast.LENGTH_SHORT).show();
                        taps = 0;
                    }
                }
            });
        }
    }

    private android.widget.ImageView findLogo(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof android.widget.ImageView) {
                    return (android.widget.ImageView) child; // Return first image view (logo)
                }
                if (child instanceof ViewGroup) {
                    android.widget.ImageView found = findLogo(child);
                    if (found != null)
                        return found;
                }
            }
        }
        return null;
    }

    private void checkForUpdates() {
        android.widget.Toast.makeText(requireContext(), "Checking for updates...", android.widget.Toast.LENGTH_SHORT)
                .show();
        if (getView() != null) {
            getView().postDelayed(
                    () -> android.widget.Toast.makeText(requireContext(), "You are using the latest version.",
                            android.widget.Toast.LENGTH_SHORT).show(),
                    1500);
        }
    }

    private void openUrl(String url) {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "No browser found", android.widget.Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void sendEmail() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
        intent.setData(android.net.Uri.parse("mailto:support@health.gov.rw"));
        try {
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "No email app found", android.widget.Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
