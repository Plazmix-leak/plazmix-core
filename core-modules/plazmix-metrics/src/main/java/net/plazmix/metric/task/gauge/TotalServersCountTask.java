package net.plazmix.metric.task.gauge;

import net.plazmix.core.PlazmixCore;
import net.plazmix.metric.task.GaugeMetricTask;

public final class TotalServersCountTask extends GaugeMetricTask {

    public TotalServersCountTask() {
        super("total_servers_count");
    }

    @Override
    public int getValue() {
        return PlazmixCore.getInstance().getConnectedServersCount("");
    }

}
