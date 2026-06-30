package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Administrator Profiles.
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int loginId;
    private String name;
    private String email;
    private String phone;

    public Admin() {}

    public Admin(int id, int loginId, String name, String email, String phone) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               email != null && email.contains("@") &&
               loginId > 0;
    }
}
