package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BungeeHandler;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerLoginPacket extends Packet<BungeeHandler> {

    private String playerName;
    private UUID playerUuid;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeUUID(playerUuid, byteBuf);
    }
}
