package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Academic and Administrative Staff / Lecturers.
 */
public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int loginId;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private int departmentId;
    private String designation;

    public Staff() {}

    public Staff(int id, int loginId, String employeeId, String name, String email, String phone, int departmentId, String designation) {
        this.id = id;
        this.loginId = loginId;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
        this.designation = designation;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               employeeId != null && !employeeId.trim().isEmpty() &&
               email != null && email.contains("@") &&
               departmentId > 0 && designation != null && !designation.trim().isEmpty();
    }
}
