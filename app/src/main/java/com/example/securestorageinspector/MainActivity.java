package com.example.securestorageinspector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.securestorageinspector.databinding.ActivityMainBinding;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Main Activity for Secure Inspector.
 * High-end UI implementation with state management, scan analytics, and report exporting.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FindingsAdapter adapter;
    private final List<Finding> findingsList = new ArrayList<>();
    private StorageInspector inspector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setupUI();
        
        inspector = new StorageInspector(this);
        
        // Populate dummy vulnerabilities for demonstration
        // simulateVulnerabilities();
    }

    private void setupUI() {
        binding.recyclerViewFindings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FindingsAdapter(findingsList);
        binding.recyclerViewFindings.setAdapter(adapter);

        binding.btnInspect.setOnClickListener(v -> startSecurityScan());
        
        binding.btnExport.setOnClickListener(v -> exportSecurityReport());
    }

    private void startSecurityScan() {
        // UI State: Scanning
        binding.btnInspect.setEnabled(false);
        binding.btnExport.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewFindings.setAlpha(0.5f);
        binding.tvDetailedLabel.setVisibility(View.GONE);
        binding.tvScanTime.setVisibility(View.GONE);

        // Professional simulation of scanning process
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            performInspection();
            
            // UI State: Finished
            binding.progressBar.setVisibility(View.GONE);
            binding.btnInspect.setEnabled(true);
            binding.recyclerViewFindings.setAlpha(1.0f);
            
            if (!findingsList.isEmpty()) {
                binding.tvDetailedLabel.setVisibility(View.VISIBLE);
                binding.recyclerViewFindings.setVisibility(View.VISIBLE);
                binding.btnExport.setVisibility(View.VISIBLE);
                
                // Show scan duration
                String durationMsg = String.format(Locale.getDefault(), "Scan: %dms", inspector.getLastScanDuration());
                binding.tvScanTime.setText(durationMsg);
                binding.tvScanTime.setVisibility(View.VISIBLE);
            }
        }, 1200);
    }

    private void performInspection() {
        findingsList.clear();
        List<Finding> results = inspector.performFullAudit();
        findingsList.addAll(results);
        
        adapter.notifyDataSetChanged();

        if (results.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.msg_no_vulnerabilities, Toast.LENGTH_LONG).show();
            resetDashboard();
        } else {
            binding.emptyState.setVisibility(View.GONE);
            updateDashboard(results);
        }
    }

    private void updateDashboard(List<Finding> results) {
        int critical = 0;
        int warning = 0;
        int info = 0;

        for (Finding f : results) {
            switch (f.getSeverity()) {
                case CRITICAL: critical++; break;
                case WARNING: warning++; break;
                case INFO: info++; break;
            }
        }

        binding.tvCriticalCount.setText(String.valueOf(critical));
        binding.tvWarningCount.setText(String.valueOf(warning));
        binding.tvInfoCount.setText(String.valueOf(info));
        
        String message = String.format(Locale.getDefault(), 
                getString(R.string.fmt_summary_toast), results.size(), critical);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetDashboard() {
        binding.tvCriticalCount.setText("0");
        binding.tvWarningCount.setText("0");
        binding.tvInfoCount.setText("0");
    }

    private void exportSecurityReport() {
        StringBuilder report = new StringBuilder();
        report.append("--- SECURITY AUDIT REPORT ---\n");
        report.append("App: ").append(getString(R.string.app_name)).append("\n");
        report.append("Findings: ").append(findingsList.size()).append("\n\n");
        
        for (Finding f : findingsList) {
            report.append(f.toString()).append("\n");
        }
        
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, report.toString());
        sendIntent.setType("text/plain");
        
        Intent shareIntent = Intent.createChooser(sendIntent, "Export Security Audit");
        startActivity(shareIntent);
    }

    private void simulateVulnerabilities() {
        // SharedPreferences avec données sensibles
        getSharedPreferences("auth_cache", MODE_PRIVATE)
                .edit()
                .putString("last_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.t-X9")
                .apply();

        // Fichier JSON avec clé API
        try (FileOutputStream fos = openFileOutput("secrets.json", MODE_PRIVATE)) {
            String data = "{\"firebase_key\": \"AIzaSyB_12345\", \"owner\": \"dev@secure.com\"}";
            fos.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
