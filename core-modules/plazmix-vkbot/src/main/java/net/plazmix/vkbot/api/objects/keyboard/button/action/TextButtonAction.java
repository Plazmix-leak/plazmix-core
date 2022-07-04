package net.plazmix.vkbot.api.objects.keyboard.button.action;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TextButtonAction extends KeyboardButtonAction {

    private final String label;

    public TextButtonAction(@NonNull String payload, @NonNull String label) {
        super(payload);

        this.label = label;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject params = new JsonObject();

        params.addProperty("type", "text");
        params.addProperty("label", label);

        return params;
    }
}
