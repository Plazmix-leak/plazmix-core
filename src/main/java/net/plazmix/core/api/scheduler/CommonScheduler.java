package net.plazmix.core.api.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import net.plazmix.core.PlazmixCore;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class CommonScheduler implements Runnable {

    @Getter
    private final String identifier;

    public CommonScheduler() {
        this(RandomStringUtils.randomAlphanumeric(32));
    }


    /**
     * Отмена и закрытие потока
     */
    public void cancel() {
        PlazmixCore.getInstance().getSchedulerManager().cancelScheduler(identifier);
    }

    /**
     * Запустить асинхронный поток
     */
    public void runAsync() {
        PlazmixCore.getInstance().getSchedulerManager().runAsync(this);
    }

    /**
     * Запустить поток через определенное
     * количество времени
     *
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(long delay, TimeUnit timeUnit) {
        PlazmixCore.getInstance().getSchedulerManager().runLater(identifier, this, delay, timeUnit);
    }

    /**
     * Запустить цикличный поток через
     * определенное количество времени
     *
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(long delay, long period, TimeUnit timeUnit) {
        PlazmixCore.getInstance().getSchedulerManager().runTimer(identifier, this, delay, period, timeUnit);
    }

}
