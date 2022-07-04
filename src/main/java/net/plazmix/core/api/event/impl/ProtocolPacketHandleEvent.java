package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.protocol.handler.PacketHandler;

@RequiredArgsConstructor
@Getter
public class ProtocolPacketHandleEvent extends Event {

    private final PacketHandler packetHandler;
    private final Packet<?> packet;
}
