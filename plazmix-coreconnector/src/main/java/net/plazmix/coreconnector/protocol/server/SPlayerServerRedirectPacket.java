package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.AbstractServerHandler;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerServerRedirectPacket extends Packet<AbstractServerHandler> {

    private String playerName;
    private String serverName;

    private boolean redirect;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeString(serverName, byteBuf);

        BufferedQuery.writeBoolean(redirect,  byteBuf);
    }

}
