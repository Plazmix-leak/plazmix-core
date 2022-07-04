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
public class SPlayerMutePacket extends Packet<BungeeServer> {

    private String ownerName;
    private String intruderName;

    private String reason;

    private long expireTimeMillis;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(ownerName, byteBuf);
        BufferedQuery.writeString(intruderName, byteBuf);

        BufferedQuery.writeString(reason, byteBuf);

        BufferedQuery.writeVarLong(expireTimeMillis, byteBuf);
    }

}
