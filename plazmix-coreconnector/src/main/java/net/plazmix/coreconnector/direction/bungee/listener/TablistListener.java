package net.plazmix.coreconnector.direction.bungee.listener;

import lombok.NonNull;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.direction.bungee.BungeeConnectorPlugin;
import net.plazmix.coreconnector.direction.bungee.event.PlayerConnectedEvent;
import net.plazmix.coreconnector.utility.NumberUtil;
import net.plazmix.coreconnector.utility.localization.LocalizationResource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class TablistListener
        implements Listener {

    private static final List<ProxiedPlayer> PLAYERS_TAB_UPDATER
            = new ArrayList<>();

    public static final String PLAYER_TAB_HEADER_KEY      = "PLAYER_TAB_HEADER";
    public static final String PLAYER_TAB_FOOTER_KEY      = "PLAYER_TAB_FOOTER";

    private static void updatePlayerTab(@NonNull ProxiedPlayer player) {
        LocalizationResource resource = LanguageManager.INSTANCE.getPlayerLanguage(player.getName()).getResource();

        String header = String.join("\n", resource.getTextList(PLAYER_TAB_HEADER_KEY));
        String footer = String.join("\n", resource.getTextList(PLAYER_TAB_FOOTER_KEY));

        if (player.getServer() != null) {
            footer = footer
                    .replace("%server%", player.getServer().getInfo().getName())
                    .replace("%online%", NumberUtil.spaced(BungeeCord.getInstance().getOnlineCount()));
        }

        player.setTabHeader(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', header)),
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', footer)));
    }

    private static final ScheduledTask TABLIST_UPDATE_TASK = BungeeCord.getInstance().getScheduler()
            .schedule(BungeeConnectorPlugin.getInstance(), () -> {

        for (ProxiedPlayer player : PLAYERS_TAB_UPDATER) {
            updatePlayerTab(player);
        }

    }, 1000, 1500, TimeUnit.MILLISECONDS);


    @EventHandler
    public void onPlayerConnected(PlayerConnectedEvent event) {
        PLAYERS_TAB_UPDATER.add(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PLAYERS_TAB_UPDATER.remove(event.getPlayer());
    }

}
