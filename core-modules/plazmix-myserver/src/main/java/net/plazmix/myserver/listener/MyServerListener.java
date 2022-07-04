package net.plazmix.myserver.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.myserver.type.MyServerManager;

public class MyServerListener implements EventListener {

    @EventHandler
    public void onPlayerRedirect(PlayerServerRedirectEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        BukkitServer serverFrom = event.getServerFrom();

        checkServer(corePlayer, serverFrom);
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        BukkitServer serverFrom = corePlayer.getBukkitServer();

        checkServer(corePlayer, serverFrom);
    }

    private void checkServer(CorePlayer corePlayer, BukkitServer bukkitServer) {
        if (bukkitServer == null) {
            return;
        }

        if (MyServerManager.INSTANCE.isLeader(bukkitServer.getName(), corePlayer)) {
            MyServerManager.INSTANCE.getPlayerServer(corePlayer).shutdown();
        }

        else if (MyServerManager.INSTANCE.isModer(bukkitServer.getName(), corePlayer)) {
            MyServerManager.INSTANCE.getPlayerServer(corePlayer).removeModer(corePlayer);
        }
    }
}
