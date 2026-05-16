package com.example.securestorageinspector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enterprise-Grade Security Audit SDK.
 * Now expanded to perform System-Wide audits of other installed applications.
 */
public class StorageInspector {

    private static final String TAG = "SecuritySDK";
    private final Context context;
    private final List<Finding> findings = new ArrayList<>();
    private long lastScanDuration = 0;

    // Advanced Detection Patterns
    private static final Pattern JWT_PATTERN = Pattern.compile("eyJ[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*");
    private static final Pattern GENERIC_SECRET = Pattern.compile("(?i)(password|secret|api_key|auth_token|access_token|private_key|sid|session_id|key)[\"']?\\s*[:=]\\s*[\"']?([^\"'\\s,]{4,})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    public StorageInspector(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Executes a comprehensive security audit including local storage and system-wide apps.
     * @return List of discovered vulnerabilities.
     */
    @NonNull
    public List<Finding> performFullAudit() {
        long startTime = System.currentTimeMillis();
        findings.clear();

        // 1. Audit this app's storage (Deep Scan)
        auditManifestPermissions();
        auditStorageEncryption();
        auditDatabaseSecurity();
        scanInternalFilesystem();

        // 2. Audit other installed apps (System Scan)
        scanSystemWideApps();

        lastScanDuration = System.currentTimeMillis() - startTime;
        return new ArrayList<>(findings);
    }

    public long getLastScanDuration() {
        return lastScanDuration;
    }

    /**
     * Scans all installed third-party applications for public security flaws.
     */
    private void scanSystemWideApps() {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo pkg : packages) {
            // Skip system apps to focus on user-installed apps
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;
            // Skip ourselves as we are already scanned
            if (pkg.packageName.equals(context.getPackageName())) continue;

            String appLabel = pm.getApplicationLabel(pkg.applicationInfo).toString();

            // A. Check for Debuggable Apps (Critical Security Flaw)
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                addFinding("External App Vulnerability", 
                    "App '" + appLabel + "' is DEBUGGABLE. It can be easily compromised or monitored.", 
                    Finding.Severity.CRITICAL, Finding.Category.MANIFEST, 
                    "Consider uninstalling this app as it poses a risk to the whole system.");
            }

            // B. Check for Backup exposure
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0) {
                addFinding("Data Extraction Risk", 
                    "App '" + appLabel + "' allows ADB backups. Its data can be stolen via USB.", 
                    Finding.Severity.WARNING, Finding.Category.MANIFEST, 
                    "Ensure your phone's ADB debugging is turned off when not in use.");
            }

            // C. Check for Dangerous Permission combinations
            if (pkg.requestedPermissions != null) {
                int score = 0;
                for (String p : pkg.requestedPermissions) {
                    if (p.contains("SMS")) score += 2;
                    if (p.contains("RECORD_AUDIO")) score += 2;
                    if (p.contains("LOCATION")) score += 1;
                    if (p.contains("READ_CONTACTS")) score += 1;
                }
                
                if (score >= 4) {
                    addFinding("High-Privilege App", 
                        "App '" + appLabel + "' has a high-risk permission profile (SMS/Audio/Loc).", 
                        Finding.Severity.INFO, Finding.Category.MANIFEST, 
                        "Verify if this app actually needs these permissions in Android Settings.");
                }
            }
        }
    }

    private void auditManifestPermissions() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            ApplicationInfo appInfo = packageInfo.applicationInfo;

            if ((appInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0) {
                addFinding("Backup Configuration Leak", 
                    "Application data can be extracted via ADB backup.", 
                    Finding.Severity.WARNING, Finding.Category.MANIFEST, 
                    "Set android:allowBackup=\"false\" in Manifest.");
            }

            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    if (permission.equals("android.permission.READ_EXTERNAL_STORAGE")) {
                        addFinding("Excessive Permission", 
                            "App requests broad storage access. Risk of PII exposure.", 
                            Finding.Severity.INFO, Finding.Category.MANIFEST, 
                            "Use Scoped Storage (MediaStore API) instead of broad permissions.");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Manifest audit failed", e);
        }
    }

    private void auditStorageEncryption() {
        File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        if (prefsDir.exists() && prefsDir.isDirectory()) {
            File[] files = prefsDir.listFiles();
            if (files != null && files.length > 0) {
                addFinding("Unencrypted Preferences Detected", 
                    "Found " + files.length + " unencrypted XML preference files.", 
                    Finding.Severity.WARNING, Finding.Category.PREFERENCES, 
                    "Replace PreferenceManager with EncryptedSharedPreferences (Jetpack Security).");
            }
        }
    }

    private void auditDatabaseSecurity() {
        String[] dbs = context.databaseList();
        if (dbs != null) {
            for (String db : dbs) {
                if (db.endsWith("-journal") || db.endsWith("-wal")) continue;
                addFinding("Plaintext Database", 
                    "Database '" + db + "' is stored without at-rest encryption.", 
                    Finding.Severity.CRITICAL, Finding.Category.DATABASE, 
                    "Implement SQLCipher to encrypt the SQLite database file.");
            }
        }
    }

    private void scanInternalFilesystem() {
        scanDir(context.getFilesDir());
        scanDir(context.getCacheDir());
    }

    private void scanDir(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) scanDir(f);
            else analyzeFile(f);
        }
    }

    private void analyzeFile(File file) {
        if (file.getName().endsWith(".db") || file.length() > 1024 * 500) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (JWT_PATTERN.matcher(line).find()) {
                    addFinding("Hardcoded JWT Token", "Found active session token in " + file.getName(), 
                        Finding.Severity.CRITICAL, Finding.Category.FILESYSTEM, "Never store raw tokens. Use Keystore.");
                }
                if (GENERIC_SECRET.matcher(line).find()) {
                    addFinding("Sensitive Secret Detected", "Possible API Key or Secret in " + file.getName(), 
                        Finding.Severity.WARNING, Finding.Category.FILESYSTEM, "Obfuscate secrets or move to secure backend.");
                }
            }
        } catch (IOException ignored) {}
    }

    private void addFinding(String title, String desc, Finding.Severity sev, Finding.Category cat, String rec) {
        findings.add(new Finding(title, desc, sev, cat, rec));
    }
}
