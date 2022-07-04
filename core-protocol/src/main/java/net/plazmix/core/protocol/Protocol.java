package net.plazmix.core.protocol;

import java.util.function.Supplier;

public enum Protocol {

    HANDSHAKE, PLAY;

    public final PacketMapper TO_CLIENT = new PacketMapper();
    public final PacketMapper TO_SERVER = new PacketMapper();

    public <T extends Packet<?>> void registerAll(int id, Class<T> cls, Supplier<T> supplier) {
        TO_CLIENT.registerPacket(id, cls, supplier);
        TO_SERVER.registerPacket(id, cls, supplier);
    }
}
