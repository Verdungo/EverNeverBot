package Bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class NeverEverBot extends TelegramLongPollingCommandBot {

    private static final String botToken = "462721270:AAHLtx9MXbeYivPP_SyQ9Sg6GeX3nMEsm54";
    private static final String botUsername = "EverNeverBot";

    public NeverEverBot() {
        super(botUsername);
        registerCommands();
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

    private void registerCommands() {
        BotCommand newQuestionCommand = new BotCommand("never","Я никогда не...") {
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
            }
        };
        register(newQuestionCommand);
    }

}
