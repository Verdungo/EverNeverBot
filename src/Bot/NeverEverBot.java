package Bot;

import Game.Game;
import database.Chat;
import database.User;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.HashMap;
import java.util.Map;

public class NeverEverBot extends TelegramLongPollingCommandBot {

    private static final String botToken = "<your_bot_token_here>";
    private static final String botUsername = "EverNeverBot";
    private Map<Long, Game> games = new HashMap<>();

    public NeverEverBot() {
        super(botUsername);
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        TelegramLongPollingCommandBot bot = new NeverEverBot();
        bot.register(new AboutCommand());

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
            String chatName = update.getMessage().getChat().getTitle();

            // начало игры
            if (update.getMessage().getText().toLowerCase().startsWith("я никогда не")
                    || update.getMessage().getText().toLowerCase().startsWith("я ни разу не")){

                User initiator = new User(update.getMessage().getFrom().getId(), update.getMessage().getFrom().getFirstName());
                String question = update.getMessage().getText();

                startGame(chatId, chatName, question, initiator);
            }

            // закончить игру
            if (update.getMessage().getText().toLowerCase().startsWith("стоп игра")){
                endGame(chatId);
            }
        }
        else if (update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            //Chat chat = new Chat(update.getCallbackQuery().getMessage().getChatId(),update.getCallbackQuery().getMessage().getChat().getTitle());

            User player = new User(update.getCallbackQuery().getFrom().getId(), update.getCallbackQuery().getFrom().getFirstName());

            switch (update.getCallbackQuery().getData()) {
                case "never" :
                    games.get(chatId).putAnswer(player, true);
                    break;
                case "ever":
                    games.get(chatId).putAnswer(player, false);
                    break;
                default:
            }

            games.get(chatId).updateHeaderMessage();

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

    private void startGame(Long chatId, String chatName,  String question, User initiator){
        Chat chat = new Chat(chatId, chatName);
        if (!games.containsKey(chatId)) {
            //очки для чата

            Game gameToStart = new Game(this, chat, initiator);
            games.put(chatId, gameToStart);
            gameToStart.startGame(question);

        }
        else {
            // игра уже есть!
            SendMessage msg = new SendMessage()
                    .setChatId(chatId)
                    .setText("Игра уже идет!");

            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            //TODO: переопубликовать вопрос?
        }
    }

    public void endGame(Long chatId){
        if (games.containsKey(chatId)) {
            Game gameToEnd = games.remove(chatId);
            gameToEnd.endGame();
        }
        else {
            //нечего заканчивать
            SendMessage msg = new SendMessage()
                    .setChatId(chatId)
                    .setText("Нет активных игр.");

            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
