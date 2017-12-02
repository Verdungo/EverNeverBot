package Bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class AboutCommand extends BotCommand {

    public AboutCommand() {
        super("about", "О боте");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        StringBuilder about = new StringBuilder();

        about.append("Привет!\nЯ бот для игры в \"Я никогда не\".\n\n")
                .append("Для начала игры просто напишите сообщение, начинающееся со слов \"Я никогда не\" или \"Я ни разу не\". ")
                .append("Всем участникам чата будет предложено подтвердить, или опровергнуть это утверждение в течение 3 минут. \n\n")
                .append("Подсчет очков:\n")
                .append("Каждый, подтвердивший утверждение(*никогда*), получает один балл. ")
                .append("Ведущий не получит балл, если не нашлось игрока, который опровергнет утверждение (*бывало*).\n\n")
                .append("Цель игры - получить как можно больше баллов, предлагая ситуации, в которых ты не был никогда, но большинство других игроков были.");

        SendMessage aboutMessage = new SendMessage().setChatId(chat.getId()).setText(about.toString());

        try {
            absSender.execute(aboutMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
