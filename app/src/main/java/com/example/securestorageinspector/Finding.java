package com.example.securestorageinspector;

import androidx.annotation.NonNull;

/**
 * Represents a security finding discovered during the inspection.
 */
public class Finding {
    public enum Severity {
        CRITICAL, WARNING, INFO
    }

    public enum Category {
        MANIFEST, PREFERENCES, DATABASE, FILESYSTEM, LOGS
    }

    private final String title;
    private final String description;
    private final Severity severity;
    private final Category category;
    private final String recommendation;

    public Finding(@NonNull String title, @NonNull String description, @NonNull Severity severity, @NonNull Category category, @NonNull String recommendation) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.category = category;
        this.recommendation = recommendation;
    }

    @NonNull
    public String getTitle() { return title; }

    @NonNull
    public String getDescription() { return description; }

    @NonNull
    public Severity getSeverity() { return severity; }

    @NonNull
    public Category getCategory() { return category; }

    @NonNull
    public String getRecommendation() { return recommendation; }

    @Override
    @NonNull
    public String toString() {
        return String.format("[%s] %s: %s\nRecommendation: %s\n", 
                severity, title, description, recommendation);
    }
}
