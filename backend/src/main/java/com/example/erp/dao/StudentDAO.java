package com.example.erp.dao;

import com.example.erp.model.Student;
import com.example.erp.model.UserLogin;
import com.example.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing complete CRUD operations for Student Profiles.
 * Uses transactional logic to keep UserLogin credentials and Student records perfectly in sync.
 */
public class StudentDAO {

    /**
     * Creates a Student along with their UserLogin record inside a database transaction.
     * @param student Student object to register
     * @param initialPassword Default password for the student portal
     * @return boolean True if registration was successful, false otherwise
     */
    public boolean insertStudent(Student student, String initialPassword) {
        String insertLoginSql = "INSERT INTO UserLogin (username, password, role, is_active) VALUES (?, ?, 'STUDENT', 1)";
        String insertStudentSql = "INSERT INTO Student (login_id, roll_no, name, email, phone, department_id, course_id, semester_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement loginStmt = null;
        PreparedStatement studentStmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction
            
            // 1. Create Portal Login Credentials
            loginStmt = conn.prepareStatement(insertLoginSql, PreparedStatement.RETURN_GENERATED_KEYS);
            loginStmt.setString(1, student.getRollNo().toLowerCase()); // Username is roll number in lowercase
            loginStmt.setString(2, initialPassword);
            
            int loginRows = loginStmt.executeUpdate();
            if (loginRows == 0) {
                throw new SQLException("Creating portal user login failed, no rows affected.");
            }
            
            int loginId = -1;
            try (ResultSet rs = loginStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    loginId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating user login failed, no generated ID retrieved.");
                }
            }
            
            // 2. Insert Student Profile with the linked loginId
            studentStmt = conn.prepareStatement(insertStudentSql);
            studentStmt.setInt(1, loginId);
            studentStmt.setString(2, student.getRollNo());
            studentStmt.setString(3, student.getName());
            studentStmt.setString(4, student.getEmail());
            studentStmt.setString(5, student.getPhone());
            studentStmt.setInt(6, student.getDepartmentId());
            studentStmt.setInt(7, student.getCourseId());
            studentStmt.setInt(8, student.getSemesterId());
            
            int studentRows = studentStmt.executeUpdate();
            if (studentRows == 0) {
                throw new SQLException("Registering student profile failed, no rows affected.");
            }
            
            conn.commit(); // Commit Transaction if both successful
            return true;
            
        } catch (SQLException e) {
            System.err.println("Transaction Failed! Rolling back database changes: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (loginStmt != null) loginStmt.close();
                if (studentStmt != null) studentStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves all registered students.
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Student ORDER BY name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Filters and retrieves students based on search keywords (matches Name, Roll Number, or Email).
     */
    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Student WHERE name LIKE ? OR roll_no LIKE ? OR email LIKE ? ORDER BY name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String wildCard = "%" + keyword + "%";
            stmt.setString(1, wildCard);
            stmt.setString(2, wildCard);
            stmt.setString(3, wildCard);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Retrieves a single student by their ID.
     */
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM Student WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a single student by their linked login credentials ID.
     */
    public Student getStudentByLoginId(int loginId) {
        String sql = "SELECT * FROM Student WHERE login_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loginId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing student record in the database.
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE Student SET name = ?, email = ?, phone = ?, department_id = ?, course_id = ?, semester_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setInt(4, student.getDepartmentId());
            stmt.setInt(5, student.getCourseId());
            stmt.setInt(6, student.getSemesterId());
            stmt.setInt(7, student.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a student and cascadingly removes their related portal login as well.
     */
    public boolean deleteStudent(int id) {
        Student student = getStudentById(id);
        if (student == null) return false;
        
        // Deleting the UserLogin will automatically CASCADE delete the Student row in MySQL due to ON DELETE CASCADE
        String sql = "DELETE FROM UserLogin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getLoginId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setLoginId(rs.getInt("login_id"));
        student.setRollNo(rs.getString("roll_no"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDepartmentId(rs.getInt("department_id"));
        student.setCourseId(rs.getInt("course_id"));
        student.setSemesterId(rs.getInt("semester_id"));
        return student;
    }
}
