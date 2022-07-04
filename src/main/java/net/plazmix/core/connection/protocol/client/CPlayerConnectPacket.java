package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

import java.net.InetSocketAddress;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CPlayerConnectPacket extends Packet<BungeeServer> {

    private String playerName;
    private UUID playerUuid;

    private InetSocketAddress socketAddress;

    private String server;

    private int version;

    private boolean onlineMode;
    private boolean alreadyPlaying;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.playerUuid = BufferedQuery.readUUID(byteBuf);

        this.socketAddress = new InetSocketAddress(BufferedQuery.readString(byteBuf), BufferedQuery.readVarInt(byteBuf));

        this.server = BufferedQuery.readString(byteBuf);

        this.version = BufferedQuery.readVarInt(byteBuf);

        this.onlineMode = BufferedQuery.readBoolean(byteBuf);
        this.alreadyPlaying = BufferedQuery.readBoolean(byteBuf);
    }

    @Override
    public void handle(@NonNull BungeeServer bungeeServer) throws Exception {
        bungeeServer.handle(this);
    }
}
