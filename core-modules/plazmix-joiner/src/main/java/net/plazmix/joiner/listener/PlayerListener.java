package net.plazmix.joiner.listener;

import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerJoinEvent;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;
import net.plazmix.joiner.JoinMessage;
import net.plazmix.joiner.JoinerManager;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements EventListener {

    public static final String DEFAULT_JOIN_MESSAGE = ("§a▸ %player% §fприсоединился на сервер!");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleJoiner(event.getCorePlayer().getBukkitServer(), event.getCorePlayer());
    }

    @EventHandler
    public void onPlayerRedirect(PlayerServerRedirectEvent event) {
        handleJoiner(event.getServerTo(), event.getCorePlayer());
    }

    private void handleJoiner(@NonNull BukkitServer serverTo, @NonNull CorePlayer corePlayer) {
        if (corePlayer.getGroup().isDefault()) {
            return;
        }

        JoinerManager.INSTANCE.injectPlayer(corePlayer);

        new CommonScheduler(RandomStringUtils.randomAlphanumeric(32)) {

            @Override
            public void run() {
                JoinMessage selectedJoinMessage = JoinerManager.INSTANCE.getSelectedMessage(corePlayer.getName());

                if (isServerAvailable(serverTo)) {
                    String parsedMessage = selectedJoinMessage == null ? DEFAULT_JOIN_MESSAGE.replace("%player%", corePlayer.getDisplayName()) : selectedJoinMessage.parse(corePlayer);

                    for (CorePlayer onlinePlayer : serverTo.getOnlinePlayers()) {
                        onlinePlayer.sendMessage(parsedMessage);
                    }
                }
            }

        }.runLater(1, TimeUnit.SECONDS);
    }

    private boolean isServerAvailable(BukkitServer bukkitServer) {

        // MyServers module.
        if (bukkitServer.getName().startsWith("ms-")) {
            return false;
        }

        // Server modes.
        return !ServerMode.isTyped(bukkitServer, ServerMode.AUTH) &&
                !ServerMode.isTyped(bukkitServer, ServerMode.LIMBO) &&
                !ServerMode.isTyped(bukkitServer, ServerSubModeType.GAME_ARENA);
    }


}
