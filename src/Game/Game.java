package Game;

import Bot.NeverEverBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Game {
    private boolean active = false;
    private String question = "";
    private static NeverEverBot bot;

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        bot = new NeverEverBot();

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }


    }

    public void startGame(String q) {
        question = q;
    }


}
