package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.AbstractServerHandler;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CRestartServerPacket extends Packet<AbstractServerHandler> {

    private String restartReason;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.restartReason = BufferedQuery.readString(byteBuf);
    }

    @Override
    public void handle(@NonNull AbstractServerHandler abstractServerHandler) throws Exception {
        abstractServerHandler.handle(this);
    }
}
