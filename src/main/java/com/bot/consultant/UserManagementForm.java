// MultipleFiles/UserManagementForm.java
package com.bot.consultant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class UserManagementForm extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnAdd, btnEdit, btnDelete, btnClear, btnBack;

    public UserManagementForm() {
        setTitle("Kelola User Admin");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Mengatur warna latar belakang JFrame menjadi merah
        getContentPane().setBackground(new Color(255, 255, 255)); // Merah muda

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detail User"));
        inputPanel.setBackground(new Color(255, 255, 255)); 
        inputPanel.add(new JLabel("Username:")).setForeground(new Color(33, 33, 33)); // Warna teks label
        txtUsername = new JTextField();
        txtUsername.setBackground(Color.WHITE); // Warna latar belakang text field
        inputPanel.add(txtUsername);
        inputPanel.add(new JLabel("Password:")).setForeground(new Color(33, 33, 33)); // Warna teks label
        txtPassword = new JPasswordField();
        txtPassword.setBackground(Color.WHITE); // Warna latar belakang password field
        inputPanel.add(txtPassword);
        add(inputPanel, BorderLayout.NORTH);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255)); // Merah muda
        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnClear = new JButton("Clear");
        btnBack = new JButton("Kembali");

        // Mengatur warna tombol menjadi merah
        btnAdd.setBackground(new Color(200, 230, 201));
        btnAdd.setForeground(new Color(27, 94, 32));
        btnEdit.setBackground(new Color(255, 249, 196));
        btnEdit.setForeground(new Color(245, 127, 23));
        btnDelete.setBackground(new Color(255, 205, 210));
        btnDelete.setForeground(new Color(183, 28, 28));
        btnClear.setBackground(new Color(209, 242, 235));
        btnClear.setForeground(new Color(0, 105, 92));
        btnBack.setBackground(new Color(232, 234, 246));
        btnBack.setForeground(new Color(69, 39, 160));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table for displaying users
        tableModel = new DefaultTableModel(new String[]{"Username"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Mengatur warna latar belakang tabel
        userTable.setBackground(new Color(227, 242, 253)); 

        // Mengatur warna header tabel
        userTable.getTableHeader().setBackground(new Color(42, 127, 255));
        userTable.getTableHeader().setForeground(Color.WHITE);

        // Load data and set up listeners
        loadUsers();
        addListeners();
    }

    private void loadUsers() {
        tableModel.setRowCount(0); // Clear existing data
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "SELECT user FROM login";
            Statement stmt = db.getMysqlConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString("user")});
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void addListeners() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String username = (String) userTable.getValueAt(selectedRow, 0);
                    txtUsername.setText(username);
                    txtPassword.setText(""); // Clear password field for security
                }
            }
        });

        btnAdd.addActionListener(e -> addUser());
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> dispose());
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong.");
            return;
        }

        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "INSERT INTO login (user, pass) VALUES (?, ?)";
            PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan.");
            clearFields();
            loadUsers();
            pstmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username sudah ada. Gunakan username lain.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void editUser() {
        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword()).trim();
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang ingin diedit dari tabel.");
            return;
        }
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong.");
            return;
        }

        String oldUsername = (String) userTable.getValueAt(selectedRow, 0);

        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql;
            PreparedStatement pstmt;

            if (password.isEmpty()) { // Only update username if password is empty
                sql = "UPDATE login SET user = ? WHERE user = ?";
                pstmt = db.getMysqlConnection().prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, oldUsername);
            } else { // Update both username and password
                sql = "UPDATE login SET user = ?, pass = ? WHERE user = ?";
                pstmt = db.getMysqlConnection().prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, oldUsername);
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "User berhasil diupdate.");
                clearFields();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate user atau tidak ada perubahan.");
            }
            pstmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username baru sudah ada. Gunakan username lain.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error editing user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang ingin dihapus dari tabel.");
            return;
        }

        String usernameToDelete = (String) userTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus user '" + usernameToDelete + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            MySQLConnector db = new MySQLConnector();
            try {
                db.connect();
                String sql = "DELETE FROM login WHERE user = ?";
                PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sql);
                pstmt.setString(1, usernameToDelete);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User berhasil dihapus.");
                    clearFields();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus user.");
                }
                pstmt.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
                e.printStackTrace();
            } finally {
                db.closeConnection();
            }
        }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        userTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserManagementForm().setVisible(true));
    }
}
