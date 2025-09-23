package org.example.calculator.dao;

import org.example.calculator.db.DatabaseInit;
import org.example.calculator.model.HistoryEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDao {

    public void save(int userId, String type, String inputParams, String result) {
        String sql = "INSERT INTO history (user_id, type, input_params, result) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, inputParams);
            ps.setString(4, result);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HistoryEntry> findByUser(int userId) {
        List<HistoryEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new HistoryEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("input_params"),
                        rs.getString("result"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<HistoryEntry> findAll() {
        List<HistoryEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM history ORDER BY created_at DESC";

        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new HistoryEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("input_params"),
                        rs.getString("result"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public void clearByUser(int userId) {
        String sql = "DELETE FROM history WHERE user_id = ?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAll() {
        String sql = "DELETE FROM history";
        try (Connection conn = DatabaseInit.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
