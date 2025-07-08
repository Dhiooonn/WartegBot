package com.bot.consultant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class historypesan extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnKembali;

    public historypesan() {
        setTitle("History Pesan");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Mengatur warna latar belakang JFrame menjadi merah
        getContentPane().setBackground(new Color(255, 100, 100)); // Merah muda

        // Table
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Mengatur warna latar belakang tabel
        table.setBackground(new Color(255, 200, 200)); // Merah sangat muda
        // Mengatur warna header tabel
        table.getTableHeader().setBackground(new Color(200, 0, 0));
        table.getTableHeader().setForeground(Color.WHITE);

        // Tombol kembali
        btnKembali = new JButton("Kembali");
        JPanel panelButton = new JPanel();
        panelButton.setBackground(new Color(255, 255, 255)); // Merah muda
        btnKembali.setBackground(new Color(200, 0, 0)); // Warna tombol
        btnKembali.setForeground(Color.WHITE); // Warna teks tombol
        panelButton.add(btnKembali);
        add(panelButton, BorderLayout.SOUTH);

        btnKembali.addActionListener(e -> {
            new NewApplication().setVisible(true);
            dispose();
        });

        // Tampilkan data
        tampilkanData();
    }

    private void tampilkanData() {
        try {
            MySQLConnector db = new MySQLConnector();
            db.connect();
            Connection conn = db.getMysqlConnection();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM chat3 ORDER BY id DESC");

            // Ambil metadata untuk kolom
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            tableModel.setColumnCount(0);
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Isi data
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            rs.close();
            stmt.close();
            db.closeConnection();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new historypesan().setVisible(true);
    }
}
