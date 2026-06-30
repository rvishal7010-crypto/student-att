package com.example.erp.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Model representing Class Attendance sessions.
 */
public class Attendance implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private int subjectId;
    private Date date;
    private String status; // PRESENT, ABSENT
    private int markedBy; // Staff ID

    public Attendance() {}

    public Attendance(int id, int studentId, int subjectId, Date date, String status, int markedBy) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.date = date;
        this.status = status;
        this.markedBy = markedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMarkedBy() { return markedBy; }
    public void setMarkedBy(int markedBy) { this.markedBy = markedBy; }

    public boolean isValid() {
        return studentId > 0 && subjectId > 0 && date != null &&
               status != null && (status.equals("PRESENT") || status.equals("ABSENT")) &&
               markedBy > 0;
    }
}
