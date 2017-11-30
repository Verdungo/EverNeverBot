package Bot;

import Game.Game;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeverEverBot extends TelegramLongPollingCommandBot {

    private static final String botToken = "462721270:AAHLtx9MXbeYivPP_SyQ9Sg6GeX3nMEsm54";
    private static final String botUsername = "EverNeverBot";
    private Map<Long,Game> games = new HashMap<>();

    public NeverEverBot() {
        super(botUsername);
        // не нужна команда
        //registerCommands();
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

            // начало игры
            if (update.getMessage().getText().toLowerCase().startsWith("я никогда не")
                    || update.getMessage().getText().toLowerCase().startsWith("я ни разу не")){
                long chatId = update.getMessage().getChatId();
                String question = update.getMessage().getText();
                Message resultMessage = new Message();

                SendMessage msg = new SendMessage()
                        .setChatId(chatId)
                        .setText("Новый вопрос - \"" + question + "\"");

                InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                List<InlineKeyboardButton> kbRow = new ArrayList<>();
                kbRow.add(new InlineKeyboardButton().setCallbackData("never").setText("Никогда"));
                kbRow.add(new InlineKeyboardButton().setCallbackData("ever").setText("Бывало"));
                keyboard.add(kbRow);
                ikm.setKeyboard(keyboard);
                msg.setReplyMarkup(ikm);

                try {
                    resultMessage = execute(msg);
                    int i = 1;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                startGame(chatId, resultMessage.getMessageId(), question);
            }

            // закончить игру
            if (update.getMessage().getText().toLowerCase().startsWith("стоп игра")){
                long chatId = update.getMessage().getChatId();

                SendMessage msg = new SendMessage()
                        .setChatId(chatId)
                        .setText("Игра закончена!");

                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                endGame(chatId);
            }
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void registerCommands() {
        /*BotCommand newQuestionCommand = new BotCommand("never","Я никогда не...") {
            @Override
            public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
                StringBuilder sb = new StringBuilder();
                for (String s :
                        arguments) {
                    sb.append(s).append(" ");
                }

                SendMessage msg = new SendMessage()
                        .setText("Новый вопрос - \""+ sb.toString().trim() +"\"")
                        .setChatId(chat.getId());

                try {
                    absSender.execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                startGame(chat.getId(), sb.toString().trim());
            }
        };
        register(newQuestionCommand);
        */
    }

    private void startGame(long chatId, Integer messageId, String question){
        Game gameToStart = new Game(this, chatId, messageId);
        games.put(chatId, gameToStart);
        gameToStart.startGame(question);
    }

    private void endGame(long chatId){
        Game gameToEnd = games.remove(chatId);
        gameToEnd.endGame();
    }

}
