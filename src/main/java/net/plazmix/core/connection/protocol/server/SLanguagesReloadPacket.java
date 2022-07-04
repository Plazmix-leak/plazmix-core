package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.protocol.Packet;

@NoArgsConstructor
public class SLanguagesReloadPacket extends Packet<AbstractServer> {

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
    }

}
