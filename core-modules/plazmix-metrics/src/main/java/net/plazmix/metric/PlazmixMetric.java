package net.plazmix.metric;

import io.prometheus.client.exporter.HTTPServer;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.metric.task.gauge.*;

@Log4j2
@CoreModuleInfo(name = "Metric", author = "ItzStonlex")
public class PlazmixMetric extends CoreModule {

    private void registerOnlineMetrics() {
        new TotalOnlineTask().start();

        for (ServerMode serverMode : ServerMode.values()) {
            new ModeOnlineTask(serverMode).start();
        }
    }

    private void registerServersMetrics() {
        new TotalServersCountTask().start();

        for (ServerMode serverMode : ServerMode.values()) {
            new ModeServersCountTask(serverMode).start();
        }
    }

    private void registerProtocolMetrics() {
        new SendPacketsCountTask().start();
        new ReceivePacketsCountTask().start();
    }

    private void registerDatabaseMetrics() {
        new TotalPlayersCountTask().start();
    }

    private void registerModulesMetrics() {
        new PartyCountTask().start();
    }

    private void registerAllMetrics() {
        registerOnlineMetrics();
        registerServersMetrics();
        registerProtocolMetrics();
        registerDatabaseMetrics();
        registerModulesMetrics();
    }


    @SneakyThrows
    private void startMetrics() {
       new HTTPServer("10.0.0.3", 9000);
    }

    @Override
    protected void onEnable() {
        log.info(ChatColor.YELLOW + "[Metrics] Register metric tasks...");
        registerAllMetrics();

        log.info(ChatColor.YELLOW + "[Metrics] Start HTTP Server on 10.0.0.3:9000...");
        startMetrics();
    }

    @Override
    protected void onDisable() {
        // ...
    }
}
