package net.plazmix.core.protocol;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.plazmix.core.protocol.handler.PacketHandler;

public abstract class Packet<T extends PacketHandler> {

    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        //throw new UnsupportedOperationException();
    }

    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        //throw new UnsupportedOperationException();
    }

    public void handle(@NonNull T t) throws Exception {
        //throw new UnsupportedOperationException();
    }

}
