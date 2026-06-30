package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Departments.
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String code;

    public Department() {}

    public Department(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() && code.trim().length() <= 10;
    }
}
