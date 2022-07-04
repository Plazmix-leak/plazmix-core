package net.plazmix.punishment.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerJoinEvent;
import net.plazmix.core.common.punishment.Punishment;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.common.punishment.PunishmentType;
import net.plazmix.core.connection.player.CorePlayer;

public class BanListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        Punishment tempPlayerBan = PunishmentManager.INSTANCE.getPlayerPunishment(corePlayer.getName(), PunishmentType.TEMP_BAN);
        Punishment permanentPlayerBan = PunishmentManager.INSTANCE.getPlayerPunishment(corePlayer.getName(), PunishmentType.PERMANENT_BAN);

        if (tempPlayerBan != null) {
            tempPlayerBan.giveToPlayer(corePlayer);
        }

        else if (permanentPlayerBan != null) {
            permanentPlayerBan.giveToPlayer(corePlayer);
        }
    }
}
