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
public class SPlayerTagSuffixUpdatePacket extends Packet<BukkitServer> {

    private String playerName;
    private String suffix;

    private boolean global;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeString(suffix, byteBuf);

        BufferedQuery.writeBoolean(global, byteBuf);
    }
}
