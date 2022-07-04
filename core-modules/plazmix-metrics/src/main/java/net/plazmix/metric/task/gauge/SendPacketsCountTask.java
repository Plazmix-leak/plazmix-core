package net.plazmix.metric.task.gauge;

import net.plazmix.core.common.party.PartyManager;
import net.plazmix.core.protocol.metrics.PerformanceMetrics;
import net.plazmix.metric.task.GaugeMetricTask;

public final class SendPacketsCountTask extends GaugeMetricTask {

    public SendPacketsCountTask() {
        super("send_packets_count");
    }

    @Override
    public int getValue() {
        return PerformanceMetrics.SENT_PACKETS.now();
    }

}
