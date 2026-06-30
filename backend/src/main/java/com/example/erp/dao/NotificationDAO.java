package com.example.erp.dao;

import com.example.erp.model.Notification;
import com.example.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO to fetch and publish announcements/circular notifications.
 */
public class NotificationDAO {

    /**
     * Publishes a new circular announcement.
     */
    public boolean publishNotification(Notification notification) {
        String sql = "INSERT INTO Notifications (title, message, sender_role, target_role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notification.getTitle());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getSenderRole());
            stmt.setString(4, notification.getTargetRole());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all notifications applicable to a specific viewer role.
     */
    public List<Notification> getNotificationsForRole(String role) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE target_role = 'ALL' OR target_role = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getInt("id"));
                    n.setTitle(rs.getString("title"));
                    n.setMessage(rs.getString("message"));
                    n.setSenderRole(rs.getString("sender_role"));
                    n.setTargetRole(rs.getString("target_role"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
