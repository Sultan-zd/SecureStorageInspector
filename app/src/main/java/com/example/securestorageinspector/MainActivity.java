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
 * Main Activity for Secure Storage Suite.
 * Combines advanced security auditing with a professional storage explorer.
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
        inspector = new StorageInspector(this);
        
        setupUI();
        refreshStorageStats();
        
        // Simulation for demonstration purposes
        simulateVulnerabilities();
    }

    private void setupUI() {
        binding.recyclerViewFindings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FindingsAdapter(findingsList);
        binding.recyclerViewFindings.setAdapter(adapter);

        // Security Deep Audit Button
        binding.btnInspect.setOnClickListener(v -> startSecurityScan());
        
        // Export Audit Report Button
        binding.btnExport.setOnClickListener(v -> exportSecurityReport());

        // Storage Explorer Cards (Navigating to modules)
        binding.cardPrefs.setOnClickListener(v -> showFeatureToast("SharedPrefs Explorer"));
        binding.cardDatabases.setOnClickListener(v -> showFeatureToast("Database Visualizer"));
        binding.cardFiles.setOnClickListener(v -> showFeatureToast("File Browser"));
        binding.cardCache.setOnClickListener(v -> showFeatureToast("Cache Manager"));
    }

    /**
     * Updates the storage explorer cards with real-time statistics.
     */
    private void refreshStorageStats() {
        binding.tvPrefsCount.setText(String.format(Locale.getDefault(), "%d files", inspector.getPrefsCount()));
        binding.tvDbCount.setText(String.format(Locale.getDefault(), "%d DBs", inspector.getDbCount()));
        binding.tvFilesCount.setText(String.format(Locale.getDefault(), "%d items", inspector.getFilesCount()));
        binding.tvCacheSize.setText(inspector.getCacheSize());
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

        // Simulate a professional processing delay for audit depth
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            performInspection();
            refreshStorageStats(); // Refresh stats after scan
            
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
        }, 1500);
    }

    private void performInspection() {
        findingsList.clear();
        List<Finding> results = inspector.performFullAudit();
        findingsList.addAll(results);
        
        adapter.notifyDataSetChanged();

        if (results.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.msg_no_vulnerabilities, Toast.LENGTH_LONG).show();
            resetSecurityDashboard();
        } else {
            binding.emptyState.setVisibility(View.GONE);
            updateSecurityDashboard(results);
        }
    }

    private void updateSecurityDashboard(List<Finding> results) {
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

    private void resetSecurityDashboard() {
        binding.tvCriticalCount.setText("0");
        binding.tvWarningCount.setText("0");
        binding.tvInfoCount.setText("0");
    }

    private void showFeatureToast(String feature) {
        Toast.makeText(this, feature + " module ready", Toast.LENGTH_SHORT).show();
    }

    private void exportSecurityReport() {
        StringBuilder report = new StringBuilder();
        report.append("--- SECURE STORAGE SUITE AUDIT REPORT ---\n");
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
        // SharedPrefs avec token JWT simulé
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .putString("access_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.t-X9")
                .apply();

        // Fichier config avec secret
        try (FileOutputStream fos = openFileOutput("secrets.json", MODE_PRIVATE)) {
            String data = "{\"api_key\": \"sk_live_professional_key_123\"}";
            fos.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
