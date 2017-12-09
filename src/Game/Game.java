package Game;

import Bot.NeverEverBot;
import Bot.NeverEverKeyboard;
import database.Chat;
import database.DBAdapter;
import database.Score;
import database.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public class Game extends Thread{

    private static final long ROUND_MINUTES = 3*60*1000; //3 mins
    private static final long TICK_INTERVAL = 5*1000; //5 secs
    //private static final long ROUND_MINUTES = 10*1000;
    //private static final long TICK_INTERVAL = 1*1000;

    private boolean active = false;
    private String question = "";
    private User gameInitiator;
    private NeverEverBot bot;
    private Chat chat;
    private Integer headerMessageId;
    private long remainingTime = ROUND_MINUTES;
    private Map<User, Boolean> answers;
    private Map<User, Integer> chatScores;
    private DBAdapter dbAdapter;

    public Game(NeverEverBot bot, Chat chat, User gameInitiator) {
        this.bot = bot;
        this.chat = chat;
        this.gameInitiator = gameInitiator;

        dbAdapter = new DBAdapter();
        chatScores = dbAdapter.getChatScores(chat);

        answers = new LinkedHashMap<>();

        SendMessage msg = new SendMessage()
                .setChatId(chat.getId())
                .setText("Новый вопрос - \"" + question + "\"")
                .setReplyMarkup(NeverEverKeyboard.getKeybboard());

        try {
            headerMessageId = bot.execute(msg).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void startGame(String q) {
        question = q;
        active = true;
        answers.put(gameInitiator, true);
        //TODO: тут обновлять имя чата?
        dbAdapter.updateChat(chat);

        this.start();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void endGame(){
        active = false;
        calcScores();

        EditMessageText emsg = new EditMessageText()
                .setChatId(chat.getId())
                .setMessageId(headerMessageId)
                .setText(formMessage(false));

        try {
            bot.execute(emsg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        StringBuilder result = new StringBuilder("Игра закончена!\nОбщий счет:\n");
        for (Entry<User, Integer> e : chatScores.entrySet()) {
            result.append(e.getKey().getName()).append(": ").append(e.getValue()).append("\n");
        }

        SendMessage msg = new SendMessage()
                .setChatId(chat.getId())
                .setText(result.toString());

        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        dbAdapter.updateScores(chat, chatScores);
    }

    public void run() {
        // main work here
        do {
            updateHeaderMessage();

            try {
                sleep(TICK_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            remainingTime-=TICK_INTERVAL;
        } while (active && remainingTime>0);

        if (active) bot.endGame(chat.getId());
    }

    private String getRemainingTime(long mills) {
        long minutes = mills / (60 * 1000);
        long seconds = (mills / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void updateHeaderMessage(){
        EditMessageText msg = new EditMessageText()
                .setChatId(chat.getId())
                .setMessageId(headerMessageId)
                .setReplyMarkup(NeverEverKeyboard.getKeybboard())
                .setText(formMessage(true));

        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String formMessage(boolean withTime){
        StringBuilder sb = new StringBuilder(question).append("\n");
        if (withTime) sb.append("(").append(getRemainingTime(remainingTime)).append(")\n");
        for (Map.Entry<User, Boolean> answer: answers.entrySet()){
            sb.append(answer.getKey().getName()).append(" ").append(answer.getValue() ? "❌" : "✔").append("\n");
        }
        return sb.toString();
    }

    public void putAnswer(User player, boolean ans) {
        answers.put(player, ans);
    }

    private void calcScores() {
        boolean initiatorLoser = !answers.containsValue(false);
        for (Entry<User, Boolean> answer : answers.entrySet()) {
            User player = answer.getKey();
            Integer score = (!answer.getValue() || (answer.getKey().equals(gameInitiator) && initiatorLoser)) ? 0 : 1;

            if (chatScores.containsKey(player)) {
                chatScores.put(player, chatScores.get(player)+score);
            }
            else {
                chatScores.put(player, score);
            }

            //TODO: надо ли обновлять имя игрока?
            dbAdapter.updateUser(player);
        }
    }
}
