// MultipleFiles/Main.java
package com.bot.consultant;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        System.out.println("WartegBot Sedang Dijalankan..");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Dashboard());
            System.out.println("Telegram Bot started successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Failed to start Telegram Bot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
