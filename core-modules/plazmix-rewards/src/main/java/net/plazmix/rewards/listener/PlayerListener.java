package net.plazmix.rewards.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.api.event.impl.ProtocolPacketHandleEvent;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.client.CPlayerConnectPacket;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.common.dailyreward.DailyPlayer;

import java.util.concurrent.TimeUnit;

public final class PlayerListener implements EventListener {

    @EventHandler
    public void onPacketHandle(ProtocolPacketHandleEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof CPlayerConnectPacket) {
            DailyPlayer.of(((CPlayerConnectPacket) packet).getPlayerName()).injectPlayer(null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerAuthCompleteEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        DailyPlayer.of(corePlayer.getName()).injectPlayer(dailyPlayer -> {

            // Если после получения последнего приза прошло больше одного дня
            if (System.currentTimeMillis() - dailyPlayer.getLastRewardTimestamp().getTime() >= TimeUnit.DAYS.toMillis(1)) {
                corePlayer.sendMessage("§d§lНаграды §8:: §fВы можете забрать свою награду!");
            }
        });
    }

}
