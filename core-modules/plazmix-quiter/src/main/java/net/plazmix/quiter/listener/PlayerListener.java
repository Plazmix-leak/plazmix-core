package net.plazmix.quiter.listener;

import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;
import net.plazmix.quiter.QuitMessage;
import net.plazmix.quiter.QuiterManager;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements EventListener {

    public static final String DEFAULT_QUIT_MESSAGE = ("§c✘ %player% §fвышел с сервера!");

    @EventHandler
    public void onPlayerQuit(PlayerLeaveEvent event) {
        handleQuit(event.getCorePlayer().getPlayerOfflineData().getLastServer(), event.getCorePlayer());
    }

    @EventHandler
    public void onPlayerRedirect(PlayerServerRedirectEvent event) {
        handleQuit(event.getServerFrom(), event.getCorePlayer());
    }

    private void handleQuit(BukkitServer serverFrom, @NonNull CorePlayer corePlayer) {
        if (serverFrom == null || corePlayer.getGroup().isDefault()) {
            return;
        }

        QuiterManager.INSTANCE.injectPlayer(corePlayer);

        new CommonScheduler(RandomStringUtils.randomAlphanumeric(32)) {

            @Override
            public void run() {
                QuitMessage selectedJoinMessage = QuiterManager.INSTANCE.getSelectedMessage(corePlayer.getName());

                if (isServerAvailable(serverFrom)) {
                    String parsedMessage = selectedJoinMessage == null ? DEFAULT_QUIT_MESSAGE.replace("%player%", corePlayer.getDisplayName()) : selectedJoinMessage.parse(corePlayer);

                    for (CorePlayer onlinePlayer : serverFrom.getOnlinePlayers()) {
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
