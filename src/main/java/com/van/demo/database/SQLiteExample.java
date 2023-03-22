package com.van.demo.database;

import java.sql.*;

public class SQLiteExample {
    
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Connect to the database
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            // Create a statement
            Statement stmt = conn.createStatement();
//            stmt.executeQuery("CREATE TABLE user (\n" +
//                    "    id INTEGER PRIMARY KEY,\n" +
//                    "    username TEXT NOT NULL UNIQUE,\n" +
//                    "    password TEXT NOT NULL,\n" +
//                    "    email TEXT NOT NULL UNIQUE,\n" +
//                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
//                    ");\n");
            stmt.execute("INSERT INTO user (username, password, email) VALUES ('john_doe2', 'password123', '2john_doe@example.com');");
            // Execute a query
            ResultSet rs = stmt.executeQuery("SELECT * FROM user");

            // Process the results
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ", " + rs.getString("username"));
            }

            // Close the result set, statement, and connection
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
