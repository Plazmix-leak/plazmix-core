package net.plazmix.coreconnector.protocol;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.direction.bungee.event.PlayerLanguageChangeEvent;
import net.plazmix.coreconnector.direction.bungee.listener.ChatListener;
import net.plazmix.coreconnector.protocol.client.*;
import net.plazmix.coreconnector.protocol.server.SPlayerConnectPacket;
import net.plazmix.coreconnector.utility.mojang.MojangApi;

import java.net.InetSocketAddress;

public class BungeeHandler extends AbstractServerHandler {

    @Override
    public void channelInactive() {
        super.channelInactive();

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {

            proxiedPlayer.sendMessage("§cСоединение с игровым координатором было разорвано, многие системы и команды по типу /hub, /online могут не работать!");
            proxiedPlayer.sendMessage("§cПосле установки соединения Вы сможете продолжать пользоваться командами");
            proxiedPlayer.sendMessage("§cЕсли этого не произошло, то пиши сюда - vk.me/plazmixnetwork");
        }

        ProxyServer.getInstance().getLogger().info(ChatColor.RED + "[Core] Connection refused");
    }

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        super.channelActive(wrapper);

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {

            PendingConnection pendingConnection = proxiedPlayer.getPendingConnection();
            proxiedPlayer.sendMessage("§d§lPlazmix §8:: §aСоединение с игровым координатором было установлено!");

            channelWrapper.write(new SPlayerConnectPacket(proxiedPlayer.getName(), proxiedPlayer.getUniqueId(), proxiedPlayer.getAddress(), proxiedPlayer.getServer().getInfo().getName(),
                    pendingConnection.getVersion(), MojangApi.isPremium(proxiedPlayer.getName(), proxiedPlayer.getUniqueId()), true));
        }

        ProxyServer.getInstance().getLogger().info(ChatColor.GREEN + "[Core] Success connected as " + CoreConnector.getInstance().getServerName());
    }

    @Override
    public void handle(@NonNull CRestartServerPacket packet) {
        ProxyServer.getInstance().stop();
    }

    @Override
    public void handle(@NonNull CPlayerLocaleUpdatePacket packet) {
        LanguageType languageFrom = LanguageManager.INSTANCE.getPlayerLanguage(packet.getPlayerName());
        LanguageType languageTo = LanguageType.VALUES[packet.getLanguageIndex()];

        if (languageTo.equals(languageFrom)) {
            return;
        }

        // Update player language.
        LanguageManager.INSTANCE.getPlayerLanguageMap().put(NetworkManager.INSTANCE.getPlayerId(packet.getPlayerName()), languageTo);

        // Call the event.
        PlayerLanguageChangeEvent event = new PlayerLanguageChangeEvent(ProxyServer.getInstance().getPlayer(packet.getPlayerName()), languageFrom, languageTo);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    public void handle(@NonNull CBungeeServerCreatePacket packet) {
        String serverName = packet.getServerName();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(packet.getServerHost(), packet.getServerPort());

        ServerInfo serverInfo = ProxyServer.getInstance()
                .constructServerInfo(serverName, inetSocketAddress, serverName, false);

        ProxyServer.getInstance().getServers().put(serverName.toLowerCase(), serverInfo);
        ProxyServer.getInstance().getLogger().info(ChatColor.YELLOW + "[Core] Server '" + serverName + "' has been connected on " + inetSocketAddress.toString());
    }

    public void handle(@NonNull CPlayerServerRedirectPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayerName());
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(packet.getServerName());

        if (player != null && serverInfo != null) {
            player.connect(serverInfo);
        }
    }

    public void handle(@NonNull CPlayerKickPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayerName());

        if (player != null) {
            player.disconnect(packet.getReasonMessage());
        }
    }

    public void handle(@NonNull CPlayerMutePacket packet) {
        int intruderId = NetworkManager.INSTANCE.getPlayerId(packet.getIntruderName());

        // Знаю что кал, мне лень ок делать(((
        ChatListener.PLAYER_MUTES_MAP.put(intruderId, packet);
    }

    public void handle(@NonNull CPlayerUnmutePacket packet) {
        int intruderId = NetworkManager.INSTANCE.getPlayerId(packet.getIntruderName());

        // Знаю что кал, мне лень ок делать(((
        ChatListener.PLAYER_MUTES_MAP.remove(intruderId);
    }

}
