package com.example.erp.dao;

import com.example.erp.model.UserLogin;
import com.example.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO to handle Login authentication, active status checks, and password updates.
 */
public class UserLoginDAO {

    /**
     * Verifies user credentials against the database.
     * @param username String portal user identifier
     * @param password String secure password
     * @return UserLogin if credentials are correct and account is active, null otherwise
     */
    public UserLogin authenticate(String username, String password) {
        String sql = "SELECT * FROM UserLogin WHERE username = ? AND password = ? AND is_active = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // Note: In production use hashed password checking e.g. bcrypt or SHA-256
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserLogin user = new UserLogin();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("JDBC Authentication Query Failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers a new system login credential.
     * @param user UserLogin credentials to create
     * @return Generated auto-incremented ID, or -1 on error
     */
    public int createLogin(UserLogin user) throws SQLException {
        String sql = "INSERT INTO UserLogin (username, password, role, is_active) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Hash before passing in production
            stmt.setString(3, user.getRole());
            stmt.setBoolean(4, user.isActive());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Updates profile password.
     */
    public boolean updatePassword(int id, String newPassword) {
        String sql = "UPDATE UserLogin SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
