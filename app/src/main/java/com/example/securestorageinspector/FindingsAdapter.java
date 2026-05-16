package com.example.securestorageinspector;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.securestorageinspector.databinding.ItemFindingBinding;
import java.util.List;

/**
 * Adapter for displaying security findings with an ultra-professional UI.
 */
public class FindingsAdapter extends RecyclerView.Adapter<FindingsAdapter.ViewHolder> {

    private final List<Finding> findings;

    public FindingsAdapter(List<Finding> findings) {
        this.findings = findings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFindingBinding binding = ItemFindingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Finding finding = findings.get(position);
        holder.binding.tvTitle.setText(finding.getTitle());
        holder.binding.tvDescription.setText(finding.getDescription());
        holder.binding.tvRecommendation.setText(finding.getRecommendation());
        holder.binding.tvCategory.setText(finding.getCategory().name());
        
        // Severity Styling using professional color palette
        String severityName = finding.getSeverity().name();
        holder.binding.chipSeverity.setText(severityName);
        
        int colorRes;
        switch (finding.getSeverity()) {
            case CRITICAL:
                colorRes = R.color.severity_critical;
                break;
            case WARNING:
                colorRes = R.color.severity_warning;
                break;
            case INFO:
            default:
                colorRes = R.color.severity_info;
                break;
        }
        
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorRes);
        holder.binding.chipSeverity.setChipStrokeColor(ColorStateList.valueOf(color));
        holder.binding.chipSeverity.setTextColor(color);
        holder.binding.chipSeverity.setChipIconTint(ColorStateList.valueOf(color));
    }

    @Override
    public int getItemCount() {
        return findings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFindingBinding binding;

        ViewHolder(ItemFindingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
