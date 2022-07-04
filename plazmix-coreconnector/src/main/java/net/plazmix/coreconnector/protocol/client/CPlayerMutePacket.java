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
public class CPlayerMutePacket extends Packet<BungeeHandler> {

    private String ownerName;
    private String intruderName;

    private String reason;

    private long expireTimeMillis;


    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.ownerName = BufferedQuery.readString(byteBuf);
        this.intruderName = BufferedQuery.readString(byteBuf);

        this.reason = BufferedQuery.readString(byteBuf);

        this.expireTimeMillis = BufferedQuery.readVarLong(byteBuf);
    }

    @Override
    public void handle(@NonNull BungeeHandler bungeeHandler) throws Exception {
        bungeeHandler.handle(this);
    }

    public boolean isExpired() {
        return expireTimeMillis - System.currentTimeMillis() <= 0;
    }
}
