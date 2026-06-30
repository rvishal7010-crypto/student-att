package com.example.erp.dao;

import com.example.erp.model.InternalMarks;
import com.example.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing continuous assessment evaluations (CIA1, CIA2, Model Exam, Assignment, Lab, Seminar).
 * Automatically maps calculated Total, Average, and Percentage scores back and forth to the DB.
 */
public class InternalMarksDAO {

    /**
     * Retrieves internal marks details for a student.
     */
    public List<InternalMarks> getStudentMarks(int studentId) {
        List<InternalMarks> list = new ArrayList<>();
        String sql = "SELECT * FROM InternalMarks WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMarks(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves specific internal marks statement for a student in a particular subject.
     */
    public InternalMarks getStudentMarksForSubject(int studentId, int subjectId) {
        String sql = "SELECT * FROM InternalMarks WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMarks(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Upserts (inserts or updates) marks for a student in a specific subject.
     */
    public boolean saveOrUpdateMarks(InternalMarks m) {
        // Automatically ensure scores are calculated before persisting
        m.calculateScores();

        String sql = "INSERT INTO InternalMarks (student_id, subject_id, cia1, cia2, model_exam, assignment, lab, seminar, total, average, percentage) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE cia1 = ?, cia2 = ?, model_exam = ?, assignment = ?, lab = ?, seminar = ?, total = ?, average = ?, percentage = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, m.getStudentId());
            stmt.setInt(2, m.getSubjectId());
            stmt.setDouble(3, m.getCia1());
            stmt.setDouble(4, m.getCia2());
            stmt.setDouble(5, m.getModelExam());
            stmt.setDouble(6, m.getAssignment());
            stmt.setDouble(7, m.getLab());
            stmt.setDouble(8, m.getSeminar());
            stmt.setDouble(9, m.getTotal());
            stmt.setDouble(10, m.getAverage());
            stmt.setDouble(11, m.getPercentage());
            
            // Updates fields on duplicate key
            stmt.setDouble(12, m.getCia1());
            stmt.setDouble(13, m.getCia2());
            stmt.setDouble(14, m.getModelExam());
            stmt.setDouble(15, m.getAssignment());
            stmt.setDouble(16, m.getLab());
            stmt.setDouble(17, m.getSeminar());
            stmt.setDouble(18, m.getTotal());
            stmt.setDouble(19, m.getAverage());
            stmt.setDouble(20, m.getPercentage());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private InternalMarks mapResultSetToMarks(ResultSet rs) throws SQLException {
        InternalMarks marks = new InternalMarks();
        marks.setId(rs.getInt("id"));
        marks.setStudentId(rs.getInt("student_id"));
        marks.setSubjectId(rs.getInt("subject_id"));
        marks.setCia1(rs.getDouble("cia1"));
        marks.setCia2(rs.getDouble("cia2"));
        marks.setModelExam(rs.getDouble("model_exam"));
        marks.setAssignment(rs.getDouble("assignment"));
        marks.setLab(rs.getDouble("lab"));
        marks.setSeminar(rs.getDouble("seminar"));
        marks.setTotal(rs.getDouble("total"));
        marks.setAverage(rs.getDouble("average"));
        marks.setPercentage(rs.getDouble("percentage"));
        return marks;
    }
}
