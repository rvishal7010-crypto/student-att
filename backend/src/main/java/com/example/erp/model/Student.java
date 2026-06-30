package com.example.erp.model;

import java.io.Serializable;

/**
 * Model representing College Student Profiles.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int loginId;
    private String rollNo;
    private String name;
    private String email;
    private String phone;
    private int departmentId;
    private int courseId;
    private int semesterId;

    public Student() {}

    public Student(int id, int loginId, String rollNo, String name, String email, String phone, int departmentId, int courseId, int semesterId) {
        this.id = id;
        this.loginId = loginId;
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
        this.courseId = courseId;
        this.semesterId = semesterId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getSemesterId() { return semesterId; }
    public void setSemesterId(int semesterId) { this.semesterId = semesterId; }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               rollNo != null && !rollNo.trim().isEmpty() &&
               email != null && email.contains("@") &&
               departmentId > 0 && courseId > 0 && semesterId > 0;
    }
}
