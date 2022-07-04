package net.plazmix.metric.task;

import io.prometheus.client.Gauge;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.scheduler.CommonScheduler;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class GaugeMetricTask extends CommonScheduler {

    private final Gauge metric;

    public GaugeMetricTask(String id) {
        metric = Gauge.build()
                .name(id)
                .help("Тинькофф 4377723763742255")
                .register();
    }

    public abstract int getValue();

    @Override
    public void run() {
        metric.set(getValue());
    }

    public void start() {
        runTimer(10, 10, TimeUnit.SECONDS);
    }

}
