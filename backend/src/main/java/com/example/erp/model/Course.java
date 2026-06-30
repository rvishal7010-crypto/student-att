package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Academic Courses.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String code;
    private int departmentId;

    public Course() {}

    public Course(int id, String name, String code, int departmentId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.departmentId = departmentId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               departmentId > 0;
    }
}
