package com.example.healthtracker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.healthtracker.models.OfflineCase;
import com.example.healthtracker.R;
import java.util.ArrayList;
import java.util.List;

public class OfflinePendingCasesFragment extends Fragment {

    private LinearLayout containerOfflineCases;
    private List<OfflineCase> offlineCaseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_pending_cases, container, false);

        containerOfflineCases = view.findViewById(R.id.containerOfflineCases);

        // Sample data (replace with real offline cases)
        offlineCaseList = new ArrayList<>();
        offlineCaseList.add(new OfflineCase("C001", "John Doe", "Pending"));
        offlineCaseList.add(new OfflineCase("C002", "Jane Smith", "Pending"));

        // Dynamically add each offline case as a TextView
        for (OfflineCase offlineCase : offlineCaseList) {
            TextView tv = new TextView(getContext());
            tv.setText(offlineCase.getCaseId() + " - " + offlineCase.getPatientName() + " - " + offlineCase.getStatus());
            tv.setTextSize(16f);
            tv.setPadding(0, 8, 0, 8);
            containerOfflineCases.addView(tv);
        }

        return view;
    }
}
