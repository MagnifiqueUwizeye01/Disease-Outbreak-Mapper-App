package com.example.healthtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.healthtracker.models.CaseHistory; // You need a model class for CaseHistory
import com.example.healthtracker.R;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CaseHistoryAdapter extends RecyclerView.Adapter<CaseHistoryAdapter.ViewHolder> {

    private Context context;
    private List<CaseHistory> caseHistoryList;

    public CaseHistoryAdapter(Context context, List<CaseHistory> caseHistoryList) {
        this.context = context;
        this.caseHistoryList = caseHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_case_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CaseHistory caseHistory = caseHistoryList.get(position);
        holder.tvCaseId.setText(caseHistory.getCaseId());
        holder.tvPatientName.setText(caseHistory.getPatientName());
        holder.tvDate.setText(caseHistory.getDateReported());
    }

    @Override
    public int getItemCount() {
        return caseHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCaseId, tvPatientName, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaseId = itemView.findViewById(R.id.tvCaseId);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
