package net.plazmix.coreconnector.direction.bungee.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.utility.server.ServerMode;

public final class KickListener implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo kickedFrom = event.getKickedFrom();

        if (!ConnectionListener.ONLINE_PLAYERS.contains(player)) {
            return;
        }

        if (kickedFrom == null) {
            String limboServer = NetworkModule.getInstance().getBestServer(false, ServerMode.LIMBO);
            event.setCancelled(true);

            if (limboServer == null) {
                event.setCancelServer(ProxyServer.getInstance().getServerInfo(NetworkModule.getInstance()
                        .getBestServer(false, ServerMode.HUB)));

                return;
            }

            event.setCancelServer(ProxyServer.getInstance().getServerInfo(limboServer));

        } else {

            if (ServerMode.isTyped(kickedFrom.getName(), ServerMode.AUTH) || ServerMode.isTyped(kickedFrom.getName(), ServerMode.LIMBO)) {
                return;
            }

            String lobbyServer = ServerMode.getFallbackLobby(kickedFrom.getName());
            if (lobbyServer != null) {

                event.setCancelled(true);
                event.setCancelServer(ProxyServer.getInstance().getServerInfo(lobbyServer));
            }

            // Announce
            ServerInfo cancelServer = event.getCancelServer();

            if (cancelServer != null) {
                player.sendMessage("§cСоединение с сервером " + kickedFrom.getName() + " было потеряно по причине: " + ChatColor.WHITE + ChatColor.stripColor(event.getKickReason()));
            }
        }
    }

}
