package net.plazmix.metric.task.gauge;

import net.plazmix.core.PlazmixCore;
import net.plazmix.metric.task.GaugeMetricTask;

public final class TotalOnlineTask extends GaugeMetricTask {

    public TotalOnlineTask() {
        super("total_online");
    }

    @Override
    public int getValue() {
        return PlazmixCore.getInstance().getGlobalOnline();
    }

}
