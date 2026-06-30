package com.example.erp.dao;

import com.example.erp.model.Staff;
import com.example.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing complete CRUD operations for Faculty & Staff profiles.
 */
public class StaffDAO {

    /**
     * Creates a Staff member along with their UserLogin record inside a database transaction.
     */
    public boolean insertStaff(Staff staff, String initialPassword) {
        String insertLoginSql = "INSERT INTO UserLogin (username, password, role, is_active) VALUES (?, ?, 'STAFF', 1)";
        String insertStaffSql = "INSERT INTO Staff (login_id, employee_id, name, email, phone, department_id, designation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement loginStmt = null;
        PreparedStatement staffStmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction
            
            // 1. Create Portal Login Credentials
            loginStmt = conn.prepareStatement(insertLoginSql, PreparedStatement.RETURN_GENERATED_KEYS);
            loginStmt.setString(1, staff.getEmployeeId().toLowerCase()); // Username is employee id in lowercase
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
            
            // 2. Insert Staff Profile with the linked loginId
            staffStmt = conn.prepareStatement(insertStaffSql);
            staffStmt.setInt(1, loginId);
            staffStmt.setString(2, staff.getEmployeeId());
            staffStmt.setString(3, staff.getName());
            staffStmt.setString(4, staff.getEmail());
            staffStmt.setString(5, staff.getPhone());
            staffStmt.setInt(6, staff.getDepartmentId());
            staffStmt.setString(7, staff.getDesignation());
            
            int staffRows = staffStmt.executeUpdate();
            if (staffRows == 0) {
                throw new SQLException("Registering staff profile failed, no rows affected.");
            }
            
            conn.commit(); // Commit Transaction if both successful
            return true;
            
        } catch (SQLException e) {
            System.err.println("Transaction Failed! Rolling back database changes: " + e.getMessage());
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
                if (loginStmt != null) loginStmt.close();
                if (staffStmt != null) staffStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves all registered staff.
     */
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM Staff ORDER BY name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Retrieves a single staff by ID.
     */
    public Staff getStaffById(int id) {
        String sql = "SELECT * FROM Staff WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a single staff member by their linked login credentials ID.
     */
    public Staff getStaffByLoginId(int loginId) {
        String sql = "SELECT * FROM Staff WHERE login_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loginId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing staff record.
     */
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE Staff SET name = ?, email = ?, phone = ?, department_id = ?, designation = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getEmail());
            stmt.setString(3, staff.getPhone());
            stmt.setInt(4, staff.getDepartmentId());
            stmt.setString(5, staff.getDesignation());
            stmt.setInt(6, staff.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes staff and cascades to login.
     */
    public boolean deleteStaff(int id) {
        Staff staff = getStaffById(id);
        if (staff == null) return false;
        
        String sql = "DELETE FROM UserLogin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staff.getLoginId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getInt("id"));
        staff.setLoginId(rs.getInt("login_id"));
        staff.setEmployeeId(rs.getString("employee_id"));
        staff.setName(rs.getString("name"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone"));
        staff.setDepartmentId(rs.getInt("department_id"));
        staff.setDesignation(rs.getString("designation"));
        return staff;
    }
}
