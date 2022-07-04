package net.plazmix.coreconnector.direction.bungee.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.auth.AuthManager;
import net.plazmix.coreconnector.direction.bungee.event.PlayerConnectedEvent;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.protocol.server.SPlayerConnectPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerLeavePacket;
import net.plazmix.coreconnector.protocol.server.SPlayerLoginPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerServerRedirectPacket;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.server.ServerMode;

import java.util.ArrayList;
import java.util.List;

public final class ConnectionListener implements Listener {

    public static final List<ProxiedPlayer> ONLINE_PLAYERS
            = new ArrayList<>();

    @EventHandler
    public void onLogin(LoginEvent event) {
        if (!CoreConnector.getInstance().isConnected()) {

            event.setCancelled(true);
            event.setCancelReason(TextComponent.fromLegacyText("§d§lPlazmix\n§cНет соединения с главным координатором сервера"));
            return;
        }

        PendingConnection pendingConnection = event.getConnection();

        SPlayerLoginPacket playerLoginPacket = new SPlayerLoginPacket(pendingConnection.getName(), pendingConnection.getUniqueId());
        CoreConnector.getInstance().sendPacket(playerLoginPacket);
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        ONLINE_PLAYERS.remove(proxiedPlayer);

        CoreConnector.getInstance().sendPacket(new SPlayerLeavePacket(proxiedPlayer.getName()));
    }

    @EventHandler
    public void onPlayerConnected(ServerConnectedEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        String serverName = event.getServer().getInfo().getName();

        if (ONLINE_PLAYERS.contains(proxiedPlayer)) {

            SPlayerServerRedirectPacket redirectPacket = new SPlayerServerRedirectPacket(proxiedPlayer.getName(), serverName, false);
            CoreConnector.getInstance().sendPacket(redirectPacket);
            return;
        }

        ONLINE_PLAYERS.add(proxiedPlayer);

        CoreConnector.getInstance().sendPacket(new SPlayerConnectPacket(proxiedPlayer.getName(), proxiedPlayer.getUniqueId(), proxiedPlayer.getAddress(), serverName,
                proxiedPlayer.getPendingConnection().getVersion(), MojangApi.isPremium(proxiedPlayer.getName(), proxiedPlayer.getUniqueId()), false));

        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerConnectedEvent(proxiedPlayer, event.getServer()));
    }

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!ONLINE_PLAYERS.contains(player)) {
            ServerInfo playerJoinServer = getJoinServer(player);

            if (playerJoinServer != null) {
                event.setTarget(playerJoinServer);
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (player.isConnected()) {
            player.sendMessage(ChatColor.GREEN + "Вы были перемещены на сервер " + event.getServer().getInfo().getName());
        }
    }

    private boolean hasAuthSession(ProxiedPlayer player) {
        int playerId = NetworkModule.getInstance().getPlayerId(player.getName());

        AuthManager.INSTANCE.getAuthPlayerMap().remove(playerId);
        return AuthManager.INSTANCE.hasAuthSession(playerId);
    }

    private ServerInfo getJoinServer(ProxiedPlayer player) {
        ServerInfo authServer = ProxyServer.getInstance().getServerInfo(ServerMode.AUTH.getFirstActiveServer());

        if (authServer == null) {
            ServerInfo limboServer = ProxyServer.getInstance().getServerInfo(ServerMode.LIMBO.getFirstActiveServer());;

            if (limboServer != null) {
                player.connect(limboServer);

            } else {

                player.disconnect("§d§lPlazmix\n§cОшибка, лимбо-сервера и сервера авторизации временно недоступны!");
            }
        }

        if (!hasAuthSession(player)) {
            return authServer;
        }

        return ProxyServer.getInstance().getServerInfo(ServerMode.HUB.getFirstActiveServer());
    }

}
