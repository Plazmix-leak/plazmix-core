package net.plazmix.core.connection.server.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.MinecraftVersion;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.event.impl.PlayerJoinEvent;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.api.event.impl.PlayerLoginEvent;
import net.plazmix.core.api.mysql.MysqlDatabaseConnection;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.common.language.LanguageManager;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.client.CPlayerConnectPacket;
import net.plazmix.core.connection.protocol.client.CPlayerLeavePacket;
import net.plazmix.core.connection.protocol.client.CPlayerLoginPacket;
import net.plazmix.core.connection.protocol.server.SBungeeServerCreatePacket;
import net.plazmix.core.connection.protocol.server.SLanguagesReloadPacket;
import net.plazmix.core.connection.protocol.server.SPlayerKickPacket;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.core.protocol.ChannelWrapper;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Pattern;

@Log4j2
public class BungeeServer extends AbstractServer {

    public static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("[А-яA-z0-9_]{4,16}");

    public static final String INSERT_PLAYER_ID     = "INSERT INTO `PlayerIdentifier` (`Name`) VALUES (?)";

    public BungeeServer(@NonNull String name,
                        @NonNull InetSocketAddress inetSocketAddress,

                        @NonNull ChannelWrapper channelWrapper) {

        super(name, "BungeeCord", inetSocketAddress, MinecraftVersion.V_1_12_2, channelWrapper);

        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();
        serverManager.addBungee(this);
    }

    @Override
    public Collection<CorePlayer> getOnlinePlayers() {
        return PlazmixCore.getInstance().getOnlinePlayers();
    }


    // <--------------------------------------------------> // HANDLE PROTOCOL // <--------------------------------------------------> //

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        super.channelActive(wrapper);

        log.info("[Bungee] {} (v{}) was successfully connected", name, getMinecraftVersionName());

        //wrapper.setCompression(256);

        for (BukkitServer bukkitServer : PlazmixCore.getInstance().getBukkitServers()) {
            wrapper.write(new SBungeeServerCreatePacket(bukkitServer.getName(), bukkitServer.getServerHost(), bukkitServer.getServerPort()));
        }

        sendOnlineUpdatePacket();
        wrapper.write(new SLanguagesReloadPacket());
    }


    private final Map<String, String> denyLoginPlayers = new TreeMap<>();

    public void handle(CPlayerLoginPacket packet) {
        String playerName   = packet.getPlayerName();
        UUID playerUuid     = packet.getPlayerUuid();

        PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(playerName,
                "No connection to Core.", playerUuid);

        if (playerLoginEvent.isCancelled()) {
            denyLoginPlayers.put(playerName.toLowerCase(), playerLoginEvent.getCancelReason());
            return;
        }

        if (NetworkManager.INSTANCE.getPlayerId(playerName) <= 0) {
            PlazmixCore.getInstance().getMysqlConnection().execute(false, INSERT_PLAYER_ID, playerName);
        }
    }

    @SneakyThrows
    public void handle(CPlayerConnectPacket packet) {
        String playerName                   = packet.getPlayerName();
        UUID playerUuid                     = packet.getPlayerUuid();
        InetSocketAddress socketAddress     = packet.getSocketAddress();

        if (PlazmixCore.getInstance().getPlayer(playerName) != null) {
            return;
        }

        // проверяем на наличие разрешения входа на сервер
        if (denyLoginPlayers.containsKey(playerName.toLowerCase())) {
            sendPacket(new SPlayerKickPacket(playerName, ChatColor.RED + denyLoginPlayers.get(playerName.toLowerCase())));
            return;
        }

        // содержит ли ник неприемлемые символы
        if (!PLAYER_NAME_PATTERN.matcher(playerName).matches()) {
            sendPacket(new SPlayerKickPacket(playerName, ChatColor.RED + "Ваш ник не должен содержать лишних символов!"));
            return;
        }

        // уникальный идентификатор игрока
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        // группа игрока
        Group group = GroupManager.INSTANCE.getPlayerGroup(playerName);

        if (group == null) {
            GroupManager.INSTANCE.setGroupToPlayer(NetworkManager.INSTANCE.getPlayerName(playerId), group = Group.DEFAULT);
        }

        // ладно, помучали, а теперь кешируем уже долбаеба...
        CorePlayer corePlayer = new CorePlayer(playerId, playerName, playerUuid, group, PlazmixCore.getInstance().getBestBungee(), socketAddress);

        corePlayer.setVersionId(packet.getVersion());
        corePlayer.setPremiumAccount(packet.isOnlineMode());

        // язык игрока
        corePlayer.setLanguageType( LanguageManager.INSTANCE.getPlayerLanguage(playerName) );

        // Bukkit server checker
        BukkitServer bukkitServer = PlazmixCore.getInstance().getBukkitServer(packet.getServer());
        corePlayer.setBukkitServer(bukkitServer);

        // Add player in Core.
        PlazmixCore.getInstance().getPlayerManager().playerConnect(corePlayer);

        log.info("[Player] " + ChatColor.stripColor(corePlayer.getDisplayName()) + " has been connected "
                + (bukkitServer != null ? "(" + bukkitServer.getName() + ")" : ""));

        // Call join event.
        if (!packet.isAlreadyPlaying()) {
            PlazmixCore.getInstance().getEventManager().callEvent(new PlayerJoinEvent(corePlayer));
        }

        // Send update packets
        sendOnlineUpdatePacket();

        if (bukkitServer != null) {
            sendPlayerRequestPackets(corePlayer, bukkitServer);
        }
    }

    public void handle(@NonNull CPlayerLeavePacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(packet.getPlayerName());

        log.info("[Player] " + ChatColor.stripColor(corePlayer.getDisplayName()) + " has been disconnected");

        PlayerLeaveEvent playerLeaveEvent = new PlayerLeaveEvent(corePlayer);
        PlazmixCore.getInstance().getEventManager().callEvent(playerLeaveEvent);

        PlazmixCore.getInstance().getPlayerManager().playerDisconnect(corePlayer);
        sendOnlineUpdatePacket();
    }

}
