package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class SPlayerTagPrefixUpdatePacket extends Packet<BukkitServer> {

    private String playerName;
    private String prefix;

    private boolean global;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeString(prefix, byteBuf);

        BufferedQuery.writeBoolean(global, byteBuf);
    }
}
