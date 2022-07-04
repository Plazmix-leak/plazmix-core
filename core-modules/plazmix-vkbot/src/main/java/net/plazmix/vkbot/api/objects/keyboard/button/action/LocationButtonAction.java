package net.plazmix.vkbot.api.objects.keyboard.button.action;

import com.google.gson.JsonObject;
import lombok.NonNull;

public class LocationButtonAction extends KeyboardButtonAction {

    public LocationButtonAction(@NonNull String payload) {
        super(payload);
    }

    @Override
    protected JsonObject toJsonObject() {
        JsonObject params = new JsonObject();

        params.addProperty("type", "location");

        return params;
    }
}
