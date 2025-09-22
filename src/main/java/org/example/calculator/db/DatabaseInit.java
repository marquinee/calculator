package org.example.calculator.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInit {
    private static final String DB_URL = "jdbc:sqlite:calculator.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }
    public static void init() {
        try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement()) {
            stmt.execute(""" 
                CREATE TABLE IF NOT EXISTS roles (id integer primary key autoincrement, name text unique not null)
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id integer primary key autoincrement, username text not null,
                    password_hash text not null, role_id integer not null, foreign key(role_id) references roles(id))
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    type TEXT NOT NULL,               -- "OHM" или "DIVIDER"
                    input_params TEXT NOT NULL,       -- входные данные (Vin, Vout, R1..R4 и т.п.)
                    result TEXT NOT NULL,             -- результат вычисления
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                )
            """);

            stmt.executeUpdate("INSERT OR IGNORE INTO roles (id, name) VALUES (1, 'USER')");
            stmt.executeUpdate("INSERT OR IGNORE INTO roles (id, name) VALUES (2, 'ADMIN')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
