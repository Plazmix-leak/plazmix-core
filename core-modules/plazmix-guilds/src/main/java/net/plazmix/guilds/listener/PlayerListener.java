package net.plazmix.guilds.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.common.guild.CoreGuild;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.guilds.GuildRequestManager;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements EventListener {

    @EventHandler
    public void onPlayerAuthComplete(PlayerAuthCompleteEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        CoreGuild coreGuild = CoreGuild.of(corePlayer);

        if (coreGuild == null) {
            return;
        }

        new CommonScheduler("guilds_" + event.getCorePlayer().getPlayerId() + "_injection") {

            @Override
            public void run() {

                // Чекаем актуальные запросы в гильдию
                Collection<Integer> guildRequestCollection = GuildRequestManager.INSTANCE.getGuildsRequestsIds(corePlayer.getPlayerId());
                if (!guildRequestCollection.isEmpty()) {

                    corePlayer.sendMessage("§d§lГильдии §8:: §fУ Вас есть §6" + guildRequestCollection.size() + " §fнеотвеченных заявок!");
                    corePlayer.sendMessage(" §fЧтобы проверить их, напишите - §6/guilds requests");
                }
            }

        }.runLater(1, TimeUnit.SECONDS);
    }

}
