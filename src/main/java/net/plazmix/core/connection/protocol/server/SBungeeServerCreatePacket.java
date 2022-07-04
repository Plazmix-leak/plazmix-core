package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class SBungeeServerCreatePacket extends Packet<BungeeServer> {

    private String serverName;

    private String serverHost;
    private int serverPort;

    @Override
    public void writePacket(ByteBuf byteBuf) {
        BufferedQuery.writeString(serverName, byteBuf);
        BufferedQuery.writeString(serverHost, byteBuf);
        BufferedQuery.writeVarInt(serverPort, byteBuf);
    }
}
