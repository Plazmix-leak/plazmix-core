package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class SPlayerKickPacket extends Packet<BungeeServer> {

    private String playerName;
    private String reasonMessage;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeString(reasonMessage, byteBuf);
    }

}
