package com.example.healthtracker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.healthtracker.models.CaseHistory;
import com.example.healthtracker.R;
import java.util.ArrayList;
import java.util.List;

public class CaseHistoryFragment extends Fragment {

    private LinearLayout containerCaseHistory;
    private List<CaseHistory> caseHistoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case_history, container, false);

        containerCaseHistory = view.findViewById(R.id.containerCaseHistory);

        // Sample data
        caseHistoryList = new ArrayList<>();
        caseHistoryList.add(new CaseHistory("C001", "John Doe", "2025-12-18"));
        caseHistoryList.add(new CaseHistory("C002", "Jane Smith", "2025-12-17"));

        // Dynamically add each case as TextView
        for (CaseHistory caseHistory : caseHistoryList) {
            TextView tv = new TextView(getContext());
            tv.setText(caseHistory.getCaseId() + " - " + caseHistory.getPatientName() + " - " + caseHistory.getDateReported());
            tv.setTextSize(16f);
            tv.setPadding(0, 8, 0, 8);
            containerCaseHistory.addView(tv);
        }

        return view;
    }
}
