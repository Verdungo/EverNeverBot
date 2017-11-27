package Bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class NeverEverBot extends TelegramLongPollingCommandBot {

    private static final String botToken = "462721270:AAHLtx9MXbeYivPP_SyQ9Sg6GeX3nMEsm54";
    private static final String botUsername = "EverNeverBot";

    public NeverEverBot() {
        super(botUsername);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        SendMessage message = new SendMessage()
                .setText("Я получил сообщение!")
                .setChatId(update.getMessage().getChatId());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new NeverEverBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
