package net.plazmix.core.protocol.handshake;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

import java.net.InetSocketAddress;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Handshake extends Packet<HandshakeHandler> {

    private String name;
    private String motd;

    private InetSocketAddress inetSocketAddress;

    private int versionId;

    private boolean bungee;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(name, byteBuf);
        BufferedQuery.writeString(motd, byteBuf);

        BufferedQuery.writeString(inetSocketAddress.getHostString(), byteBuf);
        BufferedQuery.writeVarInt(inetSocketAddress.getPort(), byteBuf);

        BufferedQuery.writeVarInt(versionId, byteBuf);

        BufferedQuery.writeBoolean(bungee, byteBuf);
    }

    @Override
    public void readPacket(ByteBuf byteBuf) throws Exception {
        this.name = BufferedQuery.readString(byteBuf);
        this.motd = BufferedQuery.readString(byteBuf);

        this.inetSocketAddress = new InetSocketAddress(BufferedQuery.readString(byteBuf), BufferedQuery.readVarInt(byteBuf));

        this.versionId = BufferedQuery.readVarInt(byteBuf);

        this.bungee = BufferedQuery.readBoolean(byteBuf);
    }

    @Override
    public void handle(HandshakeHandler handshakeHandler) throws Exception {
        handshakeHandler.handle(this);
    }
}
