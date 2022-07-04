package net.plazmix.metric.task.gauge;

import net.plazmix.core.protocol.metrics.PerformanceMetrics;
import net.plazmix.metric.task.GaugeMetricTask;

public final class ReceivePacketsCountTask extends GaugeMetricTask {

    public ReceivePacketsCountTask() {
        super("receive_packets_count");
    }

    @Override
    public int getValue() {
        return PerformanceMetrics.RECEIVED_PACKETS.now();
    }

}
