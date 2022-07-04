package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BungeeHandler;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class CPlayerKickPacket extends Packet<BungeeHandler> {

    private String playerName;
    private String reasonMessage;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.reasonMessage = BufferedQuery.readString(byteBuf);
    }

    @Override
    public void handle(@NonNull BungeeHandler bungeeHandler) throws Exception {
        bungeeHandler.handle(this);
    }
}
