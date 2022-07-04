package net.plazmix.core.connection.player.offline;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;
import java.util.function.Supplier;

public class OfflineMessageListener implements EventListener {

    @EventHandler
    public void onPlayerAuthComplete(PlayerAuthCompleteEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        Collection<Supplier<String>> offlineMessageCollection = PlazmixCore.getInstance().getPlayerManager()
                .getOfflineMessageMap().get(corePlayer.getName().toLowerCase());

        for (Supplier<String> offlineMessageSupplier : offlineMessageCollection) {
            String offlineMessage = offlineMessageSupplier.get();

            if (offlineMessage != null) {
                corePlayer.sendMessage(offlineMessage);
            }
        }
    }

}
