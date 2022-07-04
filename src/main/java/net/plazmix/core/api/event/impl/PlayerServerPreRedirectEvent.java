package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.CancellableEvent;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

@RequiredArgsConstructor
@Getter
public class PlayerServerPreRedirectEvent extends CancellableEvent {

    private final CorePlayer corePlayer;
    private final BukkitServer serverTo, serverFrom;
}
