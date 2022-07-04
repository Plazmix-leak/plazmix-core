package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.connection.player.CorePlayer;

@RequiredArgsConstructor
@Getter
public class PlayerJoinEvent extends Event {

    private final CorePlayer corePlayer;
}
