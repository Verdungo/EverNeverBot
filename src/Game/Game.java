package Game;

import Bot.NeverEverBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Game extends Thread{
    private boolean active = false;
    private String question = "";
    private NeverEverBot bot;
    private long chatId;
    private int counter;
    private Integer headerMessageId;

    public Game(NeverEverBot bot, long chatId, Integer headerMessageId) {
        this.bot = bot;
        this.chatId = chatId;
        this.headerMessageId = headerMessageId;

        this.start();
    }

    public void startGame(String q) {
        question = q;
        active = true;
    }

    public void endGame(){
        //this.interrupt(); // ?????????
        active = false;
    }

    public void run() {
        // main work here
        // check updates, calc scores(?), countdown
        do {
            // =============================================================== DEBUG
            EditMessageText msg = new EditMessageText()
                    .setChatId(chatId)
                    .setMessageId(headerMessageId)
                    .setText(String.valueOf(counter++));
            /*SendMessage msg = new SendMessage()
                    .setChatId(chatId)
                    .setText(String.valueOf(counter++));*/
            try {
                bot.execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            // =============================================================== DEBUG

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (active);
        this.interrupt();
    }
}
