package net.plazmix.coreconnector.utility;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.plazmix.coreconnector.CoreConnector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class CoreReconnector {

    private final ScheduledExecutorService reconnectService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> reconnectTask;

    public boolean hasReconnectProcess() {
        return reconnectTask != null;
    }

    public void enableReconnect() {
        if (hasReconnectProcess())
            return;

        System.out.println(ChatColor.YELLOW + "[Connector] Auto-reconnect enabled!");

        reconnectTask = reconnectService.scheduleAtFixedRate(() -> {

            if (CoreConnector.getInstance().isConnected()) {
                disableReconnect();
                return;
            }

            CoreConnector.getInstance().tryConnectionToCore();

        }, 5, 5, TimeUnit.SECONDS);
    }

    public void disableReconnect() {
        if (!hasReconnectProcess())
            return;

        System.out.println(ChatColor.YELLOW + "[Connector] Auto-reconnect disabled!");

        reconnectTask.cancel(true);
        reconnectTask = null;
    }

}
