package Game;

import Bot.NeverEverBot;
import Bot.NeverEverKeyboard;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public class Game extends Thread{

    private static final long ROUND_MINUTES = 5;

    private boolean active = false;
    private String question = "";
    private String gameInitiator;
    private NeverEverBot bot;
    private long chatId;
    private Integer headerMessageId;
    private long remainingTime;
    private Map<String, Boolean> answers;
    private Map<String, Integer> scores;

    public Game(NeverEverBot bot, long chatId, String gameInitiator) {
        this.bot = bot;
        this.chatId = chatId;
        remainingTime = ROUND_MINUTES * 1000*60;
        answers = new LinkedHashMap<>();
        scores = new LinkedHashMap<>();
        this.gameInitiator = gameInitiator;

        SendMessage msg = new SendMessage()
                .setChatId(chatId)
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
        this.start();
    }

    public void endGame(){
        active = false;
        calcScores();

        EditMessageText emsg = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(headerMessageId)
                .setText(formMessage(false));

        try {
            bot.execute(emsg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        SendMessage msg = new SendMessage()
                .setChatId(chatId)
                .setText("Игра закончена!\n" + );

        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        // main work here
        // check updates, calc scores(?), countdown
        do {
            EditMessageText msg = new EditMessageText()
                    .setChatId(chatId)
                    .setMessageId(headerMessageId)
                    .setReplyMarkup(NeverEverKeyboard.getKeybboard())
                    .setText(formMessage(true));

            try {
                bot.execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            remainingTime-=1000;
        } while (active);

        this.interrupt();
        endGame();
    }

    private String getRemainingTime(long mills) {
        long minutes = mills / (60 * 1000);
        long seconds = (mills / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private String formMessage(boolean withTime){
        StringBuilder sb = new StringBuilder(question).append("\n");
        if (withTime) sb.append("(" + getRemainingTime(remainingTime)+")\n");
        for (Map.Entry<String, Boolean> answer: answers.entrySet()){
            sb.append(answer.getKey() + " " + (answer.getValue() ? "❌" : "✔") + "\n");
        }

        return sb.toString();
    }

    public void putAnswer(String player, boolean ans) {
        answers.put(player, ans);
    }

    private void calcScores() {
        boolean initiatorLoser = !answers.containsValue(false);
        for (Entry<String, Boolean> answer : answers.entrySet()) {
            addScore(answer.getKey(), (!answer.getValue() || (answer.getKey().equals(gameInitiator) && initiatorLoser)) ? 0 : 1);
        }
    }

    private void addScore(String player, Integer score){
        scores.put(player, score);
    }

}
