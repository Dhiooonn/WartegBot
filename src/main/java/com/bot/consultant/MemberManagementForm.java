// MultipleFiles/MemberManagementForm.java
package com.bot.consultant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class MemberManagementForm extends JFrame {

    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField txtChatId, txtUsername;
    private JCheckBox chkVerified;
    private JButton btnVerify, btnUnverify, btnDelete, btnRefresh, btnBack;

    public MemberManagementForm() {
        setTitle("Kelola Member Telegram");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Mengatur warna latar belakang JFrame menjadi merah
        getContentPane().setBackground(new Color(255, 255, 255)); // Merah muda

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detail Member"));
        inputPanel.setBackground(new Color(255, 255, 255)); // Merah yang lebih terang
        inputPanel.add(new JLabel("Chat ID:")).setForeground(new Color(33, 33, 33)); // Warna teks label
        txtChatId = new JTextField();
        txtChatId.setEditable(false); // Chat ID should not be editable
        txtChatId.setBackground(Color.WHITE); // Warna latar belakang text field
        inputPanel.add(txtChatId);
        inputPanel.add(new JLabel("Username:")).setForeground(new Color(33, 33, 33)); // Warna teks label
        txtUsername = new JTextField();
        txtUsername.setEditable(false); // Username should not be editable
        txtUsername.setBackground(Color.WHITE); // Warna latar belakang text field
        inputPanel.add(txtUsername);
        inputPanel.add(new JLabel("Status Verifikasi:")).setForeground(new Color(33, 33, 33)); // Warna teks label
        chkVerified = new JCheckBox("Terverifikasi");
        chkVerified.setBackground(Color.WHITE); // Warna latar belakang checkbox
        chkVerified.setForeground(new Color(33, 33, 33)); // Warna teks checkbox
        inputPanel.add(chkVerified);
        add(inputPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255)); // Merah muda
        btnVerify = new JButton("Verifikasi");
        btnUnverify = new JButton("Batalkan Verifikasi");
        btnDelete = new JButton("Hapus Member");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Kembali");

        // Mengatur warna tombol menjadi merah
        btnVerify.setBackground(new Color(200, 230, 201));
        btnVerify.setForeground(new Color(27, 94, 32));
        btnUnverify.setBackground(new Color(255, 235, 238));
        btnUnverify.setForeground(new Color(183, 28, 28));
        btnDelete.setBackground(new Color(255, 224, 178));
        btnDelete.setForeground(new Color(230, 81, 0));
        btnRefresh.setBackground(new Color(232, 234, 246));
        btnRefresh.setForeground(new Color(48, 63, 159));
        btnBack.setBackground(new Color(224, 242, 241));
        btnBack.setForeground(new Color(0, 105, 92));

        buttonPanel.add(btnVerify);
        buttonPanel.add(btnUnverify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Chat ID", "Username", "Terverifikasi", "Tanggal Daftar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Boolean.class; // For "Terverifikasi" column
                return super.getColumnClass(columnIndex);
            }
        };
        memberTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Mengatur warna latar belakang tabel
        memberTable.setBackground(new Color(227, 242, 253));
        // Mengatur warna header tabel
        memberTable.getTableHeader().setBackground(new Color(42, 127, 255));
        memberTable.getTableHeader().setForeground(Color.WHITE);

        loadMembers();
        addListeners();
    }

    private void loadMembers() {
        tableModel.setRowCount(0);
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "SELECT chat_id, username, is_verified, registration_date FROM members ORDER BY registration_date DESC";
            Statement stmt = db.getMysqlConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("chat_id"),
                        rs.getString("username"),
                        rs.getBoolean("is_verified"),
                        rs.getTimestamp("registration_date")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void addListeners() {
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow != -1) {
                    txtChatId.setText((String) tableModel.getValueAt(selectedRow, 0));
                    txtUsername.setText((String) tableModel.getValueAt(selectedRow, 1));
                    chkVerified.setSelected((Boolean) tableModel.getValueAt(selectedRow, 2));
                }
            }
        });

        btnVerify.addActionListener(e -> updateVerificationStatus(true));
        btnUnverify.addActionListener(e -> updateVerificationStatus(false));
        btnDelete.addActionListener(e -> deleteMember());
        btnRefresh.addActionListener(e -> loadMembers());
        btnBack.addActionListener(e -> dispose());
    }

    private void updateVerificationStatus(boolean verify) {
        String chatId = txtChatId.getText();
        if (chatId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih member dari tabel terlebih dahulu.");
            return;
        }

        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "UPDATE members SET is_verified = ? WHERE chat_id = ?";
            PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sql);
            pstmt.setBoolean(1, verify);
            pstmt.setString(2, chatId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Status verifikasi member berhasil diupdate.");
                loadMembers();
                clearFields();
                String message = verify ? "Akun Anda telah diverifikasi. Anda sekarang dapat menggunakan bot ini sepenuhnya." : "Verifikasi akun Anda telah dibatalkan.";
                TelegramSender.sendMessage(chatId, message);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status verifikasi.");
            }
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating verification status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void deleteMember() {
        String chatId = txtChatId.getText();
        if (chatId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih member yang ingin dihapus dari tabel.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus member ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            MySQLConnector db = new MySQLConnector();
            try {
                db.connect();
                String sql = "DELETE FROM members WHERE chat_id = ?";
                PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sql);
                pstmt.setString(1, chatId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Member berhasil dihapus.");
                    loadMembers();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus member.");
                }
                pstmt.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting member: " + e.getMessage());
                e.printStackTrace();
            } finally {
                db.closeConnection();
            }
        }
    }

    private void clearFields() {
        txtChatId.setText("");
        txtUsername.setText("");
        chkVerified.setSelected(false);
        memberTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemberManagementForm().setVisible(true));
    }
}
