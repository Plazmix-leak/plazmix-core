package net.plazmix.core.api.command;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.component.TextComponent;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.language.LocalizedSender;

public interface CommandSender extends LocalizedSender {

    String getName();

    String getDisplayName();

    CommandSendingType getCommandSendingType();

    Group getGroup();

    default void sendMessage(@NonNull String message) {
        sendMessage(ChatMessageType.CHAT, message);
    }

    default void sendMessage(@NonNull String... messages) {
        sendMessage(ChatMessageType.CHAT, messages);
    }

    default void sendMessage(@NonNull ChatMessageType messageType, @NonNull String message) {
        sendMessage(messageType, message.split("\n"));
    }

    default void sendMessage(@NonNull ChatMessageType messageType, @NonNull String... messages) {
        for (String message : messages) {
            sendMessage(messageType, TextComponent.fromLegacyText(message));
        }
    }

    void sendMessage(@NonNull ChatMessageType messageType, BaseComponent[] baseComponents);
}
