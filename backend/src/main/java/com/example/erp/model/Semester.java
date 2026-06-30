package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Semesters.
 */
public class Semester implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int number;
    private String academicYear;

    public Semester() {}

    public Semester(int id, int number, String academicYear) {
        this.id = id;
        this.number = number;
        this.academicYear = academicYear;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public boolean isValid() {
        return number >= 1 && number <= 8 &&
               academicYear != null && !academicYear.trim().isEmpty();
    }
}
