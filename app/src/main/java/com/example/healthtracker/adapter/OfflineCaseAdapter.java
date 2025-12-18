package com.example.healthtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.healthtracker.models.OfflineCase;
import com.example.healthtracker.R;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class OfflineCaseAdapter extends RecyclerView.Adapter<OfflineCaseAdapter.ViewHolder> {

    private Context context;
    private List<OfflineCase> offlineCaseList;

    public OfflineCaseAdapter(Context context, List<OfflineCase> offlineCaseList) {
        this.context = context;
        this.offlineCaseList = offlineCaseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offline_cases, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OfflineCase offlineCase = offlineCaseList.get(position);
        holder.tvCaseId.setText(offlineCase.getCaseId());
        holder.tvPatientName.setText(offlineCase.getPatientName());
        holder.tvStatus.setText(offlineCase.getStatus());
    }

    @Override
    public int getItemCount() {
        return offlineCaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCaseId, tvPatientName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaseId = itemView.findViewById(R.id.tvCaseId);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}