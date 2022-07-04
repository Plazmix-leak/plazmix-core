package net.plazmix.punishment.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerJoinEvent;
import net.plazmix.core.common.punishment.Punishment;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.common.punishment.PunishmentType;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerMutePacket;

public class MuteListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        CorePlayer corePlayer = playerJoinEvent.getCorePlayer();
        Punishment mutePunishment = PunishmentManager.INSTANCE.getPlayerPunishment(corePlayer.getName(), PunishmentType.TEMP_MUTE);

        if (mutePunishment != null) {
            corePlayer.getBungeeServer().sendPacket(new SPlayerMutePacket(mutePunishment.getPunishmentOwner(), mutePunishment.getPunishmentIntruder(),
                    mutePunishment.getPunishmentReason(), mutePunishment.getPunishmentTime())
            );
        }
    }
}
