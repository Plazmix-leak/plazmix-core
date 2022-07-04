package net.plazmix.vkbot.api.handler;

import lombok.NonNull;

public interface SessionMessageHandler {

    /**
     * Вызывается при получении сообщения
     *
     * @param messageBody - сообщение
     */
    void onMessage(@NonNull String messageBody);
}
