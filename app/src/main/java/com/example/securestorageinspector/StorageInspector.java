package com.example.securestorageinspector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enterprise-Grade Security Audit & Storage Explorer Engine.
 */
public class StorageInspector {

    private static final String TAG = "SecuritySDK";
    private final Context context;
    private final List<Finding> findings = new ArrayList<>();
    private long lastScanDuration = 0;

    // Advanced Detection Patterns (Sensitive Data Classifier)
    private static final Pattern JWT_PATTERN = Pattern.compile("eyJ[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*");
    private static final Pattern GENERIC_SECRET = Pattern.compile("(?i)(password|secret|api_key|auth_token|access_token|private_key|key)[\"']?\\s*[:=]\\s*[\"']?([^\"'\\s,]{4,})");
    private static final Pattern PII_EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern HEALTH_DATA = Pattern.compile("(?i)(heart_rate|blood_pressure|glucose|weight|height|bmi)\\s*[:=]\\s*\\d+");

    public StorageInspector(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    public List<Finding> performFullAudit() {
        long startTime = System.currentTimeMillis();
        findings.clear();

        auditManifestPermissions();
        auditStorageEncryption();
        auditDatabaseSecurity();
        scanInternalFilesystem();
        scanSystemWideApps();

        lastScanDuration = System.currentTimeMillis() - startTime;
        return new ArrayList<>(findings);
    }

    public long getLastScanDuration() { return lastScanDuration; }

    // --- EXPLORER MODULES ---

    public List<String> getSharedPreferencesFiles() {
        List<String> fileList = new ArrayList<>();
        File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        if (prefsDir.exists() && prefsDir.isDirectory()) {
            File[] files = prefsDir.listFiles();
            if (files != null) {
                for (File f : files) if (f.getName().endsWith(".xml")) fileList.add(f.getName().replace(".xml", ""));
            }
        }
        return fileList;
    }

    public int getPrefsCount() {
        return getSharedPreferencesFiles().size();
    }

    public List<String> getDatabaseFiles() {
        List<String> dbList = new ArrayList<>();
        String[] dbs = context.databaseList();
        if (dbs != null) {
            for (String db : dbs) {
                if (!db.endsWith("-journal") && !db.endsWith("-wal") && !db.endsWith("-shm")) dbList.add(db);
            }
        }
        return dbList;
    }

    public int getDbCount() {
        return getDatabaseFiles().size();
    }

    public List<File> getInternalFilesList() {
        List<File> fileList = new ArrayList<>();
        File filesDir = context.getFilesDir();
        if (filesDir.exists()) {
            File[] files = filesDir.listFiles();
            if (files != null) {
                for (File f : files) fileList.add(f);
            }
        }
        return fileList;
    }

    public int getFilesCount() {
        return getInternalFilesList().size();
    }

    public long getCacheSizeRaw() {
        return getFolderSize(context.getCacheDir());
    }

    public String getCacheSize() {
        return getFormattedCacheSize();
    }

    public String getFormattedCacheSize() {
        long size = getCacheSizeRaw();
        if (size <= 0) return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new java.text.DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private long getFolderSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) for (File f : files) size += getFolderSize(f);
        } else {
            size = file.length();
        }
        return size;
    }

    // --- AUDIT LOGIC ---

    private void scanSystemWideApps() {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo pkg : packages) {
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;
            if (pkg.packageName.equals(context.getPackageName())) continue;
            String label = pm.getApplicationLabel(pkg.applicationInfo).toString();
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                addFinding("Critical Vulnerability: " + label, "App is DEBUGGABLE.", Finding.Severity.CRITICAL, Finding.Category.MANIFEST, "Contact dev to disable debug mode.");
            }
        }
    }

    private void auditManifestPermissions() {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0) {
                addFinding("Backup Risk", "android:allowBackup is TRUE.", Finding.Severity.WARNING, Finding.Category.MANIFEST, "Set allowBackup to false.");
            }
        } catch (Exception ignored) {}
    }

    private void auditStorageEncryption() {
        if (!getSharedPreferencesFiles().isEmpty()) {
            addFinding("Unencrypted Preferences", "Found plain XML preferences.", Finding.Severity.WARNING, Finding.Category.PREFERENCES, "Use EncryptedSharedPreferences.");
        }
    }

    private void auditDatabaseSecurity() {
        if (!getDatabaseFiles().isEmpty()) {
            addFinding("Unencrypted Database", "SQLite databases are not encrypted at rest.", Finding.Severity.WARNING, Finding.Category.DATABASE, "Use SQLCipher with Room.");
        }
    }

    private void scanInternalFilesystem() {
        scanDir(context.getFilesDir());
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
        if (file.getName().endsWith(".db") || file.length() > 500000) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (JWT_PATTERN.matcher(line).find()) addFinding("JWT Detected", "Found token in " + file.getName(), Finding.Severity.CRITICAL, Finding.Category.FILESYSTEM, "Use Keystore.");
                if (GENERIC_SECRET.matcher(line).find()) addFinding("Secret Key Detected", "API Key in " + file.getName(), Finding.Severity.CRITICAL, Finding.Category.FILESYSTEM, "Obfuscate keys.");
                if (HEALTH_DATA.matcher(line).find()) addFinding("Health Data Exposure", "Unencrypted medical data in " + file.getName(), Finding.Severity.WARNING, Finding.Category.FILESYSTEM, "Encrypt health records.");
            }
        } catch (IOException ignored) {}
    }

    private void addFinding(String title, String desc, Finding.Severity sev, Finding.Category cat, String rec) {
        findings.add(new Finding(title, desc, sev, cat, rec));
    }
}
