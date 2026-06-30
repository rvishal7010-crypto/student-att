package com.example.erp.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing audit and analytic reports (including AI evaluation reports).
 */
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String description;
    private String generatedBy;
    private String reportType; // e.g., "ATTENDANCE_AGGREGATE", "GEMINI_AI_INTERVENTION"
    private Timestamp createdAt;

    public Report() {}

    public Report(int id, String title, String description, String generatedBy, String reportType, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.generatedBy = generatedBy;
        this.reportType = reportType;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isValid() {
        return title != null && !title.trim().isEmpty() &&
               generatedBy != null && !generatedBy.trim().isEmpty() &&
               reportType != null && !reportType.trim().isEmpty();
    }
}
