package com.example.erp.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing circular Notifications distributed within the college hierarchy.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String message;
    private String senderRole; // ADMIN, STAFF, SYSTEM
    private String targetRole; // ALL, ADMIN, STAFF, STUDENT
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int id, String title, String message, String senderRole, String targetRole, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.senderRole = senderRole;
        this.targetRole = targetRole;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isValid() {
        return title != null && !title.trim().isEmpty() &&
               message != null && !message.trim().isEmpty() &&
               senderRole != null && !senderRole.trim().isEmpty() &&
               targetRole != null && !targetRole.trim().isEmpty();
    }
}
