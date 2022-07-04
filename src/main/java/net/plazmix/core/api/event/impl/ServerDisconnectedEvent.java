package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.connection.server.AbstractServer;

@RequiredArgsConstructor
@Getter
public class ServerDisconnectedEvent extends Event {

    private final AbstractServer server;
}
