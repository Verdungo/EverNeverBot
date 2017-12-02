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

    private static final long ROUND_MINUTES = 3*60*1000; //3 mins
    private static final long TICK_INTERVAL = 5*1000; //5 secs
    //private static final long ROUND_MINUTES = 10*1000;
    //private static final long TICK_INTERVAL = 1*1000;

    private boolean active = false;
    private String question = "";
    private String gameInitiator;
    private NeverEverBot bot;
    private long chatId;
    private Integer headerMessageId;
    private long remainingTime;
    private Map<String, Boolean> answers;
    private Map<String, Integer> chatScores;

    public Game(NeverEverBot bot, long chatId, String gameInitiator, Map<String, Integer> chatScores) {
        this.bot = bot;
        this.chatId = chatId;
        remainingTime = ROUND_MINUTES;
        answers = new LinkedHashMap<>();
        this.chatScores = chatScores;
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
        answers.put(gameInitiator, true);

        this.start();
    }

    public void setActive(boolean active) {
        this.active = active;
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

        StringBuilder result = new StringBuilder("Игра закончена!\nОбщий счет:\n");
        for (Entry<String, Integer> s : chatScores.entrySet()) {
            result.append(s.getKey()).append(": ").append(s.getValue()).append("\n");
        }
        SendMessage msg = new SendMessage()
                .setChatId(chatId)
                .setText(result.toString());

        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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

        if (active) bot.endGame(chatId);
    }

    private String getRemainingTime(long mills) {
        long minutes = mills / (60 * 1000);
        long seconds = (mills / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void updateHeaderMessage(){
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
    }

    private String formMessage(boolean withTime){
        StringBuilder sb = new StringBuilder(question).append("\n");
        if (withTime) sb.append("(").append(getRemainingTime(remainingTime)).append(")\n");
        for (Map.Entry<String, Boolean> answer: answers.entrySet()){
            sb.append(answer.getKey()).append(" ").append(answer.getValue() ? "❌" : "✔").append("\n");
        }
        return sb.toString();
    }

    public void putAnswer(String player, boolean ans) {
        answers.put(player, ans);
    }

    private void calcScores() {
        boolean initiatorLoser = !answers.containsValue(false);
        for (Entry<String, Boolean> answer : answers.entrySet()) {
            Integer playerScore = (!answer.getValue() || (answer.getKey().equals(gameInitiator) && initiatorLoser)) ? 0 : 1;
            if (!chatScores.containsKey(answer.getKey())) {
                chatScores.put(answer.getKey(), playerScore);
            }
            else {
                chatScores.put(answer.getKey(), chatScores.get(answer.getKey())+playerScore);
            }
        }
    }
}
