package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

public class HelpSupportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Views
        View cardGuide = view.findViewById(R.id.card_user_guide);
        View cardFaq = view.findViewById(R.id.card_faqs);
        View cardContact = view.findViewById(R.id.card_contact_admin);
        View cardTraining = view.findViewById(R.id.card_training);
        View btnEmergency = view.findViewById(R.id.btn_emergency_contact);

        // Set Listeners
        if (cardGuide != null) {
            cardGuide.setOnClickListener(v -> showUserGuideDialog());
        }

        if (cardFaq != null) {
            cardFaq.setOnClickListener(v -> showFaqDialog());
        }

        if (cardContact != null) {
            cardContact.setOnClickListener(v -> showContactDialog());
        }

        if (cardTraining != null) {
            cardTraining.setOnClickListener(v -> showTrainingDialog());
        }

        if (btnEmergency != null) {
            btnEmergency.setOnClickListener(v -> dialEmergency());
        }
    }

    private void showUserGuideDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("User Guide")
                .setMessage("1. **Offline Mode**: Use the switch in the Dashboard to stop syncing.\n\n" +
                        "2. **Submitting Reports**: Fill out the form. If offline, it saves locally. If online, it sends immediately.\n\n"
                        +
                        "3. **Syncing**: When back online, tap 'Sync Now' on the dashboard to upload pending reports.\n\n"
                        +
                        "4. **Map**: Use the map to see high-risk areas. Red pin = High Risk.")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showFaqDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Frequently Asked Questions")
                .setMessage("**Q: How do I change my password?**\nA: Go to Profile -> Change Password.\n\n" +
                        "**Q: What if I have no internet?**\nA: The app works offline! Reports are saved and synced later.\n\n"
                        +
                        "**Q: Who sees my reports?**\nA: Only authorized medical supervisors and admins.")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showContactDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Contact Support")
                .setItems(new String[] { "Email Support", "Call Admin" }, (dialog, which) -> {
                    if (which == 0) {
                        sendEmail();
                    } else {
                        dialAdmin();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTrainingDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Training Resources")
                .setMessage("The following resources are available on the server:\n\n" +
                        "- Introduction to CHW App (Video)\n" +
                        "- Reporting Protocol 2024 (PDF)\n" +
                        "- Emergency Response Guidelines (PDF)\n\n" +
                        "*Connect to Wi-Fi to download.*")
                .setPositiveButton("OK", null)
                .show();
    }

    private void dialEmergency() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
        intent.setData(android.net.Uri.parse("tel:112"));
        try {
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "Cannot make calls", android.widget.Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void sendEmail() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
        intent.setData(android.net.Uri.parse("mailto:support@healthtrack.com"));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Support Request");
        try {
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "No email app found", android.widget.Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void dialAdmin() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
        intent.setData(android.net.Uri.parse("tel:+250788123456"));
        try {
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "Cannot make calls", android.widget.Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
