package Bot;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class NeverEverKeyboard {

    public static InlineKeyboardMarkup getKeybboard(){

        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> kbRow = new ArrayList<>();
        kbRow.add(new InlineKeyboardButton().setCallbackData("never").setText("❌ Никогда"));
        kbRow.add(new InlineKeyboardButton().setCallbackData("ever").setText("✔ Бывало"));
        keyboard.add(kbRow);
        ikm.setKeyboard(keyboard);

        return ikm;

    }
}
