package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

@RequiredArgsConstructor
@Getter
public class PlayerChatEvent extends Event {

    private final CorePlayer corePlayer;

    private final ChatMessageType chatMessageType;
    private final String message;

    private final BukkitServer bukkitServer;
}
