package net.plazmix.core.connection.server.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.core.connection.server.impl.BukkitServer;

@RequiredArgsConstructor
@Getter
public class GameServerInfo {

    private final String map;
    private final String mode;

    private final boolean available;

    private final int alivePlayers;
    private final int maxPlayers;

    public static GameServerInfo of(@NonNull BukkitServer bukkitServer) {
        String motdPrefix = ("Game@");
        String serverMotd = bukkitServer.getMotd();

        if (serverMotd == null) {
            return null;
        }

        if (!serverMotd.startsWith(motdPrefix)) {
            return null;
        }

        try {
            return JsonUtil.fromJson(serverMotd.substring(motdPrefix.length()), GameServerInfo.class);

        } catch (Exception exception) {
            return null;
        }
    }
}
