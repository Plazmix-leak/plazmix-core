package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class SRestartServerPacket extends Packet<AbstractServer> {

    private String restartReason;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeString(restartReason, byteBuf);
    }
}
