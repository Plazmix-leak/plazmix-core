package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@NoArgsConstructor
public class SGlobalOnlinePacket extends Packet<AbstractServer> {

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {

        // Bukkit servers
        BufferedQuery.writeVarInt(PlazmixCore.getInstance().getBukkitServers().size(), byteBuf);
        for (BukkitServer bukkitServer : PlazmixCore.getInstance().getBukkitServers()) {

            BufferedQuery.writeString(bukkitServer.getName(), byteBuf);
            BufferedQuery.writeVarInt(bukkitServer.getOnlineCount(), byteBuf);
        }

        // Bungee servers
        BufferedQuery.writeVarInt(PlazmixCore.getInstance().getBungeeServers().size(), byteBuf);
        for (BungeeServer bungeeServer : PlazmixCore.getInstance().getBungeeServers()) {

            BufferedQuery.writeString(bungeeServer.getName(), byteBuf);
            BufferedQuery.writeVarInt(bungeeServer.getOnlineCount(), byteBuf);
        }
    }

}
