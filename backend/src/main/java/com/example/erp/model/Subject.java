package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Academic Subjects.
 */
public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String code;
    private int courseId;
    private int semesterId;

    public Subject() {}

    public Subject(int id, String name, String code, int courseId, int semesterId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.courseId = courseId;
        this.semesterId = semesterId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getSemesterId() { return semesterId; }
    public void setSemesterId(int semesterId) { this.semesterId = semesterId; }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               courseId > 0 && semesterId > 0;
    }
}
