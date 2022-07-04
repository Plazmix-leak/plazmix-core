package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.module.ModuleExecuteType;
import net.plazmix.coreconnector.protocol.AbstractServerHandler;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CModuleDataUpdatePacket extends Packet<AbstractServerHandler> {

    private ModuleExecuteType executeType;

    private String module;

    private String key;
    private String json;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.executeType = ModuleExecuteType.VALUES[BufferedQuery.readVarInt(byteBuf)];

        this.module = BufferedQuery.readString(byteBuf);

        this.key = BufferedQuery.readString(byteBuf);
        this.json = BufferedQuery.readString(byteBuf);
    }

    @Override
    public void handle(@NonNull AbstractServerHandler abstractServerHandler) throws Exception {
        abstractServerHandler.handle(this);
    }
}
