package com.bot.consultant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewApplication extends JFrame {

    private JTextArea displayArea;

    public NewApplication() {
        setTitle("Warteg Bot - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the window

        // Mengatur warna latar belakang JFrame menjadi merah muda
        getContentPane().setBackground(new Color(255, 255, 255));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(42, 127, 255)); // Merah gelap untuk header
        JLabel titleLabel = new JLabel("Selamat Datang di Dashboard Admin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE); // Warna teks putih
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel (using a JSplitPane for better layout)
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.2); // Give more space to the right panel
        mainSplitPane.setBackground(new Color(255, 255, 255)); // Warna latar belakang split pane

        // Left Panel (Navigation Buttons)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(7, 1, 10, 10)); // 7 rows, 1 column, with gaps
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setBackground(new Color(255, 255, 255)); // Merah yang lebih terang untuk panel navigasi

        JButton btnKelolaUser = new JButton("Kelola User");
        JButton btnKelolaMember = new JButton("Kelola Member");
        JButton btnKelolaKeyword = new JButton("Kelola Keyword & Jawaban");
        JButton btnKelolaPesan = new JButton("Kelola Pesan & History");
        JButton btnBroadcast = new JButton("Broadcast Pesan");
        JButton btnLogout = new JButton("Logout");

        // Mengatur warna tombol menjadi merah dan teks putih
        btnKelolaUser.setBackground(new Color(227, 242, 253));
        btnKelolaUser.setForeground(new Color(42, 127, 255));
        btnKelolaMember.setBackground(new Color(227, 242, 253));
        btnKelolaMember.setForeground(new Color(42, 127, 255));
        btnKelolaKeyword.setBackground(new Color(227, 242, 253));
        btnKelolaKeyword.setForeground(new Color(42, 127, 255));
        btnKelolaPesan.setBackground(new Color(227, 242, 253));
        btnKelolaPesan.setForeground(new Color(42, 127, 255));
        btnBroadcast.setBackground(new Color(227, 242, 253));
        btnBroadcast.setForeground(new Color(42, 127, 255));
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);

        navPanel.add(btnKelolaUser);
        navPanel.add(btnKelolaMember);
        navPanel.add(btnKelolaKeyword);
        navPanel.add(btnKelolaPesan);
        navPanel.add(btnBroadcast);
        navPanel.add(new JLabel("")); // Spacer
        navPanel.add(btnLogout);

        mainSplitPane.setLeftComponent(navPanel);

        // Right Panel (Display Area / Welcome Message)
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(new Color(255, 255, 255)); // Merah sangat muda untuk panel display
        displayArea = new JTextArea("Pilih modul dari menu di samping untuk memulai.");
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayArea.setBackground(new Color(255, 255, 255)); // Warna latar belakang text area
        displayPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        mainSplitPane.setRightComponent(displayPanel);
        add(mainSplitPane, BorderLayout.CENTER);

        // Watermark Panel
        JPanel watermarkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        watermarkPanel.setBackground(new Color(42, 127, 255)); // Merah gelap untuk watermark
        JLabel watermarkLabel = new JLabel("© 2025 BangTeg Bot - Dhion");
        watermarkLabel.setForeground(Color.WHITE);
        watermarkLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        watermarkPanel.add(watermarkLabel);
        add(watermarkPanel, BorderLayout.SOUTH);


        // Action Listeners
        btnKelolaUser.addActionListener(e -> {
            new UserManagementForm().setVisible(true);
        });

        btnKelolaMember.addActionListener(e -> {
            new MemberManagementForm().setVisible(true);
        });

        btnKelolaKeyword.addActionListener(e -> {
            new katakunci().setVisible(true);
        });

        btnKelolaPesan.addActionListener(e -> {
            new MessageHistoryForm().setVisible(true);
        });

        btnBroadcast.addActionListener(e -> {
            new BroadcastMessageForm().setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NewApplication().setVisible(true);
        });
    }
}
