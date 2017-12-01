package Bot;

import Game.Game;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.HashMap;
import java.util.Map;

public class NeverEverBot extends TelegramLongPollingCommandBot {

    private static final String botToken = "462721270:AAHLtx9MXbeYivPP_SyQ9Sg6GeX3nMEsm54";
    private static final String botUsername = "EverNeverBot";
    private Map<Long,Game> games = new HashMap<>();

    public NeverEverBot() {
        super(botUsername);
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        TelegramLongPollingCommandBot bot = new NeverEverBot();

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()){
            long chatId = update.getMessage().getChatId();

            // начало игры
            if (update.getMessage().getText().toLowerCase().startsWith("я никогда не")
                    || update.getMessage().getText().toLowerCase().startsWith("я ни разу не")){

                String initiator = update.getMessage().getFrom().getFirstName();
                String question = update.getMessage().getText();

                startGame(chatId, question, initiator);
            }

            // закончить игру
            if (update.getMessage().getText().toLowerCase().startsWith("стоп игра")){
                endGame(chatId);
            }
        }
        else if (update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String player = update.getCallbackQuery().getFrom().getFirstName();
            switch (update.getCallbackQuery().getData()) {
                case "never" :
                    games.get(chatId).putAnswer(player, true);
                    break;
                case "ever":
                    games.get(chatId).putAnswer(player, false);
                    break;
                default:
            }

            // чтобы не крутился бесконечно спиннер
            try {
                execute(new AnswerCallbackQuery().setCallbackQueryId(update.getCallbackQuery().getId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void startGame(long chatId,  String question, String initiator){
        Game gameToStart = new Game(this, chatId, initiator);
        games.put(chatId, gameToStart);
        gameToStart.startGame(question);
    }

    private void endGame(long chatId){
        Game gameToEnd = games.remove(chatId);
        gameToEnd.endGame();
    }

}
