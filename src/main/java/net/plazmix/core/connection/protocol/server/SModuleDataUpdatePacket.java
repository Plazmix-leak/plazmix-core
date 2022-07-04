package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.module.execute.ModuleExecuteQuery;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class SModuleDataUpdatePacket extends Packet<AbstractServer> {

    private ModuleExecuteType executeType;
    private ModuleExecuteQuery moduleExecuteQuery;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeVarInt(executeType.ordinal(), byteBuf);

        BufferedQuery.writeString(moduleExecuteQuery.getModuleName(), byteBuf);

        BufferedQuery.writeString(moduleExecuteQuery.getKey(), byteBuf);
        BufferedQuery.writeString(JsonUtil.toJson(moduleExecuteQuery.getValue()), byteBuf);
    }

}
