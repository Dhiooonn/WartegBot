// MultipleFiles/MySQLConnector.java
package com.bot.consultant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector {
    private Connection mysqlConnection = null;
    // Pastikan nama database sesuai dengan yang Anda gunakan
    String dbn = "db_chatbot"; // Ganti jika nama database Anda berbeda
    String driver = "com.mysql.cj.jdbc.Driver"; // Menggunakan cj driver untuk versi MySQL terbaru
    String database = "jdbc:mysql://localhost:3306/" + dbn;
    private String user = "root";
    private String password = "";

    public MySQLConnector(String driver, String database, String user, String password) {
        this.driver = driver;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public MySQLConnector() {
        // Default constructor, menggunakan nilai default di atas
    }

    public void connect() {
        try {
            Class.forName(this.driver); // Memuat driver
            this.mysqlConnection = DriverManager.getConnection(this.database, this.user, this.password);
            System.out.println("Database connected successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (this.mysqlConnection != null && !this.mysqlConnection.isClosed()) {
                this.mysqlConnection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getMysqlConnection() {
        return this.mysqlConnection;
    }

    public void setMysqlConnection(Connection mysqlConnection) {
        this.mysqlConnection = mysqlConnection;
    }
}


/* Location:              /home/x0r/Documents/pbo/telebotKarangjati.jar!/telebot2/MySQLConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.2.1
 */

