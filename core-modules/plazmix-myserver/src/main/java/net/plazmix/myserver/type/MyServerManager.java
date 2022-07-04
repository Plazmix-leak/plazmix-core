package net.plazmix.myserver.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.player.CorePlayer;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MyServerManager {

    public static final MyServerManager INSTANCE = new MyServerManager();

    @Getter
    private final Map<String, PlayerMyServer> playerMyServers
            = new HashMap<>();


    public void createServer(@NonNull CorePlayer corePlayer, @NonNull MyServerType myServerType) {
        PlayerMyServer playerMyServer = new PlayerMyServer(corePlayer, myServerType);
        boolean isStarted = playerMyServer.start();

        if (isStarted) {
            playerMyServers.put(corePlayer.getName().toLowerCase(), playerMyServer);
        }
    }

    public Collection<PlayerMyServer> getActiveServers() {
        return playerMyServers.values();
    }

    public Collection<PlayerMyServer> getActiveServers(@NonNull MyServerCategory category) {
        return playerMyServers.values()
                .stream()
                .filter(playerMyServer -> playerMyServer.getServerType().getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public Collection<PlayerMyServer> getActiveServers(@NonNull MyServerType type) {
        return playerMyServers.values()
                .stream()
                .filter(playerMyServer -> playerMyServer.getServerType().equals(type))
                .collect(Collectors.toList());
    }

    public void removeServer(@NonNull CorePlayer corePlayer) {
        playerMyServers.remove(corePlayer.getName().toLowerCase());
    }

    public boolean isAvailable(@NonNull Path serverFolder) {
        for (PlayerMyServer playerMyServer : playerMyServers.values())

            if (playerMyServer.getServerFolder() != null && playerMyServer.getServerFolder().equals(serverFolder))
                return true;

        return false;
    }

    public boolean hasServer(@NonNull String serverName) {
        for (PlayerMyServer playerMyServer : playerMyServers.values())

            if (playerMyServer.getServerName().equalsIgnoreCase(serverName))
                return true;

        return false;
    }

    public boolean isLeader(@NonNull String serverName, @NonNull CorePlayer corePlayer) {
        return hasServer(serverName) && getPlayerServer(corePlayer) != null &&

                getPlayerServer(corePlayer).getServerName().equalsIgnoreCase(serverName) &&
                getPlayerServer(corePlayer).isLeader(corePlayer);
    }

    public boolean isModer(@NonNull String serverName, @NonNull CorePlayer corePlayer) {
        return hasServer(serverName) && getPlayerServer(corePlayer) != null &&

                getPlayerServer(corePlayer).getServerName().equalsIgnoreCase(serverName) &&
                getPlayerServer(corePlayer).isModer(corePlayer);
    }

    public PlayerMyServer getPlayerServer(@NonNull CorePlayer corePlayer) {
        return playerMyServers.get(corePlayer.getName().toLowerCase());
    }

}
