package net.plazmix.coreconnector.direction.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.plazmix.coreconnector.direction.bungee.BungeeConnectorPlugin;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.module.type.skin.SkinsModule;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;

import java.util.HashMap;
import java.util.Map;

public final class SkinsListener
        implements Listener {

    private final SkinsModule skinsModule = NetworkModule.getInstance().getSkinsModule();
    private final Map<PendingConnection, Long> playerLoginTimeMap = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(LoginEvent event) {

        BungeeCord.getInstance().getScheduler()
                .runAsync(BungeeConnectorPlugin.getInstance(), () -> {

                    PendingConnection connection = event.getConnection();
                    PlayerSkin playerSkin = skinsModule.generate(connection.getName());

                    // обновляем скин игроку
                    skinsModule.updateSkin(connection.getName(), playerSkin);
                    playerLoginTimeMap.put(connection, System.currentTimeMillis());
                });
    }

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (playerLoginTimeMap.containsKey(player.getPendingConnection())) {
            playerLoginTimeMap.remove(player.getPendingConnection());
            return;
        }

        PlayerSkin playerSkin = skinsModule.getCurrentPlayerSkin(player.getName());

        if (playerSkin != null) {
            BungeeConnectorPlugin.SKIN_SETTER.updateSkin(player, playerSkin);
        }
    }

    @EventHandler
    public void onPlayerDisconnected(PlayerDisconnectEvent event) {
        playerLoginTimeMap.remove(event.getPlayer().getPendingConnection());
        skinsModule.playerSkinsHistoryMap.remove(NetworkModule.getInstance().getPlayerId(event.getPlayer().getName()));
    }

    @EventHandler
    public void onPluginReceived(PluginMessageEvent event) {
        if (!event.getTag().equals("tc:setskin")) {
            return;
        }

        event.setCancelled(true);

        // Read & set player skin.
        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(input.readUTF());
        MojangSkin mojangSkin = MojangApi.createSkinObject(input.readUTF(), input.readUTF());

        BungeeConnectorPlugin.SKIN_SETTER.updateSkin(player, mojangSkin);
    }

}
