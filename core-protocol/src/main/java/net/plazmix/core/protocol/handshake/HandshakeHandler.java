package net.plazmix.core.protocol.handshake;

import net.plazmix.core.protocol.handler.PacketHandler;

public interface HandshakeHandler extends PacketHandler {

    default void handle(Handshake handshakePacket) {
    }

}
