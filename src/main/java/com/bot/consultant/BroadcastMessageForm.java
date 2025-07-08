package com.bot.consultant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BroadcastMessageForm extends JFrame {
    private boolean allfinish = false;
    // ...

    private JTextArea txtMessage;
    private JButton btnSendBroadcast, btnRefresh, btnBack;
    private JTable broadcastTable;
    private DefaultTableModel tableModel;

    public BroadcastMessageForm() {
        setTitle("Broadcast Pesan ke Member");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Mengatur warna latar belakang JFrame menjadi merah
        getContentPane().setBackground(new Color(227, 242, 253)); // Merah muda

        // Message Input Panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createTitledBorder("Tulis Pesan Broadcast"));
        messagePanel.setBackground(new Color(227, 242, 253)); // Merah yang lebih terang
        txtMessage = new JTextArea(5, 40);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(txtMessage);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255)); // putih
        btnSendBroadcast = new JButton("Kirim Broadcast");
        btnRefresh = new JButton("Refresh Status");
        btnBack = new JButton("Kembali");
        
        // Mengatur warna tombol menjadi merah
        btnSendBroadcast.setBackground(new Color(227, 242, 253)); // Merah gelap
        btnSendBroadcast.setForeground(new Color(42, 127, 255));
        btnRefresh.setBackground(new Color(232, 245, 233));
        btnRefresh.setForeground(new Color(56, 142, 60));
        btnBack.setBackground(new Color(255, 236, 179));
        btnBack.setForeground(new Color(255, 152, 0));

        buttonPanel.add(btnSendBroadcast);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Broadcast History Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Pesan", "Status", "Waktu Dibuat", "Waktu Dikirim"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        broadcastTable = new JTable(tableModel);
        broadcastTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        broadcastTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        broadcastTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Message
        broadcastTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Status
        broadcastTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Created At
        broadcastTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Sent At
        
        // Mengatur warna header tabel
        broadcastTable.getTableHeader().setBackground(new Color(42, 127, 255));
        broadcastTable.getTableHeader().setForeground(Color.WHITE);
        // Mengatur warna latar belakang tabel
        broadcastTable.setBackground(new Color(227, 242, 253)); // Merah sangat muda

        JScrollPane tableScrollPane = new JScrollPane(broadcastTable);
        add(tableScrollPane, BorderLayout.CENTER);

        loadBroadcastHistory();
        addListeners();
    }

    private void loadBroadcastHistory() {
        tableModel.setRowCount(0);
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "SELECT id, message_text, status, created_at, sent_at FROM broadcast_messages ORDER BY created_at DESC";
            Statement stmt = db.getMysqlConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("message_text"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("sent_at")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading broadcast history: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void addListeners() {
        btnSendBroadcast.addActionListener(e -> sendBroadcast());
        btnRefresh.addActionListener(e -> loadBroadcastHistory());
        btnBack.addActionListener(e -> dispose());
    }

    private void sendBroadcast() {
        String message = txtMessage.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesan broadcast tidak boleh kosong.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin mengirim pesan broadcast ini ke semua member terverifikasi?", "Konfirmasi Broadcast", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            final long broadcastId = saveBroadcastMessage(message);
            if (broadcastId == -1) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan pesan broadcast ke database.");
                return;
            }

            List<String> verifiedChatIds = getVerifiedMemberChatIds();

            if (verifiedChatIds.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada member terverifikasi untuk dikirimi broadcast.");
                updateBroadcastStatus(broadcastId, "failed");
                loadBroadcastHistory();
                return;
            }

            new Thread(() -> {
                boolean allSent = true;
                for (String chatId : verifiedChatIds) {
                    boolean sent = TelegramSender.sendMessage(chatId, message);
                    if (!sent) {
                        allSent = false;
                        System.err.println("Failed to send broadcast to " + chatId);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    if (allfinish) {
                        JOptionPane.showMessageDialog(this, "Pesan broadcast berhasil dikirim ke semua member");
                        updateBroadcastStatus(broadcastId, "sent");
                    } else {
                        JOptionPane.showMessageDialog(this, "Beberapa pesan broadcast gagal dikirim. Cek log");
                        updateBroadcastStatus(broadcastId, "failed");
                    }
                    loadBroadcastHistory();
                    txtMessage.setText("");
                });
            }).start();
        }
    }

    private long saveBroadcastMessage(String message) {
        MySQLConnector db = new MySQLConnector();
        long id = -1;
        try {
            db.connect();
            String sql = "INSERT INTO broadcast_messages (message_text, status) VALUES (?, 'pending')";
            PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, message);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong(1);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error saving broadcast message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return id;
    }

    private void updateBroadcastStatus(long broadcastId, String status) {
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "UPDATE broadcast_messages SET status = ?, sent_at = NOW() WHERE id = ?";
            PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setLong(2, broadcastId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error updating broadcast status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private List<String> getVerifiedMemberChatIds() {
        List<String> chatIds = new ArrayList<>();
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "SELECT chat_id FROM members WHERE is_verified = TRUE";
            Statement stmt = db.getMysqlConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                chatIds.add(rs.getString("chat_id"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting verified member chat IDs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return chatIds;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BroadcastMessageForm().setVisible(true));
    }
}
