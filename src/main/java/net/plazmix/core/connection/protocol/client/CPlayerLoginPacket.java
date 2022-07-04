package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CPlayerLoginPacket extends Packet<BungeeServer> {

    private String playerName;
    private UUID playerUuid;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.playerUuid = BufferedQuery.readUUID(byteBuf);
    }

    @Override
    public void handle(@NonNull BungeeServer bungeeServer) throws Exception {
        bungeeServer.handle(this);
    }
}
