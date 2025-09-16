package org.example.calculator.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInit {
    public static void init() {
        try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(""" 
                CREATE TABLE IF NOT EXISTS roles (id integer primary key autoincrement, name text unique not null)
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id integer primary key autoincrement, username text not null,
                    password_hash text not null, role_id integer not null, foreign key(role_id) references roles(id))
            """);

            stmt.executeUpdate("INSERT OR IGNORE INTO roles (id, name) VALUES (1, 'USER')");
            stmt.executeUpdate("INSERT OR IGNORE INTO roles (id, name) VALUES (2, 'ADMIN')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
