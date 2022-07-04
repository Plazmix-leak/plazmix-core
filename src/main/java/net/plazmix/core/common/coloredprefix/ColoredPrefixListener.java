package net.plazmix.core.common.coloredprefix;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerJoinEvent;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

public final class ColoredPrefixListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        BukkitServer bukkitServer = corePlayer.getBukkitServer();

        ChatColor prefixColor = ColoredPrefixSqlHandler.INSTANCE.getPrefixColor(corePlayer.getName());

        if (prefixColor != null) {
            ColoredPrefixSqlHandler.INSTANCE.getPlayerPrefixColorsMap().put(corePlayer.getPlayerId(), prefixColor);

            PlazmixCore.getInstance().execute(bukkitServer, ModuleExecuteType.INSERT, "ColoredPrefix", "PREFIX_COLOR_" + corePlayer.getPlayerId(), prefixColor);
        }
    }

    @EventHandler
    public void onRedirect(PlayerServerRedirectEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        ChatColor prefixColor = ColoredPrefixSqlHandler.INSTANCE.getPrefixColor(corePlayer.getName());

        if (prefixColor != null) {
            PlazmixCore.getInstance().execute(event.getServerTo(), ModuleExecuteType.INSERT, "ColoredPrefix", "PREFIX_COLOR_" + corePlayer.getPlayerId(), prefixColor);
        }
    }
}
