package net.plazmix.vkbot.api.objects.keyboard.button;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.vkbot.api.objects.keyboard.Keyboard;
import net.plazmix.vkbot.api.objects.keyboard.button.action.KeyboardButtonAction;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class KeyboardButton {

    private final Keyboard keyboard;

    private KeyboardButtonColor color;

    private KeyboardButtonAction action;

    public KeyboardButton(@NonNull KeyboardButtonColor color, @NonNull KeyboardButtonAction action) {
        this(null, color, action);
    }

    public KeyboardButton() {
        this(null, null, null);
    }

    public KeyboardButton color(@NonNull KeyboardButtonColor color) {
        this.color = color;
        return this;
    }

    public KeyboardButton action(@NonNull KeyboardButtonAction action) {
        this.action = action;
        return this;
    }

    public Keyboard keyboard() {
        return keyboard;
    }

    public JsonObject toJsonObject() {
        JsonObject params = new JsonObject();

        if (color != null) {
            params.addProperty("color", color.toString().toLowerCase());
        }

        params.add("action", action.convertToJson());

        return params;
    }

}
