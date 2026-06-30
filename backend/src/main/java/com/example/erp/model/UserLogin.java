package com.example.erp.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing User Credentials and Roles for the College ERP portal.
 */
public class UserLogin implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password;
    private String role; // ADMIN, STAFF, STUDENT
    private boolean isActive;
    private Timestamp createdAt;

    public UserLogin() {}

    public UserLogin(int id, String username, String password, String role, boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Minimal input validations for user credentials.
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               password != null && password.trim().length() >= 6 &&
               role != null && (role.equals("ADMIN") || role.equals("STAFF") || role.equals("STUDENT"));
    }
}
