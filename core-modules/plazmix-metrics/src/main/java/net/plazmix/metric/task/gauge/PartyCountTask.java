package net.plazmix.metric.task.gauge;

import net.plazmix.core.common.party.PartyManager;
import net.plazmix.metric.task.GaugeMetricTask;

public final class PartyCountTask extends GaugeMetricTask {

    public PartyCountTask() {
        super("party_count");
    }

    @Override
    public int getValue() {
        return PartyManager.INSTANCE.getAllParties().size();
    }

}
