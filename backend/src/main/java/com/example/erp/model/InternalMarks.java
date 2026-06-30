package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing Internal Marks academic score statements.
 * Features CIA1, CIA2, Model Exam, Assignment, Lab, and Seminar components.
 * Automatically handles evaluation logic to calculate Total, Average, and Percentage.
 */
public class InternalMarks implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private int subjectId;
    
    // Core Components
    private double cia1;         // Max 50
    private double cia2;         // Max 50
    private double modelExam;    // Max 100
    private double assignment;   // Max 10
    private double lab;          // Max 50
    private double seminar;      // Max 20

    // Calculated fields
    private double total;
    private double average;
    private double percentage;

    // Maximum possible marks constant for percentage calculations
    public static final double MAX_POSSIBLE_MARKS = 280.0; // 50+50+100+10+50+20

    public InternalMarks() {}

    public InternalMarks(int id, int studentId, int subjectId, double cia1, double cia2, 
                         double modelExam, double assignment, double lab, double seminar) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.cia1 = cia1;
        this.cia2 = cia2;
        this.modelExam = modelExam;
        this.assignment = assignment;
        this.lab = lab;
        this.seminar = seminar;
        calculateScores();
    }

    /**
     * Automatically calculates Total, Average and Percentage based on current marks.
     */
    public final void calculateScores() {
        this.total = this.cia1 + this.cia2 + this.modelExam + this.assignment + this.lab + this.seminar;
        this.average = Math.round((this.total / 6.0) * 100.0) / 100.0;
        this.percentage = Math.round((this.total / MAX_POSSIBLE_MARKS * 100.0) * 100.0) / 100.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public double getCia1() { return cia1; }
    public void setCia1(double cia1) { this.cia1 = cia1; calculateScores(); }

    public double getCia2() { return cia2; }
    public void setCia2(double cia2) { this.cia2 = cia2; calculateScores(); }

    public double getModelExam() { return modelExam; }
    public void setModelExam(double modelExam) { this.modelExam = modelExam; calculateScores(); }

    public double getAssignment() { return assignment; }
    public void setAssignment(double assignment) { this.assignment = assignment; calculateScores(); }

    public double getLab() { return lab; }
    public void setLab(double lab) { this.lab = lab; calculateScores(); }

    public double getSeminar() { return seminar; }
    public void setSeminar(double seminar) { this.seminar = seminar; calculateScores(); }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isValid() {
        return studentId > 0 && subjectId > 0 &&
               cia1 >= 0 && cia1 <= 50 &&
               cia2 >= 0 && cia2 <= 50 &&
               modelExam >= 0 && modelExam <= 100 &&
               assignment >= 0 && assignment <= 10 &&
               lab >= 0 && lab <= 50 &&
               seminar >= 0 && seminar <= 20;
    }
}
