package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BungeeHandler;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CBungeeServerCreatePacket extends Packet<BungeeHandler> {

    private String serverName;

    private String serverHost;
    private int serverPort;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.serverName = BufferedQuery.readString(byteBuf);

        this.serverHost = BufferedQuery.readString(byteBuf);
        this.serverPort = BufferedQuery.readVarInt(byteBuf);
    }

    @Override
    public void handle(@NonNull BungeeHandler bungeeHandler) {
        bungeeHandler.handle(this);
    }
}
