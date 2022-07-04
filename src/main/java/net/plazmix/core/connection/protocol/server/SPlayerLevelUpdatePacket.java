package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerLevelUpdatePacket extends Packet<BukkitServer> {

    private String playerName;
    private int experience;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeVarInt(experience, byteBuf);
    }

}
