// MultipleFiles/TelegramSender.java
package com.bot.consultant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramSender {

    // Ganti dengan token asli bot kamu!
    private static final String BOT_TOKEN = "7870361348:AAEDwe6YATCbLV6xO_xi7NPc1eK-Gnei5pU"; // TOKEN BOT API ANDA

    public static boolean sendMessage(String chatId, String messageText) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Encode messageText to handle special characters
            String encodedMessageText = URLEncoder.encode(messageText, StandardCharsets.UTF_8.toString());
            String data = "chat_id=" + chatId + "&text=" + encodedMessageText;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Telegram response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Telegram API Response: " + response.toString());
                }
                return true;
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.err.println("Telegram API Error Response: " + response.toString());
                }
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error sending message to Telegram: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
