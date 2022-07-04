package net.plazmix.core.connection.server.game;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.api.MinecraftVersion;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.ChannelWrapper;

import java.net.InetSocketAddress;

@Getter
public class BukkitGameServer extends BukkitServer {

    private GameServerInfo gameServerInfo;

    public BukkitGameServer(@NonNull String serverName, @NonNull InetSocketAddress socketAddress, @NonNull MinecraftVersion minecraftVersion, @NonNull ChannelWrapper channelWrapper) {
        super(serverName, "", socketAddress, minecraftVersion, channelWrapper);
    }

    @Override
    public void setMotd(@NonNull String motd) {
        super.setMotd(motd);

        this.gameServerInfo = GameServerInfo.of(this);
    }

}
