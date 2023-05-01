package ru.ProjectFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBD {
    static private Connection conn;

    public static void setConnection(String url){
        try{
            conn = DriverManager.getConnection(url, "admin", "admin");
        }
        catch (SQLException e) {
            conn = null;
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
    }

    static Connection getConnection(){
        return conn;
    }
}
