package net.plazmix.metric.task.gauge;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.metric.task.GaugeMetricTask;

public final class ModeServersCountTask extends GaugeMetricTask {

    private final ServerMode serverMode;

    public ModeServersCountTask(ServerMode serverMode) {
        super(serverMode.name().toLowerCase() + "_servers_count");

        this.serverMode = serverMode;
    }

    @Override
    public int getValue() {
        return PlazmixCore.getInstance().getConnectedServersCount(serverMode.getServersPrefix());
    }

}
