package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BungeeHandler;

import java.net.InetSocketAddress;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerConnectPacket extends Packet<BungeeHandler> {

    private String playerName;
    private UUID playerUuid;

    private InetSocketAddress socketAddress;

    private String server;

    private int version;

    private boolean onlineMode;
    private boolean alreadyPlaying;


    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeUUID(playerUuid, byteBuf);

        BufferedQuery.writeString(socketAddress.getHostName(), byteBuf);
        BufferedQuery.writeVarInt(socketAddress.getPort(), byteBuf);

        BufferedQuery.writeString(server, byteBuf);

        BufferedQuery.writeVarInt(version, byteBuf);

        BufferedQuery.writeBoolean(onlineMode, byteBuf);
        BufferedQuery.writeBoolean(alreadyPlaying, byteBuf);
    }
}
