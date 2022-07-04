package net.plazmix.vkbot.api.objects.keyboard;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.vkbot.api.objects.keyboard.button.KeyboardButton;
import net.plazmix.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import net.plazmix.vkbot.api.objects.keyboard.button.action.KeyboardButtonAction;
import net.plazmix.vkbot.api.objects.message.Message;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class Keyboard {

    private final Message message;

    private boolean oneTime, inline;

    public Keyboard(@NonNull Message message) {
        this.message = message;
    }

    private final TIntObjectMap<List<KeyboardButton>> buttons = new TIntObjectHashMap<>();

    public Keyboard oneTime(boolean oneTime) {
        this.oneTime = oneTime;
        return this;
    }

    public Keyboard oneTime() {
        return oneTime(true);
    }

    public Keyboard inline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public Keyboard inline() {
        return inline(true);
    }

    public Keyboard button(int line, @NonNull KeyboardButtonAction action) {
        addButton(line, new KeyboardButton(this, null, action));
        return this;
    }

    public Keyboard button(@NonNull KeyboardButtonColor color, int line, @NonNull KeyboardButtonAction action) {
        addButton(line, new KeyboardButton(this, color, action));
        return this;
    }

    public KeyboardButton button(int line) {
        KeyboardButton button = new KeyboardButton(this);
        addButton(line, button);

        return button;
    }

    private void addButton(int line, @NonNull KeyboardButton button) {
        this.buttons.putIfAbsent(line, new ArrayList<>());
        this.buttons.get(line).add(button);
    }

    public Message message() {
        return message;
    }

}
