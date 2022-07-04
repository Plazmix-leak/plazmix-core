package net.plazmix.core.connection.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.impl.BungeeServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Getter
public final class ServerManager {

    private final Map<String, BukkitServer> bukkitServers = new HashMap<>();
    private final Map<String, BungeeServer> bungeeServers = new HashMap<>();


    public void addBukkit(@NonNull BukkitServer bukkitServer) {

        synchronized (bukkitServers) {
            bukkitServers.put(bukkitServer.getName().toLowerCase(), bukkitServer);
        }
    }

    public void addBungee(@NonNull BungeeServer bungeeServer) {

        synchronized (bungeeServers) {
            bungeeServers.put(bungeeServer.getName().toLowerCase(), bungeeServer);
        }
    }

    public BukkitServer getBukkit(@NonNull String serverName) {
        return bukkitServers.get(serverName.toLowerCase());
    }

    public BungeeServer getBungee(@NonNull String serverName) {
        return bungeeServers.get(serverName.toLowerCase());
    }

    public void removeBukkit(@NonNull String serverName) {

        synchronized (bukkitServers) {
            bukkitServers.remove(serverName.toLowerCase());
        }
    }

    public void removeBungee(@NonNull String serverName) {

        synchronized (bungeeServers) {
            bungeeServers.remove(serverName.toLowerCase());
        }
    }

    /**
     * Получить сумму онлайна нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public int getOnlineByServerPrefix(@NonNull String serverPrefix) {
        Collection<BukkitServer> serverCollection = getServersByPrefix(serverPrefix);

        int serversOnline = 0;
        for (AbstractServer coreServer : serverCollection) {
            serversOnline += coreServer.getOnlineCount();
        }

        return serversOnline;
    }

    /**
     * Получить список нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public Collection<BukkitServer> getServersByPrefix(@NonNull String serverPrefix) {
        return bukkitServers.values()
                .stream()
                .filter(server -> server.getName().toLowerCase().startsWith(serverPrefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
