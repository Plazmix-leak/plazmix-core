package net.plazmix.metric.task.gauge;

import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.metric.task.GaugeMetricTask;

public final class ModeOnlineTask extends GaugeMetricTask {

    private final ServerMode serverMode;

    public ModeOnlineTask(ServerMode serverMode) {
        super(serverMode.name().toLowerCase() + "_online");

        this.serverMode = serverMode;
    }

    @Override
    public int getValue() {
        return serverMode.getOnline();
    }

}
