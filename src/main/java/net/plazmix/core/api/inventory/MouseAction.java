package net.plazmix.core.api.inventory;

import lombok.NonNull;

import java.util.Arrays;

public enum MouseAction {

    LEFT,
    SHIFT_LEFT,
    RIGHT,
    SHIFT_RIGHT,
    WINDOW_BORDER_LEFT,
    WINDOW_BORDER_RIGHT,
    MIDDLE,
    NUMBER_KEY,
    DOUBLE_CLICK,
    DROP,
    CONTROL_DROP,
    CREATIVE,
    UNKNOWN;


    public static final MouseAction[] MOUSE_ACTIONS = values();

    public static MouseAction getMouseAction(@NonNull String actionName) {
        return Arrays.stream(MOUSE_ACTIONS).filter(mouseAction -> mouseAction.name().equalsIgnoreCase(actionName))
                .findFirst().orElse(null);
    }

}