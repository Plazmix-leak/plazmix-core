package net.plazmix.party.listener;

import lombok.NonNull;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;
import net.plazmix.core.common.party.Party;
import net.plazmix.core.common.party.PartyManager;

public final class PartyWarpListener
        implements EventListener {

    @EventHandler
    public void onPlayerRedirect(PlayerServerRedirectEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        Party party = PartyManager.INSTANCE.getParty(corePlayer);

        BukkitServer bukkitServer = event.getServerTo();

        if (canWarp(bukkitServer) && party != null && party.isLeader(corePlayer)) {
            party.warp(bukkitServer);
        }
    }

    private boolean canWarp(@NonNull BukkitServer bukkitServer) {
        return ServerMode.isTyped(bukkitServer, ServerSubModeType.GAME_ARENA) || bukkitServer.getName().startsWith("ms-");
    }

}
