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
@NoArgsConstructor
@Data
public class CServerMotdPacket extends Packet<AbstractServer> {

    private String motd;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.motd = BufferedQuery.readString(byteBuf);
    }

    @Override
    public void handle(@NonNull AbstractServer abstractServer) throws Exception {
        abstractServer.handle(this);
    }

}
