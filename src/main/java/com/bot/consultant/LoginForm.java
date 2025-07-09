// MultipleFiles/LoginForm.java
package com.bot.consultant;
import javax.swing.*;
import java.awt.*;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.Font;
import java.awt.Color; // Import java.awt.Color
import java.awt.FlowLayout;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, exitButton;
    private JLabel userLabel, passLabel, titleLabel;
    private MySQLConnector db;

    public LoginForm() {
        db = new MySQLConnector();
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Login - Warteg Bot");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Mengatur warna latar belakang JFrame menjadi merah
        getContentPane().setBackground(Color.white); // Background Putih
        
        titleLabel = new JLabel("SILAHKAN LOGIN");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleLabel.setBounds(120, 20, 200, 25);
        titleLabel.setForeground(new Color(42, 127, 255)); // Mengatur warna teks menjadi putih
        add(titleLabel);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 60, 100, 25);
        userLabel.setForeground(new Color(62, 109, 53));; // Mengatur warna teks menjadi putih
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 60, 180, 25);
        usernameField.setBackground(Color.white); // Warna latar belakang text field
        usernameField.setForeground(Color.BLACK); // Warna latar belakang text field

        add(usernameField);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 25);
        passLabel.setForeground(new Color(62, 109, 53));; // Mengatur warna teks menjadi putih
        usernameField.setForeground(new Color(255, 255, 255)); // Warna latar belakang text field

        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 180, 25);
        passwordField.setBackground(Color.WHITE); // Warna latar belakang password field
        usernameField.setForeground(Color.BLACK); // Warna latar belakang text field

        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(80, 150, 100, 30);
        loginButton.setBackground(new Color(214, 44, 44)); // Warna tombol
        loginButton.setForeground(Color.WHITE); // Warna teks tombol
        add(loginButton);

        exitButton = new JButton("Keluar");
        exitButton.setBounds(200, 150, 100, 30);
        exitButton.setBackground(new Color(214, 44, 44)); // Warna tombol
        exitButton.setForeground(Color.WHITE); // Warna teks tombol
        add(exitButton);

        // Event handler login
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loginActionPerformed();
            }
        });

        Font font = new Font("Segoe UI", Font.BOLD, 14);
        titleLabel.setFont(font);
        userLabel.setFont(font);
        passLabel.setFont(font);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        exitButton.setFont(font);

        // Event handler keluar
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void loginActionPerformed() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password harus diisi!");
            return;
        }

        
        try {
            db.connect();
            String sql = "SELECT * FROM login WHERE user = ? AND pass = ?";
            PreparedStatement stmt = db.getMysqlConnection().prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                new NewApplication().setVisible(true); // Buka dashboard admin
                dispose(); // Tutup form login
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal! Username atau password salah.");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
