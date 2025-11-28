package com.hotwax.oms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 1. Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/hotwax_assignment";
    private static final String USER = "root";
    private static final String PASS = "root"; // Change if your password is different

    // 2. Get Connection Method
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}