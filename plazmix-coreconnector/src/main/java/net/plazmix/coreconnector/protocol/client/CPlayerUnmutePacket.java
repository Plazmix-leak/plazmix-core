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
@NoArgsConstructor
@Data
public class CPlayerUnmutePacket extends Packet<BungeeHandler> {

    private String intruderName;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.intruderName = BufferedQuery.readString(byteBuf);
    }

    @Override
    public void handle(@NonNull BungeeHandler bungeeHandler) throws Exception {
        bungeeHandler.handle(this);
    }
}
