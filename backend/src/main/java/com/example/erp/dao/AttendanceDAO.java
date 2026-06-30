package com.example.erp.dao;

import com.example.erp.model.Attendance;
import com.example.erp.model.Student;
import com.example.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO implementing student checklist loading and transaction-safe, idempotent batch attendance marking.
 */
public class AttendanceDAO {

    /**
     * Loads the list of students registered under a specific course/subject and semester for attendance marking.
     */
    public List<Student> getStudentsForMarking(int subjectId) {
        List<Student> list = new ArrayList<>();
        // Find Course ID and Semester ID associated with this Subject
        String findSubjectDetailsSql = "SELECT course_id, semester_id FROM Subject WHERE id = ?";
        String findStudentsSql = "SELECT * FROM Student WHERE course_id = ? AND semester_id = ? ORDER BY roll_no ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement subStmt = conn.prepareStatement(findSubjectDetailsSql)) {
            
            subStmt.setInt(1, subjectId);
            int courseId = -1;
            int semesterId = -1;
            
            try (ResultSet rs = subStmt.executeQuery()) {
                if (rs.next()) {
                    courseId = rs.getInt("course_id");
                    semesterId = rs.getInt("semester_id");
                }
            }
            
            if (courseId != -1 && semesterId != -1) {
                try (PreparedStatement stuStmt = conn.prepareStatement(findStudentsSql)) {
                    stuStmt.setInt(1, courseId);
                    stuStmt.setInt(2, semesterId);
                    
                    try (ResultSet rs = stuStmt.executeQuery()) {
                        while (rs.next()) {
                            Student s = new Student();
                            s.setId(rs.getInt("id"));
                            s.setRollNo(rs.getString("roll_no"));
                            s.setName(rs.getString("name"));
                            s.setEmail(rs.getString("email"));
                            s.setPhone(rs.getString("phone"));
                            s.setDepartmentId(rs.getInt("department_id"));
                            s.setCourseId(rs.getInt("course_id"));
                            s.setSemesterId(rs.getInt("semester_id"));
                            list.add(s);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts or Updates a batch list of attendance records using an idempotent SQL transaction.
     * @param list List of Attendance records to register
     * @return boolean True if all operations completed successfully
     */
    public boolean saveAttendanceBatch(List<Attendance> list) {
        String sql = "INSERT INTO Attendance (student_id, subject_id, date, status, marked_by) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = ?, marked_by = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Enable transactional batch
            
            stmt = conn.prepareStatement(sql);
            for (Attendance record : list) {
                stmt.setInt(1, record.getStudentId());
                stmt.setInt(2, record.getSubjectId());
                stmt.setDate(3, record.getDate());
                stmt.setString(4, record.getStatus());
                stmt.setInt(5, record.getMarkedBy());
                
                // Fields for DUPLICATE KEY UPDATE
                stmt.setString(6, record.getStatus());
                stmt.setInt(7, record.getMarkedBy());
                
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            return results.length == list.size();
            
        } catch (SQLException e) {
            System.err.println("Batch Attendance submission failed, executing rollback: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves attendance percentage calculations grouped by subjects for a specific student.
     * Keys: Subject Code, Values: Map with "total", "present", "percentage"
     */
    public Map<String, Map<String, Object>> getStudentAttendanceStats(int studentId) {
        Map<String, Map<String, Object>> stats = new HashMap<>();
        String sql = "SELECT sub.code AS subject_code, sub.name AS subject_name, " +
                     "COUNT(att.id) AS total_classes, " +
                     "SUM(CASE WHEN att.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_classes " +
                     "FROM Subject sub " +
                     "LEFT JOIN Attendance att ON att.subject_id = sub.id AND att.student_id = ? " +
                     "GROUP BY sub.id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String subCode = rs.getString("subject_code");
                    int total = rs.getInt("total_classes");
                    int present = rs.getInt("present_classes");
                    double percentage = total > 0 ? ((double) present / total) * 100 : 0.0;
                    
                    Map<String, Object> details = new HashMap<>();
                    details.put("subjectName", rs.getString("subject_name"));
                    details.put("total", total);
                    details.put("present", present);
                    details.put("percentage", Math.round(percentage * 10.0) / 10.0);
                    
                    stats.put(subCode, details);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
