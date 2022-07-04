package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

import java.util.ArrayList;
import java.util.List;

public class SBukkitCommandsPacket extends Packet<BukkitServer> {

    private final List<String> commandCollection
            = new ArrayList<>(PlazmixCore.getInstance().getCommandManager().getCommandMap().keySet());

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeStringArray(commandCollection, byteBuf);
    }
}
