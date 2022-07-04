package net.plazmix.core.protocol.handler;

import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.Packet;

public interface PacketHandler {

    default void addHandler(PacketHandler handler) {
        // add handler to channel
    }

    default void removeHandler(PacketHandler handler) {
        // remove handler from channel
    }

    default void handle(Packet msg) throws Exception {
        msg.handle(this);

        // handle packet
    }

    default void channelActive(ChannelWrapper wrapper) {
        // active server or client channel
    }

    default void channelInactive() {
        // inactive server or client channel
    }

    default void handle(Throwable t) {
        // handle exception fucking error
    }

}
