package net.plazmix.coreconnector.direction.bungee.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;

@RequiredArgsConstructor
@Getter
public class PlayerConnectedEvent extends Event {

    private final ProxiedPlayer player;
    private final Server server;
}
