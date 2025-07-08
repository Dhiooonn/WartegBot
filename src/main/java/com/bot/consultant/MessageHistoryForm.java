// MultipleFiles/MessageHistoryForm.java
package com.bot.consultant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MessageHistoryForm extends JFrame {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnBack;
    private JTextField txtFilterChatId;
    private JComboBox<String> cmbMessageType;

    public MessageHistoryForm() {
        setTitle("Riwayat Pesan Bot");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Mengatur warna latar belakang JFrame menjadi merah
        getContentPane().setBackground(new Color(255, 255, 255)); // Merah muda

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Pesan"));
        filterPanel.setBackground(Color.WHITE); // Merah yang lebih terang
        filterPanel.add(new JLabel("Chat ID:")).setForeground(Color.BLACK); // Warna teks label
        txtFilterChatId = new JTextField(15);
        txtFilterChatId.setBackground(new Color(255, 255, 255)); // Warna latar belakang text field
        filterPanel.add(txtFilterChatId);
        filterPanel.add(new JLabel("Tipe Pesan:")).setForeground(Color.BLACK); // Warna teks label
        cmbMessageType = new JComboBox<>(new String[]{"Semua", "in", "out"});
        cmbMessageType.setBackground(new Color(255, 255, 255)); // Warna latar belakang combo box
        filterPanel.add(cmbMessageType);
        btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(227, 242, 253)); // Warna tombol
        btnRefresh.setForeground(new Color(25, 118, 210)); // Warna teks tombol
        filterPanel.add(btnRefresh);
        add(filterPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Chat ID", "Tipe", "Pesan", "Waktu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Chat ID
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(50); // Type
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(400); // Message
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Timestamp

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Mengatur warna latar belakang tabel
        historyTable.setBackground(new Color(227, 242, 253)); // Merah sangat muda
        // Mengatur warna header tabel
        historyTable.getTableHeader().setBackground(new Color(42, 127, 255));
        historyTable.getTableHeader().setForeground(new Color(227, 242, 253));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(227, 242, 253)); // Merah muda
        btnBack = new JButton("Kembali");
        btnBack.setBackground(new Color(42, 127, 255)); // Warna tombol
        btnBack.setForeground(new Color(227, 242, 253)); // Warna teks tombol
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        loadMessageHistory();
        addListeners();
    }

    private void loadMessageHistory() {
        tableModel.setRowCount(0);
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            StringBuilder sqlBuilder = new StringBuilder("SELECT id, chat_id, message_type, message_text, timestamp FROM chat_history WHERE 1=1");
            String filterChatId = txtFilterChatId.getText().trim();
            String filterMessageType = (String) cmbMessageType.getSelectedItem();

            if (!filterChatId.isEmpty()) {
                sqlBuilder.append(" AND chat_id LIKE ?");
            }
            if (!filterMessageType.equals("Semua")) {
                sqlBuilder.append(" AND message_type = ?");
            }
            sqlBuilder.append(" ORDER BY timestamp DESC");

            PreparedStatement pstmt = db.getMysqlConnection().prepareStatement(sqlBuilder.toString());
            int paramIndex = 1;
            if (!filterChatId.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + filterChatId + "%");
            }
            if (!filterMessageType.equals("Semua")) {
                pstmt.setString(paramIndex++, filterMessageType);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("chat_id"),
                        rs.getString("message_type"),
                        rs.getString("message_text"),
                        rs.getTimestamp("timestamp")
                });
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading message history: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

    private void addListeners() {
        btnRefresh.addActionListener(e -> loadMessageHistory());
        btnBack.addActionListener(e -> dispose());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessageHistoryForm().setVisible(true));
    }
}
