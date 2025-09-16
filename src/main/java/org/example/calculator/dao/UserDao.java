package org.example.calculator.dao;

import org.example.calculator.Main;
import org.example.calculator.db.Database;
import org.example.calculator.model.User;

import java.sql.*;

public class UserDao {
    public void saveUser(String username, String passwordHash, String role) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            int roleId = getRoleId(role, conn);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(username, password_hash, role_id) VALUES(?, ?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, passwordHash);
                ps.setInt(3, roleId);
                ps.executeUpdate();
            }
        }
    }

    public User findByUsername(String username) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT u.id, u.username, u.password_hash, r.name as role " +
                             "FROM users u JOIN roles r ON u.role_id = r.id WHERE u.username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                );
            }
            return null;
        }
    }

    private int getRoleId(String role, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM roles WHERE name=?")) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            throw new SQLException("Role not found: " + role);
        }
    }
}
