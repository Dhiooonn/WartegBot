package com.bot.consultant;

import javax.swing.*;

public class ChatWindow extends JFrame {
    public ChatWindow() {
        setTitle("Warteg Bang");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Halo! Selamat Datang di Warteg Bang😁");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label); // tambahkan label ke frame
    }
}
