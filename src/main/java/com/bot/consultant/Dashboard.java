package com.bot.consultant;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dashboard extends TelegramLongPollingBot {
    
    // Ganti dengan token bot Telegram Anda yang sebenarnya
    @Override
    public String getBotToken() {
        return "7870361348:AAEDwe6YATCbLV6xO_xi7NPc1eK-Gnei5pU"; // TOKEN BOT API 
    }

    @Override
    public String getBotUsername() {
        return "BangTegBot"; // USERNAME BOT ANDA
    }

    private String getAllKeywords() {
        StringBuilder keywords = new StringBuilder();
        MySQLConnector conn = new MySQLConnector();

        try {
            conn.connect();
            String sql = "SELECT kata FROM kamut ORDER BY kata ASC";
            PreparedStatement pstmt = conn.getMysqlConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String kata = rs.getString("kata");
                if (kata != null) {
                    kata = kata.trim();

                    if (!kata.isEmpty() && !kata.equals("/")) {
                        // Hilangkan semua '/' di awal, lalu tambahkan satu '/' saja
                        while (kata.startsWith("/")) {
                            kata = kata.substring(1);
                        }
                        keywords.append("/").append(kata).append("\n");
                    }
                }
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.closeConnection();
        }

        return keywords.toString();
    }



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String username = update.getMessage().getFrom().getUserName();
            // String pesan = update.getMessage().getText(); // Variabel ini tidak digunakan, bisa dihapus
            // String jawaban = katakunci.cariJawaban(pesan); // Variabel ini tidak digunakan, bisa dihapus
        
            if (username == null || username.isEmpty()) {
                username = update.getMessage().getFrom().getFirstName() + (update.getMessage().getFrom().getLastName() != null ? " " + update.getMessage().getFrom().getLastName() : "");
            }

            System.out.println(">> Bot menerima pesan: " + receivedMessage);
            System.out.println(">> Dari chat ID: " + chatId + ", Username: " + username);

            // Simpan pesan masuk ke chat_history
            saveMessageToHistory(chatId, "in", receivedMessage);

            String replyText;

            // Cek apakah member terdaftar dan terverifikasi
            boolean isMemberRegistered = isMemberRegistered(chatId);
            boolean isMemberVerified = isMemberVerified(chatId);

            if (receivedMessage.startsWith("/start")) {
                if (!isMemberRegistered) {
                    registerNewMember(chatId, username);
                    replyText = "Halo " + username + "! Selamat datang di BangTegBot. Anda telah terdaftar. Mohon tunggu verifikasi dari admin untuk dapat berinteraksi penuh.";
                } else if (!isMemberVerified) {
                    replyText = "Halo " + username + "! Anda sudah terdaftar, namun akun Anda belum diverifikasi. Mohon tunggu verifikasi dari admin.";
                } else {
                    replyText = "Halo " + username + "! Anda sudah terdaftar dan terverifikasi. Ada yang bisa saya bantu? Ketik /help untuk melihat daftar perintah.";
                }
            } else if (receivedMessage.startsWith("/help")) { // Menambahkan perintah /help
                if (!isMemberVerified) {
                    replyText = "Maaf, akun Anda belum diverifikasi. Mohon tunggu verifikasi dari admin untuk dapat menggunakan bot ini. Jika Anda belum mendaftar, ketik /start.";
                } else {
                    replyText = "Selamat Dang di BangTegBot(Bang Warteg)😁, Berikut Daftar Perintah yang Tersedia:\n"
                    // + "____________________________________________\n"
                    // + "/Alamat_Kampus - Menampilkan informasi alamat kampus\n"
                    // + "/Info_Pendaftaran - Menampilkan informasi pendaftaran umum\n"
                    // + "____________________________________________\n\n"
                    + "Kata kunci yang tersedia:\n"
                    + getAllKeywords();  // ← ini akan mengambil semua kata dari tabel kamut

                }
            } else {
                if (!isMemberVerified) {
                    replyText = "Maaf, akun Anda belum diverifikasi. Mohon tunggu verifikasi dari admin untuk dapat menggunakan bot ini. Jika Anda belum mendaftar, ketik /start.";
                } else {
                    // Logika balasan otomatis dari kamut
                    replyText = getReplyFromKeyword(receivedMessage);
                    if (replyText == null) {
                        replyText = "Maaf, saya tidak mengerti pertanyaan Anda. Silakan coba kata kunci lain atau hubungi admin. klik /help untuk mencari daftar perintah";
                    }
                }
            }

            SendMessage reply = new SendMessage();
            reply.setChatId(chatId);
            reply.setText(replyText);

            try {
                execute(reply);
                // Simpan pesan keluar ke chat_history
                saveMessageToHistory(chatId, "out", replyText);
            } catch (TelegramApiException e) {
                System.err.println("Failed to send message to " + chatId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getReplyFromKeyword(String message) {
        MySQLConnector db = new MySQLConnector();
        String reply = null;
        try {
            db.connect();
            String sql = "SELECT jawaban FROM kamut WHERE kata = ?";
            PreparedStatement stmt = db.getMysqlConnection().prepareStatement(sql);
            stmt.setString(1, message);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                reply = rs.getString("jawaban");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting reply from keyword: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
        return reply;
    }

    private void saveMessageToHistory(String chatId, String type, String message) {
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "INSERT INTO chat_history (chat_id, message_type, message_text) VALUES (?, ?, ?)";
            PreparedStatement stmt = db.getMysqlConnection().prepareStatement(sql);
            stmt.setString(1, chatId);
            stmt.setString(2, type);
            stmt.setString(3, message);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error saving message to history: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
    }

    private boolean isMemberRegistered(String chatId) {
        MySQLConnector db = new MySQLConnector();
        boolean registered = false;
        try {
            db.connect();
            String sql = "SELECT COUNT(*) FROM members WHERE chat_id = ?";
            PreparedStatement stmt = db.getMysqlConnection().prepareStatement(sql);
            stmt.setString(1, chatId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                registered = true;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error checking member registration: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
        return registered;
    }

    private boolean isMemberVerified(String chatId) {
        MySQLConnector db = new MySQLConnector();
        boolean verified = false;
        try {
            db.connect();
            String sql = "SELECT is_verified FROM members WHERE chat_id = ?";
            PreparedStatement stmt = db.getMysqlConnection().prepareStatement(sql);
            stmt.setString(1, chatId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                verified = rs.getBoolean("is_verified");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error checking member verification: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
        return verified;
    }

    private void registerNewMember(String chatId, String username) {
        MySQLConnector db = new MySQLConnector();
        try {
            db.connect();
            String sql = "INSERT INTO members (chat_id, username, is_verified) VALUES (?, ?, FALSE)";
            PreparedStatement stmt = db.getMysqlConnection().prepareStatement(sql);
            stmt.setString(1, chatId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            stmt.close();
            System.out.println("New member registered: " + username + " (" + chatId + ")");
        } catch (SQLException e) {
            System.err.println("Error registering new member: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
    }
}
