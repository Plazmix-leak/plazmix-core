package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CPlayerServerRedirectPacket extends Packet<AbstractServer> {

    private String playerName;
    private String serverName;

    private boolean redirect;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.serverName = BufferedQuery.readString(byteBuf);

        this.redirect = BufferedQuery.readBoolean(byteBuf);
    }

    @Override
    public void handle(@NonNull AbstractServer abstractServer) {
        abstractServer.handle(this);
    }

}
